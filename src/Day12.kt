class Day12(val input: List<String>) {
    fun part1(): Any {
        fun solve(input: String): Long {
            val s = input.split(' ')[0].let { str -> List(5) { str }.joinToString("?") }
            val n = s.length
            val lens = input.split(' ')[1].split(',').map { it.toInt() }.let { arr -> List(5) { arr }.flatten() }
            val m = lens.size

            val dp = Array(n + 2) { LongArray(m + 1) { -1 } }

            val canEnd = BooleanArray(n)
            canEnd[n - 1] = s[n - 1] != '#'
            for (i in n - 2 downTo 0) canEnd[i] = canEnd[i + 1] && s[i] != '#'

            fun dfs(at: Int, b: Int): Long {
                fun calc(): Long {
                    if (b == m) {
                        return if (at >= n || canEnd[at]) return 1 else 0
                    }
                    if (at >= n) return 0

                    var ans = 0L
                    if (s[at] != '#') ans += dfs(at + 1, b) // skip this char

                    val len = lens[b]
                    if (at + len <= n) {
                        if ((0 until len).all { s[at + it] != '.' }) {
                            if (at + len == n || s[at + len] != '#') {
                                ans += dfs(at + len + 1, b + 1)
                            }
                        }
                    }

                    return ans
                }

                if (dp[at][b] == -1L) dp[at][b] = calc()
                return dp[at][b]
            }

            return dfs(0, 0)
        }

        var ans = 0L
        for (s in input) {
            ans += solve(s)
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
        val day = String.format("%02d", 12)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        val solver = ::Day12
        println("Part 1 test - " + solver(testInput).part1())
        println("Part 1 real - " + solver(input).part1())

        println("---")

        println("Part 2 test - " + solver(testInput).part2())
        println("Part 2 real - " + solver(input).part2())
    }
}
