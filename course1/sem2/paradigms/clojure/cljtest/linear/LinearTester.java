package cljtest.linear;

import base.Tester;
import base.TestCounter;
import cljtest.ClojureScript;
import clojure.lang.IPersistentVector;
import jstest.Engine;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Tester for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#clojure-linear">Linear Clojure</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class LinearTester extends Tester {
    public static final ClojureScript.F<IPersistentVector> VECTOR_C = ClojureScript.function("clojure.core/vector", IPersistentVector.class);

    private static final List<Item.Fun> VECTOR = Item.functions("v");
    private static final List<Item.Fun> MATRIX = Item.functions("m");

    static {
        ClojureScript.loadScript("linear.clj");
    }

    public static final Item.Fun SCALAR = new Item.Fun("scalar", args -> Item.value(
            IntStream.range(0, args.get(0).size())
                    .mapToDouble(i -> product(args.stream().map(arg -> arg.get(i))))
                    .sum()
    ));

    public static final Item.Fun V_BY_S = new Item.Fun("v*s", args -> {
        final double q = product(args.stream().skip(1));
        return args.get(0).map(v -> v.mapValue(a -> a * q));
    });

    public static final Item.Fun M_BY_S = new Item.Fun("m*s", args -> {
        final double q = product(args.stream().skip(1));
        return args.get(0).map(row -> row.map(v -> v.mapValue(a -> a * q)));
    });

    public static final Item.Fun M_BY_V = new Item.Fun("m*v", args -> {
        final Item matrix = args.get(0);
        final Item vector = args.get(1);
        final double[] result = new double[matrix.size()];
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < vector.size(); j++) {
                result[i] += matrix.get(i).get(j).value() * vector.get(j).value();
            }
        }
        return Item.vector(Arrays.stream(result).mapToObj(Item::value));
    });

    public static final Item.Fun M_BY_M = new Item.Fun("m*m", args -> {
        Item a = args.get(0);
        for (final Item b : args.subList(1, args.size())) {
            final double[][] result = new double[a.size()][b.get(0).size()];
            for (int i = 0; i < result.length; i++) {
                for (int j = 0; j < result[i].length; j++) {
                    for (int k = 0; k < b.size(); k++) {
                        result[i][j] += a.get(i).get(k).value() * b.get(k).get(j).value();
                    }
                }
            }
            a = Item.vector(Arrays.stream(result).map(row -> Item.vector(Arrays.stream(row).mapToObj(Item::value))));
        }
        return a;
    });

    public static final Item.Fun VECT = new Item.Fun("vect", args -> {
        double[] a = IntStream.range(0, 3).mapToDouble(i -> args.get(0).get(i).value()).toArray();
        for (final Item bb : args.subList(1, args.size())) {
            double[] b = IntStream.range(0, 3).mapToDouble(i -> bb.get(i).value()).toArray();
            a = new double[]{a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]};
        }
        return Item.vector(Arrays.stream(a).mapToObj(Item::value));
    });

    public static final Item.Fun TRANSPOSE = new Item.Fun("transpose", args -> {
        final Item matrix = args.get(0);
        return Item.vector(IntStream.range(0, matrix.get(0).size()).mapToObj(i -> matrix.map(row -> row.get(i))));
    });


    private static double product(final Stream<Item> items) {
        return items.mapToDouble(Item::value).reduce(1, (a, b) -> a * b);
    }

    public LinearTester(final TestCounter counter) {
        super(counter);
    }

    protected static Engine.Result<IPersistentVector> vector(final Number... xs) {
        return wrap(LinearTester::number, xs);
    }

    protected static Engine.Result<IPersistentVector> matrix(final Number[]... m) {
        return wrap(LinearTester::vector, m);
    }

    protected static <I, T> Engine.Result<IPersistentVector> wrap(final Function<I, Engine.Result<T>> wrapper, final I[] m) {
        return vector(Arrays.stream(m).map(wrapper).toArray(Engine.Result[]::new));
    }

    protected static Engine.Result<Number> number(final Number x) {
        return new Engine.Result<>(x.toString(), x);
    }

    protected static Engine.Result<IPersistentVector> vector(final Engine.Result<?>... xs) {
        return VECTOR_C.call(xs);
    }

    protected static Number[] row(final Number... numbers) {
        return numbers;
    }

    @Override
    public void test() {
        runTest(2);

        if (isHard()) {
            runTest(1);
            for (int i = 3; i <= 5; i++) {
                runTest(i);
            }

            expectException(VECTOR, new int[]{3}, new int[][]{{}, {3, 3}});
            expectException(MATRIX, new int[]{3, 3}, new int[][]{{}, {3}, {3, 3, 3}});
            expectException(List.of(VECT, SCALAR), new int[]{3}, new int[][]{{}, {3, 3}});

            final Engine.Result<IPersistentVector> v123 = vector(1L, 2L, 3L);
            final Engine.Result<IPersistentVector> v12 = vector(1.1, 2.1);

            final Engine.Result<IPersistentVector> m123_456 = matrix(row(1.1, 2.1, 3.1), row(4.1, 5.1, 6.1));

            M_BY_S.expectException(counter, m123_456, v123);
            M_BY_V.expectException(counter, m123_456, v12);
            M_BY_M.expectException(counter, m123_456, v123);
        }
    }

    private void runTest(final int args) {
        counter.scope(args + " arg(s)", () -> test(args));
    }

    protected boolean isHard() {
        return counter.mode() > 0;
    }

    protected void expectException(final List<Item.Fun> funs, final int[] okDims, final int[][] failDims) {
        final Supplier<Item> ok = Item.generator(okDims);
        Stream.concat(Arrays.stream(failDims), corrupted(okDims)).map(Item::generator).forEach(fail -> {
            expectException(funs, ok, fail);
            expectException(funs, fail, ok);
        });
    }

    private static Stream<int[]> corrupted(final int... dims) {
        return IntStream.range(0, dims.length)
                .boxed()
                .flatMap(i -> Stream.of(corruptIndex(i, -1, dims), corruptIndex(i, +1, dims)));
    }

    private static int[] corruptIndex(final int i, final int delta, final int[] dims) {
        final int[] nd = dims.clone();
        nd[i] += delta;
        return nd;
    }

    @SafeVarargs
    protected final void expectException(final List<Item.Fun> funs, final Supplier<Item>... generators) {
        for (final Item.Fun fun : funs) {
            final Stream<Item> args = Arrays.stream(generators).map(Supplier::get);
            fun.expectException(counter, args);
        }
    }

    protected void test(final int args) {
        for (int dim = 0; dim <= 10 / TestCounter.DENOMINATOR2; dim++) {
            final Supplier<Item> generator = Item.generator(dim);

            test(args, VECTOR, generator);
            V_BY_S.test(counter, andScalars(args, generator));
            SCALAR.test(args, generator.get(), counter, random());
        }

        for (int complexity = 1; complexity <= 20 / TestCounter.DENOMINATOR2; complexity++) {
            for (int dim1 = 1; dim1 <= complexity; dim1++) {
                final int dim2 = complexity - dim1;
                if (dim2 > 0 || isHard()) {
                    final Supplier<Item> generator = Item.generator(dim1, dim2);

                    test(args, MATRIX, generator);
                    M_BY_S.test(counter, andScalars(args, generator));
                    M_BY_V.test(counter, Stream.of(generator.get().refill(random()), Item.generator(dim2).get().refill(
                            random())));
                    TRANSPOSE.test(counter, Stream.of(generator.get().refill(random())));
                }
            }

            final int complex = complexity;
            final int[] dims = IntStream.generate(() -> 1 + random().nextInt(complex)).limit(args + 1).toArray();
            M_BY_M.test(counter, IntStream.range(0, args).mapToObj(i -> Item.generator(dims[i], dims[i + 1]).get().refill(
                    random())));
        }

        VECT.test(args, Item.generator(3).get(), counter, random());
    }

    private Stream<Item> andScalars(final int args, final Supplier<Item> generator) {
        return Stream.concat(Stream.of(generator.get()), Item.args(args - 1, Item.ZERO, random()));
    }

    protected void test(final int args, final List<Item.Fun> funs, final Supplier<Item> generator) {
        for (final Item.Fun fun : funs) {
            fun.test(args, generator.get(), counter, random());
        }
    }
}
