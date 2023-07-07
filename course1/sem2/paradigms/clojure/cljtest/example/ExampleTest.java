package cljtest.example;

import base.Asserts;
import base.Selector;
import base.TestCounter;
import cljtest.ClojureScript;
import jstest.Engine;

import java.util.Arrays;

/**
 * Tests for Example Clojure
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ExampleTest {
    private static class Test {
        private final TestCounter counter;
        private final ClojureScript.F<String> hello;
        private final ClojureScript.F<Number> add;

        private Test(final TestCounter counter) {
            this.counter = counter;

            ClojureScript.loadScript("example.clj");
            hello = ClojureScript.function("hello", String.class);
            add = ClojureScript.function("add", Number.class);
        }

        private void test() {
            counter.scope("hello", () -> {
                assertHello("Clojure");
                assertHello(MODES[counter.mode()]);
            });
            counter.scope("add", () -> {
                assertAdd();
                assertAdd(1);
                assertAdd(1, 2);
                assertAdd(1, 2, 3);
            });
        }

        private void assertHello(final String name) {
            counter.test(() -> Asserts.assertEquals(
                    "Hello", "Hello, " + name + "!",
                    hello.call(new Engine.Result<>(name, name)).value
            ));
        }

        private void assertAdd(final int... numbers) {
            final Engine.Result<?>[] args = Arrays.stream(numbers).mapToObj(v -> new Engine.Result<>(
                    v + "",
                    v
            )).toArray(Engine.Result<?>[]::new);
            counter.test(() -> Asserts.assertEquals(
                    Arrays.toString(numbers), Arrays.stream(numbers).sum(),
                    add.call(args).value.intValue()
            ));
        }
    }

    private static final String[] MODES = {"easy", "hard"};

    public static final Selector SELECTOR = new Selector(ExampleTest.class, MODES)
            .variant("base", counter -> new Test(counter).test());

    private ExampleTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
