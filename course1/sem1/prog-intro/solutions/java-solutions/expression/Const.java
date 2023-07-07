package expression;

import java.util.Objects;

public class Const implements BaseExpression {
    private final double value;
    int type;

    public Const(int value) {
        this.value = value;
        this.type = 1;
    }

    public Const(double value) {
        this.value = value;
        this.type = 2;
    }

    @Override
    public int evaluate(int x) {
        return (int) value;
    }

    @Override
    public double evaluate(double x) {
        return value;
    }

    @Override
    public String toString() {
        switch(type) {
            case 1 -> {return String.valueOf((int) value);}
            case 2 -> {return String.valueOf(value);}
            default -> throw new IllegalArgumentException("IllegalArgumentException");
        }
    }
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Const c = (Const) object;
        return value == c.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return (int) value;
    }
}
