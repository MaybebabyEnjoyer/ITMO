import kotlin.math.acos

fun isEven(x: Int): Boolean {
    return x % 2 == 0
}

fun getPi(): Double {
    return acos(-1.0)
}

fun `21 hi`(): String {
    return "hi".repeat(21)
}

fun maxLong(): Long {
    return 9223372036854775807L
}

fun twoPowers(n: Int): IntArray {
    val x = IntArray(n)
    x[0] = 1
    for (i in 1 until n) {
        x[i] = x[i - 1] * 2
    }
    return x
}

fun currentAge(): UInt {
    return 20u
}
