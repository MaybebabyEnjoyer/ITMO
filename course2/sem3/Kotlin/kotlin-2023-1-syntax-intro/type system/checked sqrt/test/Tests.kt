import org.junit.Assert
import org.junit.Test

class Tests {
    @Test
    fun testNotNegative() {
        Assert.assertEquals(0.0, checkedSqrt(0.0), 1e-10)
        Assert.assertEquals(3.0, checkedSqrt(9.0), 1e-10)
    }

    @Test
    fun testNegative() {
        val result = runCatching { checkedSqrt(-3.14) }
        Assert.assertEquals("sqrt with negative value", result.exceptionOrNull()?.message)
    }
}
