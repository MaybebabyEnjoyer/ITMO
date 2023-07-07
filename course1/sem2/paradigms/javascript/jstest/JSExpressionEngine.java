package jstest;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Expression-aware JavaScript engine.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class JSExpressionEngine implements Engine<Object> {
    private final JSEngine engine;
    private final String evaluate;
    private final String parse;
    private final String toString;

    public JSExpressionEngine(final Path script, final String evaluate, final String parse, final String toString) {
        engine = new JSEngine(script);
        this.evaluate = evaluate;
        this.parse = parse;
        this.toString = toString;
    }

    @Override
    public Result<Object> prepare(final String expression) {
        return parse("eval", expression);
    }

    @Override
    public Result<Object> parse(final String expression) {
        return parse(parse, expression);
    }

    private Result<Object> parse(final String parse, final String expression) {
        return engine.eval(expression, String.format("%s(\"%s\")", parse, expression), Object.class);
    }

    @Override
    public Result<Number> evaluate(final Result<Object> prepared, final double[] vars) {
        final String code = String.format(
                "expr%s(%s);",
                evaluate,
                Arrays.stream(vars).mapToObj(v -> String.format("%.20f", v)).collect(Collectors.joining(","))
        );
        return evaluate(prepared, code, Number.class);
    }

    public Result<String> toString(final Result<Object> prepared) {
        return evaluate(prepared, "expr." + toString + "()", String.class);
    }

    protected <T> Engine.Result<T> evaluate(
            final Engine.Result<Object> prepared,
            final String code,
            final Class<T> result
    ) {
        engine.set("expr", prepared);
        return engine.eval(String.format("%n    in %s%n    where expr = %s%n", code, prepared.context), code, result);
    }
}
