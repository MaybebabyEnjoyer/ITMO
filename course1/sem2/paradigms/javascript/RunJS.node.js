// Node.js compatible runner
// Run: node RunJS.node.js [script.js]

"use strict";

var context = {
    println: function() {
        console.log(Array.prototype.map.call(arguments, String).join(' '));
    },
    print: function() {
        process.stdout.write(Array.prototype.map.call(arguments, String).join(' '));
    },
    eval: function(script, file) {
        return require("vm").runInNewContext(script, context, file || "eval");
    },
    fs: require("fs"),
    include: function(file) {
        if (file.endsWith(".mjs")) {
            context.println(`ES module loading not supported: ${file}`);
        } else {
            context.eval(context.fs.readFileSync(file), {encoding: "utf8"});
        }
    },
    readLine: function(prompt) {
        context.reader = context.reader || require("readline-sync"); //npm install readline-sync
        if (prompt !== undefined) {
            context.print(prompt);
        }
        return context.reader.question();
    },
    getScript() {
        const argv = process.argv.slice(2);
        return argv.length == 0 ? "examples.js" : argv[0];
    }
};
context.global = context;

context.include(context.getScript());
