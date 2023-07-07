package expression.common;

import base.ExtendedRandom;
import base.Functional;
import base.Pair;
import expression.ToMiniString;

import java.util.List;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ExpressionKind<E extends ToMiniString, C> {
    private final Type<C> type;
    private final Class<E> kind;
    public final Variables<E> variables;
    private final Evaluator<E, C> evaluator;

    public ExpressionKind(
            final Type<C> type,
            final Class<E> kind,
            final Variables<E> variables,
            final Evaluator<E, C> evaluator
    ) {
        this.type = type;
        this.kind = kind;
        this.variables = variables;
        this.evaluator = evaluator;
    }

    public ExpressionKind(
            final Type<C> type,
            final Class<E> kind,
            final List<Pair<String, E>> variables,
            final Evaluator<E, C> evaluator
    ) {
        this(type, kind, (r, c) -> variables, evaluator);
    }

    public C evaluate(final E expression, final List<String> variables, final List<C> values) throws Exception {
        return evaluator.evaluate(expression, variables, values);
    }

    public E cast(final Object expression) {
        return kind.cast(expression);
    }

    public String getName() {
        return kind.getSimpleName();
    }

    public E constant(final C value) {
        return cast(type.constant(value));
    }

    public C randomValue(final ExtendedRandom random) {
        return type.randomValue(random);
    }

    public List<List<C>> allValues(final int length, final List<Integer> values) {
        return Functional.allValues(fromInts(values), length);
    }

    public List<C> fromInts(final List<Integer> values) {
        return Functional.map(values, type::fromInt);
    }

    @Override
    public String toString() {
        return kind.getName();
    }

    @FunctionalInterface
    public interface Variables<E> {
        List<Pair<String, E>> generate(final ExtendedRandom random, final int count);
    }

    @FunctionalInterface
    public interface Evaluator<E, R> {
        R evaluate(final E expression, final List<String> vars, final List<R> values) throws Exception;
    }
}
