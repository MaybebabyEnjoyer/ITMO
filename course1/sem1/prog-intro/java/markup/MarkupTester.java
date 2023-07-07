package markup;

import base.Asserts;
import base.BaseChecker;
import base.TestCounter;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class MarkupTester {
    private final Map<String, String> mapping;
    private final String toString;

    private MarkupTester(final Map<String, String> mapping, final String toString) {
        this.mapping = mapping;
        this.toString = toString;
    }

    public static Consumer<TestCounter> variant(final Consumer<Checker> checker, final String name, final Map<String, String> mapping) {
        return counter -> test(checker).accept(new MarkupTester(mapping, "to" + name), counter);
    }

    public static BiConsumer<MarkupTester, TestCounter> test(final Consumer<Checker> tester) {
        return (checker, counter) -> tester.accept(checker.new Checker(counter));
    }

    @Override
    public String toString() {
        return toString;
    }

    public class Checker extends BaseChecker {
        public Checker(final TestCounter counter) {
            super(counter);
        }

        private <T> MethodHandle findMethod(final T value) {
            try {
                return MethodHandles.publicLookup().findVirtual(value.getClass(), toString, MethodType.methodType(void.class, StringBuilder.class));
            } catch (final NoSuchMethodException | IllegalAccessException e) {
                throw Asserts.error("Cannot find method %s(StringBuilder) for %s", toString, value.getClass());
            }
        }

        public <T> void test(final T value, String expectedTemplate) {
            final MethodHandle method = findMethod(value);
            for (final Map.Entry<String, String> entry : mapping.entrySet()) {
                expectedTemplate = expectedTemplate.replace(entry.getKey(), entry.getValue());
            }

            final String expected = expectedTemplate;
            counter.println("Test " + counter.getTestNo());
            counter.test(() -> {
                final StringBuilder sb = new StringBuilder();
                try {
                    method.invoke(value, sb);
                } catch (final Throwable e) {
                    throw Asserts.error("%s(StringBuilder) for %s thrown exception: %s", toString, value.getClass(), e);
                }
                Asserts.assertEquals("Result", expected, sb.toString());
            });
        }
    }
}
