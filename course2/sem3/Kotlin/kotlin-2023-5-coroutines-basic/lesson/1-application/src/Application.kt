import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.*

fun CoroutineScope.runApplication(
    runUI: suspend () -> Unit,
    runApi: suspend () -> Unit,
) {
    launch {
        try {
            runUI()
        } catch (e: Exception) {
            throw e
        }
    }

    launch {
        while (true) {
            try {
                runApi()
                break
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                delay(1.seconds)
            }
        }
    }
}
