package prtest.map;

import base.ExtendedRandom;
import prtest.Rule;
import prtest.Value;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Common Prolog Map checking code.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class MapChecker<M> {
    private static final Rule PUT = Rule.func("map_put", 3);
    private static final Rule REMOVE = Rule.func("map_remove", 2);

    private final PrologMapTest test;

    private final List<Consumer<State<M>>> updaters = new ArrayList<>();
    private final List<Consumer<State<M>>> checkers = new ArrayList<>();
    private final Function<List<Entry>, M> modelFactory;
    private final Function<List<Entry>, Value> actualFactory;

    public MapChecker(
            final PrologMapTest test,
            final Function<List<Entry>, M> modelFactory,
            final Function<List<Entry>, Value> actualFactory,
            final Updater<M> modelPut,
            final BiConsumer<M, Integer> modelRemove,
            final Consumer<State<M>> checker
    ) {
        this.test = test;
        this.modelFactory = modelFactory;
        this.actualFactory = actualFactory;

        checker(checker);

        updater(PUT, (state, key, value) -> {
            state.expected.put(key, value);
            modelPut.update(state.model, key, value);
            state.keys.add(key);
            state.values.add(value);
        });
        keyUpdater(REMOVE, (state, key, value) -> {
            state.expected.remove(key);
            modelRemove.accept(state.model, key);
            state.keys.add(key);
            state.values.remove(value);
        });
    }

    /* package-private*/ void check(final Settings settings) {
        settings.run(() -> {
            final SetHistory<Integer> keys = new SetHistory<>(() -> test.random().nextInt(-settings.range, settings.range), test.random());
            final SetHistory<Value> values = new SetHistory<>(() -> Value.string(test.random().randomString(ExtendedRandom.ENGLISH)), test.random());
            final List<Entry> entries = Stream.generate(() -> new Entry(keys.uniqueAndAdd(), values.uniqueAndAdd()))
                    .limit(settings.size)
                    .collect(Collectors.toList());

            if (settings.sorted) {
                entries.sort(Comparator.comparing(Entry::getKey));
            }

            settings.log("build", "%s", entries);

            final State<M> state = new State<>(
                    test, settings,
                    keys,
                    values,
                    new TreeMap<>(entries.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue))),
                    actualFactory.apply(entries),
                    modelFactory.apply(entries)
            );

            check(state);

            for (int i = 0; i < settings.updates; i++) {
                settings.tick(i);
                test.random().randomItem(updaters).accept(state);
                check(state);
            }
        });
    }

    private void check(final State<M> state) {
        checkers.forEach(checker -> test.check(() -> checker.accept(state)));
    }

    public void checker(final Consumer<State<M>> checker) {
        checkers.add(checker);
    }

    public <R> void checker(final Rule rule, final Function<NavigableMap<Integer, Value>, R> f) {
        checker(state -> state.assertRule(rule, f));
    }

    public <S, R> void checker(final Rule rule, final Function<State<M>, S> gen, final BiFunction<NavigableMap<Integer, Value>, S, R> f) {
        checker(state -> {
            final S s = gen.apply(state);
            state.assertRule(rule, map -> f.apply(map, s), s);
        });
    }

    public <S, U, R> void checker(final Rule rule, final Function<State<M>, S> gen1, final Function<State<M>, U> gen2, final BiFunction<S, U, Function<NavigableMap<Integer, Value>, R>> f) {
        checker(state -> {
            final S s = gen1.apply(state);
            final U u = gen2.apply(state);
            state.assertRule(rule, f.apply(s, u), s, u);
        });
    }

    public <R> void keyChecker(final Rule rule, final BiFunction<NavigableMap<Integer, Value>, Integer, R> f) {
        checker(rule, State::randomKey, f);
    }

    public <R> void valueChecker(final Rule rule, final BiFunction<NavigableMap<Integer, Value>, Value, R> f) {
        checker(rule, state -> state.values.random(), f);
    }

    public void updater(final Rule rule, final Updater<State<M>> updater) {
        updaters.add(state -> state.updater(rule, updater));
    }

    public void keyUpdater(final Rule rule, final Updater<State<M>> updater) {
        updaters.add(state -> state.keyUpdater(rule, updater));
    }

    public void noneUpdater(final Rule rule, final Consumer<State<M>> stateUpdater) {
        updaters.add(state -> state.noneUpdater(rule, stateUpdater));
    }

    public void clearUpdaters() {
        updaters.clear();
    }

    public int mode() {
        return test.mode();
    }

    public interface Updater<T> {
        void update(T model, int key, Value value);
    }
}
