"use strict";

lecture("2. Objects and Closures");
chapter("Fields");
section("Objects as associative arrays");

let point = new Object();
point["x"] = 1;
point["y"] = 2;
dumpObject("initial point", point);
example('point["x"]');
example('point["y"]');
example('point["x"] === point.x && point["y"] === point.y', "shorthand syntax");
println();

point["x"] = 10;
point["y"] = 20;
dumpObject("modified point", point);
println();

point = {"x": 100, z: 200};
dumpObject("point with default values", point);
println();

println("Undefined property: " + point.qqq + " === " + point['qqq']);
println();

const strangeObject = {
    "hello world": "zzz",
    1: "qqq"
};
strangeObject["1 2"] = false;
dumpObject("strangeObject", strangeObject);
example('strangeObject[1] === strangeObject["1"]', "keys are strings");

section("Properties testing");
example("'x' in point", "    point has property 'x'");
example("'a' in point", "    point has property 'a'");

section("Properties dumping");
println("Properties:");
for (const property in point) {
    println("    property");
}
println("Property values:");
for (const property in point) {
    println("    point['" + property + "'] = " + point[property]);
}

section("Inheritance");
point = {x: 10, y: 20};
let shiftedPoint = Object.create(point);
shiftedPoint.dx = 1;
shiftedPoint.dy = 2;

dumpObject("point", point);
dumpObject("shiftedPoint", shiftedPoint);
println();

println("point is prototype of shiftedPoint: " + (Object.getPrototypeOf(shiftedPoint) === point));
println();

shiftedPoint.dx = -1;
dumpObject("shiftedPoint with modified dx", shiftedPoint);
println();

shiftedPoint.x = 1;
dumpObject("shiftedPoint with modified x", shiftedPoint);
dumpObject("point remains intact", point);
println();

point.y = 1000;
dumpObject("point with modified y", point);
dumpObject("shiftedPoint with propagated y", shiftedPoint);
println();

point.x = 1000;
dumpObject("point with modified x", point);
dumpObject("shiftedPoint without propagated x", shiftedPoint);
println();

delete shiftedPoint.x;
dumpObject("shiftedPoint with deleted local x", shiftedPoint);
println();
