package expression.generic;

import base.Selector;
import expression.common.Op;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.*;
import java.util.stream.IntStream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class GenericTest {
    private static final Consumer<GenericTester> ADD = binary("+", 200);
    private static final Consumer<GenericTester> SUBTRACT = binary("-", -200);
    private static final Consumer<GenericTester> MULTIPLY = binary("*", 301);
    private static final Consumer<GenericTester> DIVIDE = binary("/", -300);
    private static final Consumer<GenericTester> NEGATE = unary("-");

    private static Integer i(final long v) {
        if (v != (int) v) {
            throw new ArithmeticException("Overflow");
        }
        return (int) v;
    }
    private static final Mode<Integer> INTEGER_CHECKED = mode("i", c -> c)
            .binary("+", (a, b) -> i(a + (long) b))
            .binary("-", (a, b) -> i(a - (long) b))
            .binary("*", (a, b) -> i(a * (long) b))
            .binary("/", (a, b) -> i(a / (long) b))
            .unary("-", a -> i(- (long) a))

            .unary("abs", a -> i(Math.abs((long) a)))
            .unary("square", a -> i(a * (long) a))
            .binary("mod", (a, b) -> i(a % (long) b))
            ;

    @SuppressWarnings("Convert2MethodRef")
    private static final Mode<Double> DOUBLE = mode("d", c -> (double) c)
            .binary("+", (a, b) -> a + b)
            .binary("-", (a, b) -> a - b)
            .binary("*", (a, b) -> a * b)
            .binary("/", (a, b) -> a / b)
            .unary("-", a -> -a)

            .unary("abs", Math::abs)
            .unary("square", a -> a * a)
            .binary("mod", (a, b) -> a % b)
            ;

    private static final Mode<BigInteger> BIG_INTEGER = mode("bi", BigInteger::valueOf)
            .binary("+", BigInteger::add)
            .binary("-", BigInteger::subtract)
            .binary("*", BigInteger::multiply)
            .binary("/", BigInteger::divide)
            .unary("-", BigInteger::negate)

            .unary("abs", BigInteger::abs)
            .unary("square", a -> a.multiply(a))
            .binary("mod", BigInteger::mod)
            ;

    private static final int PRIME = 10079;
    private static final int[] PRIME_INVERSES = IntStream.range(0, PRIME)
            .map(a -> IntStream.iterate(a, r -> prime(r * a)).skip(PRIME - 3).findFirst().orElseThrow())
            .toArray();
    private static int prime(final int v) {
        return v >= 0 ? v % PRIME : PRIME + v % PRIME;
    }
    private static final Mode<Integer> INTEGER_PRIME = mode("p", GenericTest::prime)
            .binary("+", (a, b) -> prime(a + b))
            .binary("-", (a, b) -> prime(a - b))
            .binary("*", (a, b) -> prime(a * b))
            .binary("/", (a, b) -> {
                if (b == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return prime(a * PRIME_INVERSES[b]);
            })
            .unary("-", a -> prime(-a))

            .unary("abs", a -> a)
            .unary("square", a -> prime(a * a))
            .binary("mod", (a, b) -> prime(a % b))

            .unary("count", Integer::bitCount)
            .binary("min", Math::min)
            .binary("max", Math::max)
            ;

    private GenericTest() {
    }

    /* package-private */ static Consumer<GenericTester> unary(final String name) {
        return tester -> tester.unary(name);
    }

    /* package-private */ static Consumer<GenericTester> binary(final String name, final int priority) {
        return tester -> tester.binary(name, priority);
    }

    private static final Consumer<GenericTester> ABS = unary("abs");
    private static final Consumer<GenericTester> SQUARE = unary("square");
    private static final Consumer<GenericTester> MOD = binary("mod", -300);

    @SuppressWarnings("Convert2MethodRef")
    private static final Mode<Integer> INTEGER_UNCHECKED = mode("u", c -> c)
            .binary("+", (a, b) -> a + b)
            .binary("-", (a, b) -> a - b)
            .binary("*", (a, b) -> a * b)
            .binary("/", (a, b) -> a / b)
            .unary("-", a -> -a)

            .unary("abs", Math::abs)
            .unary("square", a -> a * a)
            .binary("mod", (a, b) -> a % b)
            ;

    @SuppressWarnings("Convert2MethodRef")
    private static final Mode<Long> LONG = mode("l", c -> (long) c)
            .binary("+", (a, b) -> a + b)
            .binary("-", (a, b) -> a - b)
            .binary("*", (a, b) -> a * b)
            .binary("/", (a, b) -> a / b)
            .unary("-", a -> -a)

            .unary("abs", Math::abs)
            .unary("square", a -> a * a)
            .binary("mod", (a, b) -> a % b)
            ;


    private static short s(final int x) {
        return (short) x;
    }
    private static final Mode<Short> SHORT = mode("s", c -> (short) c, c -> (short) c)
            .binary("+", (a, b) -> s(a + b))
            .binary("-", (a, b) -> s(a - b))
            .binary("*", (a, b) -> s(a * b))
            .binary("/", (a, b) -> s(a / b))
            .unary("-", a -> s(-a))

            .unary("abs", a -> s(Math.abs(a)))
            .unary("square", a -> s(a * a))
            .binary("mod", (a, b) -> s(a % b))
            ;

    @SuppressWarnings("Convert2MethodRef")
    private static final Mode<Float> FLOAT = mode("f", c -> (float) c)
            .binary("+", (a, b) -> a + b)
            .binary("-", (a, b) -> a - b)
            .binary("*", (a, b) -> a * b)
            .binary("/", (a, b) -> a / b)
            .unary("-", a -> -a)

            .unary("abs", Math::abs)
            .unary("square", a -> a * a)
            .binary("mod", (a, b) -> a % b)

            .unary("count", a -> (float) Integer.bitCount(Float.floatToIntBits(a)))
            .binary("min", Math::min)
            .binary("max", Math::max)
            ;


    public static final Selector SELECTOR = Selector.composite(GenericTest.class, GenericTester::new, "easy", "hard")
            .variant("Base", INTEGER_CHECKED, DOUBLE, BIG_INTEGER, ADD, SUBTRACT, MULTIPLY, DIVIDE, NEGATE)
            .variant("Ufs", INTEGER_UNCHECKED, FLOAT, SHORT)
            .variant("AsmUls", ABS, SQUARE, MOD, INTEGER_UNCHECKED, LONG, SHORT)
            .variant("AsmUps", ABS, SQUARE, MOD, INTEGER_UNCHECKED, INTEGER_PRIME, SHORT)
            .selector();

    private static <T> Mode<T> mode(final String mode, final IntFunction<T> constant) {
        return new Mode<>(mode, constant, IntUnaryOperator.identity());
    }

    private static <T> Mode<T> mode(final String mode, final IntFunction<T> constant, final IntUnaryOperator fixer) {
        return new Mode<>(mode, constant, fixer);
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }


    /* package-private */ static class Mode<T> implements Consumer<GenericTester> {
        private final String mode;
        private final IntFunction<T> constant;
        private final List<Op<UnaryOperator<GenericTester.F<T>>>> unary = new ArrayList<>();
        private final List<Op<BinaryOperator<GenericTester.F<T>>>> binary = new ArrayList<>();
        private final IntUnaryOperator fixer;

        public Mode(final String mode, final IntFunction<T> constant, final IntUnaryOperator fixer) {
            this.mode = mode;
            this.constant = constant;
            this.fixer = fixer;
        }

        public Mode<T> unary(final String name, final UnaryOperator<T> op) {
            unary.add(Op.of(name, arg -> (x, y, z) -> op.apply(arg.apply(x, y, z))));
            return this;
        }

        public Mode<T> binary(final String name, final BinaryOperator<T> op) {
            binary.add(Op.of(name, (a, b) -> (x, y, z) -> op.apply(a.apply(x, y, z), b.apply(x, y, z))));
            return this;
        }

        @Override
        public void accept(final GenericTester tester) {
            tester.mode(mode, constant, unary, binary, fixer);
        }
    }
}
