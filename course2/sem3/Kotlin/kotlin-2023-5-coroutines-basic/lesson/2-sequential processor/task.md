## Sequential processor

Реализуйте метод `process` в классе `SequentialProcessor` так,
чтобы он вызывал лямбда-функцию `handler`от строки `argument`.

Важно, ваш класс `SquentialProcessor` должен запускать handler только в одном рабочем потоке.
Таким образом, ваш `TaskProcessor` должен гарантировать, что запуски `handler` не будут параллельными.
