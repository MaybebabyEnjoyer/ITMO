package chatbot.dsl

import chatbot.api.*

data class MessageWithKeyboard(
    val text: String,
    val keyboard: Keyboard?,
    val replyTo: MessageId?,
)

class MessageBuilder {
    var text: String = ""
    var replyTo: MessageId? = null
    private var keyboard: Keyboard? = null

    fun removeKeyboard() {
        keyboard = Keyboard.Remove
    }

    fun withKeyboard(block: KeyboardMarkupBuilder.() -> Unit) {
        val keyboardMarkupBuilder = KeyboardMarkupBuilder()
        block(keyboardMarkupBuilder)
        keyboard = keyboardMarkupBuilder.build()
    }

    inner class KeyboardMarkupBuilder {
        var oneTime: Boolean = false
        var keyboard = mutableListOf<MutableList<Keyboard.Button>>()

        fun row(block: KeyboardRowBuilder.() -> Unit) {
            val rowBuilder = KeyboardRowBuilder()
            block(rowBuilder)
            keyboard.add(rowBuilder.row)
        }

        inner class KeyboardRowBuilder {
            val row = mutableListOf<Keyboard.Button>()

            fun button(text: String) {
                row.add(Keyboard.Button(text))
            }

            operator fun String.unaryMinus() {
                row.add(Keyboard.Button(this))
            }
        }

        fun build(): Keyboard.Markup {
            return Keyboard.Markup(oneTime, keyboard)
        }
    }

    fun isNotEmpty(): Boolean {
        return text.isNotBlank() || keyboard != null
    }

    fun build(): MessageWithKeyboard {
        return MessageWithKeyboard(text, keyboard, replyTo)
    }
}
