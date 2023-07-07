package expression.common;

import base.ExtendedRandom;
import base.Functional;
import base.Pair;
import base.TestCounter;
import expression.ToMiniString;
import expression.common.ExpressionKind.Variables;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class TestGenerator<C> {
    private final TestCounter counter;
    private final ExtendedRandom random;

    private final Generator<C> generator;
    private final NodeRenderer<C> renderer;

    private final Set<String> forbidden = new HashSet<>();
    private final List<Function<List<Node<C>>, Stream<Node<C>>>> basicTests = new ArrayList<>();
    private final List<Node<C>> consts;
    private final boolean verbose;

    public TestGenerator(
            final TestCounter counter,
            final ExtendedRandom random,
            final Supplier<C> constant,
            final List<C> constants,
            final boolean verbose
    ) {
        this.counter = counter;
        this.random = random;
        this.verbose = verbose;

        generator = new Generator<>(random, constant);
        renderer = new NodeRenderer<>(this.random);

        consts = Functional.map(constants, Node::constant);
        basicTests.add(vars -> consts.stream());
        basicTests.add(List::stream);
    }

    private <E> void test(final Expr<C, E> expr, final Consumer<Test<C, E>> consumer) {
        consumer.accept(new Test<>(
                expr,
                renderer.render(expr, NodeRenderer.FULL),
                renderer.render(expr, NodeRenderer.FULL_EXTRA),
                renderer.render(expr, NodeRenderer.MINI),
                renderer.render(expr, NodeRenderer.SAME)
        ));
    }

    private Node<C> c() {
        return random.randomItem(consts);
    }

    private Node<C> v(final List<Node<C>> variables) {
        return random.randomItem(variables);
    }

    private static <C> Node<C> f(final String name, final Node<C> arg) {
        return Node.op(name, arg);
    }

    private static <C> Node<C> f(final String name, final Node<C> arg1, final Node<C> arg2) {
        return Node.op(name, arg1, arg2);
    }

    @SafeVarargs
    private void basicTests(final Function<List<Node<C>>, Node<C>>... tests) {
        Arrays.stream(tests).map(test -> test.andThen(Stream::of)).forEachOrdered(basicTests::add);
    }

    public void unary(final String name) {
        generator.add(name, 1);
        renderer.unary(name);
        forbidden.add(name);

        if (verbose) {
            basicTests.add(vars -> Stream.concat(consts.stream(), vars.stream()).map(a -> f(name, a)));
        } else {
            basicTests(
                    vars -> f(name, c()),
                    vars -> f(name, v(vars))
            );
        }

        final Function<List<Node<C>>, Node<C>> p1 = vars -> f(name, f(name, f("+", v(vars), c())));
        final Function<List<Node<C>>, Node<C>> p2 = vars -> f("*", v(vars), f("*", v(vars), f(name, c())));
        basicTests(
                vars -> f(name, f("+", v(vars), v(vars))),
                vars -> f(name, f(name, v(vars))),
                vars -> f(name, f("/", f(name, v(vars)), f("+", v(vars), v(vars)))),
                p1,
                p2,
                vars -> f("+", p1.apply(vars), p2.apply(vars))
        );
    }

    public void binary(final String name, final int priority) {
        generator.add(name, 2);
        renderer.binary(name, priority);
        forbidden.add(name);

        if (verbose) {
            basicTests.add(vars -> Stream.concat(consts.stream(), vars.stream().limit(3))
                            .flatMap(a -> consts.stream().map(b -> f(name, a, b))));
        } else {
            basicTests(
                    vars -> f(name, c(), c()),
                    vars -> f(name, v(vars), c()),
                    vars -> f(name, c(), v(vars)),
                    vars -> f(name, v(vars), v(vars))
            );
        }

        final Function<List<Node<C>>, Node<C>> p1 = vars -> f(name, f(name, f("+", v(vars), c()), v(vars)), v(vars));
        final Function<List<Node<C>>, Node<C>> p2 = vars -> f("*", v(vars), f("*", v(vars), f(name, c(), v(vars))));

        basicTests(
                vars -> f(name, f(name, v(vars), v(vars)), v(vars)),
                vars -> f(name, v(vars), f(name, v(vars), v(vars))),
                vars -> f(name, f(name, v(vars), v(vars)), f(name, v(vars), v(vars))),
                vars -> f(name, f("-", f(name, v(vars), v(vars)), c()), f("+", v(vars), v(vars))),
                p1,
                p2,
                vars -> f("+", p1.apply(vars), p2.apply(vars))
        );
    }

    public <E> void testBasic(final Variables<E> variables, final Consumer<Test<C, E>> consumer) {
        basicTests.forEach(test -> {
            final List<Pair<String, E>> vars = generator.variables(variables, random.nextInt(5) + 3);
            test.apply(Functional.map(vars, v -> Node.op(v.first()))).forEachOrdered(node -> {
//                counter.println(full.render(Expr.of(node, vars)));
                test(Expr.of(node, vars), consumer);
            });
        });
    }

    public <E> void testRandom(final int denominator, final Variables<E> variables, final Consumer<Test<C, E>> consumer) {
        generator.testRandom(denominator, counter, variables, expr -> test(expr, consumer));
    }

    public String full(final Expr<C, ?> expr) {
        return renderer.render(expr, NodeRenderer.FULL);
    }

    public <E extends ToMiniString> List<Pair<String,E>> variables(final Variables<E> variables, final int count) {
        return generator.variables(variables, count);
    }

    public static class Test<C, E> {
        public final Expr<C, E> expr;
        public final String full;
        public final String fullExtra;
        public final String mini;
        public final String safe;

        public Test(final Expr<C, E> expr, final String full, final String fullExtra, final String mini, final String safe) {
            this.expr = expr;
            this.full = full;
            this.fullExtra = fullExtra;
            this.mini = mini;
            this.safe = safe;
        }
    }
}
