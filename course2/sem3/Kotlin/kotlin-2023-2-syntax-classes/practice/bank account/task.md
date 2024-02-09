# Банковский аккаунт

Требуется реализовать простой класс хранящий информацию о банковсом аккаунте.

Публичный интерфейс:

```kotlin
val account = BankAccount(amount = 100)
println(account.balance) // 100

account.deposit(100)
println(account.balance) // 200

account.withdraw(150)
println(account.balance) // 50
```

Так же требуется при изменении баланса вызывать функцию `logTransaction` для логирования.
