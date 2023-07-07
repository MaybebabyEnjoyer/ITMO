package jstest.expression;

import base.Asserts;
import jstest.Engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static jstest.expression.BaseTester.EPS;
import static jstest.expression.BaseTester.Test;

/**
 * Expression differentiator.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Diff {
    public static final double D = 1e-6;

    private final Dialect dialect;
    private final int min;
    private final int max;
    private static final List<Expr> VARIABLES;
    static {
        final List<String> variables = List.of("x", "y", "z");
        VARIABLES = IntStream.range(0, variables.size())
                .mapToObj(i -> Expr.variable(variables.get(i), i))
                .collect(Collectors.toUnmodifiableList());
    }

    public Diff(final int min, final int max, final Dialect dialect) {
        this.dialect = dialect;
        this.min = min;
        this.max = max;
    }

    public <X> void diff(final BaseTester<X, ?> tester) {
        tester.addStage(() -> {
            for (final Test expr : tester.language.getTests()) {
                diff(tester, expr, false);
            }
        });
    }

    private <X> List<Engine.Result<String>> diff(final BaseTester<X, ?> tester, final Test test, final boolean simplify) {
        final List<Engine.Result<String>> results = new ArrayList<>(3);
        System.out.println("    Testing diff: " + test.getParsed());
        for (int variable = 0; variable < 3; variable++) {
            final String diff = dialect.meta("diff", test.getParsed(), dialect.render(VARIABLES.get(variable)));
            final String value = simplify ? dialect.meta("simplify", diff) : diff;
            final Engine.Result<X> expression = tester.engine.prepare(value);

            results.add(tester.engine.toString(expression));

            final double di = variable == 0 ? D : 0;
            final double dj = variable == 1 ? D : 0;
            final double dk = variable == 2 ? D : 0;
            for (int i = min; i <= max; i++) {
                for (int j = min; j <= max; j++) {
                    for (int k = min; k <= max; k++) {
                        final double d = Math.abs(test.evaluate(i, j, k));
                        if (EPS < d && d < 1 / EPS) {
                            final double expected = (
                                    test.evaluate(i + di, j + dj, k + dk) -
                                            test.evaluate(i - di, j - dj, k - dk)) / D / 2;
                            if (Math.abs(expected) < 1 / EPS) {
                                try {
                                    tester.evaluate(expression, new double[]{i, j, k}, expected);
                                } catch (final AssertionError e) {
                                    System.err.format("d = %f%n", d);
                                    throw e;
                                }
                            }
                        }
                    }
                }
            }
        }
        return results;
    }

    public <X> void simplify(final BaseTester<X, ?> tester) {
        tester.addStage(() -> {
            final List<int[]> newSimplifications = new ArrayList<>();
            final List<int[]> simplifications = tester.language.getSimplifications();
            final List<Test> tests = tester.language.getTests();

            for (int i = 0; i < simplifications.size(); i++) {
                final Test expr = tests.get(i);
                final int[] expected = simplifications.get(i);
                final List<Engine.Result<String>> actual = diff(tester, expr, true);
                if (expected != null) {
                    for (int j = 0; j < expected.length; j++) {
                        final Engine.Result<String> result = actual.get(j);
                        final int length = result.value.length();
                        Asserts.assertTrue(
                                String.format("Simplified length too long: %d instead of %d%s", length, expected[j], result.context),
                                length <= expected[j]
                        );
                    }
                } else {
                    newSimplifications.add(actual.stream().mapToInt(result -> result.value.length()).toArray());
                }
            }
            if (!newSimplifications.isEmpty()) {
                System.err.println(newSimplifications.stream()
                        .map(row -> Arrays.stream(row).mapToObj(Integer::toString).collect(Collectors.joining(", ", "{", "}")))
                        .collect(Collectors.joining(", ", "new int[][]{", "}")));
                System.err.println(simplifications.size() + " " + newSimplifications.size());
                throw new AssertionError("Uncovered");
            }
        });
    }
}
