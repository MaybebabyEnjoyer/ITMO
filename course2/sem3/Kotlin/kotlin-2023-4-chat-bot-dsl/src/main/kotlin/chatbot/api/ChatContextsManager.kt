package chatbot.api

interface ChatContext

interface ChatContextsManager {
    fun getContext(chatId: ChatId): ChatContext?
    fun setContext(chatId: ChatId, newState: ChatContext?)
}
