"use strict";

// Magic helper functions
function example(s, description) {
    const result = (() => {
        try {
            return eval(s);
        } catch (e) {
            return e;
        }
    })();

    if (description) {
        println(description + ":", s, "->", result);
    } else {
        println(s, "->", result);
    }
}

function examples(collection, template) {
    collection.forEach(function(name) {
        return example(template.replace('#', name).replace('#', name));
    });
}

function section(name) {
    println();
    println("---", name, "---");
}

function chapter(name) {
    println();
    println("==========", name, "==========");
}

function lecture(name) {
    println();
    println("=".repeat(name.length + 16));
    println("=== Lecture " + name + " ===");
    println("=".repeat(name.length + 16));
}

// Helper function
function dumpObject(name, o) {
    println(name);
    for (const attribute in o) {
        if (typeof(o[attribute]) === "function") {
            if (o[attribute].length === 0) {
                println("    " + attribute + "() -> " + o[attribute]());
            } else {
                println("    " + attribute + "(...)");
            }
        } else {
            println("    " + attribute + " = " + o[attribute]);
        }
    }
}

if (!String.prototype.repeat) {
    String.prototype.repeat = function(count) {
        let result = "";
        for (let i = 0; i < count; i++) {
            result += this;
        }
        return result;
    }
}
