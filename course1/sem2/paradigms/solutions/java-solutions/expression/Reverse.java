package expression;

public class Reverse implements BaseExpression {
    private final BaseExpression expression;

    public Reverse(BaseExpression expression) {
        this.expression = expression;
    }

    public static BaseExpression getReverse(BaseExpression expression) {
        if (expression instanceof Const) {
            return new Const(Reverse(expression.evaluate(0)));
        }
        return new Reverse(expression);
    }

    public static int Reverse(int x) {
        int result = 0;
        while (x != 0) {
            result = result * 10 + x % 10;
            x /= 10;
        }
        return result;
    }

    @Override
    public double evaluate(double x) {
        return 0;
    }

    @Override
    public int evaluate(int x) {
        return Reverse(expression.evaluate(x));
    }

    public String toString() {
        return "reverse(" + expression.toString() + ")";
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return (Reverse(expression.evaluate(x, y, z)));
    }
}
