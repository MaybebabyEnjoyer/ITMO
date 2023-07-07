package expression.generic.parser.expressions;

import expression.generic.parser.type.TypeInterface;
public class Mod<T> extends AbstractGenericBinary<T> {
    public Mod(GenericExpression<T> a, GenericExpression<T> b, TypeInterface<T> type) {
        super(a, b, type);
    }
    protected String operator() {
        return "mod";
    }
    protected T operate(T a, T b) {
        return type.mod(a, b);
    }
}
