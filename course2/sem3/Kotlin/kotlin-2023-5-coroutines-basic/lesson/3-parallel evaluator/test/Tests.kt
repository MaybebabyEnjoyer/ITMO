import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicIntegerArray
import kotlinx.coroutines.*
import org.junit.Assert
import org.junit.Test

class Tests {
    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun testOneTask() {
        val result = AtomicBoolean(false)

        val task = object : Task {
            override fun run(i: Int) {
                result.set(true)
            }
        }

        runBlocking {
            val ctx = newFixedThreadPoolContext(2, "ctx")
            ParallelEvaluator().run(task, 1, ctx)
        }

        Assert.assertTrue(result.get())
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun testBase() {
        val results = AtomicIntegerArray(10)

        val task = object : Task {
            override fun run(i: Int) {
                if (i == 0) {
                    results.set(0, 1)
                    return
                }
                Thread.sleep(((5 - i) / 4 + 1) * 100L)
                results.set(i, 1)
            }
        }

        runBlocking {
            val evaluator = ParallelEvaluator()
            val ctx = newFixedThreadPoolContext(5, "ctx")

            evaluator.run(task, 10, ctx)
        }
        for (i in 0 until 10) {
            Assert.assertEquals(1, results.get(i))
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun testFailure() {
        val task = object : Task {
            override fun run(i: Int) {
                Thread.sleep((i % 3) * 100L)
                if (i == 5 || i == 2) {
                    throw RuntimeException("")
                }
            }
        }

        runBlocking {
            val evaluator = ParallelEvaluator()
            val ctx = newFixedThreadPoolContext(5, "ctx")

            val result = runCatching {
                evaluator.run(task, 10, ctx)
            }

            Assert.assertTrue(result.isFailure)
            Assert.assertTrue(result.exceptionOrNull() is TaskEvaluationException)
            Assert.assertTrue(result.exceptionOrNull()?.cause is RuntimeException)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun testCancellation() {
        val results = AtomicIntegerArray(10)

        val task = object : Task {
            override fun run(i: Int) {
                if (i == 3) {
                    Thread.sleep(10)
                    throw RuntimeException("cancel")
                }
                Thread.sleep(200)
                results.set(i, 1)
            }
        }

        runBlocking {
            val evaluator = ParallelEvaluator()
            val ctx = newFixedThreadPoolContext(2, "ctx")

            val result = runCatching {
                evaluator.run(task, 10, ctx)
            }

            Assert.assertTrue(result.isFailure)
            Assert.assertTrue(result.exceptionOrNull() is TaskEvaluationException)
            Assert.assertTrue(result.exceptionOrNull()?.cause is RuntimeException)
        }

        Assert.assertEquals(1, results.get(0))
        Assert.assertEquals(1, results.get(1))
        Assert.assertEquals(1, results.get(2))
        Assert.assertEquals(0, results.get(3))

        Assert.assertEquals(0, results.get(6))
        Assert.assertEquals(0, results.get(7))
        Assert.assertEquals(0, results.get(8))
        Assert.assertEquals(0, results.get(9))
    }
}
