class Day13(val input: List<String>) {
    fun getPuzzle(start: Int): MutableList<String> {
        val res = mutableListOf<String>()
        var at = start
        while (at < input.size && input[at].isNotEmpty()) {
            res += input[at++]
        }
        return res
    }

    fun solve(puzzle: MutableList<String>): Long {
        val n = puzzle.size
        val m = puzzle[0].length
        fun reflectVerLine(offset: Int): Boolean {
            for (i in 0 until n) {
                for (j in 0 until m) {
                    val j1 = if (j < offset) offset + (offset - j - 1) else offset - (j - offset + 1)
                    if (j1 in 0 until m && puzzle[i][j] != puzzle[i][j1]) {
                        return false
                    }
                }
            }
            return true
        }

        fun reflectHorLine(offset: Int): Boolean {
            for (j in 0 until m) {
                for (i in 0 until n) {
                    val i1 = if (j < offset) offset + (offset - i - 1) else offset - (i - offset + 1)
                    if (i1 in 0 until n && puzzle[i][j] != puzzle[i1][j]) return false
                }
            }
            return true
        }

        fun findReflections(): List<Int> {
            val res = mutableListOf<Int>()
            for (i in 1 until m) {
                if (reflectVerLine(i)) {
                    res += i
                }
            }
            for (i in 1 until n) {
                if (reflectHorLine(i)) {
                    res += -i
                }
            }
            return res
        }
        var ref1 = findReflections()
        outer@for (i in 0 until n) {
            for (j in 0 until m) {
                val was = puzzle[i][j]
                val now = if (was == '.') '#' else '.'
                puzzle[i] = puzzle[i].replaceRange(j..j, now + "")
                val ref = findReflections()
                puzzle[i] = puzzle[i].replaceRange(j..j, was + "")
                if (ref.any { it !in ref1 }) {
                    ref1 = ref.first { it !in ref1 }.let(::listOf)
                    break@outer
                }
            }
        }
        return ref1.sumOf { if (it < 0) -it * 100L else it.toLong() }
    }

    fun part1(): Any {
        val ans = input.joinToString("\n").split("\n\n").map { it.split("\n") }.sumOf {
            val res = solve(it.toMutableList())
            res
        }
        return ans
    }

    fun part2(): Any {
        return part1()
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 13)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        val solver = ::Day13
        println("Part 1 test - " + solver(testInput).part1())
        println("Part 1 real - " + solver(input).part1())

        println("---")

        println("Part 2 test - " + solver(testInput).part2())
        println("Part 2 real - " + solver(input).part2())
    }
}
