package expression.exceptions;

import expression.BaseExpression;
import expression.BinaryOperator;

public class CheckedDivide extends BinaryOperator {
    public CheckedDivide(BaseExpression a, BaseExpression b) {
        super(a, b);
    }

    @Override
    protected String operator() {
        return " / ";
    }

    @Override
    protected int operate(int a, int b) {
        if (b == 0) {
            throw new DivideByZeroException(Integer.toString(b));
        }
        if (a == Integer.MIN_VALUE && b == -1) {
            throw new DivideOverflowException(a, b);
        }
        return a / b;
    }

    @Override
    protected double operate(double a, double b) {
        return a / b;
    }
}
