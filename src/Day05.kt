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

    data class Mapping(val fromPos: Long, val toPos: Long, val len: Long) {
        fun map(v: Long): Long {
            return if (v in (fromPos until fromPos + len)) v - fromPos + toPos else -1
        }
    }

    data class Part(val from: String, val to: String, val mappings: List<Mapping>) {
        fun map(v: Long): Long {
            for (m in mappings) {
                val r = m.map(v)
                if (r != -1L) return r
            }
            return v
        }
    }

    fun readPart(input: List<String>): Part {
        var at = 0
        val (from, to) = input[at++].split(" ")[0].split("-to-")
        val mappings = mutableListOf<Mapping>()
        while (at < input.size) {
            val (toPos, fromPos, len) = input[at++].split(" +".toRegex()).map { it.toLong() }
            mappings += Mapping(fromPos, toPos, len)
        }
        return Part(from, to, mappings)
    }

    fun part1(input: List<String>): Any {
        val seeds = readSeeds(input)
        val splits = input.indices.filter { input[it].isEmpty() } + input.size

        val parts = splits.indices.toList().dropLast(1).map {
            readPart(input.subList(splits[it] + 1, splits[it + 1]))
        }

        return seeds.sorted().minOf {
            val dist = hashMapOf("seed" to it)

            while (true) {
                var anyBetter = false
                val keys = dist.keys.toList()
                for (fromKey in keys) {
                    val fromValue = dist[fromKey]!!
                    for (part in parts.filter { it.from == fromKey }) {
                        part.map(fromValue)
                        val toKey = part.to
                        val toValue = part.map(fromValue)
                        val was = dist[toKey]
                        if (was == null || was > toValue) {
                            dist[toKey] = toValue
                            anyBetter = true
                        }
                    }
                }
                if (!anyBetter) break
            }

            dist["location"]!!
        }



        return Unit
    }

    fun Part.nextChange(v: Long): Long {
        var minNext = Long.MAX_VALUE
        for (mapping in mappings) {
            if (mapping.fromPos > v) minNext = minOf(minNext, mapping.fromPos)
            if (v in mapping.fromPos until mapping.fromPos + mapping.len) minNext = minOf(minNext, mapping.fromPos + mapping.len)
        }
        return minNext - v
    }

    fun part2(input: List<String>): Any {
        val seeds = readSeeds(input)
        val splits = input.indices.filter { input[it].isEmpty() } + input.size

        val parts = splits.indices.toList().dropLast(1).map {
            readPart(input.subList(splits[it] + 1, splits[it + 1]))
        }

        var ans = Long.MAX_VALUE
        for (i in seeds.indices.filter { it % 2 == 0 }) {
            val from = seeds[i]
            val to = from + seeds[i + 1] - 1
            var cur = from
            while (cur <= to) {
                var minJump = Long.MAX_VALUE - cur
                var v = cur
                for (part in parts) {
                    minJump = minOf(minJump, part.nextChange(v))
                    v = part.map(v)
                }
                ans = minOf(ans, v)
                cur += minJump
            }
        }

        return ans
    }

    @Suppress("DuplicatedCode")
    run {
        val day = String.format("%02d", 5)
        val testInput = readInput("Day${day}_test")
        val input = readInput("Day$day")
        println("Part 1 test - " + part1(testInput))
        println("Part 1 real - " + part1(input))

        println("---")

        println("Part 2 test - " + part2(testInput))
        println("Part 2 real - " + part2(input))
    }
}

