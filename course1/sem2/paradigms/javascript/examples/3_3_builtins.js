"use strict";

chapter("Built-ins");

section("Primitive types");

example("null", "Null type");
example("undefined", "Undefined type");
example("[true,false]", "Boolean type");
example("[1,3.14,Infinity,NaN]", "Number type");
example("['','hello']", "String type");
//example("[Symbol(),Symbol('hello'),Symbol.for('hello')]", "Symbol type");

section("Object types");

example("new Object()", "Object");
let func = function(a, b) { return a + b; };
example("func", "Function");
example("/ +/", "Regular expression");

println("Functions");
    example("eval('2+3')", "    eval(code)");
    example("parseInt('20', 16)", "    parseInt(string, radix)");
    example("parseFloat('1.1e1')", "    parseFloat(string)");
    example("[isNaN(10),isNaN(Infinity),isNaN(NaN)]", "    isNaN(number)");
    example("[isFinite(10),isFinite(Infinity),isFinite(NaN)]", "    isFinite(number)");
println("URI functions");
    example("encodeURI('https://ru.wikipedia.org/wiki/Язык#Ссылки')", "    encodeURI(uri)");
    example("decodeURI('https://ru.wikipedia.org/wiki/%D0%AF%D0%B7%D1%8B%D0%BA')", "    decodeURI(uri)");
    example("encodeURIComponent('Язык/Ссылки')", "    encodeURIComponent(uri)");
    example("decodeURIComponent('%D0%AF%D0%B7%D1%8B%D0%BA%2F%D0%A1%D1%81%D1%8B%D0%BB%D0%BA%D0%B8')", "    decodeURIComponent(uri)");

section("Globals");

println("Constructors");
    example("new Object('Hello')", "    Object");
    example("new Function('a', 'b', 'return a + b;')", "    Function");
    example("new Array(1,2,3)", "    Array");
    example("new String('Hello')", "    String");
    example("new Boolean(true)", "    Boolean");
    example("new Number(10)", "    Number");
    example("new Date('01.01.2015')", "    Date");
    example("new RegExp('a*')", "    RegExp");
println("Errors");
    example("new Error('Message')", "    Error");
    example("new RangeError('Message')", "    RangeError");
    example("new ReferenceError('Message')", "    ReferenceError");
    example("new SyntaxError('Message')", "    SyntaxError");
    example("new TypeError('Message')", "    TypeError");
    example("new URIError('Message')", "    URIError");
println("Objects");
    example("Math", "    Math");
    example("JSON", "    JSON");

section("Object");
println("Properties");
    example("Object.prototype", "    prototype");
println("Functions");
    example("Object.getPrototypeOf(new Error()) == Error.prototype", "    getPrototypeOf(object)");
    example("Object.keys(new Point(10, 20))", "    keys");

section("Function properties and methods");
function add(a, b) { return "this=" + this + ", arguments=[" + Array.prototype.join.call(arguments, ",") + "]"; }
example("add.length", "    length");
example("add.prototype", "    prototype");
example("add.prototype.constructor", "    prototype.constructor");
example("add.call('aaa', 'bbb', 'ccc')", '    call(this, ...arguments)');
example("add.apply('aaa', ['bbb', 'ccc'])", '    call(this, arguments)');
example("add.bind('aaa', 'bbb')('ccc')", '    bind(this, ...arguments)');

section("Arrays properties and methods");
let arr = ['a', 'b', 'c'];
arr.toString = function() { return "[" + this.join(",") + "]" };
example("Array.isArray(arr)", "isArray(object)");
example("Array.isArray('arr')", "isArray(object)");
println("Reads");
    example("arr.length", "    length");
    example("arr.toString()", "    toString");
    example("arr.join(' - ')", "    join(separator)");
    example("arr.concat('d', 'e')", "    concat(...items)");
    example("arr.slice(1, 3)", "    slice(start, end)");
    example("[arr.indexOf('b'), arr.indexOf('b', 2)]", "    indexOf(element, index?)");
    example("[arr.lastIndexOf('b'), arr.lastIndexOf('b', 2)]", "    lastIndexOf(element, index?)");
println("Modifications");
    example("[arr.pop(), arr]", "    pop()");
    example("[arr.push('c', 'd'), arr]", "    push(...items)");
    example("[arr.shift(), arr]", "    shift()");
    example("[arr.unshift('e', 'f'), arr]", "    unshift()");
    example("[arr.splice(1, 2, '1', '2', '3'), arr]", "    splice(start, count, ...items)");
    example("[arr.reverse(), arr]", "    reverse()");
    example("[arr.sort(), arr]", "    sort(comp?)");
println("Functional");
    example("    arr");
    example("arr.map(isFinite)", "    map(f)");
    example("arr.forEach(println)", "    forEach(callback)");
    example("arr.every(isFinite)", "    every(predicate, this?)");
    example("arr.some(isFinite)", "    some(predicate, this?)");
    example("arr.filter(isFinite)", "    filter(predicate, this?)");
function concat(prev, current, index, arr) { return prev + " " + index + "=" + current; }
    example("arr.reduce(concat, 'zero')", "    reduce(f2, zero)");
    example("arr.reduceRight(concat, 'zero')", "    reduceRight(f2, zero)");
