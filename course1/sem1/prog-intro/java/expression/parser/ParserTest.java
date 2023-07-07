package expression.parser;

import base.Selector;
import expression.TripleExpression;

import java.util.function.Consumer;

import static expression.parser.Operations.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ParserTest {
    private static final TripleParser PARSER = new ExpressionParser();
    private static final Consumer<ParserTester> TRIPLE = kind(
            TripleExpression.KIND,
            (expr, variables) -> PARSER.parse(expr)
    );
    public static final Selector SELECTOR = Selector.composite(ParserTest.class, ParserTester::new, "easy", "hard")
            .variant("Base", TRIPLE, ADD, SUBTRACT, MULTIPLY, DIVIDE, NEGATE)
            .variant("SetClear", SET, CLEAR)
            .variant("Count", COUNT)
            .variant("GcdLcm", GCD, LCM)
            .variant("Reverse", REVERSE)
            .selector();

    private ParserTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
