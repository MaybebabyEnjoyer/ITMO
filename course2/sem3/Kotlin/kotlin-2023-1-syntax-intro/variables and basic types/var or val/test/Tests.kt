import org.junit.Assert
import org.junit.Test

class Tests {
    @Test
    fun testPlayerTurn() {
        Assert.assertEquals(0, playerTurn(0))
        Assert.assertEquals(0, playerTurn(4))
        Assert.assertEquals(3, playerTurn(7))
    }

    @Test
    fun testFactorial() {
        Assert.assertEquals(1, factorial(0))
        Assert.assertEquals(1, factorial(1))
        Assert.assertEquals(2, factorial(2))
        Assert.assertEquals(6, factorial(3))
        Assert.assertEquals(24, factorial(4))
        Assert.assertEquals(120, factorial(5))
    }
}
