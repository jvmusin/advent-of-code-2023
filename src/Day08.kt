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

        val maxSteps = graph.size * 2 + 10

        //(v, startAtSteps, stepsToTake) -> v2
        val shortcuts = graph.keys.associateWith { v ->
            steps.indices.associateWith { startAtSteps ->
                mutableListOf(v)
//                var curV = v
//                (0 until maxSteps).map { len ->
//                    curV.also {
//                        val atSteps = (startAtSteps + len) % steps.length
//                        curV = if (steps[atSteps] == 'L') graph[curV]!!.first else graph[curV]!!.second
//                    }
//                }
            }
        }

        fun getShortcut(v0: String, startAtSteps: Int, stepsToTake: Int): String {
            val curShortcuts = shortcuts[v0]!![startAtSteps]!!
            while (stepsToTake >= curShortcuts.size) {
                val lastV = curShortcuts.last()
                val at = (startAtSteps + curShortcuts.size - 1) % steps.length
                val nextV = if (steps[at] == 'L') graph[lastV]!!.first else graph[lastV]!!.second
                curShortcuts += nextV
            }
            return curShortcuts[stepsToTake]
        }

        println("Shortcuts done")

        // This can be hugely optimized by going lazy like getShortcut
        val stepsToZ = graph.keys.associateWith { v ->
            steps.indices.associateWith { startAtSteps ->
                var stepsToTake = 1
                val visited = hashSetOf<Pair<String, Int>>()
                while (true) {
                    val next = getShortcut(v, startAtSteps, stepsToTake)
                    if (!visited.add(next to (startAtSteps + stepsToTake) % steps.length)) return@associateWith Int.MAX_VALUE
                    if (next.endsWith('Z')) return@associateWith stepsToTake
                    stepsToTake++
                }
                Int.MAX_VALUE
            }
        }

        println("steps to Z done")

        var atSteps = 0
        var atV = graph.keys.filter { it.endsWith("A") }
        var ans = 0L
        while (atV.any { !it.endsWith('Z') }) {
            val maxStepsNeeded = atV.maxOf { stepsToZ[it]!![atSteps]!! }
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
