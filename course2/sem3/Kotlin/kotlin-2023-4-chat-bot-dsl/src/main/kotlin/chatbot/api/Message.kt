package chatbot.api

typealias MessageId = Long

data class Message(
    val id: MessageId,
    val chatId: ChatId,
    val text: String,
    val replyMessageId: MessageId? = null,
)
