class Day23(val input: List<String>) {
    var field = input.map { it.toCharArray() }
    val n = input.n
    val m = input.m
    val dr = intArrayOf(0, 1, 0, -1)
    val dc = intArrayOf(1, 0, -1, 0)
    val dirs = ">v<^"
    fun inside(p: Pair<Int, Int>) = p.first in 0 until n && p.second in 0 until m

    fun part1(): Any {
        data class State(val pos: Pair<Int, Int>, val lastDir: Int)

        val dp = HashMap<State, Int>()
        fun dfs(v: State): Int {
            dp[v]?.let { return it }
            val curChar = input[v.pos.first][v.pos.second]
            val curCharSlope = dirs.indexOf(curChar)

            var ans = 0
            for (d in 0 until 4) {
                if (d xor 2 == v.lastDir) continue
                if (curCharSlope != -1 && d != curCharSlope) {
                    continue
                }
                val nr = v.pos.first + dr[d]
                val nc = v.pos.second + dc[d]
                if (inside(nr to nc) && input[nr][nc] != '#')
                    ans = maxOf(ans, 1 + dfs(State(nr to nc, d)))
            }

            dp[v] = ans
            return ans
        }

        val start = 0 to (0 until n).single { c -> field[0][c] == '.' }
        return dfs(State(start, dirs.indexOf('v')))
    }

    fun part2(): Any {
        val intersections = mutableListOf(n - 1 to input[n - 1].indexOf('.'))
        val intersectionIndex = Array(n) { IntArray(m) { -1 } }
        intersectionIndex[intersections[0].first][intersections[0].second] = 0
        for (r in 1 until n - 1) {
            for (c in 1 until m - 1) {
                if (field[r][c] == '#') continue
                var freeAround = 0
                for (d in 0 until 4) {
                    if (field[r + dr[d]][c + dc[d]] != '#') {
                        freeAround++
                    }
                }
                if (freeAround > 2) {
                    intersections.add(r to c)
                    intersectionIndex[r][c] = intersections.lastIndex
                }
            }
        }
        println("Found ${intersections.size} intersections")

        fun Long.isBitSet(a: Int) = ((this shr a) and 1) == 1L
        fun Long.setBit(a: Int) = this or (1L shl a)

        data class State(val pos: Pair<Int, Int>, val lastDir: Int, val blockedIntersections: Long)

        val updateBlockedIntersections = run {
            fun findNeighbours(startIntersection: Int): IntArray {
                val used = hashSetOf<Pair<Int, Int>>()
                val foundIntersections = hashSetOf<Int>()
                fun dfs(cur: Pair<Int, Int>) {
                    if (!used.add(cur)) return
                    val (r, c) = cur
                    for (d in 0 until 4) {
                        val nr = r + dr[d]
                        val nc = c + dc[d]
                        if (!inside(nr to nc) || field[nr][nc] == '#') continue
                        val i = intersectionIndex[nr][nc]
                        if (i != -1) {
                            foundIntersections += i
                        } else {
                            dfs(nr to nc)
                        }
                    }
                }
                dfs(intersections[startIntersection])
                return (foundIntersections - startIntersection).toTypedArray().toIntArray()
            }

            val g = Array(intersections.size, ::findNeighbours)
            fun getAccessibleIntersections(startIntersection: Int, blockedIntersections: Long): Long {
                val used = hashSetOf<Int>()
                fun dfs(v: Int) {
                    if (!used.add(v)) return
                    for (to in g[v]) {
                        if (!blockedIntersections.isBitSet(to)) {
                            dfs(to)
                        }
                    }
                }
                dfs(startIntersection)
                var res = 0L
                for (x in used) res = res.setBit(x)
                return res
            }

            fun updateBlockedIntersections(startIntersection: Int, blockedIntersections: Long): Long {
                val accessible = getAccessibleIntersections(startIntersection, blockedIntersections)
                return blockedIntersections.setBit(startIntersection) or (accessible.inv() and ((1L shl intersections.size) - 1))
            }
            ::updateBlockedIntersections
        }

        val dp = HashMap<State, Int>()
        var maxAns = -1
        fun dfs(v: State): Int {
            if (v.pos.first == n - 1) return 0
            if (v.blockedIntersections.isBitSet(0)) {
                // if end is not reachable, exit
                return -1
            }
            dp[v]?.let { return it }

            val thisIntersection = intersectionIndex[v.pos.first][v.pos.second]
            if (thisIntersection != -1) {
                val newBlockedIntersections = updateBlockedIntersections(thisIntersection, v.blockedIntersections)
                if (newBlockedIntersections != v.blockedIntersections) {
                    return dfs(State(v.pos, v.lastDir, newBlockedIntersections))
                }
            }

            var ans = -1
            for (d in 0 until 4) {
                if (d xor 2 == v.lastDir) continue
                val nr = v.pos.first + dr[d]
                val nc = v.pos.second + dc[d]
                if (!inside(nr to nc) || field[nr][nc] == '#') continue
                val nextIntersection = intersectionIndex[nr][nc]
                if (nextIntersection != -1 && v.blockedIntersections.isBitSet(nextIntersection)) {
                    continue
                }
                val rec = dfs(State(nr to nc, d, v.blockedIntersections))
                if (rec != -1) {
                    ans = maxOf(ans, 1 + rec)
                }
            }

            if (v.pos.first == 0 && ans > maxAns) {
                maxAns = ans
                println("Possible answer is $maxAns")
            }
            dp[v] = ans
            return ans
        }

        val start = 0 to (0 until n).single { c -> field[0][c] == '.' }
        return dfs(State(start, dirs.indexOf('v'), 0))
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 23)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        val solver = ::Day23
        println("Part 1 test - " + solver(testInput).part1())
        println("Part 1 real - " + solver(input).part1())

        println("---")

        println("Part 2 test - " + solver(testInput).part2())
        println("Part 2 real - " + solver(input).part2())
    }
}
