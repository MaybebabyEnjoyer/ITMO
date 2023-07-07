package prtest.map;

import prtest.Rule;
import prtest.Value;

import java.util.Arrays;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Prolog Map checker state.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class State<M> {
    private static final Rule GET = Rule.func("map_get", 2);

    private final PrologMapTest test;

    protected final Settings settings;
    public final SetHistory<Integer> keys;
    public final SetHistory<Value> values;
    public final NavigableMap<Integer, Value> expected;
    public Value actual;
    public final M model;

    public State(
            final PrologMapTest test, final Settings settings,
            final SetHistory<Integer> keys,
            final SetHistory<Value> values,
            final NavigableMap<Integer, Value> expected,
            final Value actual,
            final M model
    ) {
        this.test = test;
        this.settings = settings;
        this.keys = keys;
        this.values = values;
        this.expected = expected;
        this.actual = actual;
        this.model = model;
    }

    public void get(final int key) {
        test.assertResult(expected.get(key), GET, actual, key);
    }

    public int randomKey() {
        return keys.random();
    }

    private void update(final Rule rule, final Object... args) {
        settings.log(rule.getName(), "(%s, V)", getArgs(args));
        actual = test.solveOne(rule.bind(0, actual), args).value;
    }

    public <R> void assertRule(final Rule rule, final Function<NavigableMap<Integer, Value>, R> f, final Object... args) {
        final R result = f.apply(expected);
        if (result instanceof Boolean) {
            settings.log(rule.getName(), "(%s)", getArgs(args));
            test.assertSuccess((Boolean) result, rule.bind(0, actual), args);
        } else {
            settings.log(rule.getName(), "(%s, R)", getArgs(args));
            test.assertResult(result, rule.bind(0, actual), args);
        }
    }

    private static String getArgs(final Object[] args) {
        return Stream.concat(Stream.of("map"), Arrays.stream(args).map(Objects::toString))
                .collect(Collectors.joining(", "));
    }

    void updater(final Rule rule, final MapChecker.Updater<State<M>> updater) {
        final int key = randomKey();
        final Value value = values.random();
        updater.update(this, key, value);
        update(rule, key, value);
    }

    void keyUpdater(final Rule rule, final MapChecker.Updater<State<M>> updater) {
        final int key = randomKey();
        updater.update(this, key, expected.get(key));
        update(rule, key);
    }

    public void noneUpdater(final Rule rule, final Consumer<State<M>> updater) {
        updater.accept(this);
        update(rule);
    }
}
