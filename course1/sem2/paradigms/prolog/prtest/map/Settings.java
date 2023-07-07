package prtest.map;

import base.TestCounter;

/**
 * Prolog test settings.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Settings {
    private final TestCounter counter;
    public final int size;
    public final int range;
    public final int updates;
    public final boolean sorted;
    public final boolean verbose;

    public Settings(final TestCounter counter, final int size, final int range, final int updates, final boolean sorted, final boolean verbose) {
        this.counter = counter;
        this.size = size;
        this.range = range;
        this.updates = updates;
        this.sorted = sorted;
        this.verbose = verbose;
    }

    public void log(final String name, final String format, final Object... args) {
        if (verbose) {
            counter.format("%15s %s%n", name, String.format(format, args));
        }
    }

    public void tick(final int i) {
        if (!verbose && i > 0 && i % 10 == 0) {
            counter.format("update %s of %s%n", i, updates);
        }
    }

    public void run(final Runnable runnable) {
        counter.scope("size = " + size, runnable);
    }
}
