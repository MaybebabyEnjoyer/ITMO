sealed interface IntResult {
    data class Ok(val value: Int) : IntResult
    data class Error(val reason: String) : IntResult

    fun getOrDefault(default: Int): Int {
        return when (this) {
            is Ok -> value
            is Error -> default
        }
    }

    fun getOrNull(): Int? {
        return when (this) {
            is Ok -> value
            is Error -> null
        }
    }

    fun getStrict(): Int {
        return when (this) {
            is Ok -> value
            is Error -> throw NoResultProvided(reason)
        }
    }
}

class NoResultProvided(reason: String) : NoSuchElementException(reason)

fun safeRun(unsafeCall: () -> Int): IntResult {
    return try {
        IntResult.Ok(unsafeCall())
    } catch (error: Throwable) {
        IntResult.Error(error.message ?: "Something went wrong")
    }
}
