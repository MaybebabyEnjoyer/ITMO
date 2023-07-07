package prtest.parsing;

import base.Selector;
import jstest.expression.Operation;

import java.util.function.BiConsumer;

import static jstest.expression.Operations.*;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#prolog-expression-parsing">Prolog Expression Parser</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ParserTest {
    private static final Operation VARIABLES = checker -> {
        final BiConsumer<Character, Integer> var = (first, index) ->
                checker.variable(first + checker.random().randomString("xyzXYZ"), index);
        for (int i = 0; i < 10; i++) {
            var.accept('x', 0);
            var.accept('y', 1);
            var.accept('z', 2);
        }
    };

    private static final Selector SELECTOR = ParserTester.builder()
            .variant("Base", ARITH)
            .variant("SinCos",      SIN,        COS)
            .variant("SinhCosh",    SINH,       COSH)
            .variant("VarBoolean",  VARIABLES,  NOT, INFIX_AND,  INFIX_OR,   INFIX_XOR)
            .variant("VarImplIff",  VARIABLES,  NOT, INFIX_AND,  INFIX_OR,   INFIX_XOR, INFIX_IMPL, INFIX_IFF)
            .selector();

    private ParserTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
