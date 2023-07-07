package expression;
import java.util.Objects;

public abstract class BinaryOperator implements BaseExpression {
    private final BaseExpression a;
    private final BaseExpression b;

    public BinaryOperator(BaseExpression a, BaseExpression b) {
        this.a = a;
        this.b = b;
    }
    protected abstract String operator();
    protected abstract int operate(int a, int b);
    protected abstract double operate(double a, double b);

    @Override
    public int evaluate(int x) {
        return operate(a.evaluate(x), b.evaluate(x));
    }

    @Override
    public double evaluate(double x) {
        return operate(a.evaluate(x), b.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return operate(a.evaluate(x, y, z), b.evaluate(x, y, z));
    }

    @Override
    public String toString() {
        return "(" + a + operator() + b + ")";
    }


    @Override
    public boolean equals(Object object) {
        if (object == null || object.getClass() != getClass()) {
            return false;
        }
        BinaryOperator binaryOperator = (BinaryOperator) object;
        return this.a.equals(binaryOperator.a) && b.equals(binaryOperator.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, getClass(), b) * 961;
    }
}
