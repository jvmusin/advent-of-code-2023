import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.abs

class Day18(val input: List<String>) {
    var field = input.map { it.toCharArray() }
    val n = input.n
    val m = input.m

    val dr = intArrayOf(0, 1, 0, -1)
    val dc = intArrayOf(1, 0, -1, 0)
    val dirs = "RDLU"

    fun part1(): Any {
        var curR = 0
        var curC = 0
        val path = TreeSet<Pair<Int, Int>>(compareBy({ it.first }, { it.second }))
        path.add(curR to curC)
        for (s in input) {
            val dir = s[0]
            val len = s.split(' ')[1].toInt()
            val d = dirs.indexOf(dir)
            repeat(len * 2) {
                curR += dr[d]
                curC += dc[d]
                path.add(curR to curC)
            }
        }
        val minR = path.minOf { it.first } - 1
        val minC = path.minOf { it.second } - 1
        val maxR = path.maxOf { it.first } + 1
        val maxC = path.maxOf { it.second } + 1
        fun insideBorders(r: Int, c: Int): Boolean {
            return r in minR..maxR && c in minC..maxC
        }

        val q = ArrayDeque<Pair<Int, Int>>()
        val allOutside = HashSet<Pair<Int, Int>>()
        fun add(p: Pair<Int, Int>) {
            if (insideBorders(p.first, p.second) && allOutside.add(p)) q.add(p)
        }
        add(minR to minC)
        while (!q.isEmpty()) {
            val (r, c) = q.removeFirst()
            for (d in 0 until 4) {
                val r1 = r + dr[d]
                val c1 = c + dc[d]
                if ((r1 to c1) !in path) {
                    add(r1 to c1)
                }
            }
        }
        var totalInside = 0
        for (r in minR + 1 until maxR step 2) {
            for (c in minC + 1 until maxC step 2) {
                if (r to c in path || r to c !in allOutside) {
                    totalInside++
                }
            }
        }
        return totalInside
    }

    data class Command(val dir: Int, val len: Int)

    data class Segment(val minR: Int, val minC: Int, val maxR: Int, val maxC: Int) {
        val length get() = (maxR - minR).toLong() * (maxC - minC)

        companion object {
            fun from(p1: Pair<Int, Int>, p2: Pair<Int, Int>): Segment {
                val minRow = minOf(p1.first, p2.first)
                val minCol = minOf(p1.second, p2.second)
                val maxRow = maxOf(p1.first, p2.first)
                val maxCol = maxOf(p1.second, p2.second)
                return Segment(minRow, minCol, maxRow, maxCol)
            }
        }
    }

    fun part2(): Any {
        val commands = input.map {
            val hex = it.split(' ')[2].drop(2).dropLast(1)
            val dir = hex.takeLast(1).toInt(16)
            val len = hex.dropLast(1).toInt(16)
            require(dir < 4)
            Command(dir, len)
        }
        for (i in commands.indices) {
            val cur = commands[i]
            val next = commands[(i + 1) % commands.size]
            require(cur.dir % 2 != next.dir % 2) // no back-and-forth
            require(cur.len > 0)
        }

        fun getSegments(): List<Segment> {
            var curR = 0
            var curC = 0
            val segments = mutableListOf<Segment>()
            for (c in commands) {
                val nextR = curR + dr[c.dir] * c.len * 2
                val nextC = curC + dc[c.dir] * c.len * 2
                segments += Segment.from(curR to curC, nextR to nextC)
                curR = nextR
                curC = nextC
            }
            require(curR == 0)
            require(curC == 0)
            return segments
        }

        val segments = getSegments()

        fun allAround(a: List<Int>) = a.flatMap { x -> List(3) { x - 1 + it } }.distinct().sorted()
        val allRows = segments.flatMap { listOf(it.minR, it.maxR) }.let(::allAround)
        val allColumns = segments.flatMap { listOf(it.minC, it.maxC) }.let(::allAround)

        val compressedSegments = segments.map { s ->
            fun compress(x: Int, all: List<Int>) = all.indexOf(x).also { require(it != -1) }
            Segment(compress(s.minR, allRows), compress(s.minC, allColumns), compress(s.maxR, allRows), compress(s.maxC, allColumns))
        }

        val path = hashSetOf<Pair<Int, Int>>()
        for (seg in compressedSegments) {
            for (r in seg.minR..seg.maxR) {
                for (c in seg.minC..seg.maxC) {
                    path += r to c
                }
            }
        }
        val minR = path.minOf { it.first } - 1
        val minC = path.minOf { it.second } - 1
        val maxR = path.maxOf { it.first } + 1
        val maxC = path.maxOf { it.second } + 1
        fun insideBorders(p: Pair<Int, Int>): Boolean {
            val (r, c) = p
            return r in minR..maxR && c in minC..maxC
        }

        val q = ArrayDeque<Pair<Int, Int>>()
        val allOutside = HashSet<Pair<Int, Int>>()
        fun add(p: Pair<Int, Int>) {
            if (insideBorders(p) && allOutside.add(p)) q.add(p)
        }
        add(minR to minC)
        while (!q.isEmpty()) {
            val (r, c) = q.removeFirst()
            for (d in 0 until 4) {
                val r1 = r + dr[d]
                val c1 = c + dc[d]
                val p = r1 to c1
                if (p !in path) {
                    add(p)
                }
            }
        }

        // 952408144115  // ans
        // 1049113187421 // my
        fun size(r: Int, c: Int): Long {
            fun evens(from: Int, to: Int): Long {
                val offset = 2L * maxOf(-from, 0) + 6
                val l = offset + from
                val r = offset + to
                require(l >= 0)
                require(r >= 0)
                return r / 2 - (l - 1) / 2
            }

            fun len(x: Int, all: List<Int>) = evens(all[x], all[x + 1] - 1)
            val w = len(c, allColumns)
            val h = len(r, allRows)
            return w * h
        }

        var totalInside = 0L
        for (r in minR..maxR) {
            for (c in minC..maxC) {
                if (r to c in path || r to c !in allOutside) {
                    totalInside += size(r, c)
                    print('#')
                } else {
                    print(' ')
                }
            }
            println()
        }

        return totalInside
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 18)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        val solver = ::Day18
        println("Part 1 test - " + solver(testInput).part1())
        println("Part 1 real - " + solver(input).part1())

        println("---")

        println("Part 2 test - " + solver(testInput).part2())
        println("Part 2 real - " + solver(input).part2())
    }
}
