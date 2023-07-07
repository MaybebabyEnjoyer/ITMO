package expression.exceptions;

import expression.BaseExpression;
import expression.BinaryOperator;
public class CheckedMultiply extends BinaryOperator {
    public CheckedMultiply(BaseExpression a, BaseExpression b) {
        super(a, b);
    }

    @Override
    protected String operator() {
        return " * ";
    }

    @Override
    protected int operate(int a, int b) {
        if (a != 0 && b != 0 && (a > 0 && b > 0 && a > Integer.MAX_VALUE / b || a < 0 && b < 0 && a <
                Integer.MAX_VALUE / b || a > 0 && b < 0 &&
                b < Integer.MIN_VALUE / a || a < 0 && b > 0 && a < Integer.MIN_VALUE / b)) {
            throw new MultiplyOverflowException(a, b);
        }
        return a * b;
    }

    @Override
    protected double operate(double a, double b) {
        return 0;
    }
}
