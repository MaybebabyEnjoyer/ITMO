import org.junit.Assert
import org.junit.Test
import java.lang.reflect.Modifier

class Tests {
    private fun getFunctionIfExists(name: String): ((Int) -> Int)? {
        val currentClass = this::javaClass.javaClass
        val classDefiningFunctions = currentClass.classLoader.loadClass("TaskKt")
        val javaMethod = classDefiningFunctions.methods
            .find { it.name == name && Modifier.isStatic(it.modifiers) } ?: return null
        return { n: Int -> javaMethod.invoke(null, n) as Int }
    }

    @Test
    fun testForSolution() {
        val f = getFunctionIfExists("fibonacciFor")!!
        Assert.assertEquals(0, f(0))
        Assert.assertEquals(1, f(1))
        Assert.assertEquals(1, f(2))
        Assert.assertEquals(2, f(3))
        Assert.assertEquals(3, f(4))
        Assert.assertEquals(5, f(5))
        Assert.assertEquals(8, f(6))
        Assert.assertEquals(13, f(7))
        Assert.assertEquals(21, f(8))
        Assert.assertEquals(6765, f(20))
    }

    @Test
    fun testIfSolution() {
        val f = getFunctionIfExists("fibonacciIf") ?: return
        Assert.assertEquals(0, f(0))
        Assert.assertEquals(1, f(1))
        Assert.assertEquals(1, f(2))
        Assert.assertEquals(2, f(3))
        Assert.assertEquals(3, f(4))
        Assert.assertEquals(5, f(5))
        Assert.assertEquals(8, f(6))
        Assert.assertEquals(13, f(7))
        Assert.assertEquals(21, f(8))
        Assert.assertEquals(6765, f(20))
    }

    @Test
    fun testWhenSolution() {
        val f = getFunctionIfExists("fibonacciWhen") ?: return
        Assert.assertEquals(0, f(0))
        Assert.assertEquals(1, f(1))
        Assert.assertEquals(1, f(2))
        Assert.assertEquals(2, f(3))
        Assert.assertEquals(3, f(4))
        Assert.assertEquals(5, f(5))
        Assert.assertEquals(8, f(6))
        Assert.assertEquals(13, f(7))
        Assert.assertEquals(21, f(8))
        Assert.assertEquals(6765, f(20))
    }
}
