# Observable Value

Вам нужно реализовать простую версию "наблюдаемого" значения.

* Интерфейс `Value` со свойством `value` для хранения значений и возможностью подписаться на изменения этого значения при помощи метода `observe`.

* Объект, возвращаемый из метода `observe`, который будет позволять отписаться от получения изменений из `Value`.

* Класс `MutableValue`, реализующий интерфейс `Value` с изменяемым свойством `value` и начальным значением `value`.

Пример использования

```kotlin
val mutableValue = MutableValue(initial = "initial")

val cancellation = mutableValue.observe { println(it) }  // > initial
mutableValue.value = "updated"                           // > updated

val newValue: Value = mutableValue

cancellation.cancel()
mutableValue.value = "final"                             // >
```
