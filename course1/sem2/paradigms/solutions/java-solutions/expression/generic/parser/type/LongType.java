package expression.generic.parser.type;
import expression.exceptions.DivideByZeroException;

public class LongType implements TypeInterface<Long> {
    @Override
    public Long add(Long a, Long b) {
        return a + b;
    }
    @Override
    public Long abs(Long a) {
        return Math.abs(a);
    }
    @Override
    public Long square(Long a) {
        return a*a;
    }
    @Override
    public Long subtract(Long a, Long b) {
        return a - b;
    }
    @Override
    public Long multiply(Long a, Long b) {
        return a * b;
    }
    @Override
    public Long divide(Long a, Long b) {
        if (b == 0)
            throw new DivideByZeroException(Long.toString(b));
        return a / b;
    }
    @Override
    public Long negate(Long a) {
        return -a;
    }
    @Override
    public Long count(Long a) {
        return (long) Long.bitCount(a);
    }
    @Override
    public Long min(Long a, Long b) {
        return Math.min(a, b);
    }
    @Override
    public Long max(Long a, Long b) {
        return Math.max(a, b);
    }
    @Override
    public Long parse(String s) {
        return Long.parseLong(s);
    }
    @Override
    public Long valueOf(int x) {
        return (long) x;
    }
    @Override
    public Long mod(Long a, Long b) {
        if (b == 0)
            throw new DivideByZeroException(Long.toString(b));
        return a % b;
    }
}
