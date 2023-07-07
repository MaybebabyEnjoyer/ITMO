package prtest.map;

import base.ExtendedRandom;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Set with remove elements history.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class SetHistory<E> {
    private final Set<E> values = new HashSet<>();
    private final Supplier<E> supplier;
    private final Set<E> removed = new HashSet<>();
    private final ExtendedRandom random;

    public SetHistory(final Supplier<E> supplier, final ExtendedRandom random) {
        this.supplier = supplier;
        this.random = random;
    }

    public E unique() {
        while (true) {
            final E element = supplier.get();
            if (!values.contains(element) && !removed.contains(element)) {
                return element;
            }
        }
    }

    public E uniqueAndAdd() {
        final E element = unique();
        add(element);
        return element;
    }

    public void add(final E element) {
        values.add(element);
        removed.remove(element);
    }

    public void remove(final E element) {
        if (values.remove(element)) {
            removed.add(element);
        }
    }

    private Optional<E> random(final Set<E> values) {
        return values.isEmpty()
                ? Optional.empty()
                : values.stream().skip(random.nextInt(values.size())).findFirst();
    }

    public Optional<E> existing() {
        return random(values);
    }

    public Optional<E> removed() {
        return random(removed);
    }

    public Set<E> allRemoved() {
        return removed;
    }

    public E random() {
        final Optional<E> existing = random.nextBoolean() ? existing() : Optional.empty();
        final Optional<E> removed = existing.or(() -> random.nextBoolean() ? removed() : Optional.empty());
        return removed.orElseGet(this::unique);
    }

    public List<E> test() {
        final List<E> all = Stream.concat(values.stream(), removed.stream()).collect(Collectors.toList());
        random.shuffle(all);
        return all;
    }
}
