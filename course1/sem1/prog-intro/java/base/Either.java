package base;

import java.util.function.Function;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Either<L, R> {
    <NR> Either<L, NR> mapRight(final Function<? super R, NR> f);
    <NR> Either<L, NR> flatMapRight(final Function<? super R, ? extends Either<L, NR>> f);
    <T> T either(Function<? super L, ? extends T> lf, Function<? super R, ? extends T> rf);

    boolean isRight();

    L getLeft();
    R getRight();

    static <L, R> Either<L, R> right(final R value) {
        return new Either<>() {
            @Override
            public <NR> Either<L, NR> mapRight(final Function<? super R, NR> f) {
                return right(f.apply(value));
            }

            @Override
            public <NR> Either<L, NR> flatMapRight(final Function<? super R, ? extends Either<L, NR>> f) {
                return f.apply(value);
            }

            @Override
            public <T> T either(final Function<? super L, ? extends T> lf, final Function<? super R, ? extends T> rf) {
                return rf.apply(value);
            }

            @Override
            public boolean isRight() {
                return true;
            }

            @Override
            public L getLeft() {
                return null;
            }

            @Override
            public R getRight() {
                return value;
            }
        };
    }

    static <L, R> Either<L, R> left(final L value) {
        return new Either<>() {
            @Override
            public <NR> Either<L, NR> mapRight(final Function<? super R, NR> f) {
                return left(value);
            }

            @Override
            public <NR> Either<L, NR> flatMapRight(final Function<? super R, ? extends Either<L, NR>> f) {
                return left(value);
            }

            @Override
            public <T> T either(final Function<? super L, ? extends T> lf, final Function<? super R, ? extends T> rf) {
                return lf.apply(value);
            }

            @Override
            public boolean isRight() {
                return false;
            }

            @Override
            public L getLeft() {
                return value;
            }

            @Override
            public R getRight() {
                return null;
            }
        };
    }
}
