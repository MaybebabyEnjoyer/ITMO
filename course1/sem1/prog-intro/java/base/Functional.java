package base;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Functional {
    private Functional() {}

    public static <T, R> List<R> map(final Collection<T> items, final Function<? super T, ? extends R> f) {
        return items.stream().map(f).collect(Collectors.toUnmodifiableList());
    }

    public static <T, R> List<R> map(final List<T> items, final BiFunction<? super Integer, ? super T, ? extends R> f) {
        return IntStream.range(0, items.size())
                .mapToObj(i -> f.apply(i, items.get(i)))
                .collect(Collectors.toUnmodifiableList());
    }

    @SafeVarargs
    public static <T> List<T> concat(final Collection<? extends T>... items) {
        final List<T> result = new ArrayList<>();
        for (final Collection<? extends T> item : items) {
            result.addAll(item);
        }
        return result;
    }

    public static <T> List<T> append(final Collection<T> collection, final T item) {
        final List<T> list = new ArrayList<>(collection);
        list.add(item);
        return list;
    }

    public static <T> List<List<T>> allValues(final List<T> vals, final int length) {
        return Stream.generate(() -> vals)
                .limit(length)
                .reduce(
                        List.of(List.of()),
                        (prev, next) -> next.stream()
                                .flatMap(value -> prev.stream().map(list -> append(list, value)))
                                .collect(Collectors.toUnmodifiableList()),
                        (prev, next) -> next.stream()
                                .flatMap(suffix -> prev.stream().map(prefix -> concat(prefix, suffix)))
                                .collect(Collectors.toUnmodifiableList())
                );
    }

    public static <K, V> V get(final Map<K, V> map, final K key) {
        final V result = map.get(key);
        if (result == null) {
            throw new NullPointerException(key.toString() + " in " + map(map.keySet(), Objects::toString));
        }
        return result;
    }

    public static void addRange(final List<Integer> values, final int d, final int c) {
        for (int i = -d; i <= d; i++) {
            values.add(c + i);
        }
    }
}
