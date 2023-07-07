package jstest.prefix;

import base.Selector;
import jstest.expression.Dialect;

import static jstest.expression.Operations.*;

/**
 * Tests for Postfix* variants of
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#js-expression-parsing">JavaScript Expression Parsing</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class PostfixTest {
    public static final Selector SELECTOR = Kind.selector(
                    PostfixTest.class,
                    "postfix", "parsePostfix", new Dialect("%s", "%s", "({args} {op})", " "),
                    "Empty input", "",
                    "Unknown variable", "a",
                    "Invalid number", "-a",
                    "Missing )", "(z (x y +) *",
                    "Missing (", "z (x y +) *)",
                    "Unknown operation", "( x y @@)",
                    "Excessive info", "(x y +) x",
                    "Empty op", "()",
                    "Invalid unary (0 args)", "(negate)",
                    "Invalid unary (2 args)", "(x y negate)",
                    "Invalid binary (0 args)", "(+)",
                    "Invalid binary (1 args)", "(x +)",
                    "Invalid binary (3 args)", "(x y z +)",
                    "Variable op (0 args)", "(x)",
                    "Variable op (1 args)", "(1 x)",
                    "Variable op (2 args)", "(1 2 x)",
                    "Const op (0 args)", "(0)",
                    "Const op (1 args)", "(0 1)",
                    "Const op (2 args)", "(0 1 2)"
            )
            .variant("Base", ARITH)
            .variant("FloorCeil", FLOOR, CEIL)
            .variant("SumexpLSE", SUMEXP, LSE)
            .variant("MeansqRMS", MEANSQ, RMS)
            .selector();

    private PostfixTest() {
    }

    public static void main(final String... args) {
        ParserTest.main(args);
        SELECTOR.main(args);
    }
}
