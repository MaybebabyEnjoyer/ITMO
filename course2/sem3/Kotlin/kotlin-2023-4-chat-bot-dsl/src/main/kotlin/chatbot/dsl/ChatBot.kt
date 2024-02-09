package chatbot.dsl

import chatbot.api.*
import kotlin.reflect.KClass

@BehaviourMarker
class ChatBotBuilder(private val client: Client) {
    private var logLevelBuilder: LogLevel = LogLevel.ERROR
    private val messageHandlers: MutableList<Handler<out ChatContext?>> = mutableListOf()
    private var contextManager: ChatContextsManager? = null

    fun use(logLevel: LogLevel) {
        this.logLevelBuilder = logLevel
    }

    operator fun LogLevel.unaryPlus() {
        use(this)
    }

    fun use(contextManager: ChatContextsManager) {
        this.contextManager = contextManager
    }

    fun behaviour(init: BehaviourBuilder<ChatContext?>.() -> Unit) {
        val behaviourBuilder = BehaviourBuilder<ChatContext?>(null)
        behaviourBuilder.init()
        messageHandlers.addAll(behaviourBuilder.messageHandlers)
    }

    fun build(): ChatBot {
        return object : ChatBot {
            override fun processMessages(message: Message) {
                val chatContext = contextManager?.getContext(message.chatId)

                var comparison: KClass<out ChatContext>? = null
                if (chatContext != null) {
                    comparison = chatContext::class
                }
                for (messageHandler in messageHandlers) {
                    val (_, condition, context) = messageHandler
                    if (condition(message) && context == comparison) {
                        messageHandler.process(message, client, contextManager)
                        break
                    }
                }
            }

            override val logLevel: LogLevel
                get() = logLevelBuilder
        }
    }
}

fun chatBot(client: Client, init: ChatBotBuilder.() -> Unit): ChatBot {
    val builder = ChatBotBuilder(client)
    builder.init()
    return builder.build()
}
