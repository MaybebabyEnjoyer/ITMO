package expression.common;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public abstract class Node<T> {
    private Node() {
    }

    public abstract <R> R get(Const<T, R> con, Nullary<R> nul, Unary<Node<T>, R> un, Binary<Node<T>, R> bin);
    public abstract <R> R cata(Const<T, R> con, Nullary<R> nul, Unary<R, R> un, Binary<R, R> bin);

    public final String toPolish() {
        return cata(
                T::toString,
                name -> name,
                (name, a) -> a + " " + name + ":1",
                (name, a1, a2) -> a1 + " " + a2 + " " + name + ":2"
        );
    }

    @Override
    public final String toString() {
        return cata(
                T::toString,
                name -> name,
                (name, a) -> name + " " + a,
                (name, a1, a2) -> "(" + a1 + " " + name + " " + a2 + ")"
        );
    }

    public static <T> Node<T> constant(final T value) {
        return new Node<>() {
            @Override
            public <R> R get(final Const<T, R> con, final Nullary<R> nul, final Unary<Node<T>, R> un, final Binary<Node<T>, R> bin) {
                return con.apply(value);
            }

            @Override
            public <R> R cata(final Const<T, R> con, final Nullary<R> nul, final Unary<R, R> un, final Binary<R, R> bin) {
                return con.apply(value);
            }
        };
    }

    public static <T> Node<T> op(final String name) {
        return new Node<>() {
            @Override
            public <R> R get(final Const<T, R> con, final Nullary<R> nul, final Unary<Node<T>, R> un, final Binary<Node<T>, R> bin) {
                return nul.apply(name);
            }

            @Override
            public <R> R cata(final Const<T, R> con, final Nullary<R> nul, final Unary<R, R> un, final Binary<R, R> bin) {
                return nul.apply(name);
            }
        };
    }

    public static <T> Node<T> op(final String name, final Node<T> arg) {
        return new Node<>() {
            @Override
            public <R> R get(final Const<T, R> con, final Nullary<R> nul, final Unary<Node<T>, R> un, final Binary<Node<T>, R> bin) {
                return un.apply(name, arg);
            }

            @Override
            public <R> R cata(final Const<T, R> con, final Nullary<R> nul, final Unary<R, R> un, final Binary<R, R> bin) {
                return un.apply(name, arg.cata(con, nul, un, bin));
            }
        };
    }

    public static <T> Node<T> op(final String name, final Node<T> arg1, final Node<T> arg2) {
        return new Node<>() {
            @Override
            public <R> R get(final Const<T, R> con, final Nullary<R> nul, final Unary<Node<T>, R> un, final Binary<Node<T>, R> bin) {
                return bin.apply(name, arg1, arg2);
            }

            @Override
            public <R> R cata(final Const<T, R> con, final Nullary<R> nul, final Unary<R, R> un, final Binary<R, R> bin) {
                return bin.apply(name, arg1.cata(con, nul, un, bin), arg2.cata(con, nul, un, bin));
            }
        };
    }

    @FunctionalInterface
    public interface Const<T, R> extends Function<T, R> {}

    @FunctionalInterface
    public interface Nullary<R> extends Function<String, R> {}

    @FunctionalInterface
    public interface Unary<T, R> extends BiFunction<String, T, R> {}

    @FunctionalInterface
    public interface Binary<T, R> {
        R apply(String name, T arg1, T arg2);
    }
}
