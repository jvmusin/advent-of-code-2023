fun main() {
    fun part1(input: List<String>): Any {
        return Unit

        var s = 0L
        for (str in input) {
            val f = str.first { it.isDigit() }.digitToInt()
            val f1 = str.last { it.isDigit() }.digitToInt()
            s += f * 10 + f1
        }

        return s
    }

    fun part2(input: List<String>): Any {
        val digs = arrayOf(
            "NEVER_USE hsfss",
            "one",
            "two",
            "three",
            "four",
            "five",
            "six",
            "seven",
            "eight",
            "nine",
        )
        var s = 0L
        for (str in input) {
            val indices = str.indices

            val firstAt = indices.first { str.substring(it)[0].isDigit() || (digs.any { d -> str.substring(it).startsWith(d) }) }
            val lastAt = indices.last { str.substring(it)[0].isDigit() || (digs.any { d -> str.substring(it).startsWith(d) }) }
            val first = str.substring(firstAt).let { w -> if (w[0].isDigit()) w[0].digitToInt() else digs.indexOfFirst { d -> w.startsWith(d) } }
            val last = str.substring(lastAt).let { w -> if (w[0].isDigit()) w[0].digitToInt() else digs.indexOfFirst { d -> w.startsWith(d) } }
            s += first * 10 + last
        }

        return s
    }

    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 1)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        println("Part 1 test - " + part1(testInput))
        println("Part 1 real - " + part1(input))

        println("---")

        println("Part 2 test - " + part2(testInput))
        println("Part 2 real - " + part2(input))
    }
}

