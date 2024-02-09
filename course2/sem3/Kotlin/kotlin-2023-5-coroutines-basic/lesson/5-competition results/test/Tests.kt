import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class Tests {
    @Test
    fun testOneResult() {
        val f = flow {
            emit(Cutoff("1", 1.minutes))
        }
        runBlocking {
            val results = f.resultsFlow().filterNot { it.results.isEmpty() }.first()
            Assert.assertEquals(mapOf("1" to 1.minutes), results.results)
        }
    }

    @Test
    fun testFive() {
        val f = flow {
            emit(Cutoff("1", 1.minutes))
            emit(Cutoff("2", 2.minutes))
            emit(Cutoff("3", 3.minutes))
            emit(Cutoff("4", 4.minutes))
            emit(Cutoff("5", 5.minutes))
        }
        runBlocking {
            val results = f.resultsFlow().drop(4)
            Assert.assertEquals(
                mapOf(
                    "1" to 1.minutes,
                    "2" to 2.minutes,
                    "3" to 3.minutes,
                    "4" to 4.minutes,
                    "5" to 5.minutes,
                ),
                results.first().results,
            )
        }
    }

    @Test
    fun testMutableFlow() {
        val f = MutableSharedFlow<Cutoff>(replay = 10, extraBufferCapacity = 10)
        val results = f.resultsFlow()
        runBlocking {
            f.emit(Cutoff("5", 5.minutes))

            Assert.assertEquals(
                mapOf("5" to 5.minutes),
                results.first { it.results.size == 1 }.results,
            )

            f.emit(Cutoff("1", 1.minutes))

            Assert.assertEquals(
                mapOf("1" to 1.minutes, "5" to 5.minutes),
                results.first { it.results.size == 2 }.results,
            )

            f.emit(Cutoff("2", 2.minutes))

            Assert.assertEquals(
                mapOf("1" to 1.minutes, "2" to 2.minutes, "5" to 5.minutes),
                results.first { it.results.size == 3 }.results,
            )

            f.emit(Cutoff("3", 3.minutes))

            Assert.assertEquals(
                mapOf(
                    "1" to 1.minutes,
                    "2" to 2.minutes,
                    "3" to 3.minutes,
                    "5" to 5.minutes,
                ),
                results.first { it.results.size == 4 }.results,
            )

            f.emit(Cutoff("4", 4.minutes))

            Assert.assertEquals(
                mapOf(
                    "1" to 1.minutes,
                    "2" to 2.minutes,
                    "3" to 3.minutes,
                    "4" to 4.minutes,
                    "5" to 5.minutes,
                ),
                results.first { it.results.size == 5 }.results,
            )
        }
    }
}
