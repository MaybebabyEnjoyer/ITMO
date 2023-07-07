package expression;

public class Set extends BinaryOperator {
    public Set(BaseExpression a, BaseExpression b) {
        super(a, b);
    }

    @Override
    protected String operator() {
        return " set ";
    }

    @Override
    protected int operate(int a, int b) {
        return a | 1 << b;
    }

    @Override
    protected double operate(double a, double b) {
        return 0;
    }
}
