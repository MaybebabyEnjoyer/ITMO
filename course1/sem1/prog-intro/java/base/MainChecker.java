package base;

import java.util.List;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
public final class MainChecker {
    private final Runner runner;

    public MainChecker(final Runner runner) {
        this.runner = runner;
    }

    public List<String> run(final TestCounter counter, final String... input) {
        return runner.run(counter, input);
    }

    public List<String> run(final TestCounter counter, final List<String> input) {
        return runner.run(counter, input);
    }

    public void testEquals(final TestCounter counter, final List<String> input, final List<String> expected) {
        runner.testEquals(counter, input, expected);
    }

}
