package base;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public abstract class BaseChecker {
    protected final TestCounter counter;

    protected BaseChecker(final TestCounter counter) {
        this.counter = counter;
    }

    public ExtendedRandom random() {
        return counter.random();
    }

    public int mode() {
        return counter.mode();
    }
}
