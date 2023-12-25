import kotlin.math.abs

class Day24(val input: List<String>) {
    companion object {
        fun intersection(start1: Point2D, v1: Point2D, start2: Point2D, v2: Point2D): Point2D? {
            val v1xv2 = v1.x * v2.y - v1.y * v2.x
            if (v1xv2 == 0.0) return null
            val t = ((start2.x - start1.x) * v2.y - (start2.y - start1.y) * v2.x) / v1xv2
            return start1 + v1 * t
        }

        data class Point2D(val x: Double, val y: Double) {
            operator fun plus(other: Point2D) = Point2D(x + other.x, y + other.y)
            operator fun times(other: Double) = Point2D(x * other, y * other)
        }

        data class Point3D(val x: Double, val y: Double, val z: Double) {
            fun to2() = Point2D(x, y)
        }

        data class Hail(val position: Point3D, val direction: Point3D) {
            fun intersection2d(other: Hail): Point2D? {
                return intersection(position.to2(), direction.to2(), other.position.to2(), other.direction.to2())
            }
        }

        const val epsilon = 0.000001
        fun inside(min: Double, max: Double, value: Double): Boolean {
            return min - epsilon <= value && value <= max + epsilon
        }
    }

    fun readPoint(s: String): Point3D {
        val (x, y, z) = s.split(", ")
        return Point3D(x.toDouble(), y.toDouble(), z.toDouble())
    }

    fun readHail(s: String): Hail {
        val (left, right) = s.split(" @ ")
        return Hail(readPoint(left), readPoint(right))
    }

    fun part1(): Any {
        val boundaries = Point2D(7.0, 27.0)
        val hails = input.map { readHail(it) }
        var count = 0
        for (i in hails.indices) {
            val hi = hails[i]
            for (j in i + 1 until hails.size) {
                val hj = hails[j]
                val intersection = hi.intersection2d(hj)
                if (intersection != null) {
                    val timeA = (intersection.x - hi.position.x) / hi.direction.x
                    val timeB = (intersection.x - hj.position.x) / hj.direction.x
                    if (timeA + epsilon > 0 && timeB + epsilon > 0) {
                        val xInside = inside(boundaries.x, boundaries.y, intersection.x)
                        val yInside = inside(boundaries.x, boundaries.y, intersection.y)
                        if (xInside && yInside) {
                            count++
                        }
                    }
                }
            }
        }
        return count
    }

    fun increasing(n: Int) = sequence {
        yield(0)
        var i = 0
        while (++i <= n) {
            yield(i)
            yield(-i)
        }
    }

    fun part2(): Any {
        val a = input.map(::readHail)
        for (vx in increasing(Int.MAX_VALUE)) {
            for (vy in increasing(abs(vx))) {
                for (vz in increasing(abs(vx))) {

                }
            }
        }

        return part1()
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 24)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        val solver = ::Day24
        println("Part 1 test - " + solver(testInput).part1())
        println("Part 1 real - " + solver(input).part1())

        println("---")

        println("Part 2 test - " + solver(testInput).part2())
        println("Part 2 real - " + solver(input).part2())
    }
}
