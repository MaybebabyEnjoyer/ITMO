package expression;

import java.util.Objects;

public class Variable implements BaseExpression {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public int evaluate(int x) {
        return x;
    }

    @Override
    public double evaluate(double x) {
        return x;
    }

    @Override
    public String toString() {
        return name;
    }
    public String toMiniString(){
        return this.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Variable v = (Variable) object;
        return Objects.equals(name, v.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        switch (name) {
            case "x" -> {return x;}
            case "y" -> {return y;}
            case "z" -> {return z;}
            default -> throw new IllegalArgumentException("Unexpected name " + name);
        }
    }
}
