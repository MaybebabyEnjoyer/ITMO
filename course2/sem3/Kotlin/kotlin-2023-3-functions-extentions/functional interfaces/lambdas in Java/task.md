# Лямбды в Java

Давайте вспомним как в Java устроены лямбды.

```java
@FunctionalInterface
interface Callable {
    fun call();
}

class Run {
    private static void doSome(Callable callable) {
    }

    public static void main(String[] args) {
        doSome(() -> System.out.println("Hello!"));
    }
}
```

Для того, чтобы можно было передать в метод лямбду, он должен принимать аргумент с некоторым типом, который подчиняется следующим правилам:
* Это интерфейс
* У него ровно 1 абстрактный (не дефолтный) метод

Так же данный интерфейс можно пометить аннотацией `@FunctionalInterface`, но это не обязательно. 