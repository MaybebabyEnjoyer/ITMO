package expression.common;

import base.Asserts;
import base.ExtendedRandom;
import expression.Const;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Type<C> {
    private final IntFunction<C> fromInt;
    private final Function<ExtendedRandom, C> random;
    private final Function<C, Const> constant;

    public Type(final IntFunction<C> fromInt, final Function<ExtendedRandom, C> random, final Class<?> type) {
        this.fromInt = fromInt;
        this.random = random;

        try {
            final MethodHandle constructor = MethodHandles.publicLookup()
                    .findConstructor(Const.class, MethodType.methodType(void.class, type));
            constant = c -> {
                try {
                    return (Const) constructor.invoke(c);
                } catch (final Throwable e) {
                    throw Asserts.error("Cannot create new Const(%s): %s", c, e);
                }
            };
        } catch (final IllegalAccessException | NoSuchMethodException e) {
            throw Asserts.error("Cannot find constructor Const(%s): %s", type, e);
        }
    }

    public Const constant(final C value) {
        return constant.apply(value);
    }

    public C fromInt(final int value) {
        return fromInt.apply(value);
    }

    public C randomValue(final ExtendedRandom random) {
        return this.random.apply(random);
    }
}
