class Day25(val input: List<String>) {
    fun readVertex(s: String): Pair<String, List<String>> {
        val (a, b) = s.split(": ")
        val c = b.split(" ")
        return a to c
    }

    fun part1(): Any {
        val vertices = input.associate(::readVertex)
        val allEdges = vertices.flatMap { (a, b) -> b.map { a to it } }
        val biGraph = HashMap<String, HashSet<String>>()
        for ((a, b) in allEdges) {
            biGraph.getOrPut(a) { hashSetOf() }.add(b)
            biGraph.getOrPut(b) { hashSetOf() }.add(a)
        }
        // See graphviz input in Day25Graph.dot output in Day25Graph.(dot|svg)
        val removed = listOf(
            "grd" to "tqr",
            "dlv" to "tqh",
            "bmd" to "ngp",
        )
        for (edge in removed) {
            val adj = requireNotNull(biGraph[edge.first])
            require(edge.second in adj)
        }
        val used = hashSetOf<String>()
        val sizes = mutableListOf<Int>()
        fun dfs(v: String) {
            if (!used.add(v)) return
            sizes[sizes.lastIndex] = sizes[sizes.lastIndex] + 1
            for (e in biGraph[v]!!) {
                if (v to e !in removed && e to v !in removed) {
                    dfs(e)
                }
            }
        }
        sizes += 0
        dfs(removed[0].first)
        require(used.size != biGraph.size)
        sizes += 0
        dfs(removed[0].second)
        require(used.size == biGraph.size)
        return sizes[0].toLong() * sizes[1]
    }

    fun part2(): Any {
        return part1()
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 25)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        val solver = ::Day25
//        println("Part 1 test - " + solver(testInput).part1())
        println("Part 1 real - " + solver(input).part1())

        println("---")

        println("Part 2 test - " + solver(testInput).part2())
        println("Part 2 real - " + solver(input).part2())
    }
}
