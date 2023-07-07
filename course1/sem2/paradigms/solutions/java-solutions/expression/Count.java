package expression;

public class Count implements BaseExpression {
    private final BaseExpression expression;

    public Count(BaseExpression expression) {
        this.expression = expression;
    }

    public static BaseExpression getCount(BaseExpression expression) {
        if (expression instanceof Const) {
            return new Const(Integer.bitCount(expression.evaluate(0)));
        }
        return new Count(expression);
    }

    @Override
    public String toString() {
        return "count(" + expression.toString() + ")";
    }

    @Override
    public int evaluate(int x) {
        return Integer.bitCount(expression.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return Integer.bitCount(expression.evaluate(x, y, z));
    }

    @Override
    public double evaluate(double x) {
        return 0;
    }
}