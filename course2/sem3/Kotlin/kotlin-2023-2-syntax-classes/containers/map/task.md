# Словарь (Ассоциативный массив)

Как и списки с множествами бывают мутабельные и нет

## Как создать массив?

Конструктор класса реализации

```kotlin
import java.util.*

val map: MutableMap<Int, String> = HashMap<Int, String>()
val map: MutableMap<Int, String> = TreeMap<Int, String>()
```

Билдер:

```kotlin
val map: Map<Int, String> = buildMap {
    put(1, "1")
}
```

Вместо литерала:

```kotlin
val map: Map<Int, String> = mapOf(1 to "1", 2 to "2")
val map: MutableMap<Int, String> = mutableMapOf(1 to "1", 2 to "2")
```

Приведение между мутабельными и имутабельными типами:

```kotlin
val map: MutableMap<Int, String> = mapOf(1 to "1", 2 to "2").toMutableMap()
val map: Map<Int, String> = mutableMapOf(1 to "1", 2 to "2").toMap()

val map: Map<Int, String> = mutableMapOf(1 to "1", 2 to "2") // upper cast
```

## Как использовать?

Изучите методы классов самостоятельно.
