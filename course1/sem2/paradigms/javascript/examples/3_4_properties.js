"use strict";

chapter("Properties");


section("Property attributes");

function TestObject(name) {
    this.name = name;
    this.existingProperty = "";
}
TestObject.prototype.toString = function() { return "TestObject('" + this.name + "')"; };
TestObject.prototype.inherited = "value";

let obj = new TestObject("test");
Object.defineProperties(obj, {
    ordinary_: {value: "value", enumerable: true , writable: true , configurable: true },
    hidden___: {value: "value", enumerable: false, writable: true , configurable: true },
    readOnly_: {value: "value", enumerable: true , writable: false, configurable: true },
    sealed___: {value: "value", enumerable: true , writable: true , configurable: false},
    default__: {value: "value"}
});

let properties = ["ordinary_", "hidden___", "readOnly_", "sealed___", "inherited", "default__"];
println("Values");
    examples(properties, "    obj.#");
println("Enumerate");
    example("    Object.keys(obj)");
    example("    Object.getOwnPropertyNames(obj)");
    examples(properties, "    obj.hasOwnProperty('#')");
    examples(properties, "    obj.propertyIsEnumerable('#')");
println("Write");
    examples(properties, "    obj.# = 'hello'; obj.#");
    examples(properties, "    Object.defineProperty(obj, '#', {value: 'zzz'}); obj.#");
println("Modify attributes");
    examples(properties, "    Object.defineProperty(obj, '#', {configurable: true})");
println("Attributes");
    function attributes(o) {
        function attr(name) { return name + "=" + o[name]; }
        return "{" + Object.getOwnPropertyNames(o).map(attr).join(", ") + "}";
    }
    examples(properties, "    attributes(Object.getOwnPropertyDescriptor(obj, '#'))");

section("Getters and setters");

obj = new TestObject("test");
Object.defineProperties(obj, {
    getter: {get: function() { return "getter" } },
    setter: {set: function(value) { println("set: " + value); }},
    both__: {
        get: function() { return "getter: " + this.value; },
        set: function(value) { this.value = value; }
    }
});

properties = ["getter", "setter", "both__"];
println("Read");
    examples(properties, "    obj.#");
println("Writer");
    examples(properties, "    obj.# = 'value'; obj.#");
println("Properties");
    examples(properties, "    attributes(Object.getOwnPropertyDescriptor(obj, '#'))");


section("Object attributes");

let common = new TestObject("common");
let notExtensible = Object.preventExtensions(new TestObject("notExtensible"));
let sealed = Object.seal(new TestObject("sealed"));
let frozen = Object.freeze(new TestObject("frozen"));
let objects = [common, notExtensible, sealed, frozen];
let names = ["common", "notExtensible", "sealed", "frozen"];
    example("objects");
    example("objects.map(Object.isExtensible)", "    isExtensible");
    example("objects.map(Object.isSealed)", "    isSealed");
    example("objects.map(Object.isFrozen)", "    isFrozen");
println("Adding new properties");
    examples(names, "    #.newProperty = 'a'");
println("Modifying property attributes");
    examples(names, "    Object.defineProperty(#, 'existingProperty', {configurable: true})");
println("Modifying properties");
    examples(names, "    #.existingProperty = 'a'");

section("delete");
obj = {a: 1, b: 2};
dumpObject("obj", obj);
delete obj.a;
dumpObject("obj", obj);
