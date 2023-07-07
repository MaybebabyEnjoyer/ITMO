package jstest.expression;

import base.ExtendedRandom;
import jstest.expression.BaseTester.Func;

import java.util.*;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Stream;

/**
 * Base expressions variant.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class BaseVariant implements Variant {
    private static final int MAX_C = 1_000;

    private final ExtendedRandom random = new ExtendedRandom(getClass());

    public final List<Expr> tests = new ArrayList<>();
    private final StringMap<Operator> operators = new StringMap<>();
    private final StringMap<Expr> nullary = new StringMap<>();
    private final StringMap<Expr> variables = new StringMap<>();

    private final Map<String, Integer> priorities = new HashMap<>();
    private final List<int[]> simplifications = new ArrayList<>();

    public List<Expr> getTests() {
        return tests;
    }

    public Expr randomTest(final int size) {
        return generate(size / 10 + 2);
    }

    private Expr generate(final int depth) {
        if (depth > 0) {
            return generateOp(depth);
        } else if (random.nextBoolean()) {
            return variables.random(random);
        } else if (nullary.isEmpty() || random.nextBoolean()){
            return c(random.nextInt(-MAX_C, MAX_C));
        } else {
            return nullary.random(random);
        }
    }

    protected Expr generateOp(final int depth) {
        if (random.nextInt(6) == 0 || operators.isEmpty()) {
            return generateP(depth);
        } else {
            final Operator operator = operators.random(random);
            final Expr[] args = Stream.generate(() -> generateP(depth))
                    .limit(random.nextInt(operator.minArity, operator.maxArity))
                    .toArray(Expr[]::new);
            return f(operator.name, args);
        }
    }

    protected Expr generateP(final int depth) {
        return generate(random.nextInt(depth));
    }

    public void tests(final Expr... tests) {
        this.tests.addAll(Arrays.asList(tests));
    }

    public void tests(final int[][] simplifications, final Expr... tests) {
        if (simplifications == null) {
            this.simplifications.addAll(Collections.nCopies(tests.length, null));
        } else {
            assert simplifications.length == tests.length : tests.length + " tests and " + simplifications.length + " simplifications";
            this.simplifications.addAll(Arrays.asList(simplifications));
        }
        tests(tests);
    }

    public void fixed(final String name, final int arity, final Func f) {
        any(name, arity, arity, f);
    }

    public void any(final String name, final int minArity, final int maxArity, final Func f) {
        operators.put(name, new Operator(name, minArity, maxArity, f));
    }

    public void unary(final String name, final DoubleUnaryOperator answer) {
        fixed(name, 1, args -> answer.applyAsDouble(args[0]));
    }

    public void binary(final String name, final DoubleBinaryOperator answer) {
        fixed(name, 2, args -> answer.applyAsDouble(args[0], args[1]));
    }

    public void infix(final String name, final int priority, final DoubleBinaryOperator answer) {
        binary(name, answer);
        priorities.put(name, priority);
    }

    public void nullary(final String name, final Func f) {
        nullary.put(name, Expr.nullary(name, f));
    }

    public Expr f(final String name, final Expr... args) {
        return Expr.f(name, operators.get(name), List.of(args));
    }

    protected Expr n(final String name) {
        return nullary.get(name);
    }

    public static Expr c(final int value) {
        return Expr.constant(value);
    }

    public Expr variable(final String name, final int index) {
        final Expr variable = Expr.variable(name, index);
        variables.put(name, variable);
        return variable;
    }

    public List<Expr> getVariables() {
        return List.copyOf(variables.values());
    }

    @Override
    public ExtendedRandom random() {
        return random;
    }

    @Override
    public boolean hasVarargs() {
        return operators.values().stream().anyMatch(Operator::isVararg);
    }

    @Override
    public Integer getPriority(final String op) {
        return priorities.get(op);
    }

    public List<int[]> getSimplifications() {
        return simplifications;
    }

    private static class Operator implements Func {
        final String name;
        final int minArity, maxArity;
        final Func f;

        public Operator(final String name, final int minArity, final int maxArity, final Func f) {
            assert 0 <= minArity && minArity <= maxArity;

            this.name = name;
            this.minArity = minArity;
            this.maxArity = maxArity;
            this.f = f;
        }

        @Override
        public double applyAsDouble(final double[] args) {
            return Arrays.stream(args).allMatch(Double::isFinite) ? f.applyAsDouble(args) : Double.NaN;
        }

        public boolean isVararg() {
            return minArity < maxArity;
        }
    }

    private static class StringMap<T> {
        private final List<String> names = new ArrayList<>();
        private final Map<String, T> values = new HashMap<>();

        public T get(final String name) {
            return values.get(name);
        }

        public T random(final ExtendedRandom random) {
            return get(random.randomItem(names));
        }

        private boolean isEmpty() {
            return values.isEmpty();
        }

        private void put(final String name, final T value) {
            names.add(name);
            values.put(name, value);
        }

        private Collection<T> values() {
            return values.values();
        }
    }
}
