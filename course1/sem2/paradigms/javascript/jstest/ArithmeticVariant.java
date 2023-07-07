package jstest;

import jstest.expression.BaseVariant;
import jstest.expression.Expr;

/**
 * Basic arithmetics.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ArithmeticVariant extends BaseVariant {
    public final Expr vx = variable("x", 0);
    public final Expr vy = variable("y", 1);
    public final Expr vz = variable("z", 2);

    public ArithmeticVariant() {
        //noinspection Convert2MethodRef
        infix("+", 100, (a, b) -> a + b);
        infix("-", 100, (a, b) -> a - b);
        infix("*", 200, (a, b) -> a * b);
        infix("/", 200, (a, b) -> a / b);
        unary("negate", a -> -a);

        tests(
                new int[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 1, 1}, {1, 2, 1}, {1, 1, 1}, {1, 2, 1}, {1, 1, 1}, {1, 1, 10}, {4, 1, 1}, {9, 28, 28}, {5, 5, 5}, {5, 2, 21}},
                c(10),
                vx,
                vy,
                vz,
                f("negate", vy),
                f("+", vx, c(2)),
                f("-", c(3), vy),
                f("*", c(4), vz),
                f("/", c(5), vz),
                f("/", f("negate", vx), c(2)),
                f("/", vx, f("*", vy, vz)),
                f("+", f("+", f("*", vx, vx), f("*", vy, vy)), f("*", vz, vz)),
                f("-", f("+", f("*", vx, vx), f("*", c(5), f("*", vz, f("*", vz, vz)))), f("*", vy, c(8)))
        );
    }
}
