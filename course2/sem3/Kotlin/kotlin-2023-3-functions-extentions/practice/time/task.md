# Time

Вам дан класс `Time`, но работать с ним не так уж и удобно.

Реализуйте внутри файла `TimeExt.kt` утилитарные функции для работы с классом `Time`.

* Extension properties для конвертации примитивных `Int` и `Long` в время, в зависимости от единиц измерения.

```kotlin
val time: Time = 10.milliseconds
val time: Time = 10.seconds
val time: Time = 10.minutes
val time: Time = 10.hours
```

Операторы для математических операций над временем
* Сложения `Time` с `Time`
* Вычитания `Time` из `Time`
* Умножения `Time` на `Int`

```kotlin
val lhs = 10.seconds
val rhs = 10.seconds

lhs + rhs shouldBe 20.seconds
lhs - rhs shouldBe  0.seconds
lhs * 5   shouldBe 50.seconds
```
