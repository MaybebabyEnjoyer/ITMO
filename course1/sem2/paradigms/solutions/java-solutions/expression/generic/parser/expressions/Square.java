package expression.generic.parser.expressions;

import expression.generic.parser.type.TypeInterface;
public class Square<T> extends AbstractGenericUnary<T> {
    public Square(GenericExpression<T> expression, TypeInterface<T> type) {
        super(expression, type);
    }
    protected String operator() {
        return "square";
    }
    protected T operate(T a) {
        return type.square(a);
    }
}
