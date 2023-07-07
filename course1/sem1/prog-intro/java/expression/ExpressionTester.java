package expression;

import base.*;
import expression.common.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static base.Asserts.assertEquals;
import static base.Asserts.assertTrue;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ExpressionTester<E extends ToMiniString, C> extends Tester {
    private final List<Integer> VALUES = IntStream.rangeClosed(-10, 10).boxed().collect(Collectors.toUnmodifiableList());
    private final ExpressionKind<E, C> kind;

    private final List<Test> basic = new ArrayList<>();
    private final List<Test> advanced = new ArrayList<>();
    private final Generator generator;

    private final List<Pair<ToMiniString, String>> prev = new ArrayList<>();

    protected ExpressionTester(
            final TestCounter counter,
            final ExpressionKind<E, C> kind,
            final Function<C, E> expectedConstant,
            final Binary<C, E> binary,
            final BinaryOperator<C> add,
            final BinaryOperator<C> sub,
            final BinaryOperator<C> mul,
            final BinaryOperator<C> div
    ) {
        super(counter);
        this.kind = kind;

        generator = new Generator(expectedConstant, kind::constant, binary, kind::randomValue);
        generator.binary("+",  1600, add, Add.class);
        generator.binary("-", 1602, sub, Subtract.class);
        generator.binary("*",  2001, mul, Multiply.class);
        generator.binary("/", 2002, div, Divide.class);
    }

    @Override
    public String toString() {
        return kind.getName();
    }

    @Override
    public void test() {
        counter.scope("Basic tests", () -> basic.forEach(Test::test));
        counter.scope("Advanced tests", () -> advanced.forEach(Test::test));
        counter.scope("Random tests", generator::testRandom);
    }

    private void checkEqualsAndToString(final String full, final String mini, final ToMiniString expression, final ToMiniString copy) {
        checkToString("toString", full, expression.toString());
        if (mode() > 0) {
            checkToString("toMiniString", mini, expression.toMiniString());
        }

        counter.test(() -> {
            assertTrue("Equals to this", expression.equals(expression));
            assertTrue("Equals to copy", expression.equals(copy));
            assertTrue("Equals to null", !expression.equals(null));
            assertTrue("Copy equals to null", !copy.equals(null));
        });

        final String expressionToString = Objects.requireNonNull(expression.toString());
        for (final Pair<ToMiniString, String> pair : prev) {
            counter.test(() -> {
                final ToMiniString prev = pair.first;
                final String prevToString = pair.second;
                final boolean equals = prevToString.equals(expressionToString);
                assertTrue("Equals to " + prevToString, prev.equals(expression) == equals);
                assertTrue("Equals to " + prevToString, expression.equals(prev) == equals);
                assertTrue("Inconsistent hashCode for " + prev + " and " + expression, (prev.hashCode() == expression.hashCode()) == equals);
            });
        }
    }

    private void checkToString(final String method, final String expected, final String actual) {
        counter.test(() -> assertTrue(String.format("Invalid %s\n     expected: %s\n       actual: %s", method, expected, actual), expected.equals(actual)));
    }

    private void check(
            final String full,
            final E expected,
            final E actual,
            final List<String> variables,
            final List<C> values
    ) {
        final String vars = IntStream.range(0, variables.size())
                .mapToObj(i -> variables.get(i) + "=" + values.get(i))
                .collect(Collectors.joining(","));
        counter.test(() -> assertEquals(
                String.format("f(%s)\nwhere f is %s", vars, full),
                evaluate(expected, variables, values),
                evaluate(actual, variables, values)
        ));
    }

    private Object evaluate(final E expression, final List<String> variables, final List<C> values) {
        try {
            return kind.evaluate(expression, variables, values);
        } catch (final Exception e) {
            return e.getClass().getName();
        }
    }

    protected ExpressionTester<E, C> basic(final String full, final String mini, final E expected, final E actual) {
        return basicF(full, mini, expected, vars -> actual);
    }

    protected ExpressionTester<E, C> basicF(final String full, final String mini, final E expected, final Function<List<String>, E> actual) {
        return basic(new Test(full, mini, expected, actual));
    }

    private ExpressionTester<E, C> basic(final Test test) {
        basic.add(test);
        return this;
    }

    public ExpressionTester<E, C> basic(final Node<C> node, final E expression) {
        final List<Pair<String, E>> variables = kind.variables.generate(random(), 3);
        return basic(generator.test(new Expr<>(node, variables), kind.cast(expression)));
    }

    protected ExpressionTester<E, C> advanced(final String full, final String mini, final E expected, final E actual) {
        return advancedF(full, mini, expected, vars -> actual);
    }

    protected ExpressionTester<E, C> advancedF(final String full, final String mini, final E expected, final Function<List<String>, E> actual) {
        advanced.add(new Test(full, mini, expected, actual));
        return this;
    }

    protected static <E> Op<E> variable(final String name, final E expected) {
        return Op.of(name, expected);
    }

    @FunctionalInterface
    public interface Binary<C, E> {
        E apply(BinaryOperator<C> op, E a, E b);
    }

    private final class Test {
        private final String full;
        private final String mini;
        private final E expected;
        private final Function<List<String>, E> actual;

        private Test(final String full, final String mini, final E expected, final Function<List<String>, E> actual) {
            this.full = full;
            this.mini = mini;
            this.expected = expected;
            this.actual = actual;
        }

        private void test() {
            final List<Pair<String, E>> variables = kind.variables.generate(random(), 3);
            final List<String> names = Functional.map(variables, Pair::first);
            final E actual = kind.cast(this.actual.apply(names));
            final String full = mangle(this.full, names);
            final String mini = mangle(this.mini, names);

            counter.test(() -> {
                kind.allValues(variables.size(), VALUES).forEach(values -> check(mini, expected, actual, names, values));
                checkEqualsAndToString(full, mini, actual, actual);
                prev.add(Pair.of(actual, full));
            });
        }

        private String mangle(String string, final List<String> names) {
            for (int i = 0; i < names.size(); i++) {
                string = string.replace("$" + (char) ('x' + i), names.get(i));
            }
            return string;
        }
    }

    private final class Generator {
        private final expression.common.Generator<C> generator;
        private final NodeRenderer<C> renderer = new NodeRenderer<>(random());
        private final Renderer<C, Unit, E> expected;
        private final Renderer<C, Unit, E> actual;
        private final Renderer<C, Unit, E> copy;
        private final Binary<C, E> binary;

        private Generator(
                final Function<C, E> expectedConstant,
                final Function<? super C, E> actualConstant,
                final Binary<C, E> binary,
                final Function<ExtendedRandom, C> randomValue
        ) {
            generator = new expression.common.Generator<>(random(), () -> randomValue.apply(random()));
            expected = new Renderer<>(expectedConstant::apply);
            actual = new Renderer<>(actualConstant::apply);
            copy = new Renderer<>(actualConstant::apply);

            this.binary = binary;
        }

        private void binary(final String name, final int priority, final BinaryOperator<C> op, final Class<?> type) {
            generator.add(name, 2);
            renderer.binary(name, priority);

            expected.binary(name, (unit, a, b) -> binary.apply(op, a, b));

            @SuppressWarnings("unchecked") final Constructor<? extends E> constructor = (Constructor<? extends E>) Arrays.stream(type.getConstructors())
                    .filter(cons -> Modifier.isPublic(cons.getModifiers()))
                    .filter(cons -> cons.getParameterCount() == 2)
                    .findFirst()
                    .orElseGet(() -> counter.fail("%s(..., ...) constructor not found", type.getSimpleName()));
            final Renderer.BinaryOperator<Unit, E> actual = (unit, a, b) -> {
                try {
                    return constructor.newInstance(a, b);
                } catch (final Exception e) {
                    return counter.fail(e);
                }
            };
            this.actual.binary(name, actual);
            copy.binary(name, actual);
        }

        private void testRandom() {
            generator.testRandom(1, counter, kind.variables, expr -> {
                final String full = renderer.render(expr, NodeRenderer.FULL);
                final String mini = renderer.render(expr, NodeRenderer.MINI);
                final E expected = this.expected.render(Unit.INSTANCE, expr);
                final E actual = this.actual.render(Unit.INSTANCE, expr);

                final List<Pair<String, E>> variables = kind.variables.generate(random(), random().nextInt(5) + 1);
                final List<String> names = Functional.map(variables, Pair::first);
                final List<C> values = Stream.generate(() -> kind.randomValue(random()))
                        .limit(variables.size())
                        .collect(Collectors.toUnmodifiableList());

                checkEqualsAndToString(full, mini, actual, copy.render(Unit.INSTANCE, expr));
                check(full, expected, actual, names, values);
            });
        }

        public Test test(final Expr<C, E> expr, final E expression) {
            return new Test(
                    renderer.render(expr, NodeRenderer.FULL),
                    renderer.render(expr, NodeRenderer.MINI),
                    expected.render(Unit.INSTANCE, expr),
                    vars -> expression
            );
        }
    }
}
