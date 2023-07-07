package base;

import java.util.Locale;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public abstract class Tester extends BaseChecker {
    protected Tester(final TestCounter counter) {
        super(counter);
    }

    public abstract void test();
}
