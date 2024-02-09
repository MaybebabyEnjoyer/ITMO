interface Value<T> {
    var value: T

    fun observe(action: (T) -> Unit): Cancellation
}

fun interface Cancellation {
    fun cancel()
}

class MutableValue<T>(val initial: T) : Value<T> {

    private var observers: MutableSet<(T) -> Unit> = mutableSetOf()
    override var value = initial
        set(value) {
            field = value
            observers.forEach {
                try {
                    it(value)
                } catch (e: Exception) {
                    println("Can't use function: $it on argument $value")
                }
            }
        }

    override fun observe(action: (T) -> Unit): Cancellation {
        try {
            action(value)
        } catch (e: Exception) {
            println("Cant use function: $action on argument $value")
        }
        observers.add(action)
        return Cancellation { observers.remove(action) }
    }
}