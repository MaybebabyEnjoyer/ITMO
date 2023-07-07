package prtest.parsing;

import base.TestCounter;
import jstest.Engine;
import jstest.expression.*;
import prtest.PrologEngine;
import prtest.Rule;
import prtest.Value;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;

/**
 * Tester for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#prolog-expression-parsing">Prolog Expression Parser</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ParserTester extends BaseTester<Value, PrologEngine> {
    private static final Function<String, String> PREFIXER = name -> Character.isLetter(name.charAt(0)) ? "op_" + name : name;

    public ParserTester(final TestCounter counter, final Language language, final String parse) {
        super(
                counter,
                new PrologEngine(
                        Path.of("expression.pl"),
                        Rule.func("evaluate", 2),
                        new Rule(parse, 2)
                ),
                language,
                STANDARD_SPOILERS
        );
        BaseTester.TESTS = 44;
    }

    @Override
    protected void test(final Engine.Result<Value> prepared, final String unparsed) {
        counter.test(() -> engine.toString(prepared).assertEquals(unparsed));
    }

    public static final Dialect PARSED = new Dialect("variable(%s)", "const(%s.0)", "operation({op}, {args})", ", ")
            .renamed(PREFIXER.andThen(String::toLowerCase));

    /* package-private*/ static base.Selector.Composite<OperationsBuilder> builder() {
        return Builder.selector(
                ParserTest.class,
                mode -> false,
                (builder, counter) -> {
                    final Mode mode = Mode.values()[counter.mode()];
                    return new ParserTester(counter, builder.language(PARSED, mode.unparsed), mode.parse);
                },
                Arrays.stream(Mode.values()).map(mode -> mode.name().toLowerCase(Locale.ROOT)).toArray(String[]::new)
        );
    }

    private enum Mode {
        PREFIX("({op} {args})", " "),
        POSTFIX("({args} {op})", " "),
        PREFN("{op}({args})", ","),
        POSTFN("({args}){op}", ","),
        POLISH("{op} {args}", " "),
        INFIX(new Dialect(
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

        private final Dialect unparsed;
        private final String parse;

        Mode(final Dialect unparsed) {
            this.unparsed = unparsed;
            parse = name().toLowerCase() + "_str";
        }

        Mode(final String operation, final String separator) {
            this(new Dialect("%s", "%s.0", operation, separator));
        }
    }
}
