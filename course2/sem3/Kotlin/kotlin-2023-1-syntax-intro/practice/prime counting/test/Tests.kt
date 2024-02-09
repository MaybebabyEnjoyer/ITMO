import org.junit.Assert
import org.junit.Test

class Tests {
    @Test(timeout = 10)
    fun testIsPrimeForPrimeNumbers() {
        Assert.assertTrue("isPrime for prime number should return true", isPrime(2))
        Assert.assertTrue("isPrime for prime number should return true", isPrime(3))
        Assert.assertTrue("isPrime for prime number should return true", isPrime(5))
        Assert.assertTrue("isPrime for prime number should return true", isPrime(7))
        Assert.assertTrue("isPrime for prime number should return true", isPrime(11))
        Assert.assertTrue("isPrime for prime number should return true", isPrime(13))
        Assert.assertTrue("isPrime for prime number should return true", isPrime(1000000007))
    }

    @Test(timeout = 10)
    fun testIsPrimeForNotPrimeNumbers() {
        Assert.assertFalse("isPrime for not prime number should return false", isPrime(-10))
        Assert.assertFalse("isPrime for not prime number should return false", isPrime(0))
        Assert.assertFalse("isPrime for not prime number should return false", isPrime(1))
        Assert.assertFalse("isPrime for prime number should return true", isPrime(4))
        Assert.assertFalse("isPrime for prime number should return true", isPrime(6))
        Assert.assertFalse("isPrime for prime number should return true", isPrime(8))
        Assert.assertFalse("isPrime for prime number should return true", isPrime(10))
        Assert.assertFalse("isPrime for prime number should return true", isPrime(12))
        Assert.assertFalse("isPrime for prime number should return true", isPrime(20))
        Assert.assertFalse("isPrime for prime number should return true", isPrime(1000000006))
    }

    @Test(timeout = 10)
    fun testPrimeCount() {
        Assert.assertEquals("piFunction(-5) = 0", 0, piFunction(-5.0))
        Assert.assertEquals("piFunction(0) = 0", 0, piFunction(0.0))
        Assert.assertEquals("piFunction(1) = 0", 0, piFunction(1.0))
        Assert.assertEquals("piFunction(2) = 1", 1, piFunction(2.0))
        Assert.assertEquals("piFunction(2.5) = 1", 1, piFunction(2.5))
        Assert.assertEquals("piFunction(3.0001) = 2", 2, piFunction(3.0001))
        Assert.assertEquals("piFunction(5) = 3", 3, piFunction(5.0))
        Assert.assertEquals("piFunction(6) = 3", 3, piFunction(6.0))
        Assert.assertEquals("piFunction(10.999999) = 4", 4, piFunction(10.999999))
        Assert.assertEquals("piFunction(23) = 9", 9, piFunction(23.0))
        Assert.assertEquals("piFunction(1039) = 175", 175, piFunction(1039.0))
    }
}
