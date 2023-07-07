"use strict";

chapter("Modules");
section("Module declaration");

println("Points is module reference");
let points = (function() {
    let name = "hello";
    function Point(x, y) {
        println("Constructor called");
        let z = 0;
        this.getX = function() { return x; };
        this.setX = function(value) { x = value; };
        this.getY = function() { return y; };
        this.setY = function(value) { y = value; };
        this.getZ = function() { return z; };
        this.setZ = function(value) { z = value; };
        this.getName = function() { return name; };
        this.setName = function(value) { name = value; };
    }
    function abs(point) {
        return Math.sqrt(point.getX() * point.getX() + point.getY() * point.getY() + point.getZ() * point.getZ());
    }
    // Exported declarations
    return {Point: Point, abs: abs};
})();

section("Module usage");

let point1 = new points.Point(30, 40);
example("points.abs(point1)", "Module function");

// "import"
let IPoint = points.Point;

point2 = new IPoint(10, 20);
dumpObject("point1", point1);
dumpObject("point2", point2);

println("name is shared");
point1.setName("Changed name");
println("    point1.getName() = " + point1.getName());
println("    point2.getName() = " + point2.getName());

println("z is per-object");
point1.setZ(100);
point2.setZ(200);
println("    point1.getZ() = " + point1.getZ());
println("    point2.getZ() = " + point2.getZ());
