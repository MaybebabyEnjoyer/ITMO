sealed class Result<out S, out E> {
    data class Ok<out S>(val value: S) : Result<S, Nothing>()

    data class Error<out E>(val error: E) : Result<Nothing, E>()
}
