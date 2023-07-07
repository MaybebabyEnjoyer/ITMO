package cljtest.linear;

import base.Asserts;
import base.ExtendedRandom;
import base.TestCounter;
import cljtest.ClojureScript;
import clojure.lang.IPersistentVector;
import jstest.Engine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Clojure bridge.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Item {
    Item ZERO = value(0);

    int dim();

    boolean isValid();

    Item refill(ExtendedRandom random);

    Engine.Result<?> toClojure();

    default Value mapValue(final DoubleUnaryOperator f) {
        return value(f.applyAsDouble(value()));
    }


    default Vector map(final Function<Item, Item> f) {
        throw new UnsupportedOperationException("map");
    }

    default int size() { throw new UnsupportedOperationException("size"); }

    default Item get(final int index) { throw new UnsupportedOperationException("get"); }

    default double value() {
        throw new UnsupportedOperationException("getValue");
    }

    static Stream<Item> args(final int argc, final Item shape, final ExtendedRandom random) {
        return Stream.generate(() -> shape.refill(random)).limit(argc);
    }

    static Item fromClojure(final Object value) {
        if (value instanceof Number) {
            return value(((Number) value).doubleValue());
        } else if (value instanceof IPersistentVector) {
            final IPersistentVector vector = (IPersistentVector) value;
            return vector(IntStream.range(0, vector.length()).mapToObj(vector::nth).map(Item::fromClojure));
        } else {
            throw new AssertionError(value == null ? "null result" : "Unknown type " + value.getClass().getSimpleName());
        }
    }

    static Vector vector(final Stream<? extends Item> items) {
        return new Vector(items(items));
    }

    static Value value(final double value) {
        return new Value(value);
    }

    static List<Item> items(final Stream<? extends Item> items) {
        return items.collect(Collectors.toUnmodifiableList());
    }

    static Supplier<Item> generator(final int... dims) {
        Supplier<Item> generator = () -> ZERO;
        for (int i = dims.length - 1; i >= 0; i--) {
            final int dim = dims[i];
            final Supplier<Item> gen = generator;
            generator = () -> vector(Stream.generate(gen).limit(dim));
        }
        return generator;
    }

    static Engine.Result<?>[] toClojure(final List<Item> args) {
        return toArray(args.stream().map(Item::toClojure));
    }

    static Engine.Result<?>[] toArray(final Stream<? extends Engine.Result<?>> resultStream) {
        return resultStream.toArray(Engine.Result<?>[]::new);
    }

    static List<Fun> functions(final String prefix) {
        return functions(prefix, Operation.values());
    }

    static List<Fun> functions(final String prefix, final Operation... ops) {
        return Arrays.stream(ops).map(op -> op.function(prefix)).collect(Collectors.toUnmodifiableList());
    }

    final class Value implements Item {
        private final double value;

        public Value(final double value) {
            this.value = value;
        }

        public boolean isValid() {
            return Double.isFinite(value);
        }

        @Override
        public int dim() {
            return 0;
        }

        public double value() {
            return value;
        }

        @Override
        public Value refill(final ExtendedRandom random) {
            return new Value((random.nextInt(99) + 1) / 10.0);
        }

        @Override
        public Engine.Result<?> toClojure() {
            return LinearTester.number(value);
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof Value && Asserts.isEqual(value, ((Value) obj).value, 1e-7);
        }

        @Override
        public String toString() {
            return Double.toString(value);
        }
    }

    final class Vector implements Item {
        private final List<Item> items;
        private final int dim;

        private Vector(final List<Item> items) {
            this.items = items;
            dim = items.stream().mapToInt(Item::dim).max().orElse(0) + 1;
        }

        @Override
        public boolean isValid() {
            return items.stream().allMatch(Item::isValid);
        }

        @Override
        public int dim() {
            return dim;
        }

        public int size() {
            return items.size();
        }

        public Item get(final int index) {
            return items.get(index);
        }

        @Override
        public Vector refill(final ExtendedRandom random) {
            return vector(items.stream().map(item -> item.refill(random)));
        }

        @Override
        public Engine.Result<?> toClojure() {
            return LinearTester.vector(toArray(items.stream().map(Item::toClojure)));
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof Vector && items.equals(((Vector) obj).items);
        }

        @Override
        public String toString() {
            return items.stream().map(Item::toString).collect(Collectors.joining(", ", "[", "]"));
        }

        @Override
        public Vector map(final Function<Item, Item> f) {
            return vector(items.stream().map(f));
        }
    }

    class Fun {
        private final Function<List<Item>, Item> expected;
        private final ClojureScript.F<?> actual;

        public Fun(final String name, final Function<List<Item>, Item> implementation) {
            expected = implementation;
            actual = ClojureScript.function(name, Object.class);
        }

        public void test(final TestCounter counter, final Stream<Item> argStream) {
            final List<Item> args = items(argStream);
            test(counter, args, args);
        }

        public void test(final TestCounter counter, final List<Item> args, final List<Item> fakeArgs) {
            final Item expected = this.expected.apply(fakeArgs);
//            if (!expected.isValid()) {
//                return;
//            }

            test(counter, () -> {
                final Engine.Result<?> result = actual.call(toClojure(args));
                final Item actual = fromClojure(result.value);
                if (!expected.equals(actual)) {
                    throw new AssertionError(result.context + ": expected " + expected + ", found " + actual);
                }
            });

//            System.err.println("Testing? " + result.context);
        }

        private static void test(final TestCounter counter, final Runnable action) {
            counter.test(() -> {
                if (counter.getTestNo() % 1000 == 0) {
                    counter.println("Test " + counter.getTestNo());
                }
                action.run();
            });
        }

        public void test(final int args, final Item shape, final TestCounter counter, final ExtendedRandom random) {
            test(Collections.nCopies(args, shape), counter, random);
        }

        public void test(final List<Item> shapes, final TestCounter counter, final ExtendedRandom random) {
            test(counter, shapes.stream().map(shape -> shape.refill(random)));
        }

        public void expectException(final TestCounter counter, final Stream<Item> items) {
            expectException(counter, toClojure(items.collect(Collectors.toUnmodifiableList())));
        }

        protected void expectException(final TestCounter counter, final Engine.Result<?>... args) {
            test(counter, () -> {
                final Engine.Result<Throwable> result = actual.expectException(args);
                Asserts.assertTrue(
                        "AssertionError expected instead of " + result.value + " in " + result.context,
                        result.value instanceof AssertionError
                );
            });
        }
    }

    enum Operation {
        ADD("+", (a, b) -> a + b, a -> a),
        SUB("-", (a, b) -> a - b, a -> -a),
        MUL("*", (a, b) -> a * b, a -> a),
        DIV("d", (a, b) -> a / b, a -> 1 / a);

        private final String suffix;
        private final DoubleBinaryOperator binary;
        private final DoubleUnaryOperator unary;

        Operation(final String suffix, final DoubleBinaryOperator binary, final DoubleUnaryOperator unary) {
            this.suffix = suffix;
            this.binary = binary;
            this.unary = unary;
        }

        private Item apply(final List<Item> args) {
            final Item first = args.get(0);
            if (first instanceof Value) {
                return value(args.size() == 1
                        ? unary.applyAsDouble(first.value())
                        : args.stream().map(Value.class::cast).mapToDouble(Value::value).reduce(binary).getAsDouble());
            } else {
                return vector(IntStream.range(0, first.size())
                        .mapToObj(i -> apply(items(args.stream().map(Vector.class::cast).map(arg -> arg.get(i))))));
            }
        }

        private Fun function(final String prefix) {
            return new Fun(prefix + suffix, this::apply);
        }
    }
}
