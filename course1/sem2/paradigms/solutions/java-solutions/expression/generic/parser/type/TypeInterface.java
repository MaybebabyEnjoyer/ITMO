package expression.generic.parser.type;

public interface TypeInterface<T> {
    T add(T a, T b);
    T abs(T a);
    T square(T a);
    T subtract(T a, T b);
    T multiply(T a, T b);
    T divide(T a, T b);
    T negate(T a);
    T count(T a);
    T min(T a, T b);
    T max(T a, T b);
    T parse(String s);
    T valueOf(int x);
    T mod(T a, T b);
}
