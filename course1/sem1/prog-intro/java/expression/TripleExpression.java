package expression;

import base.Pair;
import base.TestCounter;
import expression.common.ExpressionKind;

import java.util.List;

/**
 * Three-argument arithmetic expression over integers.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@FunctionalInterface
@SuppressWarnings("ClassReferencesSubclass")
public interface TripleExpression extends ToMiniString {
    ExpressionKind<TripleExpression, Integer> KIND = new ExpressionKind<>(
            Expression.TYPE,
            TripleExpression.class,
            List.of(
                    Pair.of("x", new Variable("x")),
                    Pair.of("y", new Variable("y")),
                    Pair.of("z", new Variable("z"))
            ),
            (expr, variables, values) -> expr.evaluate(values.get(0), values.get(1), values.get(2))
    );

    int evaluate(int x, int y, int z);

    private static Const c(final Integer c) {
        return Expression.TYPE.constant(c);
    }

    static ExpressionTester<?, ?> tester(final TestCounter counter) {
        final Variable vx = new Variable("x");
        final Variable vy = new Variable("y");
        final Variable vz = new Variable("z");

        return new ExpressionTester<>(
                counter, KIND, c -> (x, y, z) -> c,
                (op, a, b) -> (x, y, z) -> op.apply(a.evaluate(x, y, z), b.evaluate(x, y, z)),
                Integer::sum, (a, b) -> a - b, (a, b) -> a * b, (a, b) -> a / b
        )
                .basic("10", "10", (x, y, z) -> 10, c(10))
                .basic("x", "x", (x, y, z) -> x, vx)
                .basic("y", "y", (x, y, z) -> y, vy)
                .basic("z", "z", (x, y, z) -> z, vz)
                .basic("(x + 2)", "x + 2", (x, y, z) -> x + 2, new Add(vx, c(2)))
                .basic("(2 - y)", "2 - y", (x, y, z) -> 2 - y, new Subtract(c(2), vy))
                .basic("(3 * z)", "3 * z", (x, y, z) -> 3 * z, new Multiply(c(3), vz))
                .basic("(x / -2)", "x / -2", (x, y, z) -> -x / 2, new Divide(vx, c(-2)))
                .basic("((1 + 2) + 3)", "1 + 2 + 3", (x, y, z) -> 6, new Add(new Add(c(1), c(2)), c(3)))
                .basic("(1 + (2 + 3))", "1 + 2 + 3", (x, y, z) -> 6, new Add(c(1), new Add(c(2), c(3))))
                .basic("((1 - 2) - 3)", "1 - 2 - 3", (x, y, z) -> -4, new Subtract(new Subtract(c(1), c(2)), c(3)))
                .basic("(1 - (2 - 3))", "1 - (2 - 3)", (x, y, z) -> 2, new Subtract(c(1), new Subtract(c(2), c(3))))
                .basic("((1 * 2) * 3)", "1 * 2 * 3", (x, y, z) -> 6, new Multiply(new Multiply(c(1), c(2)), c(3)))
                .basic("((1 * 2) * 3)", "1 * 2 * 3", (x, y, z) -> 6, new Multiply(new Multiply(c(1), c(2)), c(3)))
                .basic("(1 * (2 * 3))", "1 * 2 * 3", (x, y, z) -> 6, new Multiply(c(1), new Multiply(c(2), c(3))))
                .basic("(1 * (2 * 3))", "1 * 2 * 3", (x, y, z) -> 6, new Multiply(c(1), new Multiply(c(2), c(3))))
                .basic("((10 / 2) / 3)", "10 / 2 / 3", (x, y, z) -> 10 / 2 / 3, new Divide(new Divide(c(10), c(2)), c(3)))
                .basic("(10 / (3 / 2))", "10 / (3 / 2)", (x, y, z) -> 10, new Divide(c(10), new Divide(c(3), c(2))))
                .basic("((x * y) + ((z - 1) / 10))", "x * y + (z - 1) / 10", (x, y, z) -> x * y + (z - 1) / 10, new Add(
                        new Multiply(vx, vy),
                        new Divide(new Subtract(vz, c(1)), c(10))
                ))
                .basic("(x + y)", "x + y", (x, y, z) -> x + y, new Add(vx, vy))
                .basic("(x + y)", "x + y", (x, y, z) -> x + y, new Add(vx, vy))
                .basic("(y + x)", "y + x", (x, y, z) -> y + x, new Add(vy, vx))

                .advanced("(1 + 1)", "1 + 1", (x, y, z) -> 1 + 1, new Add(c(1), c(1)))
                .advanced("(y - x)", "y - x", (x, y, z) -> y - x, new Subtract(vy, vx))
                .advanced("(2 * x)", "2 * x", (x, y, z) -> 2 * x, new Multiply(c(2), vx))
                .advanced("(2 / x)", "2 / x", (x, y, z) -> 2 / x, new Divide(c(2), vx))
                .advanced("(z + (1 + 1))", "z + 1 + 1", (x, y, z) -> z + 1 + 1, new Add(vz, new Add(c(1), c(1))))
                .advanced("(2 - (y - x))", "2 - (y - x)", (x, y, z) -> 2 - (y - x), new Subtract(c(2), new Subtract(vy, vx)))
                .advanced("(z * (2 / x))", "z * (2 / x)", (x, y, z) -> z * (2 / x), new Multiply(vz, new Divide(c(2), vx)))
                .advanced("(z / (y - x))", "z / (y - x)", (x, y, z) -> z / (y - x), new Divide(vz, new Subtract(vy, vx)))
                .advanced("((2 * x) + y)", "2 * x + y", (x, y, z) -> 2 * x + y, new Add(new Multiply(c(2), vx), vy))
                .advanced("((y - x) - 2)", "y - x - 2", (x, y, z) -> y - x - 2, new Subtract(new Subtract(vy, vx), c(2)))
                .advanced("((2 / x) * y)", "2 / x * y", (x, y, z) -> 2 / x * y, new Multiply(new Divide(c(2), vx), vy))
                .advanced("((1 + 1) / x)", "(1 + 1) / x", (x, y, z) -> (1 + 1) / x, new Divide(new Add(c(1), c(1)), vx))
                .advanced("(1 + (2 * 3))", "1 + 2 * 3", (x, y, z) -> 7, new Add(c(1), new Multiply(c(2), c(3))))
                .advanced("(1 - (2 * 3))", "1 - 2 * 3", (x, y, z) -> -5, new Subtract(c(1), new Multiply(c(2), c(3))))
                .advanced("(1 + (2 / 3))", "1 + 2 / 3", (x, y, z) -> 1, new Add(c(1), new Divide(c(2), c(3))))
                .advanced("(1 - (2 / 3))", "1 - 2 / 3", (x, y, z) -> 1, new Subtract(c(1), new Divide(c(2), c(3))))
                .advanced("(2 + (z + (1 + 1)))", "2 + z + 1 + 1", (x, y, z) -> 2 + z + 1 + 1, new Add(c(2), new Add(vz, new Add(
                        c(1),
                        c(1)
                ))))
                .advanced("(1 - ((2 * x) + y))", "1 - (2 * x + y)", (x, y, z) -> 1 - (2 * x + y), new Subtract(c(1), new Add(new Multiply(
                        c(2), vx), vy)))
                .advanced("(1 * (z / (y - x)))", "1 * (z / (y - x))", (x, y, z) -> 1 * (z / (y - x)), new Multiply(c(1), new Divide(vz, new Subtract(vy, vx))))
                .advanced("(z / (z + (1 + 1)))", "z / (z + 1 + 1)", (x, y, z) -> z / (z + 1 + 1), new Divide(vz, new Add(vz, new Add(
                        c(1),
                        c(1)
                ))))
                .advanced("((2 * x) + (1 + 1))", "2 * x + 1 + 1", (x, y, z) -> 2 * x + 1 + 1, new Add(new Multiply(c(2), vx), new Add(
                        c(1),
                        c(1)
                )))
                .advanced("((1 + 1) - (1 + 1))", "1 + 1 - (1 + 1)", (x, y, z) -> 1 + 1 - (1 + 1), new Subtract(new Add(
                        c(1),
                        c(1)
                ), new Add(c(1), c(1))))
                .advanced("((y - x) * (2 / x))", "(y - x) * (2 / x)", (x, y, z) -> (y - x) * (2 / x), new Multiply(new Subtract(vy, vx), new Divide(
                        c(2), vx)))
                .advanced("((y - x) / (2 * x))", "(y - x) / (2 * x)", (x, y, z) -> (y - x) / (2 * x), new Divide(new Subtract(vy, vx), new Multiply(
                        c(2), vx)))
                .advanced("(((y - x) - 2) + 1)", "y - x - 2 + 1", (x, y, z) -> y - x - 2 + 1, new Add(new Subtract(new Subtract(vy, vx),
                        c(2)
                ),
                        c(1)
                ))
                .advanced("(((2 * x) + y) - z)", "2 * x + y - z", (x, y, z) -> 2 * x + y - z, new Subtract(new Add(new Multiply(
                        c(2), vx), vy), vz))
                .advanced("(((1 + 1) / x) * 2)", "(1 + 1) / x * 2", (x, y, z) -> (1 + 1) / x * 2, new Multiply(new Divide(new Add(
                        c(1),
                        c(1)
                ), vx), c(2)))
                .advanced("((z / (y - x)) / x)", "z / (y - x) / x", (x, y, z) -> z / (y - x) / x, new Divide(new Divide(vz, new Subtract(vy, vx)), vx));
    }
}
