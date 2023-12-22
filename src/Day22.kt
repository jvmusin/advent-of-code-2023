import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.milliseconds

class Day22(val input: List<String>) {
    var field = input.map { it.toCharArray() }
    val n = input.n

    data class Position(val x: Int, val y: Int, val z: Int) {
        fun isOnTopOf(other: Position): Boolean {
            return x == other.x && y == other.y && z > other.z
        }

        fun isDirectlyOnTopOf(other: Position): Boolean {
            return x == other.x && y == other.y && z == other.z + 1
        }
    }

    data class Brick(val index: Int, val p1: Position, val p2: Position) {
        val minX = minOf(p1.x, p2.x)
        val maxX = maxOf(p1.x, p2.x)
        val minY = minOf(p1.y, p2.y)
        val maxY = maxOf(p1.y, p2.y)
        val minZ = minOf(p1.z, p2.z)
        val maxZ = maxOf(p1.z, p2.z)

        fun allCubes() = sequence {
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    for (z in minZ..maxZ) {
                        yield(Position(x, y, z))
                    }
                }
            }
        }

        fun fall(distance: Int) = Brick(index, p1.copy(z = p1.z - distance), p2.copy(z = p2.z - distance))

        fun isOnTopOf(other: Brick): Boolean {
            if (this === other) return false // avoid vertical
            val otherCubes = other.allCubes().toSet()
            return allCubes().any { thisCube -> otherCubes.any { otherCube -> thisCube.isOnTopOf(otherCube) } }
        }

        fun isDirectlyOnTopOf(other: Brick): Boolean {
            if (this === other) return false // avoid vertical
            val otherCubes = other.allCubes().toSet()
            return allCubes().any { thisCube -> otherCubes.any { otherCube -> thisCube.isDirectlyOnTopOf(otherCube) } }
        }
    }

    fun readPosition(s: String): Position {
        val (x, y, z) = s.split(',').map(String::toInt)
        return Position(x, y, z)
    }

    fun readBrick(index: Int, s: String): Brick {
        val (p1, p2) = s.split('~').map(::readPosition)
        return Brick(index, p1, p2)
    }

    fun part1(): Any {
        val bricks = input.mapIndexed { index, s -> readBrick(index, s) }.toMutableList()
        val fallen = hashSetOf<Int>()
        fun fall(b: Brick) {
            if (!fallen.add(b.index)) return
            var maxZ = 0
            for (other in bricks) {
                if (b.isOnTopOf(other)) {
                    fall(other)
                    maxZ = maxOf(maxZ, bricks[other.index].maxZ)
                }
            }
            bricks[b.index] = bricks[b.index].fall(b.minZ - maxZ - 1)
        }
        bricks.forEach(::fall)
        val was = bricks.toList()
        fallen.clear()
        bricks.forEach(::fall)
        require(bricks == was)
        val notSafe = hashSetOf<Int>()
        for (b in bricks) {
            val standsOn = bricks.count { b.isDirectlyOnTopOf(it) }
            if (standsOn == 1) notSafe += bricks.first { b.isDirectlyOnTopOf(it) }.index
        }
        return bricks.size - notSafe.size
    }

    fun part2(): Any {
        val bricks = input.mapIndexed { index, s -> readBrick(index, s) }.toMutableList()
        fun fullFall(bricks: MutableList<Brick>) {
            val fallen = hashSetOf<Int>()
            fun fall(b: Brick) {
                if (!fallen.add(b.index)) return
                var maxZ = 0
                for (other in bricks) {
                    if (b.isOnTopOf(other)) {
                        fall(other)
                        maxZ = maxOf(maxZ, bricks[other.index].maxZ)
                    }
                }
                bricks[b.index] = bricks[b.index].fall(b.minZ - maxZ - 1)
            }
            for (b in bricks) fall(b)
        }

        val poolSize = Runtime.getRuntime().availableProcessors() - 1
        println("Using pool size = $poolSize")
        val pool = Executors.newFixedThreadPool(poolSize)
        fullFall(bricks)
        val ans = AtomicInteger(0)
        val totalDone = AtomicInteger(0)
        val start = System.currentTimeMillis()
        bricks.indices.forEach { toRemove ->
            pool.submit {
                val newBricks = bricks.toMutableList()
                newBricks.removeAt(toRemove)
                for (i in toRemove until newBricks.size) newBricks[i] = newBricks[i].copy(index = i)

                val before = newBricks.toList()

                fullFall(newBricks)

                val changed = newBricks.indices.count { before[it] != newBricks[it] }
                ans.addAndGet(changed)

                println("[${(System.currentTimeMillis() - start).milliseconds}] Done for ${totalDone.incrementAndGet()}/${bricks.size} ans=$ans")
            }
        }

        pool.shutdown()
        return ans
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 22)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        val solver = ::Day22
        println("Part 1 test - " + solver(testInput).part1())
        println("Part 1 real - " + solver(input).part1())

        println("---")

        println("Part 2 test - " + solver(testInput).part2())
        println("Part 2 real - " + solver(input).part2())
    }
}
