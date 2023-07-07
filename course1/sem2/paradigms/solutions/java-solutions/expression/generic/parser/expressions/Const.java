package expression.generic.parser.expressions;

import java.util.Objects;

public class Const<T> implements GenericExpression<T> {
    private final T value;

    public Const(T value) {
        this.value = value;
    }

    public T evaluate(T x, T y, T z) {
        return value;
    }

    public String toString() {
        return value.toString();
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Const<T> other = (Const<T>) o;
        return value.equals(other.value);
    }

    public int hashCode() {
        return Objects.hash(value, getClass()) * 961;
    }
}
