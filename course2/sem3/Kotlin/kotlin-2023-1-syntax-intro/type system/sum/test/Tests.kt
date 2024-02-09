import org.junit.Assert
import org.junit.Test

class Tests {
    @Test
    fun testIntSum() {
        Assert.assertEquals("Sum of numbers should work for int arguments", 3, sum(2, 1))
        Assert.assertEquals("Sum of numbers should work for int arguments", 420, sum(-12, 432))
    }

    @Test
    fun testLongSum() {
        Assert.assertEquals("Sum of numbers should work for long arguments", 0L, sum(-1024L, 1024L))
        Assert.assertEquals(
            "Sum of numbers should work for long arguments",
            Long.MAX_VALUE,
            sum(Long.MAX_VALUE - 1, 1L),
        )
    }

    @Test
    fun testBooleanSum() {
        Assert.assertEquals("Sum of booleans should work as or", false, sum(a = false, b = false))
        Assert.assertEquals("Sum of booleans should work as or", true, sum(a = false, b = true))
        Assert.assertEquals("Sum of booleans should work as or", true, sum(a = true, b = false))
        Assert.assertEquals("Sum of booleans should work as or", true, sum(a = true, b = true))
    }

    @Test
    fun testStringConcatenation() {
        Assert.assertEquals(
            "Sum of string should join strings",
            "pizza and pasta",
            sum("pizza ", "and pasta"),
        )
    }

    @Test
    fun testSumOfDifferentTypes() {
        Assert.assertEquals("Sum of different types should be null", null, sum(true, 0))
        Assert.assertEquals("Sum of different types should be null", null, sum(true, ""))
        Assert.assertEquals("Sum of different types should be null", null, sum(true, Unit))

        Assert.assertEquals("Sum of different types should be null", null, sum(3.14, false))
        Assert.assertEquals("Sum of different types should be null", null, sum(42, ""))
        Assert.assertEquals("Sum of different types should be null", null, sum(2002, Unit))

        Assert.assertEquals("Sum of different types should be null", null, sum("hi", false))
        Assert.assertEquals("Sum of different types should be null", null, sum("world", 2504))
        Assert.assertEquals("Sum of different types should be null", null, sum("!!!", Unit))

        Assert.assertEquals("Sum of different types should be null", null, sum(RuntimeException(), false))
        Assert.assertEquals("Sum of different types should be null", null, sum(Unit, 2504))
        Assert.assertEquals("Sum of different types should be null", null, sum(mutableListOf<Int>(), ""))
    }
}
