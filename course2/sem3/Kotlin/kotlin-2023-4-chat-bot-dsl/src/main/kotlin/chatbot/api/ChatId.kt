package chatbot.api

sealed interface ChatId {
    data class Id(val id: Long) : ChatId
}
