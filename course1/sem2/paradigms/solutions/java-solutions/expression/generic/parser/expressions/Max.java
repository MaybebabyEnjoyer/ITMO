package expression.generic.parser.expressions;

import expression.generic.parser.type.TypeInterface;
public class Max<T> extends AbstractGenericBinary<T> {
    public Max(GenericExpression<T> a, GenericExpression<T> b, TypeInterface<T> type) {
        super(a, b, type);
    }
    protected String operator() {
        return "max";
    }
    protected T operate(T a, T b) {
        return type.max(a, b);
    }
}
