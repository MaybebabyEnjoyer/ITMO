package expression;

public class Clear extends BinaryOperator {
    public Clear(BaseExpression a, BaseExpression b) {
        super(a, b);
    }

    @Override
    protected String operator() {
        return " clear ";
    }

    @Override
    protected int operate(int a, int b) {
        return a & ~(1 << b);
    }

    @Override
    protected double operate(double a, double b) {
        return 0;
    }
}
