"use strict";

chapter("There is not such thing as...");

section("Block-level variable");

(function() {
    a = 10;
    println("Ussignment before declaration: a ->", a);
    var a;
    if (true) {
        var a;
        println("Same variable here: a ->", a);
        a = 20;
        println("New value in inner scope: a ->", a);
    }
    println("And outer scope: a ->", a);
})();


section("Global variables");

var globalVariable = "Hello";
example("globalVariable");
example("global.globalVariable");
let this_globalVariable = this.globalVariable;
example("this_globalVariable");


section("Global built-ins");

example("global.Object");
example("global.Infinity");
example("global.isNaN");
