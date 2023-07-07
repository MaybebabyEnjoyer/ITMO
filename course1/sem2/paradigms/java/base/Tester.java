package base;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public abstract class Tester extends BaseChecker {
    protected Tester(final TestCounter counter) {
        super(counter);
    }

    public abstract void test();

    public void run(final Class<?> test, final String... args) {
        System.out.println("=== Testing " + test.getSimpleName() + " " + String.join(" ", args));
        test();
        counter.printStatus();
    }
}
