package prtest.map;

import base.TestCounter;
import prtest.PrologTest;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Common tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#prolog-map">Prolog Map</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class PrologMapTest extends PrologTest {
    private final boolean updates;
    private final boolean sorted;
    private final MapChecker<?> test;

    public PrologMapTest(
            final TestCounter counter,
            final boolean updates,
            final boolean sorted,
            final Path file,
            final Function<PrologMapTest, MapChecker<?>> testFactory
    ) {
        super(counter, file);
        this.updates = updates;
        this.sorted = sorted;
        test = testFactory.apply(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + updates + ")";
    }

    @Override
    public void test() {
        for (int i = 0; i < 10; i++) {
            test.check(new Settings(counter, i, 10, updates ? 10 : 0, sorted, true));
        }
        test.check(new Settings(counter, 100, 10000, updates ? 100 / TestCounter.DENOMINATOR2 : 0, sorted, false));
        test.check(new Settings(counter, 200, 10000, 0, sorted, false));
    }

    protected void check(final Runnable check) {
        counter.test(check);
    }

    public static <M> void test(
            final TestCounter counter,
            final Path file,
            final boolean updates,
            final boolean sorted,
            final Consumer<MapChecker<M>> addTests,
            final Function<PrologMapTest, MapChecker<M>> testFactory
    ) {
        final Function<PrologMapTest, MapChecker<?>> newFactory = test -> {
            final MapChecker<M> tests = testFactory.apply(test);
            addTests.accept(tests);
            return tests;
        };
        new PrologMapTest(counter, updates, sorted, file, newFactory).run(PrologMapTest.class);
    }
}
