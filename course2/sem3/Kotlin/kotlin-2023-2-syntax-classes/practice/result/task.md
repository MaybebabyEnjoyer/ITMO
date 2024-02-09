# IntResult

Требуется реализовать класс IntResult

`IntResult.Ok` - содержит значение вычислений
`IntResult.Error` - содержит текст ошибки

Так же требуется реализовать следующие методы для работы с результатом.

1. `result.getOrDefault(10)`
2. `result.getOrNull()`
3. `result.getStrict()`

И следующие функции:

1. `val result = safeRun { unsafeCall() }`

Если нет значения при вызове метода `getStrict`, нужно выбросить кастомное
исключение `NoResultProvided : NoSuchElementException` с сообщением которое содержится в объекте с ошибкой  

