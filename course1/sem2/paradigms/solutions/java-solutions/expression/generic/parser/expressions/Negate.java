package expression.generic.parser.expressions;

import expression.generic.parser.type.TypeInterface;

public class Negate<T> extends AbstractGenericUnary<T> {
    public Negate(GenericExpression<T> expression, TypeInterface<T> type) {
        super(expression, type);
    }
    protected String operator() {
        return "-";
    }
    protected T operate(T a) {
        return type.negate(a);
    }
}
