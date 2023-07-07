package expression.generic.parser.type;

public class FloatType implements TypeInterface<Float> {
    @Override
    public Float add(Float a, Float b) {
        return a + b;
    }

    @Override
    public Float abs(Float a) {
        return Math.abs(a);
    }

    @Override
    public Float square(Float a) {
        return a * a;
    }

    @Override
    public Float subtract(Float a, Float b) {
        return a - b;
    }

    @Override
    public Float multiply(Float a, Float b) {
        return a * b;
    }

    @Override
    public Float divide(Float a, Float b) {
        return a / b;
    }

    @Override
    public Float mod(Float a, Float b) {
        return a % b;
    }

    @Override
    public Float negate(Float a) {
        return -a;
    }

    @Override
    public Float count(Float a) {
        return (float) Integer.bitCount(Float.floatToIntBits(a));
    }

    @Override
    public Float min(Float a, Float b) {
        return Math.min(a, b);
    }

    @Override
    public Float max(Float a, Float b) {
        return Math.max(a, b);
    }

    @Override
    public Float parse(String s) {
        return Float.parseFloat(s);
    }

    @Override
    public Float valueOf(int x) {
        return (float) x;
    }
}
