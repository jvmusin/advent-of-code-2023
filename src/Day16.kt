class Day16(val input: List<String>) {
    var field = input.map { it.toCharArray() }
    val n = input.n
    val m = input.m

    val dc = intArrayOf(1, 0, -1, 0)
    val dr = intArrayOf(0, 1, 0, -1)

    data class State(val pos: Pair<Int, Int>, val dir: Int) {
        fun rotateRight() = copy(dir = (dir + 1) % 4)
        fun rotateLeft() = copy(dir = (dir + 3) % 4)
        fun list() = listOf(this)
    }

    fun inside(p: Pair<Int, Int>): Boolean {
        val (r, c) = p
        return r in 0 until n && c in 0 until m
    }

    fun State.move(): List<State> {
        val nr = pos.first + dr[dir]
        val nc = pos.second + dc[dir]
        if (!inside(nr to nc)) return emptyList()

        val c = field[nr][nc]
        val next = State(nr to nc, dir)
        if (c == '.') return next.list()
        if (dir % 2 == 0) {
            if (c == '-') return next.list()
            if (c == '/') return next.rotateLeft().list()
            if (c == '\\') return next.rotateRight().list()
            if (c == '|') return listOf(next.rotateLeft(), next.rotateRight())
        } else {
            if (c == '|') return next.list()
            if (c == '/') return next.rotateRight().list()
            if (c == '\\') return next.rotateLeft().list()
            if (c == '-') return listOf(next.rotateLeft(), next.rotateRight())
        }
        error("no state")
    }

    fun findAns(start: State): Int {
        val q = hashSetOf(start)
        val added = mutableListOf(q.single())
        var i = 0
        while (i < added.size) {
            val v = added[i++]
            for (to in v.move()) {
                if (q.add(to)) {
                    added.add(to)
                }
            }
        }
        return q.distinctBy { it.pos }.size - 1
    }

    fun part1(): Any {
        return findAns(State(0 to -1, 0))
    }

    fun part2(): Any {
        var starts = mutableListOf<State>()
        for (c in 0 until m) {
            starts += State(-1 to 0, 1)
            starts += State(n to 0, 3)
        }
        for (r in 0 until m) {
            starts += State(r to -1, 0)
            starts += State(r to m, 2)
        }

        return starts.maxOf { findAns(it) }
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 16)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        val solver = ::Day16
        println("Part 1 test - " + solver(testInput).part1())
        println("Part 1 real - " + solver(input).part1())

        println("---")

        println("Part 2 test - " + solver(testInput).part2())
        println("Part 2 real - " + solver(input).part2())
    }
}
