package expression.generic.parser.expressions;

import java.util.Objects;

public class Variable<T> implements GenericExpression<T> {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    public T evaluate(T x, T y, T z) {
        return switch (name) {
            case "x" -> x;
            case "y" -> y;
            case "z" -> z;
            default -> throw new IllegalArgumentException("Unknown variable: " + name);
        };
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Variable<?> variable = (Variable<?>) o;
        return Objects.equals(name, variable.name);
    }

    public int hashCode() {
        return Objects.hash(name);
    }
}
