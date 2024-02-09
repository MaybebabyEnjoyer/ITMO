# SAM conversions

В Kotlin в отличие от Java у функий есть свой тип:

```kotlin
fun doSome(callable: () -> Unit) {}

fun main() {
    doSome {
        println("Hello!")
    }
}
```

Но если мы хотим более строгий тип объекта лямбды мы можем использовать `fun interface`

```kotlin
fun interface Callable {
    fun call()
}
```

Такой интерфейс можно создать спомощью следующего синтаксиса:

```kotlin
val callable = Callable { println("Hello!") }
```

А использовать как обычный объект.

```kotlin
fun doSome(callable: Callable) {}

fun main() {
    doSome(callable = Callable { println("Hello!") })
    doSome { println("Hello!") }
}
```

Замечу, что во втором вызове Kotlin смог сам определить тип аргумента на стороне вызова и поэтому мы можем передавать
аргумент как обычную лямбду. 

В остальном - это обычный интерфейс.
