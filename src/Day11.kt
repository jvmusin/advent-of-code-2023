class Day11(val input: List<String>) {
    val n = input.size
    val m = input[0].length

    fun part1(): Any {
        val galaxies = (0 until n).flatMap { r ->
            (0 until m).filter { c ->
                input[r][c] == '#'
            }.map { c -> r to c }
        }
        val usedRows = galaxies.map { it.first }.toSet()
        val usedCols = galaxies.map { it.second }.toSet()
        val rowsPs = (0..n).scan(0) { acc, r -> if (r !in usedRows) acc + 1 else acc }
        val colsPs = (0..m).scan(0) { acc, c -> if (c !in usedCols) acc + 1 else acc }
        var sum = 0L
        for (i in galaxies.indices) {
            val (r1, c1) = galaxies[i]
            for (j in i + 1 until galaxies.size) {
                val (r2, c2) = galaxies[j]
                val minR = minOf(r1, r2)
                val maxR = maxOf(r1, r2)
                val minC = minOf(c1, c2)
                val maxC = maxOf(c1, c2)
                var score = 0L
                score += maxR - minR
                score += maxC - minC
                val k = 1_000_000L
                score += (rowsPs[maxR + 1] - rowsPs[minR]) * (k - 1)
                score += (colsPs[maxC + 1] - colsPs[minC]) * (k - 1)
                sum += score
            }
        }
        return sum
    }

    fun part2(): Any {
        return part1()
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 11)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        val solver = ::Day11
        println("Part 1 test - " + solver(testInput).part1())
        println("Part 1 real - " + solver(input).part1())

        println("---")

        println("Part 2 test - " + solver(testInput).part2())
        println("Part 2 real - " + solver(input).part2())
    }
}
