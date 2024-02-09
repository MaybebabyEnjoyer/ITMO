import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.*
import org.junit.Assert
import org.junit.Test

class Tests {
    @Test
    fun testBasic1() {
        val uiResult = AtomicBoolean(false)
        val apiResult = AtomicBoolean(false)

        runBlocking {
            runApplication({
                uiResult.set(true)
            }, {
                delay(200.milliseconds)
                apiResult.set(true)
            })
        }
        Assert.assertTrue(uiResult.get())
        Assert.assertTrue(apiResult.get())
    }

    @Test
    fun testBasic2() {
        val uiResult = AtomicBoolean(false)
        val apiResult = AtomicBoolean(false)

        runBlocking {
            runApplication({
                delay(200.milliseconds)
                uiResult.set(true)
            }, {
                apiResult.set(true)
            })
        }
        Assert.assertTrue(uiResult.get())
        Assert.assertTrue(apiResult.get())
    }

    @Test
    fun testFailUi() {
        val apiResult = AtomicBoolean(false)

        var handled: Throwable? = null
        val handler = CoroutineExceptionHandler { _, throwable ->
            handled = throwable
        }
        runBlocking {
            val scope = CoroutineScope(Dispatchers.Default + handler)
            scope.launch {
                runApplication({
                    delay(100.milliseconds)
                    throw RuntimeException("Unknown ui error")
                }, {
                    delay(2.seconds)
                    apiResult.set(true)
                })
            }.join()
        }
        Assert.assertFalse(apiResult.get())
        Assert.assertEquals("Unknown ui error", handled?.message)
    }

    @Test
    fun testFailApi() {
        val uiResult = AtomicBoolean(false)
        val apiResult = AtomicBoolean(false)
        val apiCounter = AtomicInteger(0)

        runBlocking {
            runApplication({
                uiResult.set(true)
            }, {
                if (apiCounter.incrementAndGet() < 4) {
                    throw RuntimeException("Api error")
                }
                apiResult.set(true)
            })
        }
        Assert.assertEquals(true, uiResult.get())
        Assert.assertEquals(true, apiResult.get())
    }
}
