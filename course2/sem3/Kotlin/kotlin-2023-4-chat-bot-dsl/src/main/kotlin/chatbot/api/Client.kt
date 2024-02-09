package chatbot.api

interface Client {
    fun sendMessage(
        chatId: ChatId,
        text: String,
        keyboard: Keyboard? = null,
        replyMessageId: MessageId? = null,
    )
}
