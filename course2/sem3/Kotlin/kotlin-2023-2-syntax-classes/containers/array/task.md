# Массив

Это базовый контейнер.

Он не расширяемый в плане новых ячеек, но вот значения в них меняются

## Как создать массив?

Конструктор:

```kotlin
val array: Array<String> = Array<String>(10) { index -> "$index" }

val array: IntArray = IntArray(10)
val array: IntArray = IntArray(10) { index -> index }
```

Вместо литерала в котлине есть следующее соглашение:
```kotlin
val array: Array<String> = arrayOf<String>("1", "2", "3")
val array: IntArray = intArrayOf(1, 2, 3)
```

## Как использовать?

Изучите методы класса самостоятельно. 