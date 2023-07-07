package prtest.primes;

import base.Asserts;
import base.TestCounter;
import prtest.PrologTest;
import prtest.PrologUtil;
import prtest.Rule;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.LongBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#prolog-map">Prolog Primes</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class PrimesTester extends PrologTest {
    private static final Rule INIT = new Rule("init", 1);
    private static final Rule PRIME = new Rule("prime", 1);
    private static final Rule COMPOSITE = new Rule("composite", 1);
    private static final Rule PRIME_DIVISORS = new Rule("prime_divisors", 2);
    private static final int MAX_Q = 100_000;

    public static double maxRatio = 0;
    public static String SUFFIX = ".pl";

    final int max;
    final int[] primes;
    final BitSet isPrime = new BitSet();

    private final Consumer<PrimesTester> check;
    public final boolean reversible;

    protected PrimesTester(
            final TestCounter counter,
            final int max,
            final boolean reversible,
            final Consumer<PrimesTester> check
    ) {
        super(counter, Path.of("primes" + SUFFIX));

        this.max = max;
        this.reversible = reversible;
        this.check = check;

        primes = primes(max);
        Arrays.stream(primes).forEach(isPrime::set);
    }

    public void test() {
        final long limit = PrologUtil.benchmark(counter) * 40;
        final long time = PrologUtil.measure(counter, "Tests", this::runTests);
        maxRatio = Math.max(maxRatio, time / (double) limit);
        Asserts.assertTrue(String.format("Time limit exceeded: %sms instead of %sms", time, limit), time <= limit);
    }

    public void runTests() {
        PrologUtil.measure(counter, "Init", () -> counter.println("Init: " + test(INIT, max)));
        PrologUtil.measure(counter, "checkPrimes", this::checkPrimes);
        PrologUtil.measure(counter, "checkComposites", this::checkComposites);

        PrologUtil.measure(counter, "prime_divisors", () -> checkDivisors(PRIME_DIVISORS, true, Function.identity()));
        PrologUtil.measure(counter, "check", () -> check.accept(this));
    }

    void checkDivisors(final Rule rule, final boolean reversible, final Function<IntStream, IntStream> solution) {
        checkDivisorsG(rule, reversible, divisors -> solution.apply(divisors).boxed().collect(Collectors.toUnmodifiableList()));
    }

    <D> void checkDivisorsG(final Rule rule, final boolean reversible, final Function<IntStream, Collection<D>> solution) {
        final Rule function = rule.func();
        final Rule reverse = rule.func(0);
        checkDivisors(i -> counter.test(() -> {
            final Collection<D> divisors = solution.apply(divisors(i));
            final boolean reverseFirst = random().nextBoolean();
            if (!reverseFirst) {
                assertResult(divisors, function, i);
            }

            if (this.reversible && reversible) {
                assertResult(i, reverse, divisors);
                final List<D> copy = new ArrayList<>(divisors);
                random().shuffle(copy);
                if (copy.hashCode() != divisors.hashCode()) {
                    assertResult(null, reverse, copy);
                }
            }

            if (reverseFirst) {
                assertResult(divisors, function, i);
            }
        }));
    }

    private void checkDivisors(final IntConsumer checker) {
        for (int i = 1; i < 10; i++) {
            checker.accept(i);
        }
        checker.accept(255);
        checker.accept(256);

        for (int i = 0; i < primes.length / 10; i++) {
            checker.accept(randomN());
        }
    }

    int randomN() {
        return random().nextInt(1, max);
    }

    private void checkComposites() {
        for (int i = 0; i < Math.min(primes.length, MAX_Q); i++) {
            checkPrime(randomN());
        }
    }

    private void checkPrimes() {
        for (int i = 0; i < primes.length; i++) {
            checkPrime(primes[i]);
            if (i > MAX_Q) {
                i += 5;
            }
        }
    }

    private void checkPrime(final int value) {
        if (value != 1) {
            counter.test(() -> {
                final boolean prime = isPrime.get(value);
                assertSuccess(prime, PRIME, value);
                assertSuccess(!prime, COMPOSITE, value);
            });
        }
    }

    private IntStream divisors(final int n) {
        final IntStream.Builder divisors = IntStream.builder();
        int value = n;
        for (final int prime : primes) {
            if (prime * prime > n) {
                break;
            }
            while (value % prime == 0) {
                divisors.add(prime);
                value /= prime;
            }
        }
        if (value > 1) {
            divisors.add(value);
        }
        return divisors.build();
    }

    protected void testBinary(final String name, final LongBinaryOperator op) {
        final Rule f = Rule.func(name, 2);

        for (int a = 1; a < 10; a++) {
            for (int b = 1; b < 10; b++) {
                assertResult(op, f, a, b);
            }
        }
        for (int i = 0; i < 1000; i++) {
            assertResult(op, f, randomN(), randomN());
        }
    }

    private void assertResult(final LongBinaryOperator op, final Rule f, final int a, final int b) {
        assertResult(op.applyAsLong(a, b), f, a, b);
    }

    private static int[] primes(final int max) {
        final int[] primes = new int[(int) (2 * max / Math.log(max))];
        primes[0] = 2;
        int t = 1;
        int h = 0;
        for (int i = 3; i <= max; i += 2) {
            primes[t++] = i;
            if (primes[h] * primes[h] <= i) {
                h++;
            }
            for (int j = 0; j < h; j++) {
                if (i % primes[j] == 0) {
                    t--;
                    break;
                }
            }
        }
        return Arrays.copyOf(primes, t);
    }
}
