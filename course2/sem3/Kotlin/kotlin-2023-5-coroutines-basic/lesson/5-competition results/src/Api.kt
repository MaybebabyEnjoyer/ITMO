import kotlin.time.Duration

data class Cutoff(val number: String, val time: Duration)

@JvmInline
value class Results(val results: Map<String, Duration>)
