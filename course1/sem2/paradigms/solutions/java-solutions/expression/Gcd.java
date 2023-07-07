package expression;

import java.math.BigInteger;

public class Gcd extends BinaryOperator {
    public Gcd(BaseExpression a, BaseExpression b) {
        super(a, b);
    }

    @Override
    protected String operator() {
        return " gcd ";
    }

    @Override
    protected int operate(int a, int b) {
        return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).intValue();
    }

    @Override
    protected double operate(double a, double b) {
        return 0;
    }
}
