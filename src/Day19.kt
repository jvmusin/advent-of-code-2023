class Day19(val input: List<String>) {
    var field = input.map { it.toCharArray() }
    val n = input.n
    val m = input.m

    sealed interface Rule {
        val outcome: String
    }

    data class NormalRule(val variableName: Int, val sign: Char, val x: Int, override val outcome: String) : Rule
    data class SimpleRule(override val outcome: String) : Rule

    data class Workflow(
        val name: String,
        val rules: List<Rule>,
    )

    val names = "xmas"
    fun Char.toName() = names.indexOf(this).also { require(it != -1) }

    fun parseRule(s: String): Workflow {
        val name = s.split("{")[0]
        val rulesStrings = s.split("{")[1].dropLast(1).split(",")
        val rules = rulesStrings.map { rs ->
            if (':' !in rs) SimpleRule(rs)
            else {
                val (cond, outcome) = rs.split(':')
                val signAt = cond.indexOfFirst { it == '<' || it == '>' }
                val variableName = cond.substring(0, signAt).single().toName()
                val sign = cond[signAt]
                val x = cond.substring(signAt + 1)
                NormalRule(variableName, sign, x.toInt(), outcome)
            }
        }
        return Workflow(name, rules)
    }

    fun parsePart(s: String): Map<Int, Int> {
        return s.drop(1).dropLast(1).split(',').associate {
            val (name, value) = it.split('=')
            name.single().toName() to value.toInt()
        }
    }

    fun part1(): Any {
        val emptyAt = input.indexOf("")
        val workflows = input.subList(0, emptyAt).map(::parseRule).associateBy { it.name }
        val parts = input.subList(emptyAt + 1, input.size).map(::parsePart)

        fun testVariables(p: Map<Int, Int>): Boolean {
            fun testWorkflow(w: Workflow): Boolean {
                return w.rules.first { r ->
                    when (r) {
                        is SimpleRule -> true
                        is NormalRule -> {
                            val x = p[r.variableName]!!
                            if (r.sign == '<') x < r.x else x > r.x
                        }
                    }
                }.let {
                    when (it.outcome) {
                        "A" -> true
                        "R" -> false
                        else -> testWorkflow(workflows[it.outcome]!!)
                    }
                }
            }
            return testWorkflow(workflows["in"]!!)
        }

        return parts.filter { m ->
            testVariables(m)
        }.sumOf { it.values.sum() }
    }

    data class State(val m: List<IntRange>) {
        val isPossible = m.all { it.first <= it.last }
        fun updated(rule: NormalRule, accepted: Boolean): State {
            val at = rule.variableName
            val x = rule.x
            val range = m[at]
            val newRange = when {
                rule.sign == '<' ->
                    if (accepted) range.first until x else x..range.last

                else ->
                    if (accepted) x + 1..range.last else range.first..x
            }
            return State(List(4) { if (it == at) newRange else m[it] })
        }
    }

    fun part2(): Any {
        val emptyAt = input.indexOf("")
        val workflows = input.subList(0, emptyAt).map(::parseRule).associateBy { it.name }

        val tested = workflows.keys.associateWith { hashSetOf<State>() }
        val goodStates = hashSetOf<State>()
        fun testWorkflow(workflow: Workflow, state: State) {
            if (state in goodStates) return
            if (!tested[workflow.name]!!.add(state)) return

            fun go(rule: Rule, state: State) {
                when (rule.outcome) {
                    "A" -> goodStates.add(state)
                    "R" -> return
                    else -> testWorkflow(workflows[rule.outcome]!!, state)
                }
            }

            var curState = state
            for (rule in workflow.rules) {
                when (rule) {
                    is SimpleRule -> go(rule, curState)
                    is NormalRule -> {
                        val ifCorrect = curState.updated(rule, true)
                        if (ifCorrect.isPossible) go(rule, ifCorrect)

                        curState = curState.updated(rule, false)
                        if (!curState.isPossible) break
                    }
                }
            }
        }

        val fullRange = 1..4000
        testWorkflow(workflows["in"]!!, State(List(4) { fullRange }))

        fun <T> allAround(a: List<T>, getInt: (T) -> Int): List<Int> {
            return a.flatMap { v -> List(3) { getInt(v) + it - 1 } }
        }

        fun <T> allAround(a: List<T>, getInt1: (T) -> Int, getInt2: (T) -> Int): Iterable<Int> {
            return (allAround(a, getInt1) + allAround(a, getInt2)).toSortedSet()
        }

        fun allAround(a: List<State>, at: Int) = allAround(a, { it.m[at].first }, { it.m[at].last })
            .zipWithNext { from, to -> from until to }
            .filter { range ->
                a.any { range.first in it.m[at] }
            }

        fun IntRange.len() = (last - first + 1)

        var ans = 0L
        for (a in fullRange) {
            println("a $a")
            val goodA = goodStates.filter { a in it.m[0] }
            for (bs in allAround(goodA, 1)) {
                val goodB = goodA.filter { bs.first in it.m[1] }
                for (cs in allAround(goodB, 2)) {
                    val goodC = goodB.filter { cs.first in it.m[2] }
                    for (ds in allAround(goodC, 3)) {
                        val anyGoodD = goodC.any { ds.first in it.m[3] }
                        if (anyGoodD) {
                            ans += 1L * bs.len() * cs.len() * ds.len()
                        }
                    }
                }
            }
        }

        return ans
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 19)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        val solver = ::Day19
        println("Part 1 test - " + solver(testInput).part1())
        println("Part 1 real - " + solver(input).part1())

        println("---")

        println("Part 2 test - " + solver(testInput).part2())
        println("Part 2 real - " + solver(input).part2())
    }
}
