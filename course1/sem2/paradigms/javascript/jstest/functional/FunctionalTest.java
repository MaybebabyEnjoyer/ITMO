package jstest.functional;

import base.Selector;
import base.TestCounter;
import jstest.JSTester;
import jstest.expression.Builder;
import jstest.expression.Dialect;
import jstest.expression.Language;
import jstest.expression.OperationsBuilder;

import java.nio.file.Path;

import static jstest.expression.Operations.*;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#js-functional-expressions">JavaScript Functional Expressions</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class FunctionalTest {
    public static final Dialect ARITHMETIC = new Dialect("variable('%s')", "cnst(%s)", "{op}({args})", ", ")
            .functional();
    public static final Dialect POLISH = new Dialect("%s", "%s", "{args} {op}", " ");
    private static final Path SCRIPT = Path.of("functionalExpression.js");

    private FunctionalTest() {
    }

    /* package-private */ static Selector.Composite<OperationsBuilder> selector() {
        return Builder.selector(
                FunctionalTest.class,
                mode -> false,
                (builder, counter) -> tester(counter, builder.language(ARITHMETIC, POLISH)),
                "easy", "hard"
        );
    }

    public static final Selector SELECTOR = selector()
            .variant("Base", ARITH)
            .variant("OneFP",           ONE,  TWO,  FLOOR,    CEIL, MADD)
            .variant("OneArgMinMax",    ONE, TWO, argMin(3),   argMin(5), argMax(3), argMax(5))
            .variant("OneSinCos",       ONE, TWO,         SIN,      COS)
            .variant("OneSinhCosh",     ONE, TWO,         SINH,      COSH)
            .selector();

    public static void main(final String... args) {
        SELECTOR.main(args);
    }

    public static JSTester tester(final TestCounter counter, final Language language) {
        return tester(counter, language, counter.mode() >= 1, SCRIPT);
    }

    /* package-private */ static JSTester tester(
            final TestCounter counter,
            final Language language,
            final boolean testParsing,
            final Path script
    ) {
        return new JSTester(counter, language, testParsing, script, "", "toString", "parse");
    }
}
