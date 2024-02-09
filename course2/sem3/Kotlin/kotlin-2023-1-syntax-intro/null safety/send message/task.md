## Send messages

В этом задании уже определены java-классы `Client`, `Contact` и интерфейс `Mailer`, позволяющий отправить сообщение на почту `email` с текстом `message`.

Реализуйте в файле __Task.kt__ функцию `sendMessageToClient` с аргументами `client` и `message`,
которая отправляет сообщение клиенту, если известен его email, с текстом `message`, если он не равен `null`, или текстом `Hello!` иначе.
