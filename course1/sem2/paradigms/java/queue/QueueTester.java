package queue;

import base.Asserts;
import base.TestCounter;

import java.util.stream.Stream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class QueueTester<M extends Queues.QueueModel> extends ArrayQueueTester<M> {
    public QueueTester(final Class<M> type, final Queues.QueueChecker<M> tester, final Queues.Splitter<M> splitter) {
        super(type, tester, splitter);
    }

    public void test(final TestCounter counter) {
        test(counter, "LinkedQueue", ReflectionTest.Mode.CLASS);
        test(counter, "ArrayQueue", ReflectionTest.Mode.CLASS);
    }

    private static boolean implementsQueue(final Class<?> type) {
        return type != Object.class
                && (Stream.of(type.getInterfaces()).map(Class::getName).anyMatch("queue.Queue"::equals)
                    || implementsQueue(type.getSuperclass()));
    }

    @Override
    protected void checkImplementation(final Class<?> type) {
        Asserts.assertTrue(type + " should extend AbstractQueue", "queue.AbstractQueue".equals(type.getSuperclass().getName()));
        Asserts.assertTrue(type + " should implement interface Queue", implementsQueue(type));
    }
}
