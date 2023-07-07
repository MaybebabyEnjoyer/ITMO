package jstest.prefix;

import base.Selector;
import jstest.expression.Builder;
import jstest.expression.Dialect;
import jstest.expression.Language;
import jstest.expression.OperationsBuilder;
import jstest.object.ObjectTester;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Kind {
    private Kind() {
    }

    public static Selector.Composite<OperationsBuilder> selector(
            final Class<?> owner,
            final String toString,
            final String parse,
            final Dialect unparsed,
            final String... parsingTests
    ) {
        assert parsingTests.length % 2 == 0;

        return Builder.selector(owner, mode -> true, (builder, counter) -> {
            final String insertions = builder.getVariant().hasVarargs() ? "abc()+*/@ABC" : "xyz()+*/@ABC";
            final Language language = builder.language(ObjectTester.OBJECT, unparsed);
            final ParserTester tester = new ParserTester(counter, language, toString, parse, insertions);
            tester.addStage(() -> {
                for (int i = 0; i < parsingTests.length; i += 2) {
                    printParsingError(tester, parsingTests[i], parsingTests[i + 1]);
                }
            });
            return tester;
        }, "", "easy", "hard");
    }

    private static void printParsingError(final ParserTester test, final String description, final String input) {
        final String message = test.assertParsingError(input, "", "");
        final int index = message.lastIndexOf("in <eval>");

        System.err.format("%-25s: %s%n", description, message.substring(0, index > 0 ? index : message.length()));
    }
}
