package expression.generic.parser.expressions;

import expression.generic.parser.type.TypeInterface;
public abstract class AbstractGenericUnary<T> implements GenericExpression<T>{
    protected final TypeInterface<T> type;
    protected GenericExpression<T> expression;
    public AbstractGenericUnary(GenericExpression<T> expression, TypeInterface<T> type) {
        this.expression = expression;
        this.type = type;
    }
    protected abstract String operator();
    protected abstract T operate(T a);

    @Override
    public T evaluate(T x, T y, T z) {
        return operate(expression.evaluate(x, y, z));
    }

    public String toString() {
        return operator() + "(" + expression + ")";
    }
}
