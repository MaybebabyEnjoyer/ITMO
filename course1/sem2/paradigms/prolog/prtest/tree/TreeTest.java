package prtest.tree;

import base.Selector;
import base.TestCounter;
import prtest.Rule;
import prtest.Value;
import prtest.map.MapChecker;

import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.NavigableMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#prolog-map">Prolog Search Trees</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class TreeTest {
    private static final Consumer<MapChecker<Void>> PUT_IF_ABSENT = test -> test.updater(
            Rule.func("map_putIfAbsent", 3),
            (state, key, value) -> {
                final Value old = state.expected.putIfAbsent(key, value);
                if (old != null) {
                    state.values.remove(old);
                    state.values.add(value);
                }
            }
    );

    private static final Consumer<MapChecker<Void>> PUT_CEILING =
            getPut("Ceiling", NavigableMap::ceilingEntry);

    private static Consumer<MapChecker<Void>> getPut(
            final String name,
            final BiFunction<NavigableMap<Integer, Value>, Integer, Map.Entry<Integer, Value>> getter
    ) {
        return test -> {
            test.keyChecker(
                    Rule.func("map_get" + name, 2),
                    (map, key) -> {
                        final Map.Entry<Integer, Value> entry = getter.apply(map, key);
                        return entry == null ? null : entry.getValue();
                    }
            );
            test.updater(
                    Rule.func("map_put" + name, 3),
                    (state, key, value) -> {
                        final Map.Entry<Integer, Value> entry = getter.apply(state.expected, key);
                        if (entry != null) {
                            state.expected.put(entry.getKey(), value);
                        }
                    }
            );
        };
    }

    private static <T, R> R extract(final Function<T, R> extractor, final T value) {
        return value == null ? null : extractor.apply(value);
    }
    private static <T, R> Consumer<MapChecker<Void>> func(
            final String rule,
            final Function<NavigableMap<Integer, Value>, T> getter,
            final Function<T, R> extractor
    ) {
        return test -> test.checker(Rule.func("map_" + rule, 1), map -> extract(extractor, getter.apply(map)));
    }

    public static final Consumer<MapChecker<Void>> KEYS = func("keys", NavigableMap::keySet, List::copyOf);
    public static final Consumer<MapChecker<Void>> VALUES = func("values", NavigableMap::values, List::copyOf);

    public static final Selector SELECTOR = new Selector(TreeTest.class, "easy", "hard")
            .variant("base", variant(tests -> {}))
            .variant("Keys", variant(KEYS))
            .variant("Values", variant(VALUES))
            .variant("PutIfAbsent", variant(PUT_IF_ABSENT))
            .variant("PutCeiling", variant(PUT_CEILING))
            ;

    private TreeTest() {
    }



    @SafeVarargs
    /* package-private */ static Consumer<TestCounter> variant(final Consumer<MapChecker<Void>>... addTests) {
        return variant(false, addTests);
    }

    @SafeVarargs
    /* package-private */ static Consumer<TestCounter> variant(final boolean alwaysUpdate, final Consumer<MapChecker<Void>>... addTests) {
        return counter -> {
            final boolean hard = counter.mode() == 1;
            TreeTester.test(
                    counter, hard || alwaysUpdate, true,
                    tests -> {
                        if (!hard) {
                            tests.clearUpdaters();
                        }
                        Arrays.stream(addTests).forEachOrdered(adder -> adder.accept(tests));
                    }
            );
        };
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
