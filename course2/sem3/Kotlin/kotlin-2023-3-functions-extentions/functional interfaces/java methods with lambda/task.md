# Вызов Java метода с лямбдой

Допустим у нас есть следующий Java код:

```java
interface Publisher {
    @FunctionalInterface
    interface Callback {
        void onEvent(@NotNull String event);
    }

    fun subscribe(Callback callback);
}
```

Понимая, что в Java у нас нет функциональных типов, можно передавать туда просто объект который реализует данный
интерфейс:

```kotlin
class Listener : Publisher.Callback {
    override fun onEvent(event: String) {
        println(event)
    }
}

fun doSome(publisher: Publisher) {
    val listener = Listener()
    publisher.subscribe(listener)
}
```

Или немного проще:

```kotlin
fun doSome(publisher: Publisher) {
    val listener = object : Publisher.Callback {
        override fun onEvent(event: String) {
            println(event)
        }
    }
    publisher.subscribe(listener)
}
```

Однако было бы странно, если бы у Kotlin не было интеграции с SAM интерфейсами из Java.
Для решения данной проблемы Kotlin просто интерпретирует такие интерфейсы как `fun interface`.

```kotlin
fun doSome(publisher: Publisher) {
    publisher.subscribe(Publisher.Callback { println(event) })
    // или
    publisher.subscribe { println(event) }
}
```
