package jstest.expression;

import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.function.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * Known expression operations.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Operations {
    Operation ARITH = checker -> {
        checker.alias("negate", "Negate");
        checker.alias("+", "Add");
        checker.alias("-", "Subtract");
        checker.alias("*", "Multiply");
        checker.alias("/", "Divide");
    };

    Operation NARY_ARITH = checker -> {
        checker.unary("negate", "Negate", a -> -a, null);

        checker.any("+", "Add", 0, 2, arith(0, Double::sum));
        checker.any("-", "Subtract", 1, 2, arith(0, (a, b) -> a - b));
        checker.any("*", "Multiply", 0, 2, arith(1, (a, b) -> a * b));
        checker.any("/", "Divide", 1, 2, arith(1, (a, b) -> a / b));
    };


    // OneTwo
    Operation ONE = constant("one", 1);
    Operation TWO = constant("two", 2);

    // FP
    Operation MADD = fixed("*+", "Madd", 3, args -> args[0] * args[1] + args[2], null);
    Operation FLOOR = unary("_", "Floor", Math::floor, null);
    Operation CEIL = unary("^", "Ceil", Math::ceil, null);

    // ArgMinMax
    static Operation argMin(final int arity) {
        return arg(arity, "Min", DoubleStream::min);
    }

    static Operation argMax(final int arity) {
        return arg(arity, "Max", DoubleStream::max);
    }

    private static Operation arg(
            final int arity,
            final String name, final Function<DoubleStream, OptionalDouble> f
    ) {
        return fix("arg" + name, "Arg" + name, arity, args -> {
            final double[] values = args.toArray();
            return f.apply(Arrays.stream(values)).stream()
                    .flatMap(value -> IntStream.range(
                            0,
                            values.length
                    ).filter(i -> values[i] == value).asDoubleStream())
                    .findFirst();
        });
    }

    // Sumsq, Distance
    private static double sumsq(final double... args) {
        return Arrays.stream(args).map(a -> a * a).sum();
    }

    static Operation sumsq(final int arity, final int[][] simplifications) {
        return fix("sumsq", "Sumsq", arity, Operations::sumsq, simplifications);
    }

    static Operation distance(final int arity, final int[][] simplifications) {
        return fix("distance", "Distance", arity, args -> Math.sqrt(sumsq(args)), simplifications);
    }

    // Sumrec, hmean
    private static double sumrec(final double... args) {
        return Arrays.stream(args).map(a -> 1 / a).sum();
    }

    static Operation sumrec(final int arity, final int[][] simplifications) {
        return fix("sumrec", "Sumrec", arity, Operations::sumrec, simplifications);
    }

    static Operation hmean(final int arity, final int[][] simplifications) {
        return fix("hmean", "HMean", arity, args -> args.length / sumrec(args), simplifications);
    }

    // Sumexp, LSE
    private static double sumexp(final double[] args) {
        return Arrays.stream(args).map(Math::exp).sum();
    }

    Operation SUMEXP = any("sumexp", "Sumexp", 0, 3, Operations::sumexp);
    Operation LSE = any("lse", "LSE", 1, 5, args -> Math.log(sumexp(args)));


    // MeanSQ, RMS
    private static double meanSq(final double[] args) {
        return sumsq(args) / args.length;
    }

    private static double rms(final double[] args) {
        return Math.sqrt(meanSq(args));
    }

    Operation MEANSQ = any("meansq", "Meansq", 1, 3, Operations::meanSq);
    Operation RMS = any("rms", "RMS", 1, 5, Operations::rms);

    // Common

    private static Operation constant(final String name, final double value) {
        return checker -> checker.constant(name, value);
    }

    private static Operation unary(final String name, final String alias, final DoubleUnaryOperator op, final int[][] simplifications) {
        return checker -> checker.unary(name, alias, op, simplifications);
    }

    private static Operation binary(final String name, final String alias, final DoubleBinaryOperator op, final int[][] simplifications) {
        return checker -> checker.binary(name, alias, op, simplifications);
    }
    private static Operation fix(final String name, final String alias, final int arity, final Function<DoubleStream, OptionalDouble> f) {
        final BaseTester.Func wf = args -> f.apply(Arrays.stream(args)).orElseThrow();
        final int[][] simplifications = null;
        return arity >= 0
               ? fix(name, alias, arity, wf, simplifications)
               : any(name, alias, -arity - 1, -arity - 1, wf);
    }

    private static Operation fix(
            final String name,
            final String alias,
            final int arity,
            final BaseTester.Func wf,
            final int[][] simplifications
    ) {
        return fixed(name + arity, alias + arity, arity, wf, simplifications);
    }

    private static Operation fixed(final String name, final String alias, final int arity, final BaseTester.Func f, final int[][] simplifications) {
        return checker -> checker.fixed(name, alias, arity, f, simplifications);
    }

    private static Operation any(final String name, final String alias, final int minArity, final int fixedArity, final BaseTester.Func f) {
        return checker -> checker.any(name, alias, minArity, fixedArity, f);
    }

    private static BaseTester.Func arith(final double zero, final DoubleBinaryOperator f) {
        return args -> args.length == 0 ? zero
                : args.length == 1 ? f.applyAsDouble(zero, args[0])
                : Arrays.stream(args).reduce(f).orElseThrow();
    }
    Operation SIN = unary("sin", "Sin", Math::sin,
            new int[][]{{1, 1, 1}, {5, 1, 1}, {9, 14, 1}, {9, 9, 1}, {48, 48, 37}, {27, 23, 23}});
    Operation COS = unary("cos", "Cos", Math::cos,
            new int[][]{{1, 1, 1}, {12, 1, 1}, {16, 21, 1}, {16, 16, 1}, {55, 55, 51}, {41, 23, 23}});


    Operation SINH = unary("sinh", "Sinh", Math::sinh,
            new int[][]{{1, 1, 1}, {6, 1, 1}, {10, 15, 1}, {10, 10, 1}, {51, 51, 40}, {30, 21, 21}});
    Operation COSH = unary("cosh", "Cosh", Math::cosh,
            new int[][]{{1, 1, 1}, {6, 1, 1}, {10, 15, 1}, {10, 10, 1}, {51, 51, 40}, {30, 22, 22}});

    Operation EXP = unary("exp", "Exp", Math::exp,
            new int[][]{{1, 1, 1}, {5, 1, 1}, {9, 14, 1}, {9, 9, 1}, {48, 48, 37}, {27, 22, 22}});
    Operation LN = unary("ln", "Ln", Math::log,
            new int[][]{{1, 1, 1}, {5, 1, 1}, {9, 14, 1}, {9, 9, 1}, {46, 46, 36}, {26, 22, 22}});

    Operation ATAN = unary("atan", "ArcTan", Math::atan,
            new int[][]{{1, 1, 1}, {13, 1, 1}, {21, 26, 1}, {21, 21, 1}, {71, 71, 67}, {59, 22, 22}});
    Operation ATAN2 = binary("atan2", "ArcTan2", Math::atan2,
            new int[][]{{1, 1, 1}, {1, 17, 1}, {16, 1, 1}, {23, 30, 1}, {48, 48, 43}, {50, 46, 41}, {78, 85, 51}, {71, 78, 58}});


    // Inc, dec
    Operation INC = unary("++", "Inc", a -> a + 1, null);
    Operation DEC = unary("--", "Dec", a -> a - 1, null);

    // Unary pow, log
    Operation UNARY_POW = unary("**", "UPow", Math::exp, null);
    Operation UNARY_LOG = unary("//", "ULog", Math::log, null);


    static Operation avg(final int arity) {
        return fix("avg", "Avg", arity, DoubleStream::average);
    }

    Operation SUM = any("sum", "Sum", 0, 3, args -> Arrays.stream(args).sum());
    Operation AVG = avg(-2);

    // Boolean
    Operation NOT           = unary("!", "Not", a -> not(bool(a)), null);
    Operation INFIX_AND     = infix("&&",   "And",  90,  bool((a, b) -> a & b));
    Operation INFIX_OR      = infix("||",   "Or",   80,  bool((a, b) -> a | b));
    Operation INFIX_XOR     = infix("^^",   "Xor",  70,  bool((a, b) -> a ^ b));
    Operation INFIX_IMPL    = infix("->",   "Impl", -60, bool((a, b) -> not(a) | b));
    Operation INFIX_IFF     = infix("<->",  "Iff",  50,  bool((a, b) -> not(a ^ b)));

    private static int not(final int a) {
        return 1 - a;
    }

    private static DoubleBinaryOperator bool(final IntBinaryOperator op) {
        return (a, b) -> op.applyAsInt(bool(a), bool(b)) == 0 ? 0 : 1;
    }

    private static int bool(final double a) {
        return a > 0 ? 1 : 0;
    }

    private static Operation infix(final String name, final String alias, final int priority, final DoubleBinaryOperator op) {
        return checker -> checker.infix(name, alias, priority, op);
    }
}
