package jstest.expression;

import base.ExtendedRandom;
import base.Selector;
import base.TestCounter;
import base.Tester;
import jstest.ArithmeticVariant;

import java.util.HashMap;
import java.util.Map;
import java.util.function.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static jstest.expression.BaseVariant.c;

/**
 * Expression test builder.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Builder implements OperationsBuilder, LanguageBuilder {
    private final ArithmeticVariant variant = new ArithmeticVariant();
    private final boolean testMulti;
    private final Map<String, String> aliases = new HashMap<>();

    private final Expr vx = variant.vx, vy = variant.vy, vz = variant.vz;
    private final Supplier<Expr> constGenerator = () -> c(variant.random().nextInt(10));
    private final Supplier<Expr> variableGenerator = () -> variant.random().randomItem(variant.getVariables());
    private final Supplier<Expr> generator = () -> variant.random().nextBoolean()
            ? variableGenerator.get()
            : constGenerator.get();

    public Builder(final boolean testMulti) {
        this.testMulti = testMulti;
    }

    public static Selector.Composite<OperationsBuilder> selector(
            final Class<?> owner,
            final IntPredicate testMulti,
            final BiFunction<LanguageBuilder, TestCounter, Tester> tester,
            final String... modes
    ) {
        return Selector.composite(
                owner,
                counter -> new Builder(testMulti.test(counter.mode())),
                (builder, counter) -> tester.apply((Builder) builder, counter).test(),
                modes
        );
    }

    private Expr f(final String name, final Expr... args) {
        return variant.f(name, args);
    }

    private void tests(final int[][] simplifications, final Expr... tests) {
        variant.tests(simplifications, tests);
    }

    @Override
    public void constant(final String name, final double value) {
        final BaseTester.Func expr = vars -> value;
        variant.nullary(name, expr);
        final Expr constant = Expr.nullary(name, expr);
        tests(null,
                f("+", constant, vx),
                f("-", vy, constant),
                f("*", vz, constant),
                f("/", constant, vx)
        );
    }

    @Override
    public void variable(final String name, final int index) {
        variant.variable(name, index);
    }

    @Override
    public ExtendedRandom random() {
        return getVariant().random();
    }

    @Override
    public void unary(
            final String name,
            final String alias,
            final DoubleUnaryOperator op,
            final int[][] simplifications
    ) {
        variant.unary(name, op);
        alias(name, alias);
        unaryTests(name, simplifications);
    }

    private void unaryTests(final String name, final int[][] simplifications) {
        tests(simplifications,
                f(name, c(2)),
                f(name, vx),
                f(name, f("-", vx, vy)),
                f(name, f("+", vx, vy)),
                f(name, f("/", f(name, vz), f("+", vx, vy))),
                f("+", f(name, f(name, f("+", vx, c(10)))), f("*", vz, f("*", vy, f(name, c(4)))))
        );
    }

    @Override
    public void binary(
            final String name,
            final String alias,
            final DoubleBinaryOperator op,
            final int[][] simplifications
    ) {
        variant.binary(name, op);
        alias(name, alias);
        binaryTests(name, simplifications);
    }

    @Override
    public void infix(final String name, final String alias, final int priority, final DoubleBinaryOperator op) {
        variant.infix(name, priority, op);
        alias(name, alias);
        binaryTests(name, null);
    }

    private void binaryTests(final String name, final int[][] simplifications) {
        tests(simplifications,
                f(name, c(2), c(3)),
                f(name, c(2), vy),
                f(name, vx, c(3)),
                f(name, vx, vy),
                f(name, f("negate", vz), f("+", vx, vy)),
                f(name, f("-", vz, vy), f("negate", vx)),
                f(name, f("negate", vz), f(name, vx, vy)),
                f(name, f(name, vx, vy), f("negate", vz))
        );
    }

    @Override
    public void fixed(
            final String name,
            final String alias,
            final int arity,
            final BaseTester.Func f,
            final int[][] simplifications
    ) {
        variant.fixed(name, arity, f);
        alias(name, alias);

        if (arity == 1) {
            unaryTests(name, simplifications);
        } else if (arity == 2) {
            binaryTests(name, simplifications);
        } else if (arity == 3) {
            final Expr e1 = f(name, vx, vy, c(0));
            final Expr e2 = f(name, vx, vy, c(1));
            final Expr e3 = f(name, f("+", vx, vy), f("-", vy, vz), f("*", vz, vx));
            tests(
                    simplifications,
                    f(name, c(1), c(2), c(3)),
                    f(name, c(0), vx, vy),
                    f(name, c(0), c(0), vz),
                    e1,
                    e2,
                    e3,
                    f(name, e1, e2, e3)
            );
        } else if (arity == 4) {
            final Expr e1 = f(name, vx, vy, vz, c(0));
            final Expr e2 = f(name, vx, vy, vz, c(1));
            final Expr e3 = f(name, c(1), c(2), c(3), vx);
            final Expr e4 = f(name, f("+", vx, vy), f("-", vy, vz), f("*", vz, vx), f("/", vx, c(3)));
            tests(
                    simplifications,
                    f(name, c(1), c(2), c(3), c(4)),
                    f(name, c(0), vx, vy, vz),
                    f(name, c(0), c(0), c(1), vz),
                    e1,
                    e2,
                    e3,
                    e4,
                    f(name, e1, e2, e3, e4)
            );
        } else {
            tests(
                    simplifications,
                    Stream.concat(
                                    Stream.of(
                                            f(name, arity, constGenerator),
                                            f(name, arity, variableGenerator)
                                    ),
                                    IntStream.range(0, 10).mapToObj(i -> f(name, arity, generator))
                            )
                            .toArray(Expr[]::new)
            );
        }
    }

    private Expr f(
            final String name,
            final int arity,
            final Supplier<Expr> generator
    ) {
        return f(name, Stream.generate(generator).limit(arity).toArray(Expr[]::new));
    }

    @Override
    public void any(
            final String name,
            final String alias,
            final int minArity,
            final int fixedArity,
            final BaseTester.Func f
    ) {
        variant.any(name, minArity, 5, f);
        alias(name, alias);
        if (testMulti) {
            variant.any(name, minArity, 5, f);
        } else {
            variant.fixed(name, fixedArity, f);
        }

        if (testMulti) {
            variant.tests(
                    f(name, vx),
                    f(name, vx, vy, vz),
                    f(name, vx, vy, vz, c(3), c(5)),
                    f(name, f("+", vx, c(2))),
                    f(name, f("+", vx, vy))
            );
        }

        for (int i = 1; i < 10; i++) {
            variant.tests(f(name, testMulti ? i : fixedArity, generator));
        }
    }

    @Override
    public void alias(final String name, final String alias) {
        aliases.put(name, alias);
    }

    @Override
    public Variant getVariant() {
        return variant;
    }

    @Override
    public Language language(final Dialect parsed, final Dialect unparsed) {
        return new Language(parsed.renamed(name -> aliases.getOrDefault(name, name)), unparsed, variant);
    }
}
