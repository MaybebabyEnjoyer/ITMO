package queue;

import base.Selector;
import base.TestCounter;

import java.util.List;
import java.util.function.Consumer;

//import static queue.Queues.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ArrayQueueTest {
    public static final Selector SELECTOR = new Selector(ArrayQueueTest.class)
            .variant("Base", variant(Queues.QueueModel.class, d -> () -> d))
            .variant("DequeToArray", variant(Queues.DequeToArrayModel.class, (Queues.DequeChecker<Queues.DequeToArrayModel>) d -> () -> d, Queues.DEQUE_TO_ARRAY))
            .variant("DequeIndexed", variant(Queues.DequeIndexedModel.class, (Queues.DequeIndexedChecker<Queues.DequeIndexedModel>) d -> () -> d))
            .variant("ToStr", variant(Queues.ToStrModel.class, d -> () -> d, Queues.TO_STR))
            .variant("ToArray", variant(Queues.ToArrayModel.class, d -> () -> d, Queues.TO_ARRAY))
            ;

    private ArrayQueueTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }

    /* package-private */ static <M extends Queues.QueueModel, T extends Queues.QueueChecker<M>> Consumer<TestCounter> variant(
            final Class<M> type,
            final T tester,
            final Queues.Splitter<M> splitter
    ) {
        return new ArrayQueueTester<>(type, tester, splitter)::test;
    }

    /* package-private */ static <M extends Queues.QueueModel, T extends Queues.QueueChecker<M>> Consumer<TestCounter> variant(
            final Class<M> type,
            final T tester
    ) {
        return variant(type, tester, (t, q, r) -> List.of());
    }
}
