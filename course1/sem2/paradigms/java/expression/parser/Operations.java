package expression.parser;

import expression.ToMiniString;
import expression.common.ExpressionKind;
import expression.common.Reason;

import java.math.BigInteger;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.stream.LongStream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Operations {
    public static final Operation NEGATE = unary("-", a -> -a);
    @SuppressWarnings("Convert2MethodRef")
    public static final Operation ADD       = binary("+", 1600, (a, b) -> a + b);
    public static final Operation SUBTRACT  = binary("-", 1602, (a, b) -> a - b);
    public static final Operation MULTIPLY  = binary("*", 2001, (a, b) -> a * b);
    public static final Operation DIVIDE    = binary("/", 2002, (a, b) -> b == 0 ? Reason.DBZ.error() : a / b);

    public static final Operation SET = binary("set", 202, (a, b) -> a | (1 << b));
    @SuppressWarnings("IntegerMultiplicationImplicitCastToLong")
    public static final Operation CLEAR = binary("clear", 202, (a, b) -> a & ~(1 << b));
    public static final Operation COUNT = unary("count", a -> Integer.bitCount((int) a));

    public static final Operation GCD = binary("gcd", 601, Operations::gcd);
    public static final Operation LCM = binary("lcm", 601, (a, b) -> {
        if (a == 0 && b == 0) {
            return 0;
        }
        return a / gcd(a, b) * b;
    });

    private static int gcd(final long a, final long b) {
        return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).intValue();
    }

    public static final Operation REVERSE = unary("reverse", v -> reduceDigits(v, (a, b) -> a * 10 + b));
    private static long reduceDigits(final long v, final LongBinaryOperator op) {
        return LongStream.iterate(v, n -> n != 0, n -> n / 10).map(n -> n % 10).reduce(0, op);
    }

    private static Operation checkedPowN(final String name, final int base, final int limit) {
        return unary(name, NEG_POW.less(0, Reason.OVERFLOW.greater(limit, a -> (long) Math.pow(base, a))));
    }
    private static final Reason NEG_POW = new Reason("Exponentiation to negative power");
    public static final Operation CHECKED_POW_10 = checkedPowN("pow10", 10, 9);

    private static Operation checkedLogN(final String name, final DoubleUnaryOperator op) {
        return unary(name, NEG_LOG.less(1, a -> (long) op.applyAsDouble(a)));
    }
    private static final Reason NEG_LOG = new Reason("Logarithm of negative value");
    public static final Operation CHECKED_LOG_10 = checkedLogN("log10", Math::log10);


    private Operations() {
    }

    @FunctionalInterface
    public interface Operation extends Consumer<ParserTester> {}

    public static Operation unary(final String name, final LongUnaryOperator op) {
        return tests -> tests.unary(name, op);
    }

    public static Operation binary(final String name, final int priority, final LongBinaryOperator op) {
        return tests -> tests.binary(name, priority, op);
    }

    public static <E extends ToMiniString, C> Operation kind(
            final ExpressionKind<E, C> kind,
            final ParserTestSet.Parser<E> parser
    ) {
        return factory -> factory.kind(kind, parser);
    }
}
