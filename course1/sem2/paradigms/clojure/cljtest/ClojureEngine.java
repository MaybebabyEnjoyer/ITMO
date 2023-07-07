package cljtest;

import clojure.lang.IFn;
import jstest.Engine;

import java.util.Optional;

/**
 * Clojure tests engine.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ClojureEngine implements Engine<Object> {
    private static final IFn HASH_MAP = ClojureScript.var("clojure.core/hash-map");

    private final Optional<IFn> evaluate;
    private final String evaluateString;
    private final ClojureScript.F<Object> parse;
    private final ClojureScript.F<String> toString;

    public ClojureEngine(final String script, final Optional<String> evaluate, final String parse, final String toString) {
        ClojureScript.loadScript(script);
        this.parse = ClojureScript.function(parse, Object.class);
        this.toString = ClojureScript.function(toString, String.class);

        this.evaluate = evaluate.map(ClojureScript::var);
        evaluateString = evaluate.map(s -> s + " ").orElse("");
    }

    @Override
    public Result<Object> prepare(final String expression) {
        return new Result<>(expression, ClojureScript.LOAD_STRING_IN.invoke(expression));
    }

    @Override
    public Result<Object> parse(final String expression) {
        return parse.call(new Result<>(expression, expression));
    }

    @Override
    public Result<Number> evaluate(final Result<Object> prepared, final double[] vars) {
        final Object map = HASH_MAP.invoke("x", vars[0], "y", vars[1], "z", vars[2]);
        final String context = java.lang.String.format("(%sexpr %s)\nwhere expr = %s", evaluateString, map, prepared.context);
        return evaluate
                .map(f -> ClojureScript.call(f, Number.class, context, new Object[]{prepared.value, map}))
                .orElseGet(() -> ClojureScript.call((IFn) prepared.value, Number.class, context, new Object[]{map}));
    }

    @Override
    public Result<String> toString(final Result<Object> prepared) {
        return toString.call(prepared);
    }
}
