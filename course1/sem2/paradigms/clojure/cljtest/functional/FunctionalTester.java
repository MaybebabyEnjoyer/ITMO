package cljtest.functional;

import base.Selector;
import base.TestCounter;
import cljtest.ClojureEngine;
import jstest.expression.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Tester for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#clojure-functional-expressions">Clojure Functional Expressions</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class FunctionalTester extends BaseTester<Object, ClojureEngine> {
    public static final Dialect PARSED = new Dialect("(variable \"%s\")", "(constant %s.0)", "({op} {args})", " ")
            .functional();
    public static final Dialect UNPARSED = new Dialect("%s", "%s.0", "({op} {args})", " ");

    /* package-private*/ static Selector.Composite<OperationsBuilder> builder() {
        return Builder.selector(
                FunctionalTester.class,
                mode -> mode >= 1,
                (builder, counter) -> new FunctionalTester(
                        counter,
                        builder.language(PARSED, UNPARSED),
                        Optional.empty(),
                        "parseFunction", "", List.of()
                ),
                "easy", "hard"
        );
    }

    protected FunctionalTester(
            final TestCounter counter,
            final Language language,
            final Optional<String> evaluate,
            final String parse,
            final String toString,
            final List<Spoiler> spoilers
    ) {
        super(
                counter,
                new ClojureEngine("expression.clj", evaluate, parse, toString),
                language,
                Stream.concat(STANDARD_SPOILERS.stream(), spoilers.stream()).collect(Collectors.toUnmodifiableList())
        );
    }
}
