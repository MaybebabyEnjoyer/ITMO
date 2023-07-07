package queue;

import base.Asserts;
import base.ExtendedRandom;
import base.TestCounter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ArrayQueueTester<M extends Queues.QueueModel> {
    private static final int OPERATIONS = 50_000;
    /* package-private */ static final Object[] ELEMENTS = new Object[]{
            "Hello",
            "world",
            1, 2, 3,
            List.of("a"),
            List.of("a"),
            List.of("b"),
            Map.of()
    };

    private final Class<M> model;
    private final Queues.QueueChecker<M> tester;

    protected ArrayQueueTester(final Class<M> model, final Queues.QueueChecker<M> tester, final Queues.Splitter<M> splitter) {
        this.model = model;
        this.tester = new Queues.QueueChecker<>() {
            @Override
            public M wrap(final ArrayDeque<Object> reference) {
                return tester.wrap(reference);
            }

            @Override
            public void check(final M queue, final ExtendedRandom random) {
                tester.check(queue, random);
            }

            @Override
            public void add(final M queue, final Object element, final ExtendedRandom random) {
                tester.add(queue, element, random);
            }

            @Override
            public void remove(final M queue, final ExtendedRandom random) {
                tester.remove(queue, random);
            }

            @Override
            public List<M> linearTest(final M queue, final ExtendedRandom random) {
                if (random.nextInt(50) == 0) {
                    queue.clear();
                    return List.of();
                } else {
                    return splitter.split(this, queue, random);
                }
            }
        };
    }

    protected void test(final TestCounter counter) {
        test(counter, "ArrayQueue", ReflectionTest.Mode.values());
    }

    protected void test(final TestCounter counter, final String className, final ReflectionTest.Mode... modes) {
        for (final ReflectionTest.Mode mode : modes) {
            final String scope = String.format("Running %s for %s in %s mode%n", model.getEnclosingClass().getSimpleName(), className, mode);
            counter.scope(scope, () -> new Variant(counter).test(className, mode));
        }
    }



    protected void checkImplementation(final Class<?> implementation) {
        // Do nothing by default
    }

    private static List<Object> toList(final Queues.QueueModel queue) {
        final List<Object> list = Stream.generate(queue::dequeue).limit(queue.size()).collect(Collectors.toUnmodifiableList());
        list.forEach(queue::enqueue);
        return list;
    }

    protected static ArrayDeque<Object> collect(final Stream<Object> elements) {
        return elements.collect(Collectors.toCollection(ArrayDeque::new));
    }

    private class Variant extends ReflectionTest {
        private final TestCounter counter;

        public Variant(final TestCounter counter) {
            this.counter = counter;
        }

        protected void testEmpty(final M queue) {
            counter.scope("testEmpty", () -> assertSize(0, queue));
        }

        protected void testSingleton(final M queue) {
            counter.scope("testSingleton", () -> {
                assertSize(0, queue);
                final String value = "value";
                queue.enqueue(value);
                assertSize(1, queue);
                Asserts.assertEquals("element()", value, queue.element());
                Asserts.assertEquals("dequeue()", value, queue.dequeue());
                assertSize(0, queue);
            });
        }

        protected void testClear(final M queue) {
            counter.scope("testClear", () -> {
                assertSize(0, queue);

                final String value = "value";
                queue.enqueue(value);
                queue.enqueue(value);
                queue.clear();
                assertSize(0, queue);

                final String value1 = "value1";
                queue.enqueue(value1);
                Asserts.assertEquals("deque()", value1, queue.dequeue());
            });
        }

        private int checkAndSize(final M queue) {
            final int size = queue.size();
            if (!queue.isEmpty() && random().nextBoolean()) {
                tester.check(queue, random());
            }
            return size;
        }

        protected Object randomElement() {
            return ELEMENTS[random().nextInt(ELEMENTS.length)];
        }

        protected void assertSize(final int size, final M queue) {
            counter.test(() -> {
                Asserts.assertEquals("size()", size, queue.size());
                Asserts.assertTrue("Expected isEmpty() " + (size == 0) + ", found " + queue.isEmpty(), (size == 0) == queue.isEmpty());
            });
        }

        @Override
        protected void checkResult(final String call, final Object expected, final Object actual) {
            if (expected instanceof Queues.QueueModel) {
                super.checkResult(call, toList((Queues.QueueModel) expected), toList((Queues.QueueModel) actual));
            } else {
                super.checkResult(call, expected, actual);
            }
        }

        protected Supplier<M> factory(final String name, final Mode mode) {
            final ProxyFactory<M> factory = new ProxyFactory<>(model, mode, "queue." + name);
            checkImplementation(factory.implementation);
            return () -> checking(counter, model, tester.wrap(new ArrayDeque<>()), factory.create());
        }

        private void test(final String className, final Mode mode) {
            final Supplier<M> factory = factory(className, mode);
            testEmpty(factory.get());
            testSingleton(factory.get());
            testClear(factory.get());
            for (int i = 0; i <= 10; i += 2) {
                testRandom(factory.get(), (double) i / 10);
            }
        }

        private void testRandom(final M initial, final double addFreq) {
            counter.scope("testRandom, add frequency = " + addFreq, () -> {
                final List<M> queues = new ArrayList<>();
                queues.add(initial);
                int ops = 0;
                for (int i = 0; i < OPERATIONS / TestCounter.DENOMINATOR / TestCounter.DENOMINATOR; i++) {
                    final M queue = queues.get(random().nextInt(queues.size()));

                    final int size = counter.testV(() -> {
                        if (queue.isEmpty() || random().nextDouble() < addFreq) {
                            tester.add(queue, randomElement(), random());
                        } else {
                            tester.remove(queue, random());
                        }

                        return checkAndSize(queue);
                    });

                    if (ops++ >= size && random().nextInt(4) == 0) {
                        ops -= size;

                        counter.test(() -> {
                            queues.addAll(tester.linearTest(queue, random()));
                            checkAndSize(queue);
                        });
                    }
                }

                for (final M queue : queues) {
                    counter.test(() -> {
                        tester.linearTest(queue, random());
                        checkAndSize(queue);
                        for (int i = queue.size(); i > 0; i--) {
                            tester.remove(queue, random());
                            checkAndSize(queue);
                        }
                    });
                }
            });
        }

        private ExtendedRandom random() {
            return counter.random();
        }
    }
}
