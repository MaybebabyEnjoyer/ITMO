package expression.common;

import base.ExtendedRandom;
import base.Functional;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
public class NodeRenderer<C> {
    private static final Mode MINI_MODE = Mode.SIMPLE_MINI; // Replace by TRUE_MINI for some challenge
    private static final String PAREN = "[";
    private static final String SPACE = " ";

    public static final Settings FULL = Mode.FULL.settings(0);
    public static final Settings FULL_EXTRA = Mode.FULL.settings(Integer.MAX_VALUE / 4);
    public static final Settings SAME = Mode.SAME.settings(0);
    public static final Settings MINI = MINI_MODE.settings(0);
    public static final Settings TRUE_MINI = Mode.TRUE_MINI.settings(0);

    private final Renderer<C, Settings, Node<C>> nodeRenderer = new Renderer<>(Node::constant);
    private final Map<String, Priority> priorities = new HashMap<>();
    private final ExtendedRandom random;

    public NodeRenderer(final ExtendedRandom random) {
        this.random = random;
        nodeRenderer.unary(PAREN, (mode, arg) -> paren(true, arg));
    }

    public void unary(final String name) {
        nodeRenderer.unary(name, (settings, arg) -> {
            final Node<C> inner;
            if (settings.mode == Mode.FULL) {
                inner = paren(true, arg);
            } else {
                final String op = arg.get(c -> SPACE, n -> SPACE, (n, a) -> PAREN.equals(n) ? "" : SPACE, (n, a, b) -> PAREN);
                inner = op.isEmpty() ? arg : Node.op(op, arg);
            }
            return settings.extra(Node.op(name, inner), random);
        });
    }

    public void binary(final String name, final int priority) {
        final Priority mp = new Priority(name, priority);
        priorities.put(name, mp);

        nodeRenderer.binary(name, (settings, l, r) -> settings.extra(process(settings, mp, l, r), random));
    }

    private Node<C> process(final Settings settings, final Priority mp, final Node<C> l, final Node<C> r) {
        if (settings.mode == Mode.FULL) {
            return paren(true, op(mp, l, r));
        }

        final Priority lp = priority(l);
        final Priority rp = priority(r);

        final int rc = rp.compareLevels(mp);

        // :NOTE: Especially ugly code, do not replicate
        final boolean advanced = settings.mode == Mode.SAME
                || mp.has(2)
                || mp.has(1) && (mp != rp || (settings.mode == Mode.TRUE_MINI && hasOther(r, rp)));

        final Node<C> al = paren(lp.compareLevels(mp) < 0, l);
        if (rc == 0 && !advanced) {
            return get(r, null, (n, a, b) -> rp.op(mp.op(al, a), b));
        } else {
            return mp.op(al, paren(rc == 0 && advanced || rc < 0, r));
        }
    }

    private boolean hasOther(final Node<C> node, final Priority priority) {
        return get(node, () -> false, (name, l, r) -> {
            final Priority p = Functional.get(priorities, name);
            if (p.compareLevels(priority) != 0) {
                return false;
            }
            return p != priority || hasOther(l, priority);
        });
    }

    private Node<C> op(final Priority mp, final Node<C> l, final Node<C> r) {
        return mp.op(l, r);
    }

    private Priority priority(final Node<C> node) {
        return get(node, () -> Priority.MAX, (n, a, b) -> Functional.get(priorities, n));
    }

    private <R> R get(final Node<C> node, final Supplier<R> common, final Node.Binary<Node<C>, R> binary) {
        return node.get(c -> common.get(), n -> common.get(), (n, a) -> common.get(), binary);
    }

    public String render(final Expr<C, ?> expr, final Settings settings) {
        return renderNode(renderToNode(settings, expr));
    }

    public String renderNode(final Node<C> node) {
        return node.cata(
                String::valueOf,
                name -> name,
                (name, arg) -> name == PAREN ? "(" + arg + ")" : name + arg,
                (name, a, b) -> a + " " + name + " " + b
        );
    }

    public Node<C> renderToNode(final Settings settings, final Expr<C, ?> expr) {
        return nodeRenderer.render(settings, expr.convert((name, variable) -> Node.op(name)));
    }

    private static <C> Node<C> paren(final boolean condition, final Node<C> node) {
        return condition ? Node.op(PAREN, node) : node;
    }

    // :NOTE: Especially ugly bit-fiddling, do not replicate
    private static final class Priority {
        private static final int Q = 3;
        private static final Priority MAX = new Priority("MAX", Integer.MAX_VALUE - Q);
        private final String op;

        private final int priority;

        public Priority(final String op, final int priority) {
            this.op = op;
            this.priority = priority;
        }

        private int compareLevels(final Priority that) {
            return (priority | Q) - (that.priority | Q);
        }

        @Override
        public String toString() {
            return String.format("Priority(%s, %d, %d)", op, priority | Q, priority & Q);
        }

        public <C> Node<C> op(final Node<C> l, final Node<C> r) {
            return Node.op(op, l, r);
        }

        private boolean has(final int value) {
            return (priority & Q) == value;
        }
    }

    public enum Mode {
        FULL, SAME, TRUE_MINI, SIMPLE_MINI;

        public Settings settings(final int limit) {
            return new Settings(this, limit);
        }
    }

    public static class Settings {
        private final Mode mode;
        private final int limit;

        public Settings(final Mode mode, final int limit) {
            this.mode = mode;
            this.limit = limit;
        }

        public <C> Node<C> extra(Node<C> node, final ExtendedRandom random) {
            while (random.nextInt(Integer.MAX_VALUE) < limit) {
                node = paren(true, node);
            }
            return node;
        }
    }
}
