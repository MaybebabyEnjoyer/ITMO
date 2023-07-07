package expression.generic.parser.type;

import expression.exceptions.DivideByZeroException;

import java.math.BigInteger;

public class BigIntegerType implements TypeInterface<BigInteger> {

    @Override
    public BigInteger add(BigInteger a, BigInteger b) {
        return a.add(b);
    }

    @Override
    public BigInteger subtract(BigInteger a, BigInteger b) {
        return a.subtract(b);
    }

    @Override
    public BigInteger multiply(BigInteger a, BigInteger b) {
        return a.multiply(b);
    }

    @Override
    public BigInteger divide(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            throw new DivideByZeroException(b.toString());
        }
        return a.divide(b);
    }

    @Override
    public BigInteger negate(BigInteger a) {
        return a.negate();
    }

    @Override
    public BigInteger count(BigInteger a) {
        if (a.signum() == -1) {
            BigInteger b = a.negate().subtract(BigInteger.ONE);
            return new BigInteger(String.valueOf(b.bitLength()  - b.bitCount()));
        }
        return new BigInteger(String.valueOf(a.bitCount()));
    }

    @Override
    public BigInteger min(BigInteger a, BigInteger b) {
        return a.min(b);
    }

    @Override
    public BigInteger max(BigInteger a, BigInteger b) {
        return a.max(b);
    }

    @Override
    public BigInteger parse(String s) {
        return new BigInteger(s);
    }

    @Override
    public BigInteger valueOf(int x) {
        return BigInteger.valueOf(x);
    }

    @Override
    public BigInteger abs(BigInteger a) {
        return a.abs();
    }

    @Override
    public BigInteger square(BigInteger a) {
        return a.multiply(a);
    }

    @Override
    public BigInteger mod(BigInteger a, BigInteger b) {
        if (b.compareTo(BigInteger.ZERO) <= 0) {
            throw new DivideByZeroException("Division by zero");
        }
        return a.mod(b);
    }
}
