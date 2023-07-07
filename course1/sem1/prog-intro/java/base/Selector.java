package base;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Selector {
    private final Class<?> owner;
    private final List<String> modes;
    private final Set<String> variantNames = new LinkedHashSet<>();
    private final Map<String, Consumer<TestCounter>> variants = new HashMap<>();

    public Selector(final Class<?> owner, final String... modes) {
        this.owner = owner;
        this.modes = List.of(modes);
    }

    public Selector variant(final String name, final Consumer<TestCounter> operations) {
        Asserts.assertTrue("Duplicate variant " + name, variants.put(name.toLowerCase(), operations) == null);
        variantNames.add(name);
        return this;
    }

    private static void check(final boolean condition, final String format, final Object... args) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(format, args));
        }
    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public void main(final String... args) {
        try {
            final String mode;
            if (modes.isEmpty()) {
                check(args.length >= 1, "At least one argument expected, found %s", args.length);
                mode = "";
            } else {
                check(args.length >= 2, "At least two arguments expected, found %s", args.length);
                mode = args[0];
            }

            final List<String> vars = Arrays.stream(args).skip(modes.isEmpty() ? 0 : 1)
                    .flatMap(arg -> Arrays.stream(arg.split("[ +]+")))
                    .collect(Collectors.toUnmodifiableList());

            test(mode, vars);
        } catch (final IllegalArgumentException e) {
            System.err.println("ERROR: " + e.getMessage());
            if (modes.isEmpty()) {
                System.err.println("Usage: " + owner.getName() + " VARIANT...");
            } else {
                System.err.println("Usage: " + owner.getName() + " MODE VARIANT...");
                System.err.println("Modes: " + String.join(", ", modes));
            }
            System.err.println("Variants: " + String.join(", ", variantNames));
            System.exit(1);
        }
    }

    public void test(final String mode, List<String> vars) {
        final int modeNo = modes.isEmpty() ? -1 : modes.indexOf(mode) ;
        check(modes.isEmpty() || modeNo >= 0, "Unknown mode '%s'", mode);
        if (variantNames.contains("Base") && !vars.contains("Base")) {
            vars = new ArrayList<>(vars);
            vars.add(0, "Base");
        }

        vars.forEach(variant -> check(variants.containsKey(variant.toLowerCase()), "Unknown variant '%s'", variant));

        final Map<String, String> properties = modes.isEmpty()
                                               ? Map.of("variant", String.join("+", vars))
                                               : Map.of("variant", String.join("+", vars), "mode", mode);
        final TestCounter counter = new TestCounter(owner, modeNo, properties);
        vars.forEach(variant -> counter.scope("Testing " + variant, () -> variants.get(variant.toLowerCase()).accept(counter)));
        counter.printStatus();
    }

    public static <V extends Tester> Composite<V> composite(final Class<?> owner, final Function<TestCounter, V> factory, final String... modes) {
        return new Composite<>(owner, factory, (tester, counter) -> tester.test(), modes);
    }

    public static <V> Composite<V> composite(final Class<?> owner, final Function<TestCounter, V> factory, final BiConsumer<V, TestCounter> tester, final String... modes) {
        return new Composite<>(owner, factory, tester, modes);
    }

    public List<String> getModes() {
        return modes.isEmpty() ? List.of("~") : modes;
    }

    public List<String> getVariants() {
        return List.copyOf(variants.keySet());
    }

    /**
     * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
     */
    public static final class Composite<V> {
        private final Selector selector;
        private final Function<TestCounter, V> factory;
        private final BiConsumer<V, TestCounter> tester;
        private List<Consumer<? super V>> base;

        private Composite(final Class<?> owner, final Function<TestCounter, V> factory, final BiConsumer<V, TestCounter> tester, final String... modes) {
            selector = new Selector(owner, modes);
            this.factory = factory;
            this.tester = tester;
        }

        @SafeVarargs
        public final Composite<V> variant(final String name, final Consumer<? super V>... parts) {
            if ("Base".equalsIgnoreCase(name)) {
                base = List.of(parts);
                return v(name.toLowerCase());
            } else {
                return v(name, parts);
            }
        }

        @SafeVarargs
        private Composite<V> v(final String name, final Consumer<? super V>... parts) {
            selector.variant(name, counter -> {
                final V variant = factory.apply(counter);
                for (final Consumer<? super V> part : base) {
                    part.accept(variant);
                }
                for (final Consumer<? super V> part : parts) {
                    part.accept(variant);
                }
                tester.accept(variant, counter);
            });
            return this;
        }

        public Selector selector() {
            return selector;
        }
    }
}
