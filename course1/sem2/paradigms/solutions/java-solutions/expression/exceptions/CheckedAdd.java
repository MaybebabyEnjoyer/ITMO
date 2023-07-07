package expression.exceptions;

import expression.BaseExpression;
import expression.BinaryOperator;

public class CheckedAdd extends BinaryOperator {
    public CheckedAdd(BaseExpression a, BaseExpression b) {
        super(a, b);
    }

    @Override
    protected String operator() {
        return " + ";
    }

    @Override
    protected int operate(int a, int b) {
        if (a > 0 && b > 0 && a > Integer.MAX_VALUE - b) {
            throw new AddOverflowException(a, b);
        }
        if (a < 0 && b < 0 && a < Integer.MIN_VALUE - b) {
            throw new AddOverflowException(a, b);
        }
        return a + b;
    }

    @Override
    protected double operate(double a, double b) {
        return 0;
    }
}
