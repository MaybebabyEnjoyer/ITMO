package prtest;

import alice.tuprolog.*;
import alice.tuprolog.exceptions.NoMoreSolutionException;
import alice.tuprolog.exceptions.NoSolutionException;
import base.Asserts;
import jstest.Engine;
import jstest.EngineException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tested prolog script.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class PrologScript {
    public static final Var V = Var.of("V");
    public static Path PROLOG_ROOT = Path.of(".");
    private static final Rule CONSULT = new Rule("consult", 1);

    private final Prolog prolog = new Prolog();

    public PrologScript() {
        prolog.addExceptionListener(exceptionEvent -> {
            throw new AssertionError("Prolog error: " + exceptionEvent.getMsg());
        });
        prolog.addOutputListener(event -> {
            // Strip text_term/2 output
            if (!event.getMsg().endsWith("'\nV_e0")) {
                System.out.print(event.getMsg());
            }
        });
    }

    public PrologScript(final Path file) {
        this();
        consult(file);
    }

    private static EngineException error(final Throwable cause, final String format, final Object... arguments) {
        return new EngineException(String.format(format, arguments), cause);
    }

    public void consult(final Path file) {
        final Path path = PROLOG_ROOT.resolve(file);
        System.out.println("Loading " + path);
        try {
            if (!test(CONSULT, Value.string(path.toString()))) {
                throw error(null, "Error opening '%s'", path);
            }
        } catch (final EngineException e) {
            throw error(e, "Error opening '%s': %s", path, e.getMessage());
        }
    }

    public void addTheory(final Theory theory) {
        prolog.addTheory(theory);
    }

    public boolean test(final Rule rule, final Object... args) {
        return prolog.solve(rule.apply(args)).isSuccess();
    }

    private List<Term> solve(final Term term) {
        SolveInfo info = prolog.solve(term);
        final List<Term> values = new ArrayList<>();
        try {
            while (info.isSuccess()) {
                values.add(info.getVarValue(V.getName()));
                if (!info.hasOpenAlternatives()) {
                    return values;
                }
                info = prolog.solveNext();
            }
            return values;
        } catch (final NoSolutionException | NoMoreSolutionException e) {
            throw new AssertionError(e);
        }
    }

    public Engine.Result<Value> solveOne(final Rule rule, final Object... args) {
        final Term term = rule.apply(args);
        final List<Term> values = solve(term);
        if (values.size() != 1) {
            throw Asserts.error("Exactly one solution expected for %s in %s%n  found: %d %s", V, term, values.size(), values);
        }
        return new Engine.Result<>(term.toString(), Value.convert(values.get(0)));
    }

    public void assertSuccess(final boolean expected, final Rule rule, final Object... args) {
        Asserts.assertEquals(rule.apply(args).toString(), expected, test(rule, args));
    }

    public void assertResult(final Object expected, final Rule f, final Object... args) {
        if (expected != null) {
            final Engine.Result<Value> actual = solveOne(f, args);
            if (expected instanceof Set<?>) {
                final Set<Value> expectedSet = ((Set<?>) expected).stream().map(Value::convert).collect(Collectors.toUnmodifiableSet());
                Asserts.assertEquals(actual.context, expectedSet, Set.copyOf(actual.value.toList()));
            } else {
                actual.assertEquals(Value.convert(expected));
            }
        } else {
            solveNone(f, args);
        }
    }

    private void solveNone(final Rule rule, final Object... args) {
        final Term term = rule.apply(args);
        final List<Term> values = solve(term);
        if (!values.isEmpty()) {
            throw Asserts.error("No solutions expected for %s in %s%n  found: %d %s", V, term, values.size(), values);
        }
    }
}
