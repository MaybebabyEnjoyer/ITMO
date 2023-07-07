package sum;

import base.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class SumTest {
    @FunctionalInterface
    /* package-private */ interface Op<T extends Number> extends UnaryOperator<SumTester<T>> {}

    private static final BiConsumer<Number, String> TO_STRING = (expected, out) -> Asserts.assertEquals("Sum", expected.toString(), out);

    private static final Named<Supplier<SumTester<Integer>>> BASE = Named.of("", () -> new SumTester<>(
            Integer::sum, n -> (int) n, (r, max) -> r.nextInt() % max, TO_STRING,
            10, 100, Integer.MAX_VALUE
    ));

    /* package-private */ static <T extends Number> Named<Op<T>> plain() {
        return Named.of("", test -> test);
    }

    /* package-private */ static <T extends Number> Consumer<TestCounter> variant(
            final Named<Function<String, Runner>> runner,
            final Named<Supplier<SumTester<T>>> test,
            final Named<? extends Function<? super SumTester<T>, ? extends SumTester<?>>> modifier
    ) {
        return counter -> modifier.getValue().apply(test.getValue().get())
                .test("Sum" + test.getName() + modifier.getName() + runner.getName(), counter, runner.getValue());
    }

    private static <T extends Number> Named<Op<T>> octal(final Function<T, String> toOctal) {
        //noinspection OctalInteger,StringConcatenationMissingWhitespace
        return Named.of("Octal", test -> test
                .test(1, "1o")
                .test(017, "17o")
                .testSpaces(6, " 1o 2o 3O ")
                .test(01234567, "1234567O")

                .test(Integer.MIN_VALUE, "-0" + String.valueOf(Integer.MIN_VALUE).substring(1))
                .test(Integer.MAX_VALUE, "0" + Integer.MAX_VALUE)
                .test(Integer.MAX_VALUE, Integer.toOctalString(Integer.MAX_VALUE) + "o")
                .test(Integer.MAX_VALUE, "0" + Integer.toOctalString(Integer.MAX_VALUE) + "O")
                .setToString(number -> {
                    final int hashCode = number.hashCode();
                    if ((hashCode & 1) == 0) {
                        return number.toString();
                    }

                    final String lower = toOctal.apply(number).toLowerCase(Locale.ROOT) + "o";
                    return (hashCode & 2) == 0 ? lower : lower.toUpperCase(Locale.ROOT);
                })
        );
    }

    private static final Map<Integer, List<String>> LOCAL_DIGITS = IntStream.range(0, Character.MAX_VALUE)
            .filter(Character::isDigit)
            .boxed()
            .collect(Collectors.groupingBy(
                    c -> (int) "0123456789".charAt(Character.getNumericValue(c)),
                    Collectors.mapping(c -> String.valueOf((char) c.intValue()), Collectors.toList())
            ));

    private static <T extends Number> Named<Op<T>> local(final Named<Op<T>> inner) {
        return compose("", inner, test -> test.setToString((r, n) -> n.toString().chars()
                .mapToObj(c -> {
                    final List<String> items = LOCAL_DIGITS.get(c);
                    return items == null ? String.valueOf((char) c) : r.randomItem(items);
                })
                .collect(Collectors.joining())));
    }

    private static <T extends Number> Named<Op<T>> compose(
            final String prefix,
            final Named<Op<T>> inner,
            final Op<T> outer
    ) {
        return Named.of(prefix + inner.getName(), t -> outer.apply(inner.getValue().apply(t)));
    }

    private static final Named<Supplier<SumTester<Long>>> LONG = Named.of("Long", () -> new SumTester<>(
            Long::sum, n -> n, (r, max) -> r.getRandom().nextLong() % max, TO_STRING,
            10L, 100L, (long) Integer.MAX_VALUE, Long.MAX_VALUE)
            .test(12345678901234567L, " +12345678901234567 ")
            .test(0L, " +12345678901234567 -12345678901234567")
            .test(0L, " +12345678901234567 -12345678901234567"));
    
    private static final Named<Supplier<SumTester<Double>>> DOUBLE = Named.of("Double", () -> new SumTester<>(
            Double::sum, n -> (double) n, (r, max) -> (r.getRandom().nextDouble() - 0.5) * 2 * max,
            approximate(Double::parseDouble, 1e-10),
            10.0, 0.01, 1e20, 1e100, Double.MAX_VALUE / 10000)
            .test(5, "2.5 2.5")
            .test(0, "1e100 -1e100")
            .testT(2e100, "1.5e100 0.5e100"));

    private static final Named<Supplier<SumTester<Float>>> FLOAT = Named.of("Float", () -> new SumTester<>(
            Float::sum, n -> (float) n, (r, max) -> (r.getRandom().nextFloat() - 0.5f) * 2 * max,
            approximate(Float::parseFloat, 1e-5),
            10.0f, 0.01f, 1e20f, Float.MAX_VALUE / 10000)
            .test(5, "2.5 2.5")
            .test(0, "1e10 -1e10")
            .testT(2e10f, "1.5e10 0.5E10"));

    private static BiConsumer<Number, String> approximate(final Function<String, Number> parser, final double precision) {
        return (expected, out) ->
                Asserts.assertEquals("Sum", expected.doubleValue(), parser.apply(out).doubleValue(), precision);
    }

    /* package-private */ static final Named<Function<String, Runner>> RUNNER =
            Named.of("", Runner.packages("", "sum")::args);

    public static final Selector SELECTOR = selector(SumTest.class, RUNNER);

    private SumTest() {
        // Utility class
    }

    public static Selector selector(final Class<?> owner, final Named<Function<String, Runner>> runner) {
        return new Selector(owner)
                .variant("Base",            variant(runner, BASE, plain()))
                .variant("Octal",           variant(runner, BASE, local(octal(Integer::toOctalString))))
                .variant("LongOctal",       variant(runner, LONG, local(octal(Long::toOctalString))))
                .variant("Float",           variant(runner, FLOAT, plain()))
                .variant("Double",          variant(runner, DOUBLE, plain()))
                ;
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
