package expression.common;

import base.Either;

import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Reason {
    public static final Reason OVERFLOW = new Reason("Overflow");
    public static final Reason DBZ = new Reason("Division by zero");

    private final String description;

    public Reason(final String description) {
        this.description = description;
    }

    public static <T> Either<Reason, T> eval(final Supplier<T> action) {
        try {
            return Either.right(action.get());
        } catch (final ReasonException e) {
            return Either.left(e.reason);
        }
    }

    public static int overflow(final long value) {
        return value < Integer.MIN_VALUE || Integer.MAX_VALUE < value
                ? OVERFLOW.error()
                : (int) value;
    }

    public <T> T error() {
        throw new ReasonException(this);
    }

    public LongUnaryOperator less(final long limit, final LongUnaryOperator op) {
        return a -> a < limit ? error() : op.applyAsLong(a);
    }

    public LongUnaryOperator greater(final int limit, final LongUnaryOperator op) {
        return a -> a > limit ? error() : op.applyAsLong(a);
    }

    private static class ReasonException extends RuntimeException {
        private final Reason reason;

        public ReasonException(final Reason reason) {
            super(reason.description);
            this.reason = reason;
        }
    }
}
