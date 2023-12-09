private class Day09 {
    private companion object {

    }

    fun <T> List<T>.fromEnd(at: Int) = get(size - at - 1)

    fun find0(a: List<Long>): Long {
        val a = mutableListOf(a.toMutableList())
        while (a.last().any { it != 0L }) {
            val prev = a.last()
            val b = List(prev.size - 1) { prev[it + 1] - prev[it] }
            a += b.toMutableList()
        }
        a.last() += 0
        for (i in a.size - 2 downTo  0) {
            a[i] += a[i].fromEnd(0) + a[i + 1].fromEnd(0)
        }
        return a.first().last()
    }

    fun find1(a: List<Long>): Long {
        val a = mutableListOf(a.toMutableList())
        while (a.last().any { it != 0L }) {
            val prev = a.last()
            val b = List(prev.size - 1) { prev[it + 1] - prev[it] }
            a += b.toMutableList()
        }
        a.last() += 0
        for (i in a.size - 2 downTo 0) {
            a[i].add(0, a[i].get(0) - a[i + 1].get(0))
        }
        return a.first().first()
    }

    fun part1(input: List<String>): Any {
        return input.sumOf { it.split(' ').map { it.toLong() }.let { find0(it) } }
    }

    fun part2(input: List<String>): Any {
        return input.sumOf { it.split(' ').map { it.toLong() }.let { find1(it) } }
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val solver = ::Day09
        val day = String.format("%02d", 9)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        println("Part 1 test - " + solver().part1(testInput))
        println("Part 1 real - " + solver().part1(input))

        println("---")

        println("Part 2 test - " + solver().part2(testInput))
        println("Part 2 real - " + solver().part2(input))
    }
}
