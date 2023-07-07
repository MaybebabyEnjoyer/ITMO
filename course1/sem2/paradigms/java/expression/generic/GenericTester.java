package expression.generic;

import base.*;
import expression.common.*;
import expression.parser.ParserTestSet;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class GenericTester extends Tester {
    private static final int SIZE = 10;
    private static final int MAX = Integer.MAX_VALUE - 1;
    private static final int MIN = Integer.MIN_VALUE;
    private static final List<Pair<String, F<?>>> VARIABLES = List.of(
            Pair.of("x", (x, y, z) -> x),
            Pair.of("y", (x, y, z) -> y),
            Pair.of("z", (x, y, z) -> z)
    );
    private static final ExpressionKind.Variables<F<?>> VARS = (c, r) -> VARIABLES;

    protected final List<Op<IF<?>>> tests = new ArrayList<>();
    private final Tabulator tabulator = new GenericTabulator();
    private final Set<String> operations = new HashSet<>();

    private final TestGenerator<Integer> generator;
    private final Map<String, Mode<?>> modes = new HashMap<>();

    public GenericTester(final TestCounter counter) {
        super(counter);
        generator = new TestGenerator<>(
                counter,
                random(),
                random()::nextInt,
                ParserTestSet.CONSTS,
                false
        );
    }

    protected void test(final String expression, final String name, final IF<?> f) {
        tests.add(Op.of(name + ": " + expression, f));
    }

    @Override
    public void test() {
        modes.values().forEach(mode -> mode.freeze(operations));

        for (final Op<IF<?>> test : tests) {
            final String[] parts = test.name.split(": ");
            testFull(parts[0], parts[1], test.value);
        }

        counter.scope(
                "basic",
                () -> generator.testBasic(VARS, test -> test(test.expr, test.full, true))
        );
        counter.scope(
                "random",
                () -> generator.testRandom(
                        20 + (TestCounter.DENOMINATOR - 1) * 2,
                        VARS,
                        test -> test(test.expr, test.full, false)
                )
        );
    }

    private void testFull(final String mode, final String expression, final IF<?> f) {
        testShort(mode, expression, f);
        test(mode, expression, f, MAX, -1, MAX, 0);
        test(mode, expression, f, MIN, 0, MIN, 1);
    }

    private void testShort(final String mode, final String expression, final IF<?> f) {
        test(mode, expression, f, 0, -1, 0, 1);
    }

    private void test(final String mode, final String expression, final IF<?> f, final int min, final int dMin, final int max, final int dMax) {
        test(
                mode, expression, f,
                min + random().nextInt(SIZE) * dMin, max + random().nextInt(SIZE) * dMax,
                min + random().nextInt(SIZE) * dMin, max + random().nextInt(SIZE) * dMax,
                min + random().nextInt(SIZE) * dMin, max + random().nextInt(SIZE) * dMax
        );
    }

    private void test(final Expr<Integer, F<?>> expr, final String expression, final boolean full) {
        modes.values().forEach(mode -> mode.test(expr, expression, full));
    }

    private <T> void test(final String mode, final String expression, final IF<T> f, final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
        final String context = String.format("mode=%s, x=[%d, %d] y=[%d, %d] z=[%d, %d], expression=%s%n", mode, x1, x2, y1, y2, z1, z2, expression);
        final Object[][][] result = counter.testV(() -> TestCounter.get(() -> tabulator.tabulate(mode, expression, x1, x2, y1, y2, z1, z2))
                .either(e -> counter.fail(e, "%s %s", "tabulate", context), Function.identity()));
        IntStream.rangeClosed(x1, x2).forEach(x ->
                IntStream.rangeClosed(y1, y2).forEach(y ->
                        IntStream.rangeClosed(z1, z2).forEach(z ->
                                counter.test(() -> {
                                    final Object expected = TestCounter.get(() -> f.apply(x, y, z)).either(e -> null, Function.identity());
                                    final Object actual = result[x - x1][y - y1][z - z1];
                                    counter.checkTrue(
                                            Objects.equals(actual, expected),
                                            "table[%d][%d][%d](x=%d, y=%d, z=%d]) = %s (expected %s)%n%s",
                                            x - x1, y - y1, z - z1,
                                            x, y, z,
                                            actual, expected, context
                                    );
                                }))));
    }

    public void binary(final String name, final int priority) {
        operations.add(name + ":2");
        generator.binary(name, priority);
    }

    public void unary(final String name) {
        operations.add(name + ":1");
        generator.unary(name);
    }

    private final class Mode<T> {
        private final String mode;
        private final IntFunction<T> constant;
        private final Renderer<Integer, Unit, F<T>> renderer;
        private final List<Op<UnaryOperator<F<T>>>> unary;
        private final List<Op<BinaryOperator<F<T>>>> binary;
        private final IntUnaryOperator fixer;

        public Mode(
                final String mode,
                final IntFunction<T> constant,
                final List<Op<UnaryOperator<F<T>>>> unary,
                final List<Op<BinaryOperator<F<T>>>> binary,
                final IntUnaryOperator fixer
        ) {
            this.mode = mode;
            this.constant = constant;
            this.fixer = fixer;

            renderer = new Renderer<>(value -> {
                final T result = constant.apply(value);
                return (x, y, z) -> result;
            });
//            renderer.add("x", 0, (unit, args) -> (x, y, z) -> x);
//            renderer.add("y", 0, (unit, args) -> (x, y, z) -> y);
//            renderer.add("z", 0, (unit, args) -> (x, y, z) -> z);
            this.unary = unary;
            this.binary = binary;
            for (final Op<UnaryOperator<F<T>>> op : unary) {
                renderer.unary(op.name(), (unit, arg) -> op.value().apply(arg));
            }
            for (final Op<BinaryOperator<F<T>>> op : binary) {
                renderer.binary(op.name(), (unit, a, b) -> op.value().apply(a, b));
            }
        }

        public void test(final Expr<Integer, ? extends F<?>> expr, final String expression, final boolean full) {
            final String fixed = fixer == IntUnaryOperator.identity()
                    ? expression
                    : generator.full(expr.node(node ->
                            node.cata(c -> Node.constant(fixer.applyAsInt(c)), Node::op, Node::op, Node::op)));
            @SuppressWarnings("unchecked") final Expr<Integer, F<T>> converted = (Expr<Integer, F<T>>) expr;
            final F<T> expected = renderer.render(Unit.INSTANCE, converted);
            final IF<T> f = (x, y, z) -> expected.apply(constant.apply(x), constant.apply(y), constant.apply(z));
            if (full) {
                testFull(mode, fixed, f);
            } else {
                testShort(mode, fixed, f);
            }
        }

        private void freeze(final Set<String> operations) {
            final Set<String> ops = Stream.concat(
                    unary.stream().map(op -> op.name() + ":1"),
                    binary.stream().map(op -> op.name() + ":2")
            ).collect(Collectors.toUnmodifiableSet());
            final List<String> diff = operations.stream()
                    .filter(Predicate.not(ops::contains))
                    .collect(Collectors.toUnmodifiableList());
            Asserts.assertTrue(String.format("Missing operations for %s: %s", mode, diff), diff.isEmpty());
        }
    }

    @FunctionalInterface
    protected interface IF<T> {
        T apply(int x, int y, int z);
    }

    @FunctionalInterface
    protected interface F<T> {
        T apply(T x, T y, T z);
    }

    public <T> void mode(
            final String mode,
            final IntFunction<T> constant,
            final List<Op<UnaryOperator<F<T>>>> unary,
            final List<Op<BinaryOperator<F<T>>>> binary,
            final IntUnaryOperator fixer
    ) {
        modes.put(mode, new Mode<>(mode, constant, unary, binary, fixer));
    }
}
