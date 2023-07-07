package expression;

public class UnaryMinus implements BaseExpression {
    private final BaseExpression expression;

    public UnaryMinus(BaseExpression expression) {
        this.expression = expression;
    }

    public static BaseExpression getUnaryMinus(BaseExpression expression) {
        if (expression instanceof Const) {
            return new Const(-(expression).evaluate(0));
        }
        return new UnaryMinus(expression);
    }


    @Override
    public String toString() {
        return "-(" + expression.toString() + ")";
    }


    @Override
    public int evaluate(int x) {
        return (-expression.evaluate(x));
    }


    @Override
    public int evaluate(int x, int y, int z) {
        return (-expression.evaluate(x, y, z));
    }

    @Override
    public double evaluate(double x) {
        return (-expression.evaluate(x));
    }
}
