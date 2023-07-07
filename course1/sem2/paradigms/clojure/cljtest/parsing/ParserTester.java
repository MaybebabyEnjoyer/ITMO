package cljtest.parsing;

import base.Selector;
import cljtest.functional.FunctionalTester;
import cljtest.object.ObjectTester;
import jstest.expression.*;

import java.util.List;
import java.util.function.ToIntFunction;

/**
 * Tester for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#clojure-expression-parsing">Clojure Expression Parsing</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ParserTester {
    private ParserTester() {
    }

    /* package-private*/ static Selector.Composite<OperationsBuilder> builder() {
        return Builder.selector(
                ParserTester.class,
                mode -> false,
                (builder, counter) -> {
                    final Mode mode = counter.mode() == 1 ? Mode.INFIX : Mode.SUFFIX;
                    return new ObjectTester(
                            counter,
                            builder.language(ObjectTester.PARSED, mode.unparsed),
                            false,
                            mode.parse,
                            mode.toString,
                            mode == Mode.INFIX ? List.of(spoiler(builder.getVariant())) : List.of()
                    );
                },
                "easy", "hard"
        );
    }

    private static FunctionalTester.Spoiler spoiler(final Variant variant) {
        final Expr.Cata<Parsed> cata = createCata(variant::getPriority);
        return (expression, expr, random) -> {
            expression = expr.cata(cata).convert(0);
            return random.nextBoolean() ? ObjectTester.addSpaces(expression, random) : expression;
        };
    }

    private enum Mode {
        SUFFIX("parseObjectPostfix", "toStringPostfix", new Dialect("%s", "%s.0", "({args} {op})", " " )),
        INFIX("parseObjectInfix", "toStringInfix", new Dialect(
                "%s",
                "%s.0",
                (op, args) -> {
                    switch (args.size()) {
                        case 1: return op + " " + args.get(0);
                        case 2: return "(" + args.get(0) + " " + op + " " + args.get(1) + ")";
                        default: throw new AssertionError("Unsupported op " + op + "/" + args.size());
                    }
                }
        ));

        private final String parse;
        private final String toString;
        private final Dialect unparsed;

        Mode(final String parse, final String toString, final Dialect unparsed) {
            this.unparsed = unparsed;
            this.toString = toString;
            this.parse = parse;
        }
    }

    private static Expr.Cata<Parsed> createCata(final ToIntFunction<String> priorities) {
        return Expr.cata(
                name -> priority -> name,
                value -> priority -> String.format("%d.0", value),
                name -> priority -> name,
                (name, args) -> priority -> {
                    switch (args.size()) {
                        case 1:
                            return name + " " + args.get(0).convert(Integer.MAX_VALUE);
                        case 2:
                            final int p = priorities.applyAsInt(name);
                            final int local = Math.abs(p);
                            final boolean parens = local < priority;
                            return (parens ? "(" : "") +
                                    args.get(0).convert(local + (p > 0 ? 0 : 1)) +
                                    name +
                                    args.get(1).convert(local + (p > 0 ? 1 : 0)) +
                                    (parens ? ")" : "");
                        default:
                            throw new AssertionError("Unsupported arity " + name + "/" + args.size());
                    }
                }
        );
    }

    private interface Parsed {
        String convert(int priority);
    }
}
