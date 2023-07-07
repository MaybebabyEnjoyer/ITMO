package cljtest.object;

import base.Selector;
import base.TestCounter;
import cljtest.functional.FunctionalTester;
import jstest.Engine;
import jstest.expression.*;

import java.util.List;
import java.util.Optional;

/**
 * Tester for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#clojure-object-expressions">Clojure Object Expressions</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ObjectTester extends FunctionalTester {
    public static final Dialect PARSED = new Dialect("(Variable \"%s\")", "(Constant %s.0)", "({op} {args})", " ");
    private static final Diff DIFF = new Diff(1, N, new Dialect("\"%s\"", "%s", "({op} {args})", " "));

    /* package-private*/ static Selector.Composite<OperationsBuilder> builder() {
        return Builder.selector(
                ObjectTester.class,
                mode -> mode >= 1,
                (builder, counter) -> {
                    final Language language = builder.language(PARSED, UNPARSED);
                    return new ObjectTester(
                            counter,
                            language,
                            counter.mode() >= 1,
                            "parseObject", "toString", List.of()
                    );
                },
                "easy", "hard"
        );
    }

    public ObjectTester(
            final TestCounter counter,
            final Language language,
            final boolean testDiff,
            final String parse,
            final String toString,
            final List<Spoiler> spoilers
    ) {
        super(counter, language, Optional.of("evaluate"), parse, toString, spoilers);
        if (testDiff) {
            DIFF.diff(this);
        }
    }

    @Override
    protected void test(final Engine.Result<Object> prepared, final String unparsed) {
        counter.test(() -> engine.toString(prepared).assertEquals(unparsed));
    }
}
