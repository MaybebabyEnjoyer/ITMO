# Задание 4. Chat bot DSL

Реализуйте DSL для декларативного описания функциональности чат ботов (в Telegram, VK или других мессенджеров).

Вам дано базовое api для работы с ботом (пакет `chatbot.api`):

* Интерфейс `Client`, скрывающий детали взаимодействия с api мессенджеров
* Интерфейс `ChatStateStorage`, хранит внутри состояние определенного чата.
  Например, пользователь выбрал какой-то пункт меню и теперь должен нажать какую-то кнопку подменю.

Реализуйте DSL по заданию ниже в пакете `chatbot.dsl`.

Готовое решение в ветке `solution` запушьте в гитхаб и сделайте PR со своим ФИО в названии.

## Базовая настройка

Создайте конструкцию `chatBot(client: Client)` для определения бота, возвращающая класс, реализующий
интерфейс `ChatBot`.

* Внутри `chatBot` добавьте возможность настраивать уровень логирования
    * через конструкцию `use` (`use(LogLevel.Error)`)
    * через унарный плюс (`+LogLevel.Error`)

```kotlin
val bot = chatBot(testClient) {
    use(LogLevel.INFO)
}
```

или

```kotlin
val bot = chatBot(testClient) {
    +LogLevel.INFO
}
```

## Базовое поведение

Внутри `chatBot` реализуйте конструкцию `behaviour` для определения поведения бота. Определенные ниже конструкции далее
будем называть обработчиками.

* конструкцию `onCommand(command: String)` для определения поведения бота при получении сообщения,
  содержащего `/${command}` в качестве первого слова в сообщении
* конструкцию `onMessage(predicate: ChatBot.(Message) -> Bool)` для определения поведения бота при получении сообщения,
  удовлетворяющего предикату
* конструкции `onMessagePrefix(preffix: String)`, `onMessageContains(text: String)`
  и `onMessage(messageTextExactly: String)` для определения поведения бота при получении сообщения, текст которого
  соответствует критерию
* конструкцию `onMessage` для определения поведения бота при получении сообщения, не удовлетворяющего предыдущим
  критериям

В качестве функции для обработки соответствующих событий используйте функцию с типом `MessageProcessor`.

При обработке сообщения должен выбираться соответствующий обработчик, если подходит несколько обработчиков, то должен
выбираться первый наиболее подходящий.

```kotlin
val bot = chatBot(testClient) {
    use(LogLevel.INFO)

    behaviour {
        onCommand("help") {
            client.sendMessage(message.chatId, "How can i help you?")
        }

        onMessage("ping") {
            client.sendMessage(message.chatId, "pong", replyMessageId = message.id)
        }
    }
}
```

## Контексты внутри чатов

Разработайте возможность в каждом чате с ботом хранить некоторый контекст.
Это полезно при обработке последовательности сообщений пользователя.

* Внутри `chatBot` поддержите возможность подключения менеджера контекстов пользователей бота (объекта
  интерфейса `ChatContextsManager`)
    * через оператор присваивания (`contextManager = anyContextManager`)
    * через конструкцию `use` (`use(otherContextManager)`)

* Внутри `behaviour` поддержите возможность объявлять некоторые обработчики, которые должны срабатывать только, если чат
  имеет некоторый контекст
    * для конкретного экземпляра контекста через конструкцию `into` (`SomeChatContext.into { ... }`)
    * для всех контекста, имеющих некоторый тип через `into<SomeChatContext>` (`into<SomeChatContext> { ... }`)

```kotlin
object AskNameContext : ChatContext
class WithNameContext(val name: String) : ChatContext

val bot = chatBot {
    use(testClient)

    behaviour {
        into<NamedUserContext> {
            onMessage {
                client.sendMessage(message.chatId, "Hello, ${this.context.name}!")
            }
        }

        AskNameContext.into {
            onMessage {
                client.sendMessage(message.chatId, "ok")
                setContext(NamedUserContext(message.text))
            }
        }

        onCommand("start") {
            client.sendMessage(message.chatId, "Hello! Say your name!")
            setContext(AskNameContext)
        }
    }
}
```

## Билдер сообщений

Так же требуется поддержать dsl для отправки сообщения, с поддержкой кнопок.

