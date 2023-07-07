package expression.generic.parser.expressions;

import expression.generic.parser.type.TypeInterface;
public class Min<T> extends AbstractGenericBinary<T> {
    public Min(GenericExpression<T> a, GenericExpression<T> b, TypeInterface<T> type) {
        super(a, b, type);
    }
    protected String operator() {
        return "min";
    }
    protected T operate(T a, T b) {
        return type.min(a, b);
    }
}
