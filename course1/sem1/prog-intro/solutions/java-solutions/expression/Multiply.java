package expression;

public class Multiply extends BinaryOperator {

    public Multiply(BaseExpression a, BaseExpression b) {
        super(a, b);
    }

    @Override
    protected String operator() {
        return " * ";
    }

    @Override
    protected int operate(int a, int b) {
        return a * b;
    }

    @Override
    protected double operate(double a, double b) {
        return a * b;
    }
}
