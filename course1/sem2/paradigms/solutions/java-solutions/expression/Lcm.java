package expression;

import java.math.BigInteger;

public class Lcm extends BinaryOperator {
    public Lcm(BaseExpression a, BaseExpression b) {
        super(a, b);
    }

    @Override
    protected String operator() {
        return " lcm ";
    }

    @Override
    protected int operate(int a, int b) {
        if (b == 0) {
            return 0;
        } else {
            return BigInteger.valueOf(a).multiply(BigInteger.valueOf(b)).divide(BigInteger.valueOf(a).gcd(BigInteger.valueOf(b))).intValue();
        }
    }

    @Override
    protected double operate(double a, double b) {
        return 0;
    }
}
