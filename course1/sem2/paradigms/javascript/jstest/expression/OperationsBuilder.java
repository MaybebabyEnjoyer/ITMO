package jstest.expression;

import base.ExtendedRandom;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/**
 * Operations builder.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface OperationsBuilder {
    void constant(String name, double value);

    void variable(String name, int index);

    ExtendedRandom random();

    void unary(String name, String alias, DoubleUnaryOperator op, int[][] simplifications);

    void binary(String name, String alias, DoubleBinaryOperator op, int[][] simplifications);

    void infix(String name, String alias, int priority, DoubleBinaryOperator op);

    void fixed(String name, String alias, int arity, BaseTester.Func f, int[][] simplifications);

    void any(String name, String alias, int minArity, int fixedArity, BaseTester.Func f);

    void alias(String name, String alias);
}
