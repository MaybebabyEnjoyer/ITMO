package expression;

public class Add extends BinaryOperator {

    public Add(BaseExpression a, BaseExpression b) {
        super(a, b);
    }

    @Override
    protected String operator() {
        return " + ";
    }

    @Override
    protected double operate(double a, double b) {
        return a + b;
    }

    @Override
    protected int operate(int a, int b) {
        return a + b;
    }
}
