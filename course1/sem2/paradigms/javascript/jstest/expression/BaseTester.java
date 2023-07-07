package jstest.expression;

import base.Asserts;
import base.ExtendedRandom;
import base.TestCounter;
import base.Tester;
import jstest.Engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

/**
 * Base expressions tester.
 *
 * @author Niyaz Nigmatullin
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public abstract class BaseTester<X, E extends Engine<X>> extends Tester {
    public static final int N = 5;
    public static final double EPS = 1e-3;
    public static int TESTS = 444;

    protected final E engine;
    /*package*/ final Language language;
    private final List<Runnable> stages = new ArrayList<>();

    private final List<Spoiler> spoilers;

    public static final List<Spoiler> STANDARD_SPOILERS = List.of(
            (unparsed, expr, random) -> unparsed,
            (unparsed, expr, random) -> addSpaces(unparsed, random)
    );

    protected BaseTester(
            final TestCounter counter,
            final E engine,
            final Language language,
            final List<Spoiler> spoilers
    ) {
        super(counter);
        this.engine = engine;
        this.language = language;
        this.spoilers = spoilers;
    }


    private static boolean safe(final char ch) {
        return !Character.isLetterOrDigit(ch) && "+-*/.<>=&|^".indexOf(ch) == -1;
    }

    public static String addSpaces(final String expression, final ExtendedRandom random) {
        String spaced = expression;
        for (int n = StrictMath.min(10, 200 / expression.length()); n > 0;) {
            final int index = random.nextInt(spaced.length() + 1);
            final char c = index == 0 ? 0 : spaced.charAt(index - 1);
            final char nc = index == spaced.length() ? 0 : spaced.charAt(index);
            if ((safe(c) || safe(nc)) && c != '\'' && nc != '\'' && c != '"' && nc != '"') {
                spaced = spaced.substring(0, index) + " " + spaced.substring(index);
                n--;
            }
        }
        return spaced;
    }

    @Override
    public void test() {
        for (final Test test : language.getTests()) {
            try {
                test(test, prepared -> counter.scope("Testing: " + prepared, () -> {
                    for (double i = 0; i <= N; i++) {
                        for (double j = 0; j <= N; j++) {
                            for (double k = 0; k <= N; k++) {
                                final double[] vars = new double[]{i, j, k};
                                evaluate(prepared, vars, test.evaluate(vars));
                            }
                        }
                    }
                }));
            } catch (final RuntimeException | AssertionError e) {
                throw new AssertionError("Error while testing " + test.getParsed() + ": " + e.getMessage(), e);
            }
        }

        counter.scope("Random tests", () -> testRandom(TESTS / TestCounter.DENOMINATOR));
        stages.forEach(Runnable::run);
    }

    private void test(final Test test, final Consumer<Engine.Result<X>> check) {
        final Consumer<Engine.Result<X>> fullCheck = prepared -> counter.test(() -> {
            check.accept(prepared);
            test(prepared, test.getUnparsed());
        });
        fullCheck.accept(engine.prepare(test.getParsed()));
        spoilers.forEach(spoiler -> fullCheck.accept(parse(spoiler.spoil(test.getUnparsed(), test.expr, random()))));
    }

    protected final Engine.Result<X> parse(final String expression) {
        return engine.parse(expression);
    }

    protected void test(final Engine.Result<X> prepared, final String unparsed) {
    }

    public void testRandom(final int n) {
        for (int i = 0; i < n; i++) {
            if (i % 100 == 0) {
                counter.format("Completed %3d out of %d%n", i, n);
            }
            final double[] vars = random().getRandom().doubles().limit(language.getVariables().size()).toArray();

            final Test test = language.randomTest(i);
            final double answer = test.evaluate(vars);

            test(test, prepared -> evaluate(prepared, vars, answer));
        }
    }

    public void evaluate(final Engine.Result<X> prepared, final double[] vars, final double expected) {
        counter.test(() -> {
            final Engine.Result<Number> result = engine.evaluate(prepared, vars);
            Asserts.assertEquals(result.context, expected, result.value.doubleValue(), EPS);
        });
    }

    public static int mode(final String[] args, final Class<?> type, final String... modes) {
        if (args.length == 0) {
            System.err.println("ERROR: No arguments found");
        } else if (args.length > 1) {
            System.err.println("ERROR: Only one argument expected, " + args.length + " found");
        } else if (!Arrays.asList(modes).contains(args[0])) {
            System.err.println("ERROR: First argument should be one of: \"" + String.join("\", \"", modes) + "\", found: \"" + args[0] + "\"");
        } else {
            return Arrays.asList(modes).indexOf(args[0]);
        }
        System.err.println("Usage: java -ea " + type.getName() + " {" + String.join("|", modes) + "}");
        System.exit(1);
        throw new AssertionError("Return from System.exit");
    }

    public void addStage(final Runnable stage) {
        stages.add(stage);
    }

    public interface Func extends ToDoubleFunction<double[]> {
        @Override
        double applyAsDouble(double... args);
    }

    public static class Test {
        public final Expr expr;
        private final String parsed;
        private final String unparsed;

        public Test(final Expr expr, final String parsed, final String unparsed) {
            this.expr = Objects.requireNonNull(expr);
            this.parsed = Objects.requireNonNull(parsed);
            this.unparsed = Objects.requireNonNull(unparsed);
        }

        public String getParsed() {
            return parsed;
        }

        public String getUnparsed() {
            return unparsed;
        }

        public double evaluate(final double... vars) {
            return expr.evaluate(vars);
        }
    }

    public interface Spoiler {
        String spoil(String rendered, final Expr expr, ExtendedRandom random);
    }
}
