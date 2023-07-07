package expression.generic.parser.expressions;

import expression.generic.parser.type.TypeInterface;

public class Count<T> extends AbstractGenericUnary<T> {
    public Count(GenericExpression<T> expression, TypeInterface<T> type) {
        super(expression, type);
    }
    protected String operator() {
        return "count";
    }
    protected T operate(T a) {
        return type.count(a);
    }
}
