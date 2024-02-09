import org.junit.Assert
import org.junit.Test
import kotlin.math.pow

class Tests {
    @Test
    fun testSimple() {
        Assert.assertArrayEquals(intArrayOf(1, 4, 9), mapSquares(intArrayOf(1, 2, 3)))
    }

    @Test
    fun testEmpty() {
        Assert.assertArrayEquals(intArrayOf(), mapSquares(intArrayOf()))
    }

    @Test
    fun testBig() {
        val input = IntArray(100)
        val output = IntArray(100)
        for (i in 0 until 100) {
            input[i] = (-1.0.pow(i).toInt() * i)
            output[i] = i * i
        }
        Assert.assertArrayEquals(output, mapSquares(input))
    }
}
