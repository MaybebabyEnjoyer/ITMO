package expression.common;

import base.ExtendedRandom;
import base.Functional;
import base.Pair;
import base.TestCounter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Generator<C> {
    private final ExtendedRandom random;
    private final Supplier<C> constant;

    private final List<Op<Integer>> ops = new ArrayList<>();
    private final Set<String> forbidden = new HashSet<>();

    public Generator(final ExtendedRandom random, final Supplier<C> constant) {
        this.random = random;
        this.constant = constant;
    }

    public void add(final String name, final int arity) {
        ops.add(Op.of(name, arity));
        forbidden.add(name);
    }

    private Node<C> generate(
            final List<Node<C>> variables,
            final boolean nullary,
            final Supplier<Node<C>> unary,
            final Supplier<Pair<Node<C>, Node<C>>> binary
    ) {
        if (nullary || ops.isEmpty()) {
            return random.nextBoolean() ? random.randomItem(variables) : Node.constant(constant.get());
        } else {
            final Op<Integer> op = random.randomItem(ops);
            if (op.value == 1) {
                return Node.op(op.name, unary.get());
            } else {
                final Pair<Node<C>, Node<C>> pair = binary.get();
                return Node.op(op.name, pair.first(), pair.second());
            }
        }
    }

    private Node<C> generate(final List<Node<C>> variables, final boolean nullary, final Supplier<Node<C>> child) {
        return generate(variables, nullary, child, () -> Pair.of(child.get(), child.get()));
    }

    private Node<C> generateFullDepth(final List<Node<C>> variables, final int depth) {
        return generate(variables, depth == 0, () -> generateFullDepth(variables, depth - 1));
    }

    private Node<C> generatePartialDepth(final List<Node<C>> variables, final int depth) {
        return generate(variables, depth == 0, () -> generatePartialDepth(variables, random.nextInt(depth)));
    }

    private Node<C> generateSize(final List<Node<C>> variables, final int size) {
        return generate(
                variables,
                size == 0,
                () -> generateSize(variables, size - 1),
                () -> Pair.of(generateSize(variables, (size - 1) / 2),
                        generateSize(variables, size - 1 - (size - 1) / 2))
        );
    }

    public <E> void testRandom(
            final int denominator,
            final TestCounter counter,
            final ExpressionKind.Variables<E> variables,
            final Consumer<Expr<C, E>> consumer
    ) {
        final int d = Math.max(TestCounter.DENOMINATOR, denominator);
        testRandom(counter, variables, consumer, 1, 100, 100 / d, (vars, depth) -> generateFullDepth(vars, Math.min(depth, 3)));
        testRandom(counter, variables, consumer, 2, 1000 / d, 1, this::generateSize);
        testRandom(counter, variables, consumer, 3, 12, 100 / d, this::generateFullDepth);
        testRandom(counter, variables, consumer, 4, 777 / d, 1, this::generatePartialDepth);
    }

    private <C, E> void testRandom(
            final TestCounter counter,
            final ExpressionKind.Variables<E> variables,
            final Consumer<Expr<C, E>> consumer,
            final int seq,
            final int levels,
            final int perLevel,
            final BiFunction<List<Node<C>>, Integer, Node<C>> generator
    ) {
        counter.scope("Random tests #" + seq, () -> {
            final int total = levels * perLevel;
            int generated = 0;
            for (int level = 0; level < levels; level++) {
                for (int j = 0; j < perLevel; j++) {
                    if (generated % 100 == 0) {
                        progress(counter, total, generated);
                    }
                    generated++;

                    final List<Pair<String, E>> vars = variables(variables, random.nextInt(10) + 1);

                    consumer.accept(Expr.of(generator.apply(Functional.map(vars, v -> Node.op(v.first())), level), vars));
                }
            }
            progress(counter, generated, total);
        });
    }

    public <E> List<Pair<String, E>> variables(final ExpressionKind.Variables<E> variables, final int count) {
        List<Pair<String, E>> vars;
        do {
            vars = variables.generate(random, count);
        } while (vars.stream().map(Pair::first).anyMatch(forbidden::contains));
        return vars;
    }

    private static void progress(final TestCounter counter, final int total, final int generated) {
        counter.format("Completed %4d out of %d%n", generated, total);
    }
}
