private class Day08 {
    private companion object {

    }

    fun part1(input: List<String>): Any {
        val steps = input[0]
        val edges = input.drop(2).associate {
            val parts = it.split(" = ")
            val from = parts[0]
            val (left, right) = parts[1].drop(1).dropLast(1).split(", ")
            from to (left to right)
        }
        var atSteps = 0
        var atV = "AAA"
        var ans = 0
        while (atV != "ZZZ") {
            ans++
            val atStep = atSteps++ % steps.length
            atV = edges[atV]!!.let { if (steps[atStep] == 'L') it.first else it.second }
        }
        return ans
    }

    fun part2(input: List<String>): Any {
        val steps = input[0]
        val graph = input.drop(2).associate {
            val parts = it.split(" = ")
            val from = parts[0]
            val (left, right) = parts[1].drop(1).dropLast(1).split(", ")
            from to (left to right)
        }

        //(v, startAtSteps, stepsToTake) -> v2
        val shortcuts = graph.keys.associateWith { v ->
            Array(steps.length) { hashMapOf(0 to v) }
        }

        fun getShortcut(v0: String, startAtSteps: Int, stepsToTake: Int): String {
            return shortcuts[v0]!![startAtSteps].getOrPut(stepsToTake) {
                val next = if (steps[startAtSteps] == 'L') graph[v0]!!.first else graph[v0]!!.second
                getShortcut(next, (startAtSteps + 1) % steps.length, stepsToTake - 1)
            }
        }

        val stepsToZ = graph.keys.associateWith { v ->
            hashMapOf<Int, Int>()
        }

        fun getStepsToZ(v0: String, startAtSteps: Int): Int {
            return stepsToZ[v0]!!.getOrPut(startAtSteps) {
                val v1 = getShortcut(v0, startAtSteps, 1)
                val tail = if (v1.endsWith('Z')) 0 else getStepsToZ(v1, (startAtSteps + 1) % steps.length)
                1 + tail
            }
        }

        var atSteps = 0
        var atV = graph.keys.filter { it.endsWith("A") }
        var ans = 0L
        while (atV.any { !it.endsWith('Z') }) {
            val maxStepsNeeded = atV.maxOf { getStepsToZ(it, atSteps) }
            require(maxStepsNeeded != Int.MAX_VALUE)
            atV = atV.map { getShortcut(it, atSteps, maxStepsNeeded) }
            atSteps += maxStepsNeeded
            atSteps %= steps.length
            ans += maxStepsNeeded
        }
        return ans
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val solver = ::Day08
        val day = String.format("%02d", 8)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
//        println("Part 1 test - " + solver().part1(testInput))
//        println("Part 1 real - " + solver().part1(input))

        println("---")

        println("Part 2 test - " + solver().part2(testInput))
        println("Part 2 real - " + solver().part2(input))
    }
}
