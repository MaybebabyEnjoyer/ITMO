package expression.common;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Op<T> {
    public final String name;
    public final T value;

    public Op(final String name, final T value) {
        this.name = name;
        this.value = value;
    }

    public static <T> Op<T> of(final String name, final T f) {
        return new Op<>(name, f);
    }

    public String name() {
        return name;
    }

    public T value() {
        return value;
    }

    @Override
    public String toString() {
        return name + ":" + value;
    }
}
