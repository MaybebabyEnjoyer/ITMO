package expression.exceptions;

import base.TestCounter;
import expression.common.Op;
import expression.common.Reason;
import expression.parser.ParserTestSet;
import expression.parser.ParserTester;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ExceptionsTester extends ParserTester {
    /* package-private */ final List<Op<String>> parsingTest = new ArrayList<>(List.of(
            Op.of("No first argument", "* y * z"),
            Op.of("No middle argument", "x *  * z"),
            Op.of("No last argument", "x * y * "),
            Op.of("No first argument'", "1 + (* y * z) + 2"),
            Op.of("No middle argument'", "1 + (x *  / 9) + 3"),
            Op.of("No last argument'", "1 + (x * y - ) + 3"),
            Op.of("No opening parenthesis", "x * y)"),
            Op.of("No closing parenthesis", "(x * y"),
            Op.of("Start symbol", "@x * y"),
            Op.of("Middle symbol", "x @ * y"),
            Op.of("End symbol", "x * y@"),
            Op.of("Constant overflow 1", Integer.MIN_VALUE - 1L + ""),
            Op.of("Constant overflow 2", Integer.MAX_VALUE + 1L + ""),
            Op.of("Bare +", "+"),
            Op.of("Bare -", "-"),
            Op.of("Bare a", "a"),
            Op.of("(())", "(())"),
            Op.of("Spaces in numbers", "10 20")
    ));

    public ExceptionsTester(final TestCounter counter) {
        super(counter);
    }


    private void parsingTests(final String... tests) {
        for (final String test : tests) {
            parsingTest.add(Op.of(test, test));
        }
    }

    @Override
    public void unary(final String name, final LongUnaryOperator op) {
        parsingTests(name, name + "()", name + "(1, 2)", "1 * " + name, name + " * 1");
        if (!"-".equals(name)) {
            parsingTests(name + "x");
        }
        if (allLetterAndDigit(name)) {
            parsingTests(name + "1", name + "x");
        }
        super.unary(name, op);
    }

    @Override
    public void binary(final String name, final int priority, final LongBinaryOperator op) {
        parsingTests(name, "1 " + name, "1 " + name + " * 3");
        if (!"-".equals(name)) {
            parsingTests(name + " 1", "1 * " + name + " 2");
        }
        if (allLetterAndDigit(name)) {
            parsingTests("5" + name + "5", "1" + name + "x 1");
        }
        super.binary(name, priority, op);
    }

    private static boolean allLetterAndDigit(final String name) {
        return name.chars().allMatch(Character::isLetterOrDigit);
    }

    @Override
    protected void test(final ParserTestSet.ParsedKind<?, ?> kind) {
        new ExceptionsTestSet<>(this, kind).test();
    }

    @Override
    protected int cast(final long value) {
        return Reason.overflow(value);
    }
}
