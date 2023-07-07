package expression.generic.parser.type;
import expression.exceptions.DivideByZeroException;
public class ShortType implements TypeInterface<Short> {
    @Override
    public Short add(Short a, Short b) {
        return (short) (a + b);
    }
    @Override
    public Short abs(Short a) {
        return (short) Math.abs(a);
    }
    @Override
    public Short square(Short a) {
        return (short) (a*a);
    }
    @Override
    public Short subtract(Short a, Short b) {
        return (short) (a - b);
    }
    @Override
    public Short multiply(Short a, Short b) {
        return (short) (a * b);
    }
    @Override
    public Short divide(Short a, Short b) {
        if (b == 0)
            throw new DivideByZeroException(Short.toString(b));
        return (short) (a / b);
    }
    @Override
    public Short negate(Short a) {
        return (short) (-a);
    }

    @Override
    public Short count(Short a) {
        int converted = a & 0xffff;
        return (short) Integer.bitCount(converted);
    }
    @Override
    public Short min(Short a, Short b) {
        return (short) Math.min(a, b);
    }
    @Override
    public Short max(Short a, Short b) {
        return (short) Math.max(a, b);
    }
    @Override
    public Short parse(String s) {
        return Short.parseShort(s);
    }
    @Override
    public Short valueOf(int x) {
        return (short) x;
    }
    @Override
    public Short mod(Short a, Short b) {
        if (b == 0)
            throw new DivideByZeroException(Short.toString(b));
        return (short) (a % b);
    }
}
