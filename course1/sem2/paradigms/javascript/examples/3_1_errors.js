"use strict";

lecture("3. More Features");
chapter("Error handling");
section("Standard errors");

try {
    1();
} catch (e) {
    println("Got exception");
    println("    toString():   " + e.toString());
    println("    name:         " + e.name);
    println("    message:      " + e.message);
    println("    constructor:  " + e.constructor.name);
    println("    io TypeError: " + (e instanceof TypeError));
    println("    io Error:     " + (e instanceof Error));
} finally {
    println("Finally!");
}
println();

try {
    throw new Error("Custom error message");
} catch (e) {
    println("Got exception: " + e + " (" + typeof(e) + ")");
}
println();


section("Everything is throwable");

try {
    throw 1;
} catch (e) {
    println("Got exception: " + e + " (" + typeof(e) + ")");
}
println();

try {
    throw {name: "my error"};
} catch (e) {
    println("Got exception: " + e.name + " (" + typeof(e) + ")");
}
println();

try {
    throw undefined;
} catch (e) {
    println("Got exception: " + e);
}
println();


section("Custom errors");

function CustomError(message) {
    // Error is exotic object
    //Error.call(this, message);
    this.message = message;
}
CustomError.prototype = Object.create(Error.prototype);
CustomError.prototype.name = "CustomError";
CustomError.prototype.constructor = CustomError;

try {
    throw new CustomError("Custom error message");
} catch (e) {
    println("Got exception: " + e);
    println("io CustomError: " + (e instanceof CustomError));
    println("io Error:     " + (e instanceof Error));
}
println();
