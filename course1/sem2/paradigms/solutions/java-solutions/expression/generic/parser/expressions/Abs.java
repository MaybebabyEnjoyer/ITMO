package expression.generic.parser.expressions;

import expression.generic.parser.type.TypeInterface;
public class Abs<T> extends AbstractGenericUnary<T> {
    public Abs(GenericExpression<T> expression, TypeInterface<T> type) {
        super(expression, type);
    }
    protected String operator() {
        return "abs";
    }
    protected T operate(T a) {
        return type.abs(a);
    }
}
