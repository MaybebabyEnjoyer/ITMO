import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class RunJS {
    private RunJS() {
    }

    @SuppressWarnings({"MethodMayBeStatic", "unused"})
    public static class IO {
        private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        private final ScriptEngine engine;
        public IO(final ScriptEngine engine) {
            this.engine = engine;
        }

        public void print(final String message) {
            System.out.print(message);
        }

        public void println(final String message) {
            System.out.println(message);
        }

        public void include(final String file) throws IOException, ScriptException {
            engine.getContext().setAttribute(ScriptEngine.FILENAME, file, ScriptContext.ENGINE_SCOPE);
            engine.eval(new FileReader(file, StandardCharsets.UTF_8));
        }
        
        public String readLine(final String prompt) throws IOException {
            if (prompt != null) {
                System.out.print(prompt);
            }
            return reader.readLine();
        }
    }

    public static void main(final String[] args) throws ScriptException {
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");

        final String script = args.length == 0 ? "examples.js" : args[0];

        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("Graal.js");
        if (engine == null) {
            System.err.println("Graal.js not found");
            System.err.println("Use the following command line to run RunJS:");
            System.err.println("java --module-path=graal -cp . RunJS");
            return;
        }

        engine.put("polyglot.js.allowAllAccess", true);
        engine.put("io", new IO(engine));

        engine.eval("var global = this;");
        engine.eval("var println = function() { io.println(Array.prototype.map.call(arguments, String).join(' ')); };");
        engine.eval("var print   = function() { io.print  (Array.prototype.map.call(arguments, String).join(' ')); };");
        engine.eval("var include = function(file) { io.include(file); }");
        engine.eval("var readLine = function(prompt) { return io.readLine(prompt); }");
        engine.eval("io.include('" + script + "')");
    }
}
