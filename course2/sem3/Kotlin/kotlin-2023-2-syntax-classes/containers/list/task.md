# Список

Спики бывают изменяемые и не изменяемые. В Kotlin это 2 отдельных типа.

## Как создать?

Конструктор класса реализации

```kotlin
import java.util.*

val list: MutableList<Int> = ArrayList<Int>()
val list: MutableList<Int> = LinkedList<Int>()
```

Фабричная функция `List`/`MutableList`:

```kotlin
val list: List<Int> = List(10) { index -> index }
val list: MutableList<Int> = MutableList(10) { index -> index }
```

Билдер:

```kotlin
val list = buildList<Int> {
    add(1)
    add(2)
    add(3)
}
```

Вместо литерала:

```kotlin
val list: List<Int> = listOf(1, 2, 3)
val list: MutableList<Int> = mutableListOf(1, 2, 3)
```

Приведение между мутабельными и имутабельными типами:

```kotlin
val list: MutableList<Int> = listOf(1, 2, 3).toMutableList()
val list: List<Int> = mutableListOf(1, 2, 3).toList()

val list: List<Int> = mutableListOf(1, 2, 3) // upper cast
```

## Как использовать?

Изучите методы классов самостоятельно.
