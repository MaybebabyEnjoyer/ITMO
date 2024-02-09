import kotlin.time.Duration
import kotlinx.coroutines.flow.*

fun Flow<Cutoff>.resultsFlow(): Flow<Results> {
    return scan(emptyMap<String, Duration>()) { resultMap, cutoff ->
        resultMap + (cutoff.number to cutoff.time)
    }.filter { it.isNotEmpty() }.map { Results(it) }
}
