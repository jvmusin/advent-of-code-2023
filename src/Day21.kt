import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.time.Duration.Companion.milliseconds

class Day21(val input: List<String>) {
    var field = input.map { it.toCharArray() }
    val n = input.n

    val dx = intArrayOf(0, 1, 0, -1)
    val dy = intArrayOf(1, 0, -1, 0)
    fun inside(p: Pair<Int, Int>): Boolean {
        val (r, c) = p
        return r in 0 until n && c in 0 until n
    }

    val start = input.findPos('S')

    fun part1(): Any {
        var used = Array(n) { BooleanArray(n) }
        used[start.first][start.second] = true
        repeat(64) {
            val newUsed = Array(n) { r ->
                BooleanArray(n) { c ->
                    if (field[r][c] == '#') false
                    else {
                        for (d in 0 until 4) {
                            val nr = r + dx[d]
                            val nc = c + dy[d]
                            if (inside(nr to nc) && used[nr][nc]) {
                                return@BooleanArray true
                            }
                        }
                        false
                    }
                }
            }
            used = newUsed
        }
        return used.sumOf { it.count { it } }
    }

    fun mod(x: Int, y: Int) = (((x % y) + y) % y)

    fun part2(): Any {
        val getDist = run {
            val dist = hashMapOf<Pair<Int, Int>, Int>()
            val start = input.findPos('S')
            dist[start] = 0
            var lastLater = listOf(start)
            @Synchronized
            fun grow() {
                val nextLayer = mutableListOf<Pair<Int, Int>>()
                for ((r, c) in lastLater) {
                    for (d in 0 until 4) {
                        val nr = r + dx[d]
                        val nc = c + dy[d]
                        val nv = nr to nc
                        if (field[mod(nr, n)][mod(nc, n)] != '#' && nv !in dist) {
                            dist[nv] = dist[r to c]!! + 1
                            nextLayer += nv
                        }
                    }
                }
                lastLater = nextLayer
            }

            fun getDist(p: Pair<Int, Int>): Int {
                require(field[mod(p.first, n)][mod(p.second, n)] != '#')
                while (p !in dist) {
//                    if (dist.size > 1e6) return 3e7.toInt()
                    grow()
                }
                return dist[p]!!
            }

            ::getDist
        }

        fun divUp(x: Int, y: Int) = (x + y - 1) / y

        fun posInside(pos: Int, maxValue: Int): Int {
            if (pos < 0) return -posInside(-pos, maxValue)
            val dist = pos - maxValue
            if (dist <= 0) return pos
            return pos - divUp(dist, 2 * n) * 2 * n
        }

        fun len(p: Pair<Int, Int>): Int {
            val (r, c) = p

            val baseR = posInside(r, 8 * n - 1)
            val baseC = posInside(c, 8 * n - 1)
            val baseDist = getDist(baseR to baseC)
            val step = getDist(n * 10 to 0) - getDist(n * 8 to 0)
            require((baseR - r) % (n * 2) == 0)
            require((baseC - c) % (n * 2) == 0)
            val deltaR = (baseR - r).absoluteValue / (2 * n)
            val deltaC = (baseC - c).absoluteValue / (2 * n)
            val dist = baseDist + (deltaC + deltaR) * step
//            val CHECK = getDist(r to c)
//            if (CHECK != dist) {
//                println("oopsie")
//            }
            return dist
        }

        fun test() {
            val rng = -500..500
            for (r in rng) {
                for (c in rng) {
                    if (input[mod(r, n)][mod(c, n)] != '#') {
                        len(r to c)
                    }
                }
            }

            val offR = 400
            val offC = 400
            val step = getDist(n * 10 to n * 10) - getDist(n * 8 to n * 10)
            for (i in 0 until 2 * n) {
                for (j in 0 until 2 * n) {
                    val r = offR + i
                    val c = offC + j
                    if (input[r % n][c % n] == '#') continue
                    len(r to c)
                    for (dr in 0..3) {
                        for (dc in 0..3) {
                            val expectedDiff = step * (dr + dc)
                            val actualDiff = getDist(r + dr * 2 * n to c + dc * 2 * n) - getDist(r to c)
                            require(expectedDiff == actualDiff)
                        }
                    }
                }
            }

            repeat(1000000) {
                val r0 = Random.nextInt(-1000..1000)
                val c0 = Random.nextInt(-1000..1000)
                if (r0.absoluteValue < n && c0.absoluteValue < n) return@repeat
                if (r0 * c0 == 0 || field[mod(r0, n)][mod(c0, n)] == '#') return@repeat
                val r2 = r0 + 2 * n * r0.sign
                val c2 = c0 + 2 * n * c0.sign
                val r4 = r0 + 4 * n * r0.sign
                val c4 = c0 + 4 * n * c0.sign

                val d0 = getDist(r0 to c0)
                val d2 = getDist(r2 to c2)
                val d4 = getDist(r4 to c4)
                val step2 = d2 - d0
                val step4 = d4 - d2
                if (step2 != step4) {
                    println()
                }
//            require(step2 == step4)
//            require(step2 == 22)
            }
        }
//        test()

        val n2 = n * 2

        val maxDistance = 26501365
        fun findAnswers(baseR: Int, baseC: Int): Long {
            if (field[baseR % n][baseC % n] == '#') return 0

            var r0 = baseR
            var c0 = baseC
            while (true) {
                when {
                    len(r0 - n2 to c0) < len(r0 to c0) -> r0 -= n2
                    len(r0 + n2 to c0) < len(r0 to c0) -> r0 += n2
                    len(r0 to c0 - n2) < len(r0 to c0) -> c0 -= n2
                    len(r0 to c0 + n2) < len(r0 to c0) -> c0 += n2
                    else -> break
                }
            }

            val dist0 = len(r0 to c0)
            if (dist0 % 2 != maxDistance % 2) return 0
            var cSteps = 0
            while (len(r0 to c0 - (cSteps + 1) * n2) <= maxDistance) {
                cSteps++
            }
            val minC = c0 - (cSteps + 10) * n2
            val maxC = c0 + (cSteps + 10) * n2

            var top = r0
            var bot = r0
            var ans = 0L
            for (curC in minC..maxC step n2) {
                while (len(top - n2 to curC) <= maxDistance) top -= n2
                while (len(bot + n2 to curC) <= maxDistance) bot += n2
                while (top <= r0 && len(top to curC) > maxDistance) top += n2
                while (bot >= r0 && len(bot to curC) > maxDistance) bot -= n2
                if (top <= bot) {
                    ans += (bot - top) / n2 + 1
                } else {
                    require(len(r0 to curC) > maxDistance)
                    top = r0
                    bot = r0
                }
                for (shift in 1..3) {
                    if (len(top - n2 * shift to curC) <= maxDistance) ans++
                    if (len(bot + n2 * shift to curC) <= maxDistance) ans++
                }
            }

            return ans
        }

        fun parallel(): Long {
            val cores = Runtime.getRuntime().availableProcessors()
            val threads = cores - 1
            println("Total threads to use is $threads")
            val pool = Executors.newFixedThreadPool(threads)
            val totalDone = AtomicInteger(0)
            val answer = AtomicLong(0)
            val totalCells = n2 * n2
            val startTime = System.currentTimeMillis()

            findAnswers(0, 0) // to make better time estimates

            Array(n2) { r ->
                Array(n2) { c ->
                    pool.submit {
                        answer.addAndGet(findAnswers(r, c))
                        val doneCells = totalDone.incrementAndGet()
                        val timeSpent = System.currentTimeMillis() - startTime
                        val fullTime = timeSpent.toDouble() / doneCells * totalCells
                        val timeLeft = fullTime - timeSpent
                        println("[${Thread.currentThread().name}] Done $doneCells/$totalCells cells (last was $r:$c), time spent ${timeSpent.milliseconds}, est time left ${timeLeft.milliseconds} (est total time is ${fullTime.milliseconds}), cur ans is ${answer.get()}")
                    }
                }
            }.flatten().forEach { it.get() }

            println("ANSWER IS $answer")
            return answer.get()
        }

//        var ans = 0L
//        val totalCells = n2 * n2
//        var startTime = System.currentTimeMillis()
//        for (r0 in 0 until n2) {
//            for (c0 in 0 until n2) {
//                val timeSpent = System.currentTimeMillis() - startTime
//                val doneCells = r0 * n2 + c0 + 1
//                val fullTime = timeSpent.toDouble() / doneCells * totalCells
//                val timeLeft = fullTime - timeSpent
//                ans += findAnswers(r0, c0)
//                println("Done $r0:$c0 ($doneCells/$totalCells}, time spent ${timeSpent.milliseconds}, est time left ${timeLeft.milliseconds} (est total time is ${fullTime.milliseconds}), cur ans is $ans")
//            }
//        }



        // prints this
        // [pool-1-thread-7] Done 68644/68644 cells (last was 261:258), time spent 1h 41m 4.182s, est time left 0s (est total time is 1h 41m 4.182s), cur ans is 618261433219147
        //ANSWER IS 618261433219147
        //Part 2 real - 618261433219147
        return parallel()
    }
}

fun main() {
    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 21)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        val solver = ::Day21
        println("Part 1 test - " + solver(testInput).part1())
        println("Part 1 real - " + solver(input).part1())

        println("---")

//        println("Part 2 test - " + solver(testInput).part2())
        println("Part 2 real - " + solver(input).part2())
    }
}