Для работы с клавиатурой необходимо использовать параметр `keyboard` при отправке сообщения. 
* Клавиатуру можно убрать, отправив `Keyboard.Remove`
* Клавиатура можно настроить с помощью `Keyboard.Markup`

В рамках dsl необходимо подержать:

* Блок `sendMessage(chatId: ChatId.Id) {}` в котором настраивается сообщение,
* Вызов `removeKeyboard()` должен устанавливать клавиатуру в значение `Keyboard.Remove` при отправке,
* Блок `withKeyboard {}` должен настраивать клавиатуру и устанавливать её в соответсвующее значение `Keyboard.Markup`
  при отправке,
* Блок `row {}` должен добавлять новую строку в конец клавиатуры,
* Вызов `button(text = "text")` должен добавлять кнопку в конец строки,
* Вызов `- "text"` должен добавлять кнопку в конец строки,

Так же должно быть доступно напрямую задать:

* разметку всей клавиатуры через матрицу, присвоив значение переменной `keyboard`,
* разметку строки, добавив её в массив в переменной `keyboard`

Пустые сообщения (без текста и клавиатуры или с пустой клавиатурой) должны игнорироваться

```kotlin
val bot = chatBot(testClient) {
    behaviour {
        onCommand("help") {
            sendMessage(message.chatId) {
                text = "How can i help you?" // "" by default
                replyTo = message.id // must be available

                removeKeyboard() // will send Keyboard.Remove
                // or
                withKeyboard {
                    oneTime = true // false by default
                    
                    keyboard = mutableListOf(mutableListOf(Keyboard.Button(text = "1:1"), Keyboard.Button(text = "1:2")))
                    keyboard.add(mutableListOf(Keyboard.Button(text = "2:1"), Keyboard.Button(text = "2:2")))

                    row {
                        button(text = "3:1")
                        button(text = "3:2")
                    }
                    row {
                        -"4:1"
                        -"4:2"
                    }
                }
                // will send:
                // Keyboard.Murkup(
                //   oneTime = true,
                //   keyboard = listOf(
                //     listOf(Keyboard.Button(text = "1:1"), Keyboard.Button(text = "1:2"))),
                //     listOf(Keyboard.Button(text = "2:1"), Keyboard.Button(text = "2:2"))),
                //     listOf(Keyboard.Button(text = "3:1"), Keyboard.Button(text = "3:2"))),
                //     listOf(Keyboard.Button(text = "4:1"), Keyboard.Button(text = "4:2"))),
                //   ),
                // )
            }
        }
    }
}

```

## (Бонусное) Предикатные контексты для обработчиков

Определим `MessageProcessorPredicate` следующим образом

```kotlin
val IS_ADMIN: MessageProcessorPredicate = { (it.chatId as? ChatId.Id)?.id == 316671439L }
```

* Реализуйте возможность определять обработчики для сообщений, удовлетворяющих предикатам при помощи
  конструкции `MessageProcessorPredicate.into`.

```kotlin

val IS_ADMIN: MessageProcessorPredicate = { (it.chatId as? api.ChatId.Id)?.id == 316671439L }
chatBot(client) {
    behaviour {
        IS_ADMIN.into {
            onCommand("ban_user") {
                // ...
            }

            // and other admin commands
        }

        onCommand("help") {
            // ...
        }

        // and other user commands
    }
}
```

Обратите внимание, что в отличие от контекстов `into` конструкции могут вкладываться друг в друга, то есть
внутри `MessageProcessorPredicate.into` может быть еще одна такая же конструкция.
В таком случае обработчики должны срабатывать, когда все необходимые предикаты выполняются.

* Переопределите операцию умножения для двух контекстов: в таком случае обработчики должны выполняться, когда оба
  предиката выполняются

```kotlin
val IS_ADMIN: MessageProcessorPredicate = { (it.chatId as? api.ChatId.Id)?.id == 316671439L }
val IS_EVEN_MESSAGE_ID: MessageProcessorPredicate = { it.id % 2 == 0L }

chatBot(client) {
    behaviour {
        (IS_ADMIN * IS_EVEN_MESSAGE_ID).into {
            // ... 
        }
    }
}
```

