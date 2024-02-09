# Scope functions

Ознакомитесь с документацией на страничке: https://kotlinlang.org/docs/scope-functions.html

Советы по использованию:

* `let` - помогает при работе с nullable типами
  `nullable?.let { notnull -> doSome(notnull) }`

* `apply` - помогает при работе с билдерами
  ```kotlin
  builder.apply {
    withColor("#FFFF")
    withOffset(50)
  }
  ```
  Однако, билдер не должен каждый раз возвращать новый объект (не должен быть имутабельным).

* `run`/`with` - можно использовать для захвата контекста в функциях с двумя ресиверами.
  ```kotlin
  class Context {
      fun Int.toSuperString(): String = let { i ->  "$i:$i" }
  }
  
  fun main() {
    val context = Context()
    context.run { 1.toSuperString() }
    with(context) { 1.toSuperString() }
  }
  ```