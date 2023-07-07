package expression.exceptions;

import expression.BaseExpression;

public class CheckedLog implements BaseExpression {
    private BaseExpression expression;
    public CheckedLog(BaseExpression expression) {
        this.expression = expression;
    }

    public String toString() {
        return "log10(" + expression.toString() + ")";
    }

    private static int operate(int x) {
       if (x <= 0) {
           throw new LogIllegalArgumentException(Integer.toString(x));
       }
       return Integer.toString(x).length() - 1;
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
