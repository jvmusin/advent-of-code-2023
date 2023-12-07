private class Day07 {
    companion object {
        fun String.countEach() = groupingBy { it }.eachCount().values.sorted()

        enum class CardType(val check: (String) -> Boolean) {
            FiveOfAKind({ it.countEach().max() == 5 }),
            FourOfAKind({ it.countEach().max() == 4 }),
            FullHouse({ it.countEach() == listOf(2, 3) }),
            ThreeOfAKind({ it.countEach().max() == 3 }),
            TwoPair({ it.countEach() == listOf(1, 2, 2) }),
            OnePair({ it.countEach().max() == 2 }),
            HighCard({ it.countEach().max() == 1 });

            companion object {
                fun find(s: String) = possibleHands(s).minOf { opt -> entries.first { it.check(opt) } }
            }
        }

        fun possibleHands(s: String): MutableSet<String> {
            val res = mutableSetOf<String>()
            fun dfs(at: Int, str: String) {
                if (at == s.length) {
                    res += str
                    return
                }
                if (s[at] != 'J') return dfs(at + 1, str + s[at])
                for (c in strengths.dropLast(1)) {
                    dfs(at + 1, str + c)
                }
            }
            dfs(0, "")
            return res
        }

        private val strengths = "AKQT98765432J" // change to AKQJT98765432 for part 1

        data class Hand(val index: Int, val cards: String, val rank: Int) : Comparable<Hand> {
            val type = CardType.find(cards)
            val values = cards.map { strengths.indexOf(it) }
            override fun compareTo(other: Hand): Int {
                var c = type.compareTo(other.type)
                for (i in values.indices) {
                    if (c == 0) c = values[i].compareTo(other.values[i])
                }
                return c
            }
        }

        fun String.toHand(i: Int) = split(" ").let { (cards, rank) -> Hand(i, cards, rank.toInt()) }
    }

    fun part1(input: List<String>): Any {
        val hands = input.indices.map { input[it].toHand(it) }
        val sorted = hands.sorted()
        var ans = 0L
        for (i in sorted.indices) {
            val c = sorted[i]
            ans += (sorted.size - i) * c.rank
        }
        return ans
    }

    fun part2(input: List<String>): Any {
        return part1(input)
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 7)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        println("Part 1 test - " + Day07().part1(testInput))
        println("Part 1 real - " + Day07().part1(input))

        println("---")

        println("Part 2 test - " + Day07().part2(testInput))
        println("Part 2 real - " + Day07().part2(input))
    }
}
