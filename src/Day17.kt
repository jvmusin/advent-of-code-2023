class Day17(val input: List<String>) {
    var field = input.map { it.toCharArray() }
    val n = input.n
    val m = input.m

    val dx = intArrayOf(0, 1, 0, -1)
    val dy = intArrayOf(1, 0, -1, 0)

    fun inside(p: Pair<Int, Int>): Boolean {
        val (x, y) = p
        return x in 0 until n && y in 0 until m
    }

    data class State(val x: Int, val y: Int, val dir: Int, val stepsMade: Int)

    fun part1(): Any {
        val dp = hashMapOf<State, Int>()
        val q = ArrayDeque<State>()
        for (d0 in 0 until 4) {
            val startState = State(0, 0, d0, 0)
            dp[startState] = 0
            q.add(startState)
        }
        fun update(x: Int, y: Int, wasDir: Int, toDir: Int, stepsMade: Int) {
            val newStepsMade = if (wasDir == toDir) stepsMade + 1 else 1
            if (newStepsMade > 3) return
            val x1 = x + dx[toDir]
            val y1 = y + dy[toDir]
            if (!inside(x1 to y1)) return
            val d1 = dp[State(x, y, wasDir, stepsMade)]
            requireNotNull(d1)
            val d2 = d1 + input[x1][y1].digitToInt()
            val newState = State(x1, y1, toDir, newStepsMade)
            val wasD = dp[newState]
            if (wasD == null || wasD > d2) {
                dp[newState] = d2
                q.add(newState)
            }
        }
        while (q.isNotEmpty()) {
            val (x, y, dir, stepsMade) = q.removeFirst()
            update(x, y, dir, dir, stepsMade)
            update(x, y, dir, (dir + 1) % 4, stepsMade)
            update(x, y, dir, (dir + 3) % 4, stepsMade)
        }
        return dp.filterKeys { it.x == n - 1 && it.y == m - 1 }.minOf { it.value }
    }

    fun part2(): Any {
        val dp = hashMapOf<State, Int>()
        val q = ArrayDeque<State>()
        for (d0 in 0 until 4) {
            val startState = State(0, 0, d0, 0)
            dp[startState] = 0
            q.add(startState)
        }
        fun update(x: Int, y: Int, wasDir: Int, toDir: Int, stepsMade: Int) {
            if (wasDir != toDir && stepsMade < 4) return
            val newStepsMade = if (wasDir == toDir) stepsMade + 1 else 1
            if (newStepsMade > 10) return
            val x1 = x + dx[toDir]
            val y1 = y + dy[toDir]
            if (!inside(x1 to y1)) return
            val d1 = dp[State(x, y, wasDir, stepsMade)]
            requireNotNull(d1)
            val d2 = d1 + input[x1][y1].digitToInt()
            val newState = State(x1, y1, toDir, newStepsMade)
            val wasD = dp[newState]
            if (wasD == null || wasD > d2) {
                dp[newState] = d2
                q.add(newState)
            }
        }
        while (q.isNotEmpty()) {
            val (x, y, dir, stepsMade) = q.removeFirst()
            update(x, y, dir, dir, stepsMade)
            update(x, y, dir, (dir + 1) % 4, stepsMade)
            update(x, y, dir, (dir + 3) % 4, stepsMade)
        }
        return dp.filterKeys { it.x == n - 1 && it.y == m - 1 }.minOf { it.value }
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 17)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        val solver = ::Day17
        println("Part 1 test - " + solver(testInput).part1())
        println("Part 1 real - " + solver(input).part1())

        println("---")

        println("Part 2 test - " + solver(testInput).part2())
        println("Part 2 real - " + solver(input).part2())
    }
}
