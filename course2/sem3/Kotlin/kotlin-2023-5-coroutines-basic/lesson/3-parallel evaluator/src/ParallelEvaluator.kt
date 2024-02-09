import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.*

class ParallelEvaluator {
    suspend fun run(task: Task, n: Int, context: CoroutineContext) {
        val deferredList = mutableListOf<Deferred<Unit>>()
        coroutineScope {
            repeat(n) { i ->
                val deferred = async(context) {
                    try {
                        task.run(i)
                    } catch (e: Exception) {
                        throw TaskEvaluationException(e)
                    }
                }
                deferredList.add(deferred)
            }
        }
        deferredList.awaitAll()
    }
}
