class Day14(val input: List<String>) {
    var field = input.map { it.toCharArray() }
    fun rollUp() {
        val n = field.size
        val m = field[0].size
        for (j in 0 until m) {
            var topBorder = -1
            for (i in 0 until n) {
                if (field[i][j] == '#') topBorder = i
                if (field[i][j] == 'O') {
                    field[i][j] = '.'
                    field[++topBorder][j] = 'O'
                }
            }
        }
    }

    fun rotateRight() {
        val n0 = field.size
        val m0 = field[0].size
        val field1 = List(m0) { j0 ->
            CharArray(n0) { i0 ->
                field[n0 - i0 - 1][j0]
            }
        }
        field = field1
    }

    fun state() = field.joinToString("\n") { it.concatToString() }

    fun rotateCycle(): Boolean {
        val state = state()
        repeat(4) {
            rollUp()
            rotateRight()
        }
        val state1 = state()
        return state != state1
    }

    fun load(): Int {
        val n = field.size
        val m = field[0].size
        var sum = 0
        for (i in 0 until n) {
            for (j in 0 until m) {
                if (field[i][j] == 'O') {
                    sum += n - i
                }
            }
        }
        return sum
    }

    fun part1(): Any {
        val field = input.map { it.toCharArray() }
        val n = field.size
        val m = field[0].size
        var total = 0
        for (j in 0 until m) {
            var topBorder = -1
            for (i in 0 until n) {
                if (field[i][j] == '#') topBorder = i
                if (field[i][j] == 'O') {
                    field[i][j] = '.'
                    field[++topBorder][j] = 'O'
                    total += n - topBorder
                }
            }
        }
        return total
    }

    fun part2(): Any {
        var iterLeft = 1000000000
        val path = mutableListOf<String>()
        var cycleStart = -1
        while (cycleStart == -1) {
            iterLeft--
            rotateCycle()
            val state = state()
            if (state in path) cycleStart = path.indexOf(state)
            else path += state
        }
        val cycleLen = path.size - cycleStart
        iterLeft %= cycleLen
        repeat(iterLeft) {
            rotateCycle()
        }
//        while (rotateCycle()) {
//            println("GO!")
//        }
        return load()
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 14)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        val solver = ::Day14
        println("Part 1 test - " + solver(testInput).part1())
        println("Part 1 real - " + solver(input).part1())

        println("---")

        println("Part 2 test - " + solver(testInput).part2())
        println("Part 2 real - " + solver(input).part2())
    }
}
