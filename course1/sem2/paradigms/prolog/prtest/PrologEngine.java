package prtest;

import jstest.Engine;

import java.nio.file.Path;
import java.util.Map;

/**
 * TuProlog engine.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class PrologEngine implements Engine<Value> {
    private static final Rule TEXT_TERM = Rule.func("text_term", 1);

    private final PrologScript script;
    private final Rule evaluate;
    private final Rule parse;
    private final Rule toString;

    public PrologEngine(final Path file, final Rule evaluate, final Rule parse) {
        script = new PrologScript(file);
        this.evaluate = evaluate;
        this.parse = parse.func(0);
        toString = parse.func(1);
    }

    @Override
    public Result<Value> prepare(final String expression) {
        return script.solveOne(TEXT_TERM, expression);
    }

    @Override
    public Result<Value> parse(final String expression) {
        return script.solveOne(parse, expression);
    }

    @Override
    public Result<Number> evaluate(final Result<Value> prepared, final double[] vars) {
        final Value variables = Value.convert(Map.of("x", vars[0], "y", vars[1], "z", vars[2]));
        final Result<Value> result = script.solveOne(evaluate, prepared.value, variables);
        return result.cast(Value::toNumber);
    }

    @Override
    public Result<String> toString(final Result<Value> prepared) {
        return script.solveOne(toString, prepared.value).cast(Value::toString);
    }
}
