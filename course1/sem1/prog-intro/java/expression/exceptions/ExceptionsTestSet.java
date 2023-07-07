package expression.exceptions;

import base.Functional;
import expression.ToMiniString;
import expression.TripleExpression;
import expression.Variable;
import expression.common.Op;
import expression.parser.ParserTestSet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongBinaryOperator;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ExceptionsTestSet<E extends ToMiniString, C> extends ParserTestSet<E, C> {
    private static final int D = 5;
    private static final List<Integer> OVERFLOW_VALUES = new ArrayList<>();
    private final char[] CHARS = "AZ+-*%()[]<>".toCharArray();

    private static final Variable VX = new Variable("x");
    private static final Variable VY = new Variable("y");

    static {
        Functional.addRange(OVERFLOW_VALUES, D, Integer.MIN_VALUE + D);
        Functional.addRange(OVERFLOW_VALUES, D, Integer.MIN_VALUE / 2);
        Functional.addRange(OVERFLOW_VALUES, D, (int) -Math.sqrt(Integer.MAX_VALUE));
        Functional.addRange(OVERFLOW_VALUES, D, 0);
        Functional.addRange(OVERFLOW_VALUES, D, (int) Math.sqrt(Integer.MAX_VALUE));
        Functional.addRange(OVERFLOW_VALUES, D, Integer.MAX_VALUE / 2);
        Functional.addRange(OVERFLOW_VALUES, D, Integer.MAX_VALUE - D);
    }

    private final List<Op<String>> parsingTest;

    public ExceptionsTestSet(final ExceptionsTester tester, final ParsedKind<E, C> kinds) {
        super(tester, kinds, false);
        parsingTest = tester.parsingTest;
    }

    private void testParsingErrors() {
        final ExpressionParser parser = new ExpressionParser();
        counter.testForEach(parsingTest, op -> {
            try {
                parser.parse(op.value);
                counter.fail("Successfully parsed %s", op.value);
            } catch (final Exception e) {
                counter.format("%-30s %s%n", op.name, e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        });
    }

    private void testOverflow() {
        //noinspection Convert2MethodRef
        testOverflow((a, b) -> a + b, "+", new CheckedAdd(VX, VY));
        testOverflow((a, b) -> a - b, "-", new CheckedSubtract(VX, VY));
        testOverflow((a, b) -> a * b, "*", new CheckedMultiply(VX, VY));
        testOverflow((a, b) -> b == 0 ? Long.MAX_VALUE : a / b, "/", new CheckedDivide(VX, VY));
        testOverflow((a, b) -> -b, "<- ignore first argument, unary -", new CheckedNegate(VY));
    }

    private void testOverflow(final LongBinaryOperator f, final String op, final TripleExpression expression) {
        for (final int a : OVERFLOW_VALUES) {
            for (final int b : OVERFLOW_VALUES) {
                final long expected = f.applyAsLong(a, b);
                try {
                    final int actual = expression.evaluate(a, b, 0);
                    counter.checkTrue(actual == expected, "%d %s %d == %d", a, op, b, actual);
                } catch (final Exception e) {
                    if (Integer.MIN_VALUE <= expected && expected <= Integer.MAX_VALUE) {
                        counter.fail(e, "Unexpected error in %d %s %d", a, op, b);
                    }
                }
            }
        }
    }

    @Override
    protected void test() {
        counter.scope("Overflow tests", (Runnable) this::testOverflow);
        super.test();
        counter.scope("Parsing error tests", this::testParsingErrors);
    }


    @Override
    protected E parse(final String expression, final List<String> variables, final boolean reparse) {
        final String expr = expression.strip();
        if (expr.length() > 10) {
            for (final char ch : CHARS) {
                for (int i = 0; i < 10; i++) {
                    final int index = 1 + tester.random().nextInt(expr.length() - 2);
                    int pi = index - 1;
                    while (Character.isWhitespace(expr.charAt(pi))) {
                        pi--;
                    }
                    int ni = index;
                    while (Character.isWhitespace(expr.charAt(ni))) {
                        ni++;
                    }
                    final char pc = expr.charAt(pi);
                    final char nc = expr.charAt(ni);
                    if ("-(*".indexOf(nc) < 0 && nc != ch && pc != ch && !Character.isLetterOrDigit(nc) && nc != '$') {
                        final String input = expr.substring(0, index) + ch + expr.substring(index);
                        counter.shouldFail(
                                "Parsing error expected for " + expr.substring(0, index) + "<ERROR_INSERTED -->" + ch + "<-- ERROR_INSERTED>" + expr.substring(index),
                                () -> kind.parse(input, variables)
                        );
                        break;
                    }
                }
            }
        }

        return counter.testV(() -> counter.call("parse", () -> kind.parse(expr, variables)));
    }
}
