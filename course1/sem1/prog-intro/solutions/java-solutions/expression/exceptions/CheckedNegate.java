package expression.exceptions;

import expression.BaseExpression;

public class CheckedNegate implements BaseExpression {
    private BaseExpression expression;

    public CheckedNegate(BaseExpression expression) {
        this.expression = expression;
    }

    public String toString() {
        return "-(" + expression.toString() + ")";
    }

    private static int operate(int x) {
        if (x == Integer.MIN_VALUE) {
            throw new UnaryMinusOverflowException(x);
        }
        return -x;
    }

    public int evaluate(int x) {
        return operate(expression.evaluate(x));
    }

    public int evaluate(int x, int y, int z) {
        return operate(expression.evaluate(x, y, z));
    }

    @Override
    public double evaluate(double x) {
        return 0;
    }
}
