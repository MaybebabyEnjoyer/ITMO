"use strict";

section("Methods");

point = {
    x: 10,
    y: 20,
    getX: function() { return point.x; },
    getY: function() { return point.y; }
};
dumpObject("Functions in properties: point", point);
println("Result of call to getX: " + point.getX());
println("Actual value of getX: " + point.getX);
println();

shiftedPoint = Object.create(point);
shiftedPoint.dx = 1;
shiftedPoint.dy = 2;
shiftedPoint.getX = function() { return shiftedPoint.x + shiftedPoint.dx; };
shiftedPoint.getY = function() { return shiftedPoint.y + shiftedPoint.dy; };
dumpObject("Functions in properties: shiftedPoint", shiftedPoint);
println();

println("Aliasing problem");
let point2 = point;
dumpObject("point2 references to the same object as point", point2);

point = {x: -1, y: -2};
dumpObject("point references new object", point);
dumpObject("point2 has correct x and y, but strange getX() and getY()", point2);
println("point2.getX takes value from point, not from point2: " + point2.getX);
println();

point = {
    x: 10,
    y: 20,
    getX: function() { return this.x; },
    getY: function() { return this.y; }
};
point2 = point;
point = {x: -1, y: -2};
println("'this' -- the object, method in called on");
dumpObject("point", point);
dumpObject("methods of point2 references correct object", point2);
println("dot-notations is shorthand for array-notation: " + point2.getX() + " === " + point2["getX"]());
println();

println("Specifying context object in apply: " +
    point2.getX.apply(point, ["other arguments"]) + ", " +
    point2.getX.apply(point2, ["other arguments"])
);
println("Specifying context object in call: " +
    point2.getX.call(point, "other arguments") + ", " +
    point2.getX.call(point2, "other arguments")
);

section("Constructors");

function pointFactory(x, y) {
    const point = {};
    point.x = x;
    point.y = y;
    point.getX = function() { return this.x; };
    point.getY = function() { return this.y; };
    return point;
}
dumpObject("point produced by factory", pointFactory(10, 20));
println();

function Point(x, y) {
//    const point = {};
    this.x = x;
    this.y = y;
    this.getX = function() { return this.x; };
    this.getY = function() { return this.y; };
//    return point;
}
dumpObject("point created by constructor", new Point(10, 20));
println("Constructor is ordinary function: " + (typeof Point) + "\n" + Point);
println();

function PointWithPrototype(x, y) {
    this.x = x;
    this.y = y;
}
PointWithPrototype.prototype.getX = function() { return this.x; };
PointWithPrototype.prototype.getY = function() { return this.y; };
dumpObject("PointWithPrototype.prototype", PointWithPrototype.prototype);
dumpObject("point created by constructor with prototype", new PointWithPrototype(10, 20));
println();

point = Object.create(PointWithPrototype.prototype);
PointWithPrototype.call(point, 1, 2);
dumpObject("PointWithPrototype created without new", point);

example("point.constructor", "Object's constructor");
