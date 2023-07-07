package base;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class TestCounter extends Log {
    public static final int DENOMINATOR = Integer.getInteger("base.denominator", 1);
    public static final int DENOMINATOR2 = (int) Math.round(Math.sqrt(DENOMINATOR));

    private static final String JAR_EXT = ".jar";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private final Class<?> owner;
    private final int mode;
    private final Map<String, ?> properties;
    private final ExtendedRandom random;

    private final long start = System.currentTimeMillis();
    private int passed;

    public TestCounter(final Class<?> owner, final int mode, final Map<String, ?> properties) {
        Locale.setDefault(Locale.US);
        Asserts.checkAssert(getClass());

        this.owner = owner;
        this.mode = mode;
        this.properties = properties;
        random = new ExtendedRandom(owner);
    }

    public int mode() {
        return mode;
    }

    public int getTestNo() {
        return passed + 1;
    }

    public void test(final Runnable action) {
        testV(() -> {
            action.run();
            return null;
        });
    }

    public <T> void testForEach(final Iterable<? extends T> items, final Consumer<? super T> action) {
        for (final T item : items) {
            test(() -> action.accept(item));
        }
    }

    public <T> T testV(final Supplier<T> action) {
        return silentScope("Test " + getTestNo(), () -> {
            final T result = action.get();
            passed++;
            return result;
        });
    }

    private String getLine() {
        return getIndent() == 0 ? "=" : "-";
    }

    public void printStatus() {
        final String title = String.format("%s %s", owner.getSimpleName(), properties.isEmpty() ? "" : properties);
        format("%s%n%s%n", getLine().repeat(30), title);
        format("%d tests passed in %d ms%n", passed, System.currentTimeMillis() - start);
        println("Version: " + getVersion(owner));
        println("");
    }

    private static String getVersion(final Class<?> clazz) {
        try {
            final ClassLoader cl = clazz.getClassLoader();
            final URL url = cl.getResource(clazz.getName().replace('.', '/') + ".class");
            if (url == null) {
                return "(no manifest)";
            }

            final String path = url.getPath();
            final int index = path.indexOf(JAR_EXT);
            if (index == -1) {
                return DATE_FORMAT.format(new Date(new File(path).lastModified()));
            }

            final String jarPath = path.substring(0, index + JAR_EXT.length());
            try (final JarFile jarFile = new JarFile(new File(new URI(jarPath)))) {
                final JarEntry entry = jarFile.getJarEntry("META-INF/MANIFEST.MF");
                return DATE_FORMAT.format(new Date(entry.getTime()));
            }
        } catch (final IOException | URISyntaxException e) {
            return "error: " + e;
        }
    }

    public <T> T call(final String message, final SupplierE<T> supplier) {
        return get(supplier).either(e -> fail(e, "%s", message), Function.identity());
    }

    public void shouldFail(final String message, @SuppressWarnings("TypeMayBeWeakened") final RunnableE action) {
        test(() -> get(action).either(e -> null, v -> fail("%s", message)));
    }

    public <T> T fail(final String format, final Object... args) {
        return fail(Asserts.error(format, args));
    }

    public <T> T fail(final Throwable throwable) {
        return fail(throwable, "%s: %s", throwable.getClass().getSimpleName(), throwable.getMessage());
    }

    public <T> T fail(final Throwable throwable, final String format, final Object... args) {
        final String message = String.format(format, args);
        println("ERROR: " + message);
        throw throwable instanceof Error ? (Error) throwable : new AssertionError(throwable);
    }

    public void checkTrue(final boolean condition, final String message, final Object... args) {
        if (!condition) {
            fail(message, args);
        }
    }

    public static <T> Either<Exception, T> get(final SupplierE<T> supplier) {
        return supplier.get();
    }

    public Path getFile(final String suffix) {
        return Paths.get(String.format("test%d.%s", getTestNo(), suffix));
    }

    public ExtendedRandom random() {
        return random;
    }

    @FunctionalInterface
    public interface SupplierE<T> extends Supplier<Either<Exception, T>> {
        T getE() throws Exception;

        @Override
        default Either<Exception, T> get() {
            try {
                return Either.right(getE());
            } catch (final Exception e) {
                return Either.left(e);
            }
        }
    }

    @FunctionalInterface
    public interface RunnableE extends SupplierE<Void> {
        void run() throws Exception;

        @Override
        default Void getE() throws Exception {
            run();
            return null;
        }
    }
}
