package expression.parser;

import base.*;
import expression.ToMiniString;
import expression.common.Expr;
import expression.common.ExpressionKind;
import expression.common.Reason;
import expression.common.TestGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ParserTestSet<E extends ToMiniString, C> {
    private static final int D = 5;

    private static final List<Integer> TEST_VALUES = new ArrayList<>();
    static {
        Functional.addRange(TEST_VALUES, D, D);
        Functional.addRange(TEST_VALUES, D, -D);
    }

    public static final List<Integer> CONSTS
            = List.of(0, 1, -1, 4, -4, 10, -10, 30, -30, 100, -100, Integer.MAX_VALUE, Integer.MIN_VALUE);

    protected final ParserTester tester;
    protected final ParsedKind<E, C> kind;
    private final boolean safe;

    protected final TestCounter counter;

    public ParserTestSet(final ParserTester tester, final ParsedKind<E, C> kind) {
        this(tester, kind, true);
    }

    protected ParserTestSet(final ParserTester tester, final ParsedKind<E, C> kind, final boolean safe) {
        this.tester = tester;
        this.kind = kind;
        this.safe = safe;

        counter = tester.getCounter();
    }

    private void examples() {
        example("$x+2", (x, y, z) -> x + 2);
        example("2-$y", (x, y, z) -> 2 - y);
        example("  3*  $z  ", (x, y, z) -> 3 * z);
        example("$x/  -  2", (x, y, z) -> -x / 2);
        example("$x*$y+($z-1   )/10", (x, y, z) -> x * y + (int) (z - 1) / 10);
        example("-(-(-\t\t-5 + 16   *$x*$y) + 1 * $z) -(((-11)))", (x, y, z) -> -(-(5 + 16 * x * y) + z) + 11);
        example("" + Integer.MAX_VALUE, (x, y, z) -> (long) Integer.MAX_VALUE);
        example("" + Integer.MIN_VALUE, (x, y, z) -> (long) Integer.MIN_VALUE);
        example("$x--$y--$z", (x, y, z) -> x + y + z);
        example("((2+2))-0/(--2)*555", (x, y, z) -> 4L);
        example("$x-$x+$y-$y+$z-($z)", (x, y, z) -> 0L);
        example("(".repeat(350) + "$x + $y + (-10*-$z)" + ")".repeat(350), (x, y, z) -> x + y + 10 * z);
        example("$x / $y / $z", (x, y, z) -> y == 0 || z == 0 ? Reason.DBZ.error() : (int) x / (int) y / z);
    }

    private void example(final String name, final ExampleExpression expression) {
        final List<Pair<String, E>> variables = tester.generator.variables(kind.kind.variables, 3);
        final List<String> names = Functional.map(variables, Pair::first);
        final String mangled = name
                .replace("$x", names.get(0))
                .replace("$y", names.get(1))
                .replace("$z", names.get(2));
        final TExpression expected = vars -> expression.evaluate(vars.get(0), vars.get(1), vars.get(2));

        counter.test(() -> {
            final E parsed = parse(mangled, names, true);
            Functional.allValues(TEST_VALUES, 3).forEach(values -> check(expected, parsed, names, values, mangled));
        });
    }

    protected void test() {
        counter.scope("Basic tests", () -> tester.generator.testBasic(kind.kind.variables, this::test));
        counter.scope("Handmade tests", this::examples);
        counter.scope("Random tests", () -> tester.generator.testRandom(1, kind.kind.variables, this::test));
    }

    private void test(final TestGenerator.Test<Integer, E> test) {
        final Expr<Integer, E> expr = test.expr;
        final List<Pair<String, E>> vars = expr.variables();
        final List<String> variables = Functional.map(vars, Pair::first);
        final String full = test.full;
        final String mini = test.mini;
        final String safe = test.safe;

        final E fullParsed = parse(full, variables, false);
        final E miniParsed = parse(mini, variables, false);
        final E safeParsed = parse(safe, variables, false);

        checkToString(full, mini, "base", fullParsed);
        if (tester.mode() > 0) {
            counter.test(() -> Asserts.assertEquals("mini.toMiniString", mini, miniParsed.toMiniString()));
            counter.test(() -> Asserts.assertEquals("safe.toMiniString", mini, safeParsed.toMiniString()));
        }
        checkToString(full, mini, "extraParentheses", parse(test.fullExtra, variables, false));
        checkToString(full, mini, "noSpaces", parse(removeSpaces(full), variables, false));
        checkToString(full, mini, "extraSpaces", parse(extraSpaces(full), variables, false));

        final TExpression expected = tester.renderer.render(Unit.INSTANCE, Expr.of(
                expr.node(),
                Functional.map(vars, (i, var) -> Pair.of(var.first(), args -> args.get(i)))
        ));

        check(expected, fullParsed, variables, tester.random().random(variables.size(), ExtendedRandom::nextInt), full);
        if (this.safe) {
            check(expected, safeParsed, variables, tester.random().random(variables.size(), ExtendedRandom::nextInt), safe);
        }
    }

    private static final String LOOKBEHIND = "(?<![a-zA-Z0-9<>*/+-])";
    private static final String LOOKAHEAD = "(?![a-zA-Z0-9<>*/])";
    private static final Pattern SPACES = Pattern.compile(LOOKBEHIND + " | " + LOOKAHEAD + "|" + LOOKAHEAD + LOOKBEHIND);
    private String extraSpaces(final String expression) {
        return SPACES.matcher(expression).replaceAll(r -> tester.random().randomString(
                ExtendedRandom.SPACES,
                tester.random().nextInt(5)
        ));
    }

    private static String removeSpaces(final String expression) {
        return SPACES.matcher(expression).replaceAll("");
    }

    private void checkToString(final String full, final String mini, final String prefix, final ToMiniString parsed) {
        counter.test(() -> {
            assertEquals(prefix + ".toString", full, full, parsed.toString());
            if (tester.mode() > 0) {
                assertEquals(full, mini, prefix, parsed.toMiniString());
            }
        });
    }

    private static void assertEquals(final String full, final String mini, final String prefix, final String actual) {
        final String message = String.format("%s:%n     original `%s`,%n     expected `%s`,%n       actual `%s`",
                prefix + ".toMiniString", full, mini, actual);
        Asserts.assertTrue(message, Objects.equals(mini, actual));
    }

    private Either<Reason, Integer> eval(final TExpression expression, final List<Integer> vars) {
        return Reason.eval(() -> tester.cast(expression.evaluate(vars)));
    }

    protected E parse(final String expression, final List<String> variables, final boolean reparse) {
        return counter.testV(() -> {
            final E parsed = counter.testV(() -> counter.call("parse",
                    () -> kind.parse(expression, variables)));
            if (reparse) {
                counter.testV(() -> counter.call("parse", () -> kind.parse(parsed.toString(), variables)));
            }
            return parsed;
        });
    }

    private void check(
            final TExpression expectedExpression,
            final E expression,
            final List<String> variables,
            final List<Integer> values,
            final String unparsed
    ) {
        counter.test(() -> {
            final Either<Reason, Integer> answer = eval(expectedExpression, values);
            final String args = IntStream.range(0, variables.size())
                    .mapToObj(i -> variables.get(i) + "=" + values.get(i))
                    .collect(Collectors.joining(", "));
            try {
                final C actual = kind.kind.evaluate(expression, variables, kind.kind.fromInts(values));
                counter.checkTrue(answer.isRight(), "Error expected for f(%s)%n\twhere f=%s%n\tyour f=%s", args, unparsed, expression);
                final String message = String.format("f(%s)%n\twhere f=%s%n\tyour f=%s", args, unparsed, expression);
                Asserts.assertEquals(message, answer.getRight(), actual);
            } catch (final Exception e) {
                if (answer.isRight()) {
                    counter.fail(e, "No error expected for %s", args);
                }
            }
        });
    }

    @FunctionalInterface
    public interface TExpression {
        long evaluate(List<Integer> vars);
    }

    @FunctionalInterface
    protected interface ExampleExpression {
        long evaluate(long x, long y, long z);
    }

    /**
     * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
     */
    public static class ParsedKind<E extends ToMiniString, C> {
        private final ExpressionKind<E, C> kind;
        private final Parser<E> parser;

        public ParsedKind(final ExpressionKind<E, C> kind, final Parser<E> parser) {
            this.kind = kind;
            this.parser = parser;
        }

        public E parse(final String expression, final List<String> variables) throws Exception {
            return parser.parse(expression, variables);
        }

        @Override
        public String toString() {
            return kind.toString();
        }
    }

    @FunctionalInterface
    public interface Parser<E> {
        E parse(final String expression, final List<String> variables) throws Exception;
    }

}
