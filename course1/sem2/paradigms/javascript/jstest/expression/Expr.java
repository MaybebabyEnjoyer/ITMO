package jstest.expression;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

/**
 * Expression instance.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Expr {
    private final BaseTester.Func answer;
    /* There are no forall generics in Java, so using Object as placeholder. */
    private final Function<Cata<Object>, Object> coCata;

    private Expr(final BaseTester.Func answer, final Function<Cata<Object>, Object> coCata) {
        this.answer = answer;
        this.coCata = coCata;
    }

    public <T> T cata(final Cata<T> cata) {
        @SuppressWarnings("unchecked")
        final Function<Cata<T>, T> coCata = (Function<Cata<T>, T>) (Function<?, ?>) this.coCata;
        return coCata.apply(cata);
    }

    public double evaluate(final double... vars) {
        return answer.applyAsDouble(vars);
    }

    static Expr f(final String name, final BaseTester.Func operator, final List<Expr> args) {
        Objects.requireNonNull(operator, "Unknown operation " + name);
        return new Expr(
                vars -> operator.applyAsDouble(args.stream().mapToDouble(arg -> arg.evaluate(vars)).toArray()),
                cata -> cata.operation(
                        name,
                        args.stream().map(arg -> arg.cata(cata)).collect(Collectors.toUnmodifiableList())
                )
        );
    }

    static Expr constant(final int value) {
        return new Expr(vars -> value, cata -> cata.constant(value));
    }

    static Expr variable(final String name, final int index) {
        return new Expr(vars -> vars[index], cata -> cata.variable(name));
    }

    static Expr nullary(final String name, final BaseTester.Func answer) {
        return new Expr(answer, cata -> cata.nullary(name));
    }

    /**
     * Expression catamorphism.
     *
     * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
     */
    public static <T> Cata<T> cata(
            final Function<String, T> variable,
            final IntFunction<T> constant,
            final Function<String, T> nullary,
            final BiFunction<String, List<T>, T> operation
    ) {
        return new Cata<>() {
            @Override
            public T variable(final String name) {
                return variable.apply(name);
            }

            @Override
            public T constant(final int value) {
                return constant.apply(value);
            }

            @Override
            public T nullary(final String name) {
                return nullary.apply(name);
            }

            @Override
            public T operation(final String name, final List<T> args) {
                return operation.apply(name, args);
            }
        };
    }

    public interface Cata<T> {

        T variable(String name);

        T constant(int value);

        T nullary(String name);

        T operation(String name, List<T> args);
    }
}
