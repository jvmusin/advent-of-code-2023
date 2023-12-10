private class Day10(val field: List<String>) {
    val n = field.size
    val m = field[0].length

    private val connections = mapOf(
        '|' to booleanArrayOf(false, true, false, true),
        '-' to booleanArrayOf(true, false, true, false),
        'L' to booleanArrayOf(true, false, false, true),
        'J' to booleanArrayOf(false, false, true, true),
        '7' to booleanArrayOf(false, true, true, false),
        'F' to booleanArrayOf(true, true, false, false),
        '.' to booleanArrayOf(false, false, false, false),
        'S' to booleanArrayOf(true, true, true, true),
    )
    val dc = intArrayOf(1, 0, -1, 0)
    val dr = intArrayOf(0, 1, 0, -1)

    fun getField(p: Pair<Int, Int>) = field[p.first][p.second]
    fun inField(p: Pair<Int, Int>): Boolean {
        return p.first in 0 until n && p.second in 0 until m
    }

    fun adj1(v: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
        val b = connections[getField(v)]!!
        val res = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until 4) {
            if (b[i]) {
                val next = (v.first + dr[i]) to (v.second + dc[i])
                if (inField(next)) res += next
            }
        }
        return res
    }

    fun adj(v: Pair<Int, Int>): List<Pair<Int, Int>> {
        return adj1(v).filter { to -> v in adj1(to) }
    }

    fun findLoop(): MutableList<Pair<Int, Int>> {
        val s = field.findPos('S')
        val path = mutableListOf(s)
        fun dfs(): Boolean {
            val cur = path.last()
            val prev = if (path.size == 1) (-1 to -1) else path[path.size - 2]
            val adj = adj(cur)
            for (v in adj) {
                if (v != prev) {
                    if (v == s) return true
                    if (v in path) continue
                    path += v
                    if (dfs()) return true
                    path.removeLast()
                }
            }
            return false
        }
        dfs()
        return path
    }

    fun part1(): Any {
        val loop = findLoop()
        // s1 to 0
        // s2 to 1
        // s3 to 1/2
        // s4 to 2
        val ansIndex = loop.size / 2
        return ansIndex
    }

    fun getInsideCount(used: Collection<Pair<Int, Int>>): Int {
        fun findSide(v0: Pair<Int, Int>, delta: Pair<Int, Int>, as2: Char, turnToType: Map<Char, Int>): Boolean {
            var r = v0.first
            var c = v0.second
            var score = 0
            var lastType = -1
            while (inField(r to c)) {
                if ((r to c) in used) {
                    val value = field[r][c]
                    if (value == as2) score++
                    if (value in turnToType) {
                        if (lastType == -1) lastType = turnToType[value]!!
                        else {
                            if (lastType == turnToType[value]) score++
                            lastType = -1
                        }
                    }
                }
                r += delta.first
                c += delta.second
            }
            return score % 2 == 1
        }

        var ans = 0
        val type = mapOf(
            '7' to 0, 'L' to 0,
            'F' to 1, 'J' to 1,
        )
        for (i in 0 until n) {
            for (j in 0 until m) {
                if ((i to j) in used) continue
                val in1 = findSide(i to j, -1 to 0, '-', type)
                val in2 = findSide(i to j, 0 to -1, '|', type)
                if (in1 && in2) ans++
            }
        }
        return ans
    }

    fun part2(): Any {
        val loop = findLoop()
        return getInsideCount(loop)
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 10)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        val solver = ::Day10
        println("Part 1 test - " + solver(testInput).part1())
        println("Part 1 real - " + solver(input).part1())

        println("---")

        println("Part 2 test - " + solver(testInput).part2())
        println("Part 2 real - " + solver(input).part2())
    }
}
