package expression.exceptions;

import expression.BaseExpression;

public class CheckedPow implements BaseExpression {

    static int expBySquare(int x, int y) {
        if (y == 0) {
            return 1;
        }
        int t = expBySquare(x, y / 2);
        if (y % 2 == 0) {
            return t * t;
        } else {
            return t * t * x;
        }
    }

    private BaseExpression expression;

    public CheckedPow(BaseExpression expression) {
        this.expression = expression;
    }

    public String toString() {
        return "pow10(" + expression.toString() + ")";
    }

    private static int operate(int x) {
        if (x < 0 || x >= 10) {
            throw new PowNegativeException(x);
        }
        return expBySquare(10, x);
    }
    @Override
    public int evaluate(int x) {
        return operate(expression.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return operate(expression.evaluate(x, y, z));
    }

    @Override
    public double evaluate(double x) {
        return 0;
    }
}
