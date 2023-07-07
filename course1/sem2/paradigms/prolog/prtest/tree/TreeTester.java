package prtest.tree;

import base.TestCounter;
import prtest.Rule;
import prtest.Value;
import prtest.map.Entry;
import prtest.map.MapChecker;
import prtest.map.PrologMapTest;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Tester for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#prolog-map">Prolog Search Trees</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class TreeTester {
    private static final Rule BUILD = Rule.func("map_build", 1);
    public static Path SOLUTION = Path.of("tree-map.pl");

    private TreeTester() {
    }

    public static void test(
            final TestCounter counter,
            final boolean updates,
            final boolean sorted,
            final Consumer<MapChecker<Void>> addTests
    ) {
        PrologMapTest.test(counter, SOLUTION, updates, sorted, addTests, test -> new MapChecker<>(
                test,
                entries -> null,
                getListValueFunction(test),
                (map, key, entry) -> {},
                (map, key) -> {},
                state -> state.keys.test().forEach(state::get)
        ));
    }

    private static Function<List<Entry>, Value> getListValueFunction(final PrologMapTest test) {
        return entries -> test.solveOne(BUILD, Value.list(entries, Entry::toValue)).value;
    }
}
