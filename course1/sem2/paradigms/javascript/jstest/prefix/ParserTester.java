package jstest.prefix;

import base.TestCounter;
import jstest.Engine;
import jstest.EngineException;
import jstest.expression.Dialect;
import jstest.expression.Language;
import jstest.object.ObjectTester;

/**
 * Tester for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#js-expression-parsing">JavaScript Expression Parsing</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ParserTester extends ObjectTester {
    public static final Dialect PREFIX = new Dialect("%s", "%s", "({op} {args})", " ");

    private final String insertions;

    public ParserTester(final TestCounter counter, final Language language, final String toString, final String parse, final String insertions) {
        super(counter, language, toString, parse);
        this.insertions = insertions;
    }

    @Override
    protected void test(final Engine.Result<Object> prepared, final String unparsed) {
        super.test(prepared, unparsed);
        super.test(engine.parse(removeSpaces(unparsed)), unparsed);

        for (int i = 0; i < 1 + Math.min(10, 200 / unparsed.length()); i++) {
            final int index = random().nextInt(unparsed.length());
            final char c = unparsed.charAt(index);
            if (!Character.isDigit(c) && !Character.isWhitespace(c) && "-hxyz".indexOf(c) == -1){
                counter.test(() -> assertParsingError(unparsed.substring(0, index), "<SYMBOL REMOVED>", unparsed.substring(index + 1)));
            }
            final char newC = insertions.charAt(random().nextInt(insertions.length()));
            if (!Character.isDigit(c) && c != '-') {
                counter.test(() -> assertParsingError(unparsed.substring(0, index), "<SYMBOL INSERTED -->", newC + unparsed.substring(index)));
            }
        }
    }

    private static String removeSpaces(final String expression) {
        return expression.replace(" (", "(").replace(") ", ")");
    }

    protected String assertParsingError(final String prefix, final String comment, final String suffix) {
        try {
            parse(prefix + suffix);
            throw new AssertionError("Parsing error expected for " + prefix + comment + suffix);
        } catch (final EngineException e) {
            return e.getCause().getMessage();
        }
    }
}
