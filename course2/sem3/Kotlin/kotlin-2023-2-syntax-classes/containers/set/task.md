# Множество

Множества бывают изменяемые и не изменяемые, как и списки.

## Как создать?

Конструктор класса реализации

```kotlin
import java.util.*

val set: MutableSet<Int> = HashSet<Int>()
val set: MutableSet<Int> = TreeSet<Int>()
```

Билдер:

```kotlin
val set: Set<Int> = buildSet {
    add(1)
    add(2)
    add(3)
}
```

Вместо литерала:

```kotlin
val set: Set<Int> = setOf(1, 2, 3)
val set: MutableSet<Int> = mutableSetOf(1, 2, 3)
```

Приведение между мутабельными и имутабельными типами:

```kotlin
val set: MutableSet<Int> = setOf(1, 2, 3).toMutableSet()
val set: Set<Int> = mutableSetOf(1, 2, 3).toSet()

val set: Set<Int> = mutableSetOf(1, 2, 3) // upper cast
```

## Как использовать?

Изучите методы классов самостоятельно.
