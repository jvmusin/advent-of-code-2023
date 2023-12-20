class Day20(val input: List<String>) {
    var field = input.map { it.toCharArray() }
    val n = input.n
    val m = input.m

    enum class Type(val repr: Char) {
        FlipFlop('%'),
        Conjunction('&'),
        Broadcaster(' '),
        None(' '),
    }

    data class Node(val name: String, val type: Type, val edges: List<String>) {
        var isLow: Boolean = true
        val connectedNodes: MutableList<Node> = mutableListOf()
    }

    fun parseNode(s: String): Node {
        val (first, nodes) = s.split(" -> ")
        val type = when {
            first.startsWith(Type.FlipFlop.repr) -> Type.FlipFlop
            first.startsWith(Type.Conjunction.repr) -> Type.Conjunction
            first == "broadcaster" -> Type.Broadcaster
            else -> error("unknown type")
        }
        val name = first.trimStart(type.repr)
        val adj = nodes.split(", ")
        return Node(name, type, adj)
    }

    fun part1(): Any {
        val nodes = input.map(::parseNode).associateBy { it.name }.toMutableMap()
        for (node in nodes.values.toList()) {
            for (name in node.edges) {
                node.connectedNodes += nodes.getOrPut(name) { Node(name, Type.None, emptyList()) }
            }
        }
        val lastReceivedLow = nodes.values.associate { node ->
            node.name to
                    nodes.values
                        .filter { other -> node.name in other.edges }
                        .associate { it.name to true }
                        .toMutableMap()
        }

        var totalLow = 0L
        var totalHigh = 0L
        var finished = false
        var loops = 0
        val finishNodes = arrayOf("fn", "fh", "hh", "lk")
        val lastAt = hashMapOf<Map<String, Boolean>, Int>()
        fun loop(broadcaster: Node) {
            data class State(val parent: Node?, val cur: Node, val receivedLow: Boolean)

            val q = ArrayDeque<State>()
            fun push(parent: Node?, cur: Node, receivedLow: Boolean) {
                q.add(State(parent, cur, receivedLow))
            }

            push(null, broadcaster, receivedLow = true)
            while (q.isNotEmpty()) {
                val (parent, cur, receivedLow) = q.removeFirst()
                if (cur.name == "rx" && receivedLow) finished = true
//                if (cur.name == "fn") {
//                    if (receivedLow) {
//                        println("gl on idx=$loops iterations is low=$receivedLow")
//                    }
//                }
                if (receivedLow) totalLow++ else totalHigh++
                if (cur.type == Type.FlipFlop) {
                    if (!receivedLow) continue
                    cur.isLow = !cur.isLow
                    val sendLow = cur.isLow
                    for (to in cur.connectedNodes) push(cur, to, receivedLow = sendLow)
                }
                if (cur.type == Type.Conjunction) {
                    cur.isLow = receivedLow
                    val lastReceivedLowList = lastReceivedLow[cur.name]!!
                    lastReceivedLowList[parent!!.name] = receivedLow
                    val sendLow = lastReceivedLowList.all { !it.value }
                    for (to in cur.connectedNodes) push(cur, to, receivedLow = sendLow)
                }
                if (cur.type == Type.Broadcaster) {
                    cur.connectedNodes.forEach { push(null, it, receivedLow = true) }
                }
                if (cur.name in finishNodes) {
                    val m = finishNodes.associateWith { lastReceivedLow[it]!!.values.single() }
                    val cnt = m.values.count { it }
                    if (cnt >= 1 && m[cur.name]!!) {
                        val prev = lastAt.put(m, loops) ?: 0
                        val step = loops - prev
                        println("iterations done $loops (step=$step) (same=$cnt)")
                        println(finishNodes.joinToString(" ") { it + '=' + lastReceivedLow[it]!!.values.single() })
                        println("---")
                    }
                }
            }
        }

        fun print() {
            println("digraph G {")
            for (node in nodes.values) {
                println("  ${node.name} [label=\"${node.type.repr}${node.name}\"];")
            }
            for (node in nodes.values) {
                for (to in node.connectedNodes) {
                    println("  ${node.name} -> ${to.name};")
                }
            }
            println("}")
        }
//        print()

        val broadcaster = nodes.values.single { it.type == Type.Broadcaster }
        while (!finished) {
            loops++
            loop(broadcaster)
        }

        return loops
    }

    fun part2(): Any {
        // I just found LCM of cycles for finishNodes ("fn", "fh", "hh", "lk"), graphviz forever
        return Unit
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 20)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        val solver = ::Day20
//        println("Part 1 test - " + solver(testInput).part1())
        println("Part 1 real - " + solver(input).part1())

        println("---")

//        println("Part 2 test - " + solver(testInput).part2())
        println("Part 2 real - " + solver(input).part2())
    }
}
