package chatbot.dsl

import chatbot.api.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BaseTests {
    private var testClient = TestClient()
    private var chatContext = InMemoryContextsManager()

    @BeforeEach
    fun before() {
        testClient = TestClient()
        chatContext = InMemoryContextsManager()
    }

    private fun assertMessage(
        expectedCharId: Long,
        expectedText: String,
        expectedKeyboard: Keyboard? = null,
        expectedReplyMessageId: MessageId? = null,
    ) {
        val messages = testClient.pullMessages()
        Assertions.assertEquals(1, messages.size)

        val message = messages.single()
        Assertions.assertEquals(ChatId.Id(expectedCharId), message.chatId)
        Assertions.assertEquals(expectedText, message.text)
        Assertions.assertEquals(expectedKeyboard, message.keyboard)
        Assertions.assertEquals(expectedReplyMessageId, message.replyMessageId)
    }

    private fun assertNoMessages() {
        val messages = testClient.pullMessages()
        Assertions.assertTrue(messages.isEmpty())
    }

    @Test
    fun simpleChatBot() {
        val bot = chatBot(testClient) {
            use(LogLevel.INFO)

            behaviour {
                onCommand("help") {
                    client.sendMessage(message.chatId, "I need help too")
                }

                onMessage {
                    client.sendMessage(message.chatId, "hello, user", replyMessageId = message.id)
                }
            }
        }

        Assertions.assertEquals(LogLevel.INFO, bot.logLevel)

        bot.processMessages(TEST_MESSAGE_1)
        assertMessage(42, "hello, user", expectedReplyMessageId = TEST_MESSAGE_1.id)

        bot.processMessages(TEST_MESSAGE_HELP)
        assertMessage(11, "I need help too")
    }

    @Test
    fun messageBuilder() {
        val bot = chatBot(testClient) {
            behaviour {
                onMessage("removeKeyboard") {
                    sendMessage(message.chatId) {
                        removeKeyboard()
                    }
                }

                onMessage("manualSetKeyboard") {
                    sendMessage(message.chatId) {
                        withKeyboard {
                            keyboard = mutableListOf(
                                mutableListOf(Keyboard.Button(text = "1:1"), Keyboard.Button(text = "1:2")),
                            )
                            keyboard += mutableListOf(Keyboard.Button(text = "2:1"))
                            keyboard.last() += Keyboard.Button(text = "2:2")
                        }
                    }
                }

                onMessage("buildKeyboard") {
                    sendMessage(message.chatId) {
                        withKeyboard {
                            oneTime = true
                            row {
                                button(text = "1:1")
                                button(text = "1:2")
                            }

                            row {
                                -"2:1"
                                -"2:2"
                            }
                        }
                    }
                }

                onMessage("allEmpty") {
                    sendMessage(message.chatId) { }
                }

                onMessage("emptyKeyboard") {
                    sendMessage(message.chatId) {
                        withKeyboard { }
                    }
                }

                onMessage("emptyKeyboardSet") {
                    sendMessage(message.chatId) {
                        withKeyboard {
                            keyboard = mutableListOf(mutableListOf<Keyboard.Button>())
                        }
                    }
                }

                onMessage("emptyKeyboardBuild") {
                    sendMessage(message.chatId) {
                        withKeyboard {
                            row {}
                            row {}
                        }
                    }
                }

                onMessage {
                    sendMessage(message.chatId) {
                        text = "hello, user"
                        replyTo = message.id
                    }
                }
            }
        }

        Assertions.assertEquals(LogLevel.ERROR, bot.logLevel)

        bot.processMessages(TEST_MESSAGE_1)
        assertMessage(42, "hello, user", expectedReplyMessageId = TEST_MESSAGE_1.id)

        bot.sendText("removeKeyboard")
        assertMessage(1, "", expectedKeyboard = Keyboard.Remove)

        bot.sendText("manualSetKeyboard")
        assertMessage(
            1,
            "",
            expectedKeyboard = Keyboard.Markup(
                oneTime = false,
                keyboard = listOf(
                    listOf(Keyboard.Button(text = "1:1"), Keyboard.Button(text = "1:2")),
                    listOf(Keyboard.Button(text = "2:1"), Keyboard.Button(text = "2:2")),
                ),
            ),
        )

        bot.sendText("buildKeyboard")
        assertMessage(
            1,
            "",
            expectedKeyboard = Keyboard.Markup(
                oneTime = true,
                keyboard = listOf(
                    listOf(Keyboard.Button(text = "1:1"), Keyboard.Button(text = "1:2")),
                    listOf(Keyboard.Button(text = "2:1"), Keyboard.Button(text = "2:2")),
                ),
            ),
        )

        bot.sendText("allEmpty")
        assertNoMessages()

        bot.sendText("emptyKeyboard")
        assertNoMessages()

        bot.sendText("emptyKeyboardSet")
        assertNoMessages()

        bot.sendText("emptyKeyboardBuild")
        assertNoMessages()
    }

    @Test
    fun setupLogLevelUsingPlus() {
        Assertions.assertEquals(
            LogLevel.INFO,
            chatBot(testClient) { +LogLevel.INFO }.logLevel,
        )
        Assertions.assertEquals(
            LogLevel.ERROR,
            chatBot(testClient) { +LogLevel.ERROR }.logLevel,
        )
    }

    @Test
    fun setupLogLevelUsingUse() {
        Assertions.assertEquals(
            LogLevel.INFO,
            chatBot(testClient) { use(LogLevel.INFO) }.logLevel,
        )
        Assertions.assertEquals(
            LogLevel.ERROR,
            chatBot(testClient) { use(LogLevel.ERROR) }.logLevel,
        )
    }

    @Test
    fun basicBehaviourCommands() {
        val bot = chatBot(testClient) {
            behaviour {
                onMessage({ message -> message.text == "ultra_secret_command" }) {
                    client.sendMessage(message.chatId, "my password in 777")
                }

                onCommand("start") {
                    client.sendMessage(message.chatId, "Lets start!")
                }

                onMessage("secret_key") {
                    client.sendMessage(message.chatId, "You really know it?")
                }

                onMessagePrefix("start using dsl") {
                    client.sendMessage(message.chatId, "Ok, it's easy")
                }

                onMessageContains("plagiarize") {
                    client.sendMessage(message.chatId, "ban")
                }

                onMessage {
                    client.sendMessage(message.chatId, "???")
                }

                onCommand("help") {
                    client.sendMessage(message.chatId, "Message should not being send")
                }
            }
        }

        Assertions.assertEquals(LogLevel.ERROR, bot.logLevel)

        bot.sendText("ultra_secret_command")
        assertMessage(1, "my password in 777")

        bot.sendText("/start")
        assertMessage(1, "Lets start!")

        bot.sendText("secret_key")
        assertMessage(1, "You really know it?")

        bot.sendText("secret_key ")
        assertMessage(1, "???")

        bot.sendText(" secret_key")
        assertMessage(1, "???")

        bot.sendText("start using dsl today")
        assertMessage(1, "Ok, it's easy")

        bot.sendText("start using dsl")
        assertMessage(1, "Ok, it's easy")

        bot.sendText("plagiarize")
        assertMessage(1, "ban")

        bot.sendText("ask group mate to plagiarize solution")
        assertMessage(1, "ban")

        bot.sendText("/help")
        assertMessage(1, "???")
    }

    object AskNameContext : ChatContext
    class WithNameContext(val name: String) : ChatContext

    @Test
    fun userNameContext() {
        val bot = chatBot(testClient) {
            use(chatContext)

            behaviour {
                into<WithNameContext> {
                    onCommand("change_name") {
                        client.sendMessage(message.chatId, "Say your new name")
                        setContext(AskNameContext)
                    }

                    onMessage {
                        client.sendMessage(message.chatId, "Hello, ${this.context.name}!")
                    }
                }

                AskNameContext into {
                    onMessage {
                        client.sendMessage(message.chatId, "ok")
                        setContext(WithNameContext(message.text))
                    }
                }

                onCommand("start") {
                    client.sendMessage(message.chatId, "Hello! Say your name!")
                    setContext(AskNameContext)
                }
            }
        }

        bot.sendText("/start")
        assertMessage(1L, "Hello! Say your name!")

        bot.sendText("Kotlin Student")
        assertMessage(1L, "ok")

        bot.sendText("/start")
        assertMessage(1L, "Hello, Kotlin Student!")

        bot.sendText("test")
        assertMessage(1L, "Hello, Kotlin Student!")

        bot.sendText("/change_name")
        assertMessage(1L, "Say your new name")

        bot.sendText("New Name")
        assertMessage(1L, "ok")

        bot.sendText("test")
        assertMessage(1L, "Hello, New Name!")

        bot.sendText("/start")
        assertMessage(1L, "Hello, New Name!")
    }

    private fun ChatBot.sendText(text: String) {
        processMessages(
            Message(
                id = 1,
                chatId = ChatId.Id(1),
                text = text,
            ),
        )
    }

    companion object {
        class TestClient : Client {
            data class SendMessageInfo(
                val chatId: ChatId,
                val text: String,
                val keyboard: Keyboard?,
                val replyMessageId: MessageId?,
            )

            private val messages = mutableListOf<SendMessageInfo>()

            fun pullMessages(): List<SendMessageInfo> {
                val messages = this.messages.toList()
                this.messages.clear()
                return messages
            }

            override fun sendMessage(chatId: ChatId, text: String, keyboard: Keyboard?, replyMessageId: MessageId?) {
                messages.add(SendMessageInfo(chatId, text, keyboard, replyMessageId))
            }
        }

        class InMemoryContextsManager : ChatContextsManager {
            private val data = mutableMapOf<Long, ChatContext?>()

            override fun getContext(chatId: ChatId): ChatContext? {
                return (chatId as? ChatId.Id)?.run { data[id] }
            }

            override fun setContext(chatId: ChatId, newState: ChatContext?) {
                (chatId as? ChatId.Id)?.run {
                    data[id] = newState
                }
            }
        }

        val TEST_MESSAGE_1 = Message(
            id = 123L,
            chatId = ChatId.Id(42),
            text = "hello, bot",
        )
        val TEST_MESSAGE_HELP = Message(
            id = 124L,
            chatId = ChatId.Id(11),
            text = "/help me",
        )
    }
}
