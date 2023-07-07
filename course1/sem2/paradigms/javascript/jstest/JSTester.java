package jstest;

import base.TestCounter;
import jstest.expression.BaseTester;
import jstest.expression.Language;

import java.nio.file.Path;
import java.util.List;

/**
 * Base JavaScript tester.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class JSTester extends BaseTester<Object, JSExpressionEngine> {
    public JSTester(
            final TestCounter counter, final Language language, final boolean testParsing,
            final Path script, final String evaluate,
            final String toString, final String parse
    ) {
        super(
                counter,
                new JSExpressionEngine(script, evaluate, parse, toString),
                language,
                testParsing ? STANDARD_SPOILERS : List.of()
        );
    }
}
