data class Time(
    val seconds: Long,
    val milliseconds: Int,
) {
    init {
        require(seconds >= 0) { "seconds cannot be negative" }
        require(milliseconds >= 0) { "milliseconds cannot be negative" }
    }
}
