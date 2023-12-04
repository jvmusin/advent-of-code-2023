fun main() {
    fun part1(input: List<String>): Any {
        var ans = 0L
        for (s in input) {
            val (p1, p2) = s.split(": ")
            val index = p1.split(" ")[1].toInt()
            val sets = p2.split("; ")
            var ok = true
            for (set in sets) {
                for (part in set.split(", ")) {
                    val sum = mutableMapOf<String, Int>()
                    val p = part.split(' ')
                    sum.merge(p[1], p[0].toInt(), Int::plus)
                    ok = ok and ((sum["red"] ?: 0) <= 12)
                    ok = ok and ((sum["green"] ?: 0) <= 13)
                    ok = ok and ((sum["blue"] ?: 0) <= 14)
                }
            }
            if (ok) ans += index
        }

        return ans

        return Unit
    }

    fun part2(input: List<String>): Any {

        var ans = 0L
        for (s in input) {
            val (p1, p2) = s.split(": ")
            val index = p1.split(" ")[1].toInt()
            val sets = p2.split("; ")
            var ok = true
            val mins = mutableMapOf<String, Int>()
            for (set in sets) {
                val sum = mutableMapOf<String, Int>()
                for (part in set.split(", ")) {
                    val p = part.split(' ')
                    sum.merge(p[1], p[0].toInt(), Int::plus)
                    ok = ok and ((sum["red"] ?: 0) <= 12)
                    ok = ok and ((sum["green"] ?: 0) <= 13)
                    ok = ok and ((sum["blue"] ?: 0) <= 14)
                }
                for (color in listOf("red", "green", "blue")) {
                    mins.merge(color, sum[color] ?: 0, ::maxOf)
                }
            }
            ans += mins["red"]!! * mins["green"]!! * mins["blue"]!!
        }

        return ans

        return Unit
    }

    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 2)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        println("Part 1 test - " + part1(testInput))
        println("Part 1 real - " + part1(input))

        println("---")

        println("Part 2 test - " + part2(testInput))
        println("Part 2 real - " + part2(input))
    }
}

