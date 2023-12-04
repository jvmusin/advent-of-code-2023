fun main() {
    fun part1(input: List<String>): Any {

        fun Char.isSymbol() = !isDigit() && this != '.'

        fun anyGood(x: Int, y: Int): Boolean {
            val res = mutableListOf<Char>()
            for (dx in -1..1) {
                for (dy in -1..1) {
                    val x = x + dx
                    val y = y + dy
                    if (x in 0 until input.size && y in 0 until input[x].length) {
                        if (input[x][y].isSymbol()) return true
                    }
                }
            }
            return false
        }

        var ans = 0L
        for (i in input.indices) {
            var j = 0
            while (j < input[i].length) {
                if (input[i][j].isDigit()) {
                    var any = false
                    var n = 0
                    while (j < input[i].length && input[i][j].isDigit()) {
                        n = n * 10 + input[i][j].digitToInt()
                        any = any or anyGood(i, j)
                        j++
                    }
                    if (any) ans += n
                } else {
                    j++
                }
            }
        }
        return ans



        return Unit
    }

    fun part2(input: List<String>): Any {

        fun Char.isSymbol() = !isDigit() && this != '.'

        fun anyGood(x: Int, y: Int): Boolean {
            for (dx in -1..1) {
                for (dy in -1..1) {
                    val x = x + dx
                    val y = y + dy
                    if (x in 0 until input.size && y in 0 until input[x].length) {
                        if (input[x][y].isSymbol()) return true
                    }
                }
            }
            return false
        }

        fun adj(x: Int, y: Int): List<Pair<Int, Int>> {
            val ans = mutableListOf<Pair<Int, Int>>()
            for (dx in -1..1) {
                for (dy in -1..1) {
                    val x = x + dx
                    val y = y + dy
                    if (x == 0 && y == 0) continue
                    if (x in 0 until input.size && y in 0 until input[x].length) {
                        ans += x to y
                    }
                }
            }
            return ans
        }

        val index = Array(input.size ) { IntArray(input[it].length) { -1 } }
        val nums =  mutableListOf<Int>()

        var totalIndices = 0
        for (i in input.indices) {
            var j = 0
            while (j < input[i].length) {
                if (input[i][j].isDigit()) {
                    var any = false
                    var n = 0
                    val j0 = j
                    while (j < input[i].length && input[i][j].isDigit()) {
                        n = n * 10 + input[i][j].digitToInt()
                        any = any or anyGood(i, j)
                        j++
                    }
                    if (any) {
                        val idx = totalIndices++
                        nums += n
                        for (k in j0 until j) {
                            index[i][k] = idx
                        }
                    }
                } else {
                    j++
                }
            }
        }

        var ans = 0L
        for (i in input.indices) {
            for (j in input[i].indices) {
                if (input[i][j] == '*') {
                    val adj = adj(i, j)
                    val idcs = adj.mapNotNull { index[it.first][it.second].takeIf { it != -1 } }.toSet().toList()
                    if (idcs.size == 2) {
                        val a = nums[idcs[0]]
                        val b = nums[idcs[1]]
                        val c = a.toLong() * b
                        ans += c
                    }
                }

            }
        }

        return ans


        return Unit
    }

    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 3)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        println("Part 1 test - " + part1(testInput))
        println("Part 1 real - " + part1(input))

        println("---")

        println("Part 2 test - " + part2(testInput))
        println("Part 2 real - " + part2(input))
    }
}

