package chatbot.api

sealed interface Keyboard {
    data object Remove : Keyboard
    data class Markup(
        val oneTime: Boolean,
        val keyboard: List<List<Button>>,
    ) : Keyboard

    data class Button(val text: String)
}
