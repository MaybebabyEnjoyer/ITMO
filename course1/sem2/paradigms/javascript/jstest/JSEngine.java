package jstest;

import org.graalvm.polyglot.HostAccess;

import javax.script.*;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * JavaScript engine.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class JSEngine {
    public static final String OPTIONS = "--module-path=<js>/graal";
    public static Path JS_ROOT = Path.of(".");

    private final ScriptEngine engine;

    public JSEngine(final Path script) {
        try {
            System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
            System.setProperty("polyglot.js.strict", "true");

            final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
//            engine = scriptEngineManager.getEngineFactories().stream()
//                    .filter(factory -> "Graal.js".equals(factory.getEngineName()))
//                    .map(ScriptEngineFactory::getScriptEngine)
//                    .findAny().orElse(null);
            engine = scriptEngineManager.getEngineByName("Graal.js");
            if (engine == null) {
                System.err.println("Graal.js not found");
                System.err.println("Use the following options to run tests:");
                System.err.println(OPTIONS);
                System.err.println("Where <js> - path to the javascript directory of this repository");
                System.err.println("Known engines:");
                for (final ScriptEngineFactory engineFactory : scriptEngineManager.getEngineFactories()) {
                    System.out.println("    " + engineFactory.getEngineName());
                }
                throw new AssertionError("Graal.js not found");
            }

            engine.put("io", new IO());
            engine.put("global", engine.getContext().getBindings(ScriptContext.ENGINE_SCOPE));

            engine.eval("var println = function() { io.println(Array.prototype.map.call(arguments, String).join(' ')); };");
            engine.eval("var print   = function() { io.print  (Array.prototype.map.call(arguments, String).join(' ')); };");
            engine.eval("var include = function(file) { io.include(file); }");
            engine.eval("var expr;");
        } catch (final ScriptException e) {
            throw new EngineException("Invalid initialization", e);
        }

        try {
            include(script.toString());
        } catch (final ScriptException e) {
            throw new EngineException("Script error", e);
        }
    }

    private void include(final String script) throws ScriptException {
        final Path scriptPath = JS_ROOT.resolve(script);
        try (final Reader reader = Files.newBufferedReader(scriptPath)) {
            engine.eval(reader);
        } catch (final IOException e) {
            throw new EngineException(String.format("Script '%s' not found", scriptPath), e);
        }
    }

    public  <T> Engine.Result<T> eval(final String context, final String code, final Class<T> token) {
        try {
            final Object result = engine.eval(code);
            if (result == null) {
                throw new EngineException("Result is null", null);
            }
            if (token.isAssignableFrom(result.getClass())) {
                return new Engine.Result<>(context, token.cast(result));
            }
            throw new EngineException(String.format(
                    "Expected %s, found \"%s\" (%s)%s",
                    token.getSimpleName(),
                    result,
                    result.getClass().getSimpleName(),
                    context
            ), null);
        } catch (final ScriptException e) {
            throw new EngineException("No error expected in " + context + ": " + e.getMessage(), e);
        }
    }

    public void set(final String variable, final Engine.Result<?> value) {
        engine.getBindings(ScriptContext.ENGINE_SCOPE).put(variable, value.value);
    }

    public class IO {
        @HostAccess.Export
        public void print(final String message) {
            System.out.print(message);
        }

        @HostAccess.Export
        public void println(final String message) {
            System.out.println(message);
        }

        @HostAccess.Export
        public void include(final String file) throws ScriptException {
            JSEngine.this.include(file);
        }
    }
}
