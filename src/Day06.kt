import java.util.regex.Pattern

fun main() {
    data class Line(val text: String) {

    }

    val pattern = Pattern.compile("^(?<line>.*)$")
    fun String.parseLine(): Line {
        val matcher = pattern.matcher(this)
        require(matcher.find()) { "Failed to parse line \"$this\"" }
        val text = matcher.group("line")
        return Line(text)
    }

    fun List<String>.parseLines() = map { it.parseLine() }

    fun readSeeds(input: List<String>): List<Long> {
        return input[0].split(": +".toRegex())[1].split(" +".toRegex()).map { it.toLong() }
    }

    fun solve(time: Long, record: Long): Long {
        var l = 0L
        var r = time
        while (r - l > 10) {
            val m1 = l + (r - l) / 3
            val m2 = r - (r - l) / 3
            val d1 = (time - m1) * m1
            val d2 = (time - m2) * m2
            if (d1 < d2) r = m2
            else l = m1
        }
        var mx = l
        fun f(x: Long) = (time - x) * x
        while (f(mx + 1) >= f(mx)) mx++
        while (f(mx - 1) >= f(mx)) mx--

        fun bins(mul: Int): Long {
            fun f(x: Long): Long {
                val wait = mx + x * mul
                return (time - wait) * wait
            }
            var l = 0L
            var r = 1L
            while (f(r) > record) r *= 2
            while (r - l > 1) {
                val m = (l + r) / 2
                if (f(m) > record) l = m
                else r = m
            }
            return r
        }

        val b1 = bins(-1)
        val b2 = bins(+1)
        val b = b1 + b2 - 1
        return b
    }

    fun part1(input: List<String>): Any {

        val times = input[0].split(":")[1].split(" ").filter { it.isNotEmpty() }.map { it.trim().toInt() }
        val distances = input[1].split(":")[1].split(" ").filter { it.isNotEmpty() }.map { it.trim().toInt() }

        var ans = 1L
        for (i in 0 until times.size) {
            ans *= solve(times[i].toLong(), distances[i].toLong())
        }

        return ans

        return Unit
    }


    fun part2(input: List<String>): Any {
        val times = input[0].filter { it.isDigit() }.fold(0L) { acc, c -> acc * 10 + c.digitToInt() }
        val distances = input[1].filter { it.isDigit() }.fold(0L) { acc, c -> acc * 10 + c.digitToInt() }

        var ans = solve(times, distances)

        return ans
    }

    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 6)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
//        println("Part 1 test - " + part1(testInput))
//        println("Part 1 real - " + part1(input))

        println("---")

        println("Part 2 test - " + part2(testInput))
        println("Part 2 real - " + part2(input))
    }
}

