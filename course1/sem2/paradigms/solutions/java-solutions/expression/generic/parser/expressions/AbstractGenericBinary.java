package expression.generic.parser.expressions;

import expression.generic.parser.type.TypeInterface;

import java.util.Objects;

public abstract class AbstractGenericBinary<T> implements GenericExpression<T> {
    private final GenericExpression<T> a, b;

    protected final TypeInterface<T> type;

    public AbstractGenericBinary(GenericExpression<T> a, GenericExpression<T> b, TypeInterface<T> type) {
        this.a = a;
        this.b = b;
        this.type = type;
    }
    protected abstract String operator();
    protected abstract T operate(T a, T b);

    @Override
    public T evaluate(T x, T y, T z) {
        return operate(a.evaluate(x, y, z), b.evaluate(x, y, z));
    }

    public String toString() {
        return a + operator() + b;
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractGenericBinary<T> other = (AbstractGenericBinary<T>) o;
        return a.equals(other.a) && b.equals(other.b);
    }

    public int hashCode() {
        return Objects.hash(a, getClass(), b) * 961;
    }

}
