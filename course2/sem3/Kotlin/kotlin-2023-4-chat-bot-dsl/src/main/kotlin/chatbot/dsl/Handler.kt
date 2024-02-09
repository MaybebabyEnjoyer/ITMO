package chatbot.dsl

import chatbot.api.*
import kotlin.reflect.KClass

data class Handler<T : ChatContext?>(
    val processor: MessageProcessor<T>,
    val predicate: Predicate,
    val context: KClass<out ChatContext>?,
) {
    fun process(message: Message, client: Client, contextManager: ChatContextsManager?) {
        val chatContext = contextManager?.getContext(message.chatId) as T
        processor(
            MessageProcessorContext(
                message = message,
                client = client,
                context = chatContext,
                setContext = { newContext ->
                    contextManager?.setContext(message.chatId, newContext)
                },
            ),
        )
    }
}
