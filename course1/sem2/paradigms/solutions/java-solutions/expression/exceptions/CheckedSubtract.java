package expression.exceptions;

import expression.BaseExpression;
import expression.BinaryOperator;

public class CheckedSubtract extends BinaryOperator {
    public CheckedSubtract(BaseExpression a, BaseExpression b) {
        super(a, b);
    }

    protected String operator() {
        return " - ";
    }

    protected int operate(int a, int b) {
        if (a >= 0 && b < 0 && a > Integer.MAX_VALUE + b) {
            throw new SubtractOverflowException(a, b);
        }
        if (a < 0 && b > 0 && a < Integer.MIN_VALUE + b) {
            throw new SubtractOverflowException(a, b);
        }
        return a - b;
    }
    @Override
    protected double operate(double a, double b) {
        return 0;
    }
}
