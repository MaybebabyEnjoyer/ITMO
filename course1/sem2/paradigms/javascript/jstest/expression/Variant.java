package jstest.expression;

import base.ExtendedRandom;

/**
 * Expression variant meta-info.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Variant {
    ExtendedRandom random();

    boolean hasVarargs();

    Integer getPriority(String op);
}
