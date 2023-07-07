package expression;

import base.Pair;
import base.TestCounter;
import expression.common.ExpressionKind;
import expression.common.Type;

import java.util.List;


/**
 * One-argument arithmetic expression over doubles.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@FunctionalInterface
@SuppressWarnings("ClassReferencesSubclass")
public interface DoubleExpression extends ToMiniString {
    Type<Double> TYPE = new Type<>(Double::valueOf, random -> random.getRandom().nextGaussian() * 1000, double.class);
    ExpressionKind<DoubleExpression, Double> KIND = new ExpressionKind<>(
            TYPE,
            DoubleExpression.class,
            List.of(Pair.of("x", new Variable("x"))),
            (expr, variables, values) -> expr.evaluate(values.get(0))
    );

    double evaluate(double x);

    private static Const c(final double v) {
        return TYPE.constant(v);
    }

    static ExpressionTester<?, ?> tester(final TestCounter counter) {
        final Variable vx = new Variable("x");

        //noinspection Convert2MethodRef
        return new ExpressionTester<>(
                counter, KIND, c -> x -> c,
                (op, a, b) -> x -> op.apply(a.evaluate(x), b.evaluate(x)),
                (a, b) -> a + b, (a, b) -> a - b, (a, b) -> a * b, (a, b) -> a / b
        )
                .basic("10.0", "10.0", x -> 10.0, c(10.0))
                .basic("x", "x", x -> x, vx)
                .basic("(x + 2.0)", "x + 2.0", x -> x + 2.0, new Add(vx, c(2.0)))
                .basic("(2.0 - x)", "2.0 - x", x -> 2.0 - x, new Subtract(c(2.0), vx))
                .basic("(3.0 * x)", "3.0 * x", x -> 3.0 * x, new Multiply(c(3.0), vx))
                .basic("(x + x)", "x + x", x -> x + x, new Add(vx, vx))
                .basic("(x / -2.0)", "x / -2.0", x -> -x / 2.0, new Divide(vx, c(-2.0)))
                .basic("(x + x)", "x + x", x -> x + x, new Add(vx, vx))
                .basic("(2.0 + x)", "2.0 + x", x -> 2.0 + x, new Add(c(2.0), vx))
                .basic("(x + 2.0)", "x + 2.0", x -> x + 2.0, new Add(vx, c(2.0)))
                .basic("((1.0 + 2.0) + 3.0)", "1.0 + 2.0 + 3.0", x -> 6.0, new Add(new Add(c(1.0), c(2.0)), c(3.0)))
                .basic("(1.0 + (2.0 * 3.0))", "1.0 + 2.0 * 3.0", x -> 7.0, new Add(c(1.0), new Multiply(c(2.0), c(3.0))))
                .basic("(1.0 - (2.0 * 3.0))", "1.0 - 2.0 * 3.0", x -> -5.0, new Subtract(c(1.0), new Multiply(c(2.0), c(3.0))))
                .basic("(1.0 - (2.0 * 3.0))", "1.0 - 2.0 * 3.0", x -> -5.0, new Subtract(c(1.0), new Multiply(c(2.0), c(3.0))))
                .basic("(1.0 + (2.0 + 3.0))", "1.0 + 2.0 + 3.0", x -> 6.0, new Add(c(1.0), new Add(c(2.0), c(3.0))))
                .basic("((1.0 - 2.0) - 3.0)", "1.0 - 2.0 - 3.0", x -> -4.0, new Subtract(new Subtract(c(1.0), c(2.0)), c(3.0)))
                .basic("(1.0 - (2.0 - 3.0))", "1.0 - (2.0 - 3.0)", x -> 2.0, new Subtract(c(1.0), new Subtract(c(2.0), c(3.0))))
                .basic("((1.0 * 2.0) * 3.0)", "1.0 * 2.0 * 3.0", x -> 6.0, new Multiply(new Multiply(c(1.0), c(2.0)), c(3.0)))
                .basic("(1.0 * (2.0 * 3.0))", "1.0 * 2.0 * 3.0", x -> 6.0, new Multiply(c(1.0), new Multiply(c(2.0), c(3.0))))
                .basic("((10.0 / 2.0) / 3.0)", "10.0 / 2.0 / 3.0", x -> 5.0 / 3.0, new Divide(new Divide(c(10.0), c(2.0)), c(3.0)))
                .basic("(10.0 / (3.0 / 2.0))", "10.0 / (3.0 / 2.0)", x -> 20.0 / 3, new Divide(c(10.0), new Divide(c(3.0), c(2.0))))
                .basic("((x * x) + ((x - 1.0) / 10.0))", "x * x + (x - 1.0) / 10.0", x -> x * x + (x - 1.0) / 10.0, new Add(
                        new Multiply(vx, vx),
                        new Divide(new Subtract(vx, c(1.0)), c(10.0))
                ))
                .basic("(x * -1000000.0)", "x * -1000000.0", x -> x * -1_000_000, new Multiply(vx, c(-1_000_000)))
                .basic("(x * -1.0E12)", "x * -1.0E12", x -> x * -1_000_000_000_000.0, new Multiply(vx, c(-1_000_000_000_000.0)))
                .basic("(10.0 / x)", "10.0 / x", x -> 10.0 / x, new Divide(c(10.0), vx))
                .basic("(x / x)", "x / x", x -> x / x, new Divide(vx, vx))

                .advanced("(x + x)", "x + x", x -> x + x, new Add(vx, vx))
                .advanced("(x - x)", "x - x", x -> x - x, new Subtract(vx, vx))
                .advanced("(1.1 * x)", "1.1 * x", x -> 1.1 * x, new Multiply(c(1.1), vx))
                .advanced("(1.1 / 2.1)", "1.1 / 2.1", x -> 1.1 / 2.1, new Divide(c(1.1), c(2.1)))
                .advanced("(1.1 + (1.1 / 2.1))", "1.1 + 1.1 / 2.1", x -> 1.1 + 1.1 / 2.1, new Add(c(1.1), new Divide(c(1.1),
                        c(2.1)
                )))
                .advanced("(2.1 - (1.1 * x))", "2.1 - 1.1 * x", x -> 2.1 - 1.1 * x, new Subtract(c(2.1), new Multiply(c(1.1), vx)))
                .advanced("(2.1 * (x + x))", "2.1 * (x + x)", x -> 2.1 * (x + x), new Multiply(c(2.1), new Add(vx, vx)))
                .advanced("(x / (x - x))", "x / (x - x)", x -> x / (x - x), new Divide(vx, new Subtract(vx, vx)))
                .advanced("((x - x) + 1.1)", "x - x + 1.1", x -> x - x + 1.1, new Add(new Subtract(vx, vx), c(1.1)))
                .advanced("((1.1 / 2.1) - x)", "1.1 / 2.1 - x", x -> 1.1 / 2.1 - x, new Subtract(new Divide(c(1.1),
                        c(2.1)
                ), vx))
                .advanced("((1.1 / 2.1) * x)", "1.1 / 2.1 * x", x -> 1.1 / 2.1 * x, new Multiply(new Divide(c(1.1),
                        c(2.1)
                ), vx))
                .advanced("((x - x) / 2.1)", "(x - x) / 2.1", x -> (x - x) / 2.1, new Divide(new Subtract(vx, vx),
                        c(2.1)
                ))
                .advanced("(x + ((1.1 / 2.1) * x))", "x + 1.1 / 2.1 * x", x -> x + 1.1 / 2.1 * x, new Add(vx, new Multiply(new Divide(
                        c(1.1), c(2.1)), vx)))
                .advanced("(2.1 - (2.1 - (1.1 * x)))", "2.1 - (2.1 - 1.1 * x)", x -> 2.1 - (2.1 - 1.1 * x), new Subtract(
                        c(2.1), new Subtract(c(2.1), new Multiply(
                        c(1.1), vx))))
                .advanced("(1.1 * ((x - x) + 1.1))", "1.1 * (x - x + 1.1)", x -> 1.1 * (x - x + 1.1), new Multiply(c(1.1), new Add(new Subtract(vx, vx),
                        c(1.1)
                )))
                .advanced("(x / (2.1 - (1.1 * x)))", "x / (2.1 - 1.1 * x)", x -> x / (2.1 - 1.1 * x), new Divide(vx, new Subtract(
                        c(2.1), new Multiply(
                        c(1.1), vx))))
                .advanced("((1.1 * x) + (1.1 / 2.1))", "1.1 * x + 1.1 / 2.1", x -> 1.1 * x + 1.1 / 2.1, new Add(new Multiply(
                        c(1.1), vx), new Divide(c(1.1), c(2.1))))
                .advanced("((x + x) - (1.1 * x))", "x + x - 1.1 * x", x -> x + x - 1.1 * x, new Subtract(new Add(vx, vx), new Multiply(
                        c(1.1), vx)))
                .advanced("((1.1 * x) * (1.1 / 2.1))", "1.1 * x * (1.1 / 2.1)", x -> 1.1 * x * (1.1 / 2.1), new Multiply(new Multiply(
                        c(1.1), vx), new Divide(c(1.1), c(2.1))))
                .advanced("((1.1 * x) / (x + x))", "1.1 * x / (x + x)", x -> 1.1 * x / (x + x), new Divide(new Multiply(
                        c(1.1), vx), new Add(vx, vx)))
                .advanced("(((x - x) / 2.1) + 2.1)", "(x - x) / 2.1 + 2.1", x -> (x - x) / 2.1 + 2.1, new Add(new Divide(new Subtract(vx, vx),
                        c(2.1)
                ), c(2.1)))
                .advanced("((x / (x - x)) - 1.1)", "x / (x - x) - 1.1", x -> x / (x - x) - 1.1, new Subtract(new Divide(vx, new Subtract(vx, vx)),
                        c(1.1)
                ))
                .advanced("((2.1 - (1.1 * x)) * 1.1)", "(2.1 - 1.1 * x) * 1.1", x -> (2.1 - 1.1 * x) * 1.1, new Multiply(new Subtract(
                        c(2.1), new Multiply(
                        c(1.1), vx)), c(1.1)))
                .advanced("((x / (x - x)) / x)", "x / (x - x) / x", x -> x / (x - x) / x, new Divide(new Divide(vx, new Subtract(vx, vx)), vx))

                .advanced("((1.1E10 * x) * (1.1E10 / 2.3E12))", "1.1E10 * x * (1.1E10 / 2.3E12)", x -> 1.1E10 * x * (1.1E10 / 2.3E12), new Multiply(new Multiply(
                        c(1.1E10), vx), new Divide(c(1.1E10), c(2.3E12))))
                .advanced("((1.1E10 * x) / (x + x))", "1.1E10 * x / (x + x)", x -> 1.1E10 * x / (x + x), new Divide(new Multiply(
                        c(1.1E10), vx), new Add(vx, vx)))
                .advanced("(((x - x) / 2.3E12) + 2.3E12)", "(x - x) / 2.3E12 + 2.3E12", x -> (x - x) / 2.3E12 + 2.3E12, new Add(new Divide(new Subtract(vx, vx),
                        c(2.3E12)
                ), c(2.3E12)))
                .advanced("((x / (x - x)) - 1.1E10)", "x / (x - x) - 1.1E10", x -> x / (x - x) - 1.1E10, new Subtract(new Divide(vx, new Subtract(vx, vx)),
                        c(1.1E10)
                ))
                .advanced("((2.3E12 - (1.1E10 * x)) * 1.1E10)", "(2.3E12 - 1.1E10 * x) * 1.1E10", x -> (2.3E12 - 1.1E10 * x) * 1.1E10, new Multiply(new Subtract(
                        c(2.3E12), new Multiply(
                        c(1.1E10), vx)), c(1.1E10)))
                .advanced("((x / (x - x)) / x)", "x / (x - x) / x", x -> x / (x - x) / x, new Divide(new Divide(vx, new Subtract(vx, vx)), vx));
    }
}
