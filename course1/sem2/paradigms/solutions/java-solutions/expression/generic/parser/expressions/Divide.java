package expression.generic.parser.expressions;

import expression.generic.parser.type.TypeInterface;

public class Divide<T> extends AbstractGenericBinary<T> {
    public Divide(GenericExpression<T> a, GenericExpression<T> b, TypeInterface<T> type) {
        super(a, b, type);
    }
    protected String operator() {
        return "/";
    }
    protected T operate(T a, T b) {
        return type.divide(a, b);
    }
}
