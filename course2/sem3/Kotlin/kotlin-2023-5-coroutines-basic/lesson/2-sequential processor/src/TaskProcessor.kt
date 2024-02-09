interface TaskProcessor {
    suspend fun process(argument: String): String
}
