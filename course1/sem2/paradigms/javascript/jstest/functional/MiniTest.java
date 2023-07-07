package jstest.functional;

import base.TestCounter;
import jstest.expression.BaseVariant;
import jstest.expression.BaseTester;
import jstest.expression.Language;

import java.nio.file.Path;
import java.util.Map;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class MiniTest {
    private MiniTest() {
    }

    public static void main(final String... args) {
        final int mode = BaseTester.mode(args, MiniTest.class, "easy", "hard");
        final TestCounter counter = new TestCounter(MiniTest.class, mode, Map.of());
        FunctionalTest.tester(
                counter,
                new Language(
                        FunctionalTest.ARITHMETIC, FunctionalTest.POLISH,
                        new BaseVariant() {{
                            tests(c(10), variable("x", 0));
                        }}
                ), mode == 1, Path.of("functionalMiniExpression.js")
        ).run(MiniTest.class, "mode=" + args[0]);
    }
}
