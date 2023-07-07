package sum;

import base.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class SumTester<T extends Number> {
    private static final List<String> SPACES = List.of(
            " \t\n\u000B\u2029\f",
            IntStream.rangeClosed(0, Character.MAX_VALUE)
                    .filter(Character::isWhitespace)
                    .mapToObj(Character::toString)
                    .collect(Collectors.joining())
    );

    private final BinaryOperator<T> add;
    private final LongFunction<T> fromLong;
    private BiFunction<ExtendedRandom, T, String> toString;
    private final BiFunction<ExtendedRandom, T, T> randomValue;
    private final BiConsumer<Number, String> verifier;
    private List<String> spaces;
    private final List<T> limits;

    private final List<Consumer<Checker>> tests = new ArrayList<>();

    @SafeVarargs
    public SumTester(
            final BinaryOperator<T> add,
            final LongFunction<T> fromLong,
            final BiFunction<ExtendedRandom, T, T> randomValue,
            final BiConsumer<Number, String> verifier,
            final T... limits
    ) {
        this.add = add;
        this.fromLong = fromLong;
        this.randomValue = randomValue;
        this.verifier = verifier;
        this.limits = List.of(limits);

        setSpaces(SPACES);
        setToString(String::valueOf);

        test(1, "1");
        test(6, "1", "2", "3");
        test(1, " 1");
        test(1, "1 ");
        test(1, " 1 ");
        test(12345, " 12345 ");
        test(60, "010", "020", "030");
        testSpaces(1368, " 123 456 789 ");
        test(-1, "-1");
        test(-6, "-1", "-2", "-3");
        test(-12345, " -12345 ");
        testSpaces(-1368, " -123 -456 -789 ");
        test(1, "+1");
        test(6, "+1", "+2", "+3");
        test(12345, " +12345 ");
        testSpaces(1368, " +123 +456 +789 ");
        test(0);
        testSpaces(0, " ", "  ");
    }

    protected SumTester<T> setSpaces(final List<String> spaces) {
        this.spaces = spaces;
        return this;
    }

    public SumTester<T> setToString(final Function<T, String> toString) {
        return setToString((r, n) -> toString.apply(n));
    }

    public SumTester<T> setToString(final BiFunction<ExtendedRandom, T, String> toString) {
        this.toString = toString;
        return this;
    }

    protected SumTester<T> test(final long result, final String... input) {
        return testT(fromLong.apply(result), input);
    }

    protected SumTester<T> testT(final T result, final String... input) {
        return testT(result, Arrays.asList(input));
    }

    private SumTester<T> testT(final T result, final List<String> input) {
        tests.add(checker -> checker.test(result, input));
        return this;
    }

    public SumTester<T> testSpaces(final long result, final String... input) {
        final T res = fromLong.apply(result);
        tests.add(checker -> spaces.stream()
                .flatMapToInt(String::chars)
                .forEach(space -> checker.test(
                        res,
                        Functional.map(Arrays.asList(input), s -> s.replace(' ', (char) space))
                ))
        );
        return this;
    }

    public void test(final String name, final TestCounter counter, final Function<String, Runner> runner) {
        new Checker(counter, runner.apply(name)).test();
    }

    private class Checker extends BaseChecker {
        private final Runner runner;

        public Checker(final TestCounter counter, final Runner runner) {
            super(counter);
            this.runner = runner;
        }

        public void test() {
            tests.forEach(test -> test.accept(this));

            for (final T limit : limits) {
                for (int n = 10; n <= 10_000 / TestCounter.DENOMINATOR; n *= 10) {
                    randomTest(n, limit);
                }
            }
        }

        private void test(final T result, final List<String> input) {
            counter.test(() -> {
                final List<String> out = runner.run(counter, input);
                Asserts.assertEquals("Single line expected", 1, out.size());
                verifier.accept(result, out.get(0));
            });
        }

        private void randomTest(final int numbers, final T max) {
            for (final String spaces : spaces) {
                randomTest(numbers, max, spaces);
            }
        }

        private void randomTest(final int numbers, final T max, final String spaces) {
            final List<T> values = new ArrayList<>();
            for (int i = 0; i < numbers; i++) {
                values.add(randomValue.apply(random(), max));
            }
            testRandom(values.stream().reduce(fromLong.apply(0), add), values, spaces);
        }

        private void testRandom(final T result, final List<T> args, final String spaces) {
            final List<String> spaced = args.stream()
                    .map(n -> toString.apply(random(), n))
                    .map(value -> randomSpace(spaces) + value + randomSpace(spaces))
                    .collect(Collectors.toList());
            final List<String> argsList = new ArrayList<>();
            for (final Iterator<String> i = spaced.listIterator(); i.hasNext(); ) {
                final StringBuilder next = new StringBuilder(i.next());
                while (i.hasNext() && random().nextBoolean()) {
                    next.append(randomSpace(spaces)).append(i.next());
                }
                argsList.add(next.toString());
            }
            test(result, argsList);
        }

        private String randomSpace(final String spaces) {
            return random().randomString(spaces);
        }
    }
}
