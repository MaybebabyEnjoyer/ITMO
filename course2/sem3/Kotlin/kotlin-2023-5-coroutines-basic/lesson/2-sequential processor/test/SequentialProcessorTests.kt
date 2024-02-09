import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicIntegerArray
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.*
import org.junit.Assert
import org.junit.Test

class SequentialProcessorTests {
    @Test
    fun testCorrectness() {
        val processor: TaskProcessor = SequentialProcessor { x -> x }
        testParallel {
            processor.assertProcessResult("hello", "hello")
            delay(100.milliseconds)
            processor.assertProcessResult("coroutines", "coroutines")
            delay(100.milliseconds)
            processor.assertProcessResult("in", "in")
            delay(100.milliseconds)
            processor.assertProcessResult("kotlin", "kotlin")
        }
    }

    @Test
    fun testMultiple() {
        val threadIds: Queue<Long> = ConcurrentLinkedQueue()
        val processor: TaskProcessor = SequentialProcessor { x ->
            threadIds += Thread.currentThread().id
            "Hello, $x!"
        }
        testParallel {
            for (i in 1..PARALLEL_TASK_COUNT) {
                launch {
                    processor.assertProcessResult("Hello, unit-$i!", "unit-$i")
                }
            }
        }
        Assert.assertTrue(threadIds.all { it == threadIds.first() })
    }

    @Test
    fun testParallel() {
        val processor: TaskProcessor = SequentialProcessor { x -> "Hello, $x!" }
        testParallel {
            for (i in 1..PARALLEL_TASK_COUNT) {
                launch {
                    processor.assertProcessResult("Hello, unit-$i!", "unit-$i")
                }
            }
        }
    }

    @Test
    fun testLinearizability() {
        var counter = 0
        val order = AtomicIntegerArray(PARALLEL_TASK_COUNT)
        val processor: TaskProcessor = SequentialProcessor { x ->
            val number = counter++
            val index = x.toInt()
            order.set(index, number)
            ""
        }

        testParallel {
            for (i in 0..<PARALLEL_TASK_COUNT) {
                launch {
                    processor.assertProcessResult("", i.toString())
                }
            }
        }

        Assert.assertEquals(PARALLEL_TASK_COUNT, counter)

        val actualOrder = MutableList(PARALLEL_TASK_COUNT) { order[it] }
        actualOrder.sort()
        Assert.assertEquals((0..<PARALLEL_TASK_COUNT).toList(), actualOrder)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun testParallel(block: suspend CoroutineScope.() -> Unit) {
        runBlocking {
            val scope = newFixedThreadPoolContext(10, "parallel")
            withContext(scope) {
                block()
            }
        }
    }

    companion object {
        private const val PARALLEL_TASK_COUNT = 100000

        private suspend fun TaskProcessor.assertProcessResult(expectedResult: String, argument: String) {
            val result = process(argument)
            Assert.assertEquals(expectedResult, result)
        }
    }
}
