package chatbot.dsl

import chatbot.api.*
import kotlin.reflect.KClass

typealias Predicate = ChatBot.(Message) -> Boolean

@BehaviourMarker
class BehaviourBuilder<C : ChatContext?>(private val context: KClass<out ChatContext>?) {
    val messageHandlers: MutableList<Handler<out ChatContext?>> = mutableListOf()
    fun onCommand(command: String, handler: MessageProcessor<C>) {
        messageHandlers.add(
            Handler(handler, { message -> message.text.startsWith("/$command") }, context),
        )
    }

    fun onMessage(text: String, handler: MessageProcessor<C>) {
        messageHandlers.add(
            Handler(handler, { message -> message.text == text }, context),
        )
    }

    fun onMessage(condition: Predicate, handler: MessageProcessor<C>) {
        messageHandlers.add(
            Handler(handler, condition, context),
        )
    }

    fun onMessagePrefix(prefix: String, handler: MessageProcessor<C>) {
        messageHandlers.add(
            Handler(handler, { message -> message.text.startsWith(prefix) }, context),
        )
    }

    fun onMessageContains(text: String, handler: MessageProcessor<C>) {
        messageHandlers.add(
            Handler(handler, { message -> message.text.contains(text) }, context),
        )
    }

    fun onMessage(handler: MessageProcessor<C>) {
        messageHandlers.add(
            Handler(handler, { true }, context),
        )
    }

    inline fun <reified C : ChatContext> into(init: BehaviourBuilder<C>.() -> Unit) {
        val contextClass = C::class
        val behaviourBuilder = BehaviourBuilder<C>(contextClass)
        behaviourBuilder.init()
        messageHandlers.addAll(behaviourBuilder.messageHandlers)
    }

    inline infix fun <reified C : ChatContext> C.into(block: BehaviourBuilder<C>.(C) -> Unit) {
        val contextClass = C::class
        val behaviourBuilder = BehaviourBuilder<C>(contextClass)
        behaviourBuilder.block(this)
        messageHandlers.addAll(behaviourBuilder.messageHandlers)
    }
}

fun MessageProcessorContext<ChatContext?>.sendMessage(chatId: ChatId, block: MessageBuilder.() -> Unit) {
    val messageBuilder = MessageBuilder()
    block(messageBuilder)
    if (messageBuilder.isNotEmpty()) {
        val message = messageBuilder.build()
        if (message.keyboard != null && message.keyboard is Keyboard.Markup) {
            val markup = message.keyboard
            var flag = true
            for (row in markup.keyboard) {
                if (row.isNotEmpty()) flag = false
                break
            }
            if (markup.keyboard.isNotEmpty() && !flag) {
                // Отправка сообщения с настроенной клавиатурой. Проверка сделана чтоб пустую клавиатуру(не null) не допустить
                client.sendMessage(chatId, message.text, message.keyboard, message.replyTo)
            }
        } else {
            // Отправка сообщения без клавиатуры
            client.sendMessage(chatId, message.text, message.keyboard, message.replyTo)
        }
    }
}
