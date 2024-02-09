# Actor model
Ознакомьтесь с [Модель Акторов](https://ru.wikipedia.org/wiki/%D0%9C%D0%BE%D0%B4%D0%B5%D0%BB%D1%8C_%D0%B0%D0%BA%D1%82%D0%BE%D1%80%D0%BE%D0%B2).

Рассмотрим задачу хранения разделяемого состояния простого счетчика.
Мы хотим выполнять две операции `increment` (увеличить значение счетчика на 1) и `get` (получить текущее значение счетчика).

Рассмотрим следующее решение этой задачи

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow

sealed class CounterMsg
data object IncCounter : CounterMsg() // one-way message to increment counter
class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg() // a request with reply


suspend fun main() {
    val updatesFlow = MutableSharedFlow<CounterMsg>(extraBufferCapacity = 1000000)
    val counter = launch {
        var counter = 0
        updatesFlow.collect { msg ->
            when (msg) {
                is IncCounter -> counter++
                is GetCounter -> msg.response.complete(counter)
            }
        }
    }

    suspend fun increment() {
        updatesFlow.emit(IncCounter)
    }

    suspend fun get(): Int {
        val response = CompletableDeferred<Int>()
        updatesFlow.emit(GetCounter(response))
        return response.await()
    }

    withContext(Dispatchers.Default) {
        for (i in 0..<100000) {
            launch { increment() }
        }
    }
    // send a message to get a counter value from an actor
    val result = get()
    println("Counter = ${result}")
    counter.cancel() // shutdown the actor
}
```

Первым шагом использования actor мы определяем класс для сообщений, которые мы будем обрабатывать. 
Для этого удобно использовать `sealed class`.

Мы определяем два типа сообщений `IncCounter` (будет увеличивать счетчик на 1) и `GetCounter` (будет сохранять текущее значение счетчика в `CompletableDeferred`).
Сообщения будем отправлять в отдельный `Flow`, сообщения из которого будут вычитываться в другой корутине.
