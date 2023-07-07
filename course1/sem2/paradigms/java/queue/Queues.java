package queue;

import base.ExtendedRandom;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Queues {
    private Queues() {
    }

    /* package-private */ interface QueueModel {
        @ReflectionTest.Ignore
        ArrayDeque<Object> model();

        default Object dequeue() {
            return model().removeFirst();
        }

        default int size() {
            return model().size();
        }

        default boolean isEmpty() {
            return model().isEmpty();
        }

        default void clear() {
            model().clear();
        }

        default void enqueue(final Object element) {
            model().addLast(element);
        }

        default Object element() {
            return model().getFirst();
        }
    }

    /* package-private */ interface QueueChecker<T extends QueueModel> {
        T wrap(ArrayDeque<Object> reference);

        default List<T> linearTest(final T queue, final ExtendedRandom random) {
            // Do nothing by default
            return List.of();
        }

        default void check(final T queue, final ExtendedRandom random) {
            queue.element();
        }

        default void add(final T queue, final Object element, final ExtendedRandom random) {
            queue.enqueue(element);
        }

        default Object randomElement(final ExtendedRandom random) {
            return ArrayQueueTester.ELEMENTS[random.nextInt(ArrayQueueTester.ELEMENTS.length)];
        }

        default void remove(final T queue, final ExtendedRandom random) {
            queue.dequeue();
        }

        @SuppressWarnings("unchecked")
        default T cast(final QueueModel model) {
            return (T) model;
        }
    }

    @FunctionalInterface
    protected interface Splitter<M extends QueueModel> {
        List<M> split(final QueueChecker<? extends M> tester, final M queue, final ExtendedRandom random);
    }

    @FunctionalInterface
    protected interface LinearTester<M extends QueueModel> extends Splitter<M> {
        void test(final QueueChecker<? extends M> tester, final M queue, final ExtendedRandom random);

        @Override
        default List<M> split(final QueueChecker<? extends M> tester, final M queue, final ExtendedRandom random) {
            test(tester, queue, random);
            return List.of();
        }
    }


    // === Deque

    /* package-private */ interface DequeModel extends QueueModel {
        default void push(final Object element) {
            model().addFirst(element);
        }

        @SuppressWarnings("UnusedReturnValue")
        default Object peek() {
            return model().getLast();
        }

        default Object remove() {
            return model().removeLast();
        }
    }

    /* package-private */ interface DequeChecker<T extends DequeModel> extends QueueChecker<T> {
        @Override
        default void add(final T queue, final Object element, final ExtendedRandom random) {
            if (random.nextBoolean()) {
                QueueChecker.super.add(queue, element, random);
            } else {
                queue.push(element);
            }
        }

        @Override
        default void check(final T queue, final ExtendedRandom random) {
            if (random.nextBoolean()) {
                QueueChecker.super.check(queue, random);
            } else {
                queue.peek();
            }
        }

        @Override
        default void remove(final T queue, final ExtendedRandom random) {
            if (random.nextBoolean()) {
                QueueChecker.super.remove(queue, random);
            } else {
                queue.remove();
            }
        }
    }


    // === ToArray

    /* package-private */ interface ToArrayModel extends QueueModel {
        default Object[] toArray() {
            return model().toArray();
        }
    }

    /* package-private */ static final LinearTester<ToArrayModel> TO_ARRAY = (tester, queue, random) -> queue.toArray();


    // === DequeToArray

    /* package-private */ interface DequeToArrayModel extends DequeModel, ToArrayModel {
    }

    /* package-private */ static final LinearTester<DequeToArrayModel> DEQUE_TO_ARRAY = TO_ARRAY::test;


    // === Reflection

    /* package-private */ interface ReflectionModel extends Queues.QueueModel {
        Field ELEMENTS = getField("elements");
        Field HEAD = getField("head");

        @SuppressWarnings("unchecked")
        private <Z> Z get(final Field field) {
            try {
                return (Z) field.get(model());
            } catch (final IllegalAccessException e) {
                throw new AssertionError("Cannot access field " + field.getName() + ": " + e.getMessage(), e);
            }
        }

        private static Field getField(final String name) {
            try {
                final Field field = ArrayDeque.class.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (final NoSuchFieldException e) {
                throw new AssertionError("Reflection error: " + e.getMessage(), e);
            }
        }

        @ReflectionTest.Ignore
        default int head() {
            return get(HEAD);
        }

        @ReflectionTest.Ignore
        default Object[] elements() {
            return get(ELEMENTS);
        }

        @ReflectionTest.Ignore
        default <R> R reduce(final R zero, final Predicate<Object> p, final BiFunction<R, Integer, R> f) {
            final int size = size();
            final Object[] elements = elements();
            final int head = head();
            R result = zero;
            for (int i = 0; i < size; i++) {
                if (p.test(elements[(head + i) % elements.length])) {
                    result = f.apply(result, i);
                }
            }
            return result;
        }

        @ReflectionTest.Ignore
        default <R> R reduce(final R zero, final Object v, final BiFunction<R, Integer, R> f) {
            return reduce(zero, o -> Objects.equals(v, o), f);
        }
    }


    // === Indexed

    /* package-private */ interface IndexedModel extends ReflectionModel {
        default Object get(final int index) {
            final Object[] elements = elements();
            return elements[(head() + index) % elements.length];
        }

        default void set(final int index, final Object value) {
            final Object[] elements = elements();
            elements[(head() + index) % elements.length] = value;
        }
    }

    /* package-private */ interface IndexedChecker<T extends IndexedModel> extends Queues.QueueChecker<T> {
        @Override
        default void check(final T queue, final ExtendedRandom random) {
            Queues.QueueChecker.super.check(queue, random);
            queue.get(randomIndex(queue, random));
        }

        @Override
        default void add(final T queue, final Object element, final ExtendedRandom random) {
            if (queue.isEmpty() || random.nextBoolean()) {
                Queues.QueueChecker.super.add(queue, element, random);
            } else {
                queue.set(randomIndex(queue, random), randomElement(random));
            }
        }

        private static int randomIndex(final Queues.QueueModel queue, final ExtendedRandom random) {
            return random.nextInt(queue.size());
        }
    }


    // === DequeIndexed

    /* package-private */ interface DequeIndexedModel extends DequeModel, IndexedModel {}

    /* package-private */ interface DequeIndexedChecker<M extends DequeIndexedModel> extends
            DequeChecker<M>,
            IndexedChecker<M>
    {
        @Override
        default void check(final M queue, final ExtendedRandom random) {
            DequeChecker.super.check(queue, random);
            IndexedChecker.super.check(queue, random);
        }

        @Override
        default void add(final M queue, final Object element, final ExtendedRandom random) {
            if (random.nextBoolean()) {
                DequeChecker.super.add(queue, element, random);
            } else {
                IndexedChecker.super.add(queue, element, random);
            }
        }
    }


    // === Contains

    /* package-private */ interface ContainsModel extends Queues.QueueModel {
        default boolean contains(final Object element) {
            return model().contains(element);
        }

        @SuppressWarnings("UnusedReturnValue")
        default boolean removeFirstOccurrence(final Object element) {
            return model().removeFirstOccurrence(element);
        }
    }

    /* package-private */ static final Queues.LinearTester<ContainsModel> CONTAINS = (tester, queue, random) -> {
        final Object element = random.nextBoolean() ? tester.randomElement(random) : random.nextInt();
        if (random.nextBoolean()) {
            queue.contains(element);
        } else {
            queue.removeFirstOccurrence(element);
        }
    };


    // === Nth

    /* package-private */ interface NthModel extends Queues.QueueModel {
        // Deliberately ugly implementation
        @ReflectionTest.Wrap
        default NthModel getNth(final int n) {
            final ArrayDeque<Object> deque = new ArrayDeque<>();
            final int[] index = {0};
            model().forEach(e -> {
                if (++index[0] % n == 0) {
                    deque.add(e);
                }
            });
            return () -> deque;
        }

        // Deliberately ugly implementation
        @ReflectionTest.Wrap
        default NthModel removeNth(final int n) {
            final ArrayDeque<Object> deque = new ArrayDeque<>();
            final int[] index = {0};
            model().removeIf(e -> {
                if (++index[0] % n == 0) {
                    deque.add(e);
                    return true;
                } else {
                    return false;
                }
            });
            return () -> deque;
        }

        default void dropNth(final int n) {
            final int[] index = {0};
            model().removeIf(e -> ++index[0] % n == 0);
        }
    }

    /* package-private */ static final Queues.Splitter<NthModel> NTH = (tester, queue, random) -> {
        final int n = random.nextInt(5) + 1;
        switch (random.nextInt(3)) {
            case 0:
                final NthModel model = queue.removeNth(n);
                return List.of(tester.cast(model));
            case 1:
                queue.dropNth(n);
                return List.of();
            case 2:
                return List.of(tester.cast(queue.getNth(n)));
            default:
                throw new AssertionError();
        }
    };

    // === ToStr

    /* package-private */ interface ToStrModel extends Queues.QueueModel {
        @SuppressWarnings("UnusedReturnValue")
        default String toStr() {
            return model().toString();
        }
    }

    /* package-private */ static final Queues.LinearTester<ToStrModel> TO_STR = (tester, queue, random) -> queue.toStr();

    // === Count

    /* package-private */ interface CountModel extends ReflectionModel {
        default int count(final Object element) {
            return reduce(0, element, (v, i) -> v + 1);
        }
    }

    /* package-private */ static final Queues.LinearTester<CountModel> COUNT =
            (tester, queue, random) -> queue.count(tester.randomElement(random));

    /* package-private */
    interface IndexModel extends ReflectionModel {
        default int indexOf(final Object element) {
            return reduce(-1, element, (v, i) -> v == -1 ? i : v);
        }

        default int lastIndexOf(final Object element) {
            return reduce(-1, element, (v, i) -> i);
        }
    }

    /* package-private */ static final Queues.LinearTester<IndexModel> INDEX = (tester, queue, random) -> {
        if (random.nextBoolean()) {
            queue.indexOf(tester.randomElement(random));
        } else {
            queue.lastIndexOf(tester.randomElement(random));
        }
    };



}
