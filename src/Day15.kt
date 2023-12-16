class Day15(val input: List<String>) {
    var field = input.map { it.toCharArray() }
    val n = input.n
    val m = input.m

    fun part1(): Any {
        var ans = 0L
        for (s in input.single().split(",")) {
            var cur = 0L
            for (c in s) {
                cur += c.code
                cur = cur * 17
                cur %= 256
            }
            ans += cur
        }
        return ans
    }

    fun hash(s: String): Int {
        var cur = 0
        for (c in s) {
            cur += c.code
            cur = cur * 17
            cur %= 256
        }
        return cur
    }

    fun part2(): Any {
        val nameToQueue = Array(256) { ArrayDeque<Pair<String, Int>>() }
        for (s in input.single().split(",")) {
            val opAt = s.indexOfFirst { it == '-' || it == '=' }
            val name = s.substring(0 until opAt)
            val box = hash(name)
            val q = nameToQueue[box]
            val old = q.indexOfFirst { it.first == name }
            if (old != -1) {
                if (s[opAt] == '-') q.removeAt(old)
                else q[old] = name to s[s.length - 1].digitToInt()
            } else {
                if (s[opAt] == '=') q.addLast(name to s[s.length - 1].digitToInt())
            }
        }
        var ans = 0L
        for (b in nameToQueue.indices) {
            for (i in nameToQueue[b].indices) {
                ans += (1 + b) * (1 + i) * (nameToQueue[b][i].second)
            }
        }
        return ans
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 15)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        val solver = ::Day15
        println("Part 1 test - " + solver(testInput).part1())
        println("Part 1 real - " + solver(input).part1())

        println("---")

        println("Part 2 test - " + solver(testInput).part2())
        println("Part 2 real - " + solver(input).part2())
    }
}
