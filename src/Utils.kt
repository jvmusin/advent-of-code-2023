import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun List<String>.findPos(c: Char): Pair<Int, Int> {
    for (i in indices) {
        for (j in this[i].indices) {
            if (this[i][j] == c) return i to j
        }
    }
    error("No such symbol")
}

fun Int.positiveModulo(mod: Int) = (this % mod + mod) % mod
operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = (first + other.first) to (second + other.second)
operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>) = (first - other.first) to (second - other.second)
operator fun Pair<Int, Int>.times(k: Int) = (first * k) to (second * k)
