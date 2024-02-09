package chatbot.api

interface ChatBot {
    fun processMessages(message: Message)

    val logLevel: LogLevel
}
