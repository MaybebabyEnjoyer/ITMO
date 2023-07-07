package expression.common;

import base.Functional;
import base.Pair;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Expr<C, V> {
    private final Node<C> node;
    private final List<Pair<String, V>> variables;

    public Expr(final Node<C> node, final List<Pair<String, V>> variables) {
        this.node = node;
        this.variables = variables;
    }

    public Node<C> node() {
        return node;
    }

    public List<Pair<String, V>> variables() {
        return variables;
    }

    public <T> List<Pair<String, T>> variables(final BiFunction<String, V, T> f) {
        return Functional.map(
                variables,
                variable -> variable.second(f.apply(variable.first(), variable.second()))
        );
    }

    public <T> Expr<C, T> convert(final BiFunction<String, V, T> f) {
        return of(node, variables(f));
    }

    public Expr<C, V> node(final Function<Node<C>, Node<C>> f) {
        return of(f.apply(node), variables);
    }

    public static <C, V> Expr<C, V> of(final Node<C> node, final List<Pair<String, V>> variables) {
        return new Expr<>(node, variables);
    }
}
