import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.*
import org.junit.Assert
import org.junit.Test

class Tests {
    @Test
    fun testAwaitDoneFeature() {
        val x = object : CompletableFuture<Int> {
            override val isDone = true
            override val isCancelled = true
            override val value = 42
            override fun cancel() = Unit
            override fun handle(handler: ContinuationHandler<Int>) {
                Assert.fail("handle invocation not expected, because Feature already done")
            }
        }

        runBlocking {
            Assert.assertEquals(42, x.await())
        }

        runBlocking {
            val r1 = x.await()
            val r2 = x.await()
            Assert.assertEquals(42, r2)
            Assert.assertEquals(42, r1)
        }
    }

    @Test
    fun testAwaitOne() {
        val f = TestFeature()
        runBlocking {
            launch {
                delay(200.milliseconds)
                f.done(1)
            }
            val r = f.await()
            Assert.assertEquals(1, r)
        }
    }

    @Test
    fun testAwaitTwice() {
        val f = TestFeature()
        runBlocking {
            launch {
                delay(200.milliseconds)
                f.done(1)
            }
            Assert.assertEquals(1, f.await())
            Assert.assertEquals(1, f.await())
        }
    }

    @Test
    fun testAwaitParallel() {
        val f = TestFeature()
        runBlocking {
            launch {
                delay(200.milliseconds)
                f.done(1)
            }
            coroutineScope {
                launch {
                    Assert.assertEquals(1, f.await())
                }
                launch {
                    Assert.assertEquals(1, f.await())
                }
                launch {
                    Assert.assertEquals(1, f.await())
                }
            }
            Assert.assertEquals(1, f.await())
        }
    }

    @Test
    fun testCancelEvaluation() {
        val f = TestFeature()
        runBlocking {
            val j = launch {
                Assert.assertEquals(1, f.await())
            }
            delay(100.milliseconds)
            j.cancel()
            Assert.assertTrue(f.isCancelled)
        }
    }

    @Test
    fun testCancelEvaluation2() {
        val f = TestFeature()
        runBlocking {
            launch {
                val j = launch {
                    f.await()
                }
                delay(100.milliseconds)
                j.cancel()
            }

            val j1 = async { f.await() }
            val r1 = runCatching { j1.await() }
            Assert.assertTrue(r1.isFailure && r1.exceptionOrNull() is CancellationException)

            val j2 = launch { f.await() }
            j2.join()
            Assert.assertTrue(j2.isCancelled)
        }
        runBlocking {
            val r = runCatching { f.await() }
            Assert.assertTrue(r.exceptionOrNull() is CancellationException)
        }
    }

    private class TestFeature : CompletableFuture<Int> {
        @Volatile
        private var computedValue: Int? = null
        private val handlers = ConcurrentLinkedQueue<ContinuationHandler<Int>>()
        private val mutex = ReentrantLock()

        @Volatile
        override var isCancelled: Boolean = false
            private set

        fun done(value: Int) {
            mutex.withLock {
                computedValue = value
                while (handlers.isNotEmpty()) {
                    handlers.poll().invoke(value)
                }
            }
        }

        override val isDone: Boolean
            get() = computedValue != null

        override val value: Int
            get() = computedValue!!

        override fun cancel() {
            mutex.withLock {
                isCancelled = true
                for (handler in handlers) {
                    handler.cancel()
                }
            }
        }

        override fun handle(handler: ContinuationHandler<Int>) {
            mutex.withLock {
                if (isCancelled) {
                    throw CancellationException()
                }
                computedValue?.let { handler.invoke(it) } ?: handlers.add(handler)
            }
        }
    }
}
