import java.util.regex.Pattern

fun main() {
    data class Card(val id: Int, val have: List<Int>, val winning: List<Int>) {
        val matches = have.count { it in winning }
    }

    val pattern = Pattern.compile("^Card +(?<index>\\d+): (?<winning>.*) \\| +(?<have>.*)$")
    fun String.toCard(): Card {
        val matcher = pattern.matcher(this)
        require(matcher.find())
        val index = matcher.group("index").toInt()
        fun String.numbers() = trim().split(" +".toRegex()).map { it.toInt() }
        val winning = matcher.group("winning").numbers()
        val have = matcher.group("have").numbers()
        return Card(index, have, winning)
    }

    fun part1(input: List<String>): Any {
        val cards = input.map { it.toCard() }
        return cards.sumOf {
            var r = 1L
            repeat(it.matches) { r *= 2 }
            r / 2
        }
    }

    fun part2(input: List<String>): Any {
        val matches = input.map { it.toCard() }.map { it.matches }

        var total = 0L
        val copies = LongArray(matches.size) { 1 }
        for (i in matches.indices) {
            val thisCopies = copies[i]
            total += thisCopies
            repeat(matches[i]) {
                val at = i + it + 1
                if (at < matches.size) {
                    copies[at] += thisCopies
                }
            }
        }

        return total
    }

    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 4)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        println("Part 1 test - " + part1(testInput))
        println("Part 1 real - " + part1(input))

        println("---")

        println("Part 2 test - " + part2(testInput))
        println("Part 2 real - " + part2(input))
    }
}

