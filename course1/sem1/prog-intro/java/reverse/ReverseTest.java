package reverse;

import base.ExtendedRandom;
import base.Named;
import base.Selector;
import base.TestCounter;
import reverse.ReverseTester.Op;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntToLongFunction;
import java.util.function.LongBinaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Tests for {@code Reverse} homework.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ReverseTest {
    @FunctionalInterface
    interface LongTernaryOperator {
        long applyAsLong(long a, long b, long c);
    }

    private static final Named<Op> REVERSE = Named.of("", ReverseTester::transform);

    public static final int MAX_SIZE = 10_000 / TestCounter.DENOMINATOR;
    private static final Named<Op> SUM = cross("Sum", 0, Long::sum, (r, c, v) -> r + c - v);

    private static long[][] cross(
            final int[][] ints,
            final IntToLongFunction map,
            final LongBinaryOperator reduce,
            final int zero,
            final LongTernaryOperator get
    ) {
        // This code is intentionally obscure
        final long[] rt = Arrays.stream(ints)
                .map(Arrays::stream)
                .mapToLong(row -> row.mapToLong(map).reduce(zero, reduce))
                .toArray();
        final long[] ct = new long[Arrays.stream(ints).mapToInt(r -> r.length).max().orElse(0)];
        Arrays.fill(ct, zero);
        Arrays.stream(ints).forEach(r -> IntStream.range(0, r.length)
                .forEach(i -> ct[i] = reduce.applyAsLong(ct[i], map.applyAsLong(r[i]))));
        return IntStream.range(0, ints.length)
                .mapToObj(r -> IntStream.range(0, ints[r].length)
                        .mapToLong(c -> get.applyAsLong(rt[r], ct[c], ints[r][c]))
                        .toArray())
                .toArray(long[][]::new);
    }

    private static Named<Op> cross(
            final String name,
            final int zero,
            final LongBinaryOperator reduce,
            final LongTernaryOperator get
    ) {
        return Named.of(name, ints -> cross(ints, n -> n, reduce, zero, get));
    }

    private static final Named<Op> AVG = avg(
            "Avg",
            ints -> cross(ints, n -> n, Long::sum, 0, (r, c, v) -> r + c - v),
            ints -> cross(ints, n -> 1, Long::sum, 0, (r1, c1, v1) -> r1 + c1 - 1)
    );

    private static Named<Op> avg(
            final String name,
            final Op fs,
            final Op fc
    ) {
        return Named.of(name, ints -> avg(ints, fs.apply(ints), fc.apply(ints)));
    }

    private static long[][] avg(final int[][] ints, final long[][] as, final long[][] ac) {
        return IntStream.range(0, ints.length).mapToObj(i -> IntStream.range(0, ints[i].length)
                        .mapToLong(j -> as[i][j] / ac[i][j])
                        .toArray())
                .toArray(long[][]::new);
    }

    private static String toAbc(final int value) {
        final char[] chars = Integer.toString(value).toCharArray();
        for (int i = value < 0 ? 1 : 0; i < chars.length; i++) {
            //noinspection ImplicitNumericConversion
            chars[i] += 49;
        }
        return new String(chars);
    }

    private static Named<Op> advanced(final String name, final Function<int[][], Stream<IntStream>> transform) {
        return Named.of(
                name,
                ints -> ReverseTester.transform(transform.apply(ints).map(IntStream::toArray).toArray(int[][]::new))
        );
    }

    private static final Named<Op> EVEN = advanced("Even", ints -> Stream.of(ints)
            .map(row -> Arrays.stream(row).filter(v -> v % 2 == 0)));
    private static final Named<Op> TRANSPOSE = Named.of("Transpose", ints -> {
        final List<int[]> rows = new ArrayList<>(List.of(ints));
        return IntStream.range(0, Arrays.stream(ints).mapToInt(r -> r.length).max().orElse(0))
                .mapToObj(c -> {
                    rows.removeIf(r -> r.length <= c);
                    return rows.stream().mapToLong(r -> r[c]).toArray();
                })
                .toArray(long[][]::new);
    });

    private static final Named<BiFunction<ExtendedRandom, Integer, String>> ABC = Named.of("Abc", (r, i) -> toAbc(i));

    private static final Named<BiFunction<ExtendedRandom, Integer, String>> OCT_IN = Named.of("Oct", (r, i) -> Integer.toOctalString(i));
    private static final Named<BiFunction<ExtendedRandom, Integer, String>> OCT_DEC = Named.of("OctDec", (r, i) ->
            r.nextBoolean()
            ? Integer.toString(i)
            : Integer.toOctalString(i) + (r.nextBoolean() ? "o" : "O"));
    private static final Named<BiFunction<ExtendedRandom, Integer, String>> DEC = Named.of("", (r, i) -> Integer.toString(i));
    private static final Named<BiFunction<ExtendedRandom, Integer, String>> OCT_ABC = Named.of("OctAbc", (r, i) ->
            r.nextInt(10) == 0 ? toAbc(i) :
            r.nextInt(10) > 0 ? Integer.toString(i) :
            Integer.toOctalString(i) + (r.nextBoolean() ? "o" : "O"));

    public static final Selector SELECTOR = selector(ReverseTest.class, MAX_SIZE);

    private ReverseTest() {
        // Utility class
    }

    public static Selector selector(final Class<?> owner, final int maxSize) {
        return new Selector(owner)
                .variant("Base", ReverseTester.variant(maxSize, REVERSE))
                .variant("Sum", ReverseTester.variant(maxSize, SUM))
                .variant("Avg", ReverseTester.variant(maxSize, AVG))
                .variant("Even", ReverseTester.variant(maxSize, EVEN))
                .variant("Transpose", ReverseTester.variant(maxSize, TRANSPOSE))
                .variant("OctDec",      ReverseTester.variant(maxSize, "", OCT_DEC, DEC))
                .variant("OctAbc",      ReverseTester.variant(maxSize, "", OCT_ABC, DEC))
                .variant("Oct",         ReverseTester.variant(maxSize, "", OCT_IN, OCT_IN))
                .variant("Abc",         ReverseTester.variant(maxSize, "", ABC, ABC))

                ;
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
