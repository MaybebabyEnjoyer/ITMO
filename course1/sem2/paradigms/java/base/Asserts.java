package base;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
public final class Asserts {
    static {
        Locale.setDefault(Locale.US);
    }

    private Asserts() {
    }

    public static void assertEquals(final String message, final Object expected, final Object actual) {
        final String reason = String.format("%s:%n     expected `%s`,%n       actual `%s`",
                message, toString(expected), toString(actual));
        assertTrue(reason, Objects.deepEquals(expected, actual));
    }

    private static String toString(final Object value) {
        if (value != null && value.getClass().isArray()) {
            final String result = Arrays.deepToString(new Object[]{value});
            return result.substring(1, result.length() - 1);
        } else {
            return Objects.toString(value);
        }
    }

    public static <T> void assertEquals(final String message, final List<T> expected, final List<T> actual) {
        for (int i = 0; i < Math.min(expected.size(), actual.size()); i++) {
            assertEquals(message + ":" + (i + 1), expected.get(i), actual.get(i));
        }
        assertEquals(message + ": Number of items", expected.size(), actual.size());
    }

    public static void assertTrue(final String message, final boolean value) {
        if (!value) {
            throw error("%s", message);
        }
    }

    public static void assertEquals(final String message, final double expected, final double actual, final double precision) {
        assertTrue(
                String.format("%s: Expected %.12f, found %.12f", message, expected, actual),
                isEqual(expected, actual, precision)
        );
    }

    public static boolean isEqual(final double expected, final double actual, final double precision) {
        final double error = Math.abs(actual - expected);
        return error <= precision
               || error <= precision * Math.abs(expected)
               || !Double.isFinite(expected)
               || Math.abs(expected) > 1e100
               || Math.abs(expected) < precision && !Double.isFinite(actual);
    }

    public static void assertSame(final String message, final Object expected, final Object actual) {
        assertTrue(String.format("%s: expected same objects: %s and %s", message, expected, actual), expected == actual);
    }

    public static void checkAssert(final Class<?> c) {
        if (!c.desiredAssertionStatus()) {
            throw error("You should enable assertions by running 'java -ea %s'", c.getName());
        }
    }

    public static AssertionError error(final String format, final Object... args) {
        final String message = String.format(format, args);
        return args.length > 0 && args[args.length - 1] instanceof Throwable
               ? new AssertionError(message, (Throwable) args[args.length - 1])
               : new AssertionError(message);
    }

    public static void printStackTrace(final String message) {
        new Exception(message).printStackTrace(System.out);
    }
}
