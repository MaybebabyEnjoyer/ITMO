package expression.generic.parser.type;

import expression.exceptions.DivideByZeroException;

public class PoModuluType implements TypeInterface<Integer> {

    private final int MOD;

    public PoModuluType(int mod) {
        this.MOD = mod;
    }
    private Integer mikromodule(Integer a) {
        if (a < 0) {
            return MOD + a % MOD;
        }
        return a % MOD;
    }

    private static int modInverse(int a, int m) {
        
        int m0 = m;
        int y = 0, x = 1;

        if (m == 1)
            return 0;

        while (a > 1) {
            int q = a / m;
            int t = m;
            m = a % m;
            a = t;
            t = y;
            
            y = x - q * y;
            x = t;
        }
        if (x < 0)
            x += m0;

        return x;
    }

    @Override
    public Integer add(Integer a, Integer b) {
        return mikromodule(a + b);
    }

    @Override
    public Integer abs(Integer a) {
        return mikromodule(Math.abs((a)));
    }

    @Override
    public Integer square(Integer a) {
        return mikromodule(a*a);
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        return mikromodule(a - b);
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        return mikromodule(a * b);
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        if (b == 0) throw new DivideByZeroException(Integer.toString(b));
        return mikromodule(a * modInverse(b, MOD));
    }

    @Override
    public Integer negate(Integer a) {
        return mikromodule(-a);
    }

    @Override
    public Integer count(Integer a) {
        return Integer.bitCount(mikromodule(a));
    }

    @Override
    public Integer min(Integer a, Integer b) {
        return mikromodule(Math.min(a, b));
    }

    @Override
    public Integer max(Integer a, Integer b) {
        return mikromodule(Math.max(a, b));
    }

    @Override
    public Integer parse(String s) {
        return mikromodule(Integer.parseInt(s));
    }

    @Override
    public Integer valueOf(int x) {
        return mikromodule(x);
    }

    @Override
    public Integer mod(Integer a, Integer b) {
        if (mikromodule(b) == 0)
            throw new DivideByZeroException(Integer.toString(b));
        return mikromodule(a % b);
    }
}
