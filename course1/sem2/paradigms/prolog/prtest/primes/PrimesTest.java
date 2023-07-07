package prtest.primes;

import base.Selector;
import base.TestCounter;
import prtest.Rule;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#prolog-map">Prolog Primes</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class PrimesTest {

    // Square
    private static void square(final PrimesTester t) {
        t.checkDivisors(new Rule("square_divisors", 2), true, s -> s.flatMap(v -> IntStream.of(v, v)));
    }

    // Cube
    private static void cube(final PrimesTester t) {
        t.checkDivisors(new Rule("cube_divisors", 2), true, s -> s.flatMap(v -> IntStream.of(v, v, v)));
    }

    // Compact
    private static void compact(final PrimesTester t) {
        t.checkDivisorsG(
                new Rule("compact_prime_divisors", 2),
                true,
                divisors -> compact(divisors).collect(Collectors.toUnmodifiableList())
        );
    }

    private static Stream<Map.Entry<Integer, Long>> compact(final IntStream divisors) {
        return divisors.boxed().collect(Collectors.groupingBy(
                Function.identity(),
                TreeMap::new,
                Collectors.counting()
        )).entrySet().stream();
    }

    // Divisors
    private static void divisors(final PrimesTester t) {
        t.checkDivisorsG(
                new Rule("divisors_divisors", 2),
                false,
                divisors -> compact(divisors)
                        .map(e -> {
                            final int n = e.getValue().intValue();
                            final List<Integer> repeated = Collections.nCopies(n, e.getKey());

                            return IntStream.rangeClosed(0, n).boxed()
                                    .map(i -> repeated.subList(0, i))
                                    .collect(Collectors.toUnmodifiableSet());
                        })
                        .reduce((as, bs) -> as.stream()
                                .flatMap(a -> bs.stream()
                                        .map(b -> Stream.concat(a.stream(), b.stream())
                                                .collect(Collectors.toUnmodifiableList())))
                                .collect(Collectors.toUnmodifiableSet()))
                        .orElse(Set.of(List.of()))
        );
    }

    public static final Selector SELECTOR = new Selector(PrimesTest.class, "easy", "hard", "bonus")
            .variant("Primes", variant(t -> {}))
            .variant("Square", variant(PrimesTest::square))
            .variant("Cube", variant(PrimesTest::cube))
            .variant("Compact", variant(PrimesTest::compact))
            .variant("Divisors", variant(PrimesTest::divisors))
            ;

    private PrimesTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }

    /* package-private */ static Consumer<TestCounter> variant(final Consumer<PrimesTester> check) {
        return counter -> {
            final int mode = counter.mode();
            final int max = (int) (1000 * Math.pow(100.0 / TestCounter.DENOMINATOR, mode));
            new PrimesTester(counter, max, mode > 0, check).test();
        };
    }
}
