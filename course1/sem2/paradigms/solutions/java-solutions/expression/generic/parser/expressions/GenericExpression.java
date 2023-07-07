package expression.generic.parser.expressions;

public interface GenericExpression<T> {
    T evaluate(T x, T y, T z);
}
