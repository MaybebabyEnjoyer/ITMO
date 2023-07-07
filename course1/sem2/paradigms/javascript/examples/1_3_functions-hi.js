"use strict";

chapter("Hi-order functions");
section("Minimum by absolute value");

let minimumByAbs = function(...args) {
    let result = Infinity;
    for (const arg of args) {
        if (Math.abs(result) > Math.abs(arg)) {
            result = arg;
        }
    }
    return result;
};
println("minimumByAbs =", minimumByAbs);
example("minimumByAbs(1, -2, 3)");


section("Unify minimum and minimumByAbs");

function minimumBy(comparator, init = Infinity) {
    return (...args) => {
        let result = init;
        for (const arg of args) {
            if (comparator(result, arg) > 0) {
                result = arg;
            }
        }
        return result;
    }
}
function comparing(f) {
    return (a, b) => f(a) - f(b);
}
function identity(a) { return a; }
function maximumBy(comparator, init = -Infinity) {
    return minimumBy((a, b) => -comparator(a, b), init);
}

minimum = minimumBy(comparing(identity));
minimumByAbs = minimumBy(comparing(Math.abs));
let maximumByLength = maximumBy(comparing(s => s.length), "");

example("minimumBy");
example("comparing");
example("identity");
example("minimum");
example("minimumByAbs");
example("maximumByLength");
example("minimum(1, -2, 3)");
example("minimumByAbs(1, -2, 3)");
example("minimumNegative(1, -2, 3)");
example("maximumByLength('aa', 'bbb', 'c')");


section("Unify minimumBy and sum");

function foldLeft(f, zero) {
    return (...args) => {
        let result = zero;
        for (const arg of args) {
            result = f(result, arg);
        }
        return result;
    }
}
function minBy(f) {
    return (a, b) => f(a) < f(b) ? a : b;
}

sum = foldLeft((a, b) => a + b, 0);
const multiply = foldLeft((a, b) => a * b, 1);
minimumByAbs = foldLeft(minBy(comparing(Math.abs)), Infinity);
example("sum(1, -2, 3)");
example("multiply(1, -2, 3)");
example("minimumByAbs(1, -2, 3)");


section("sumSquares and sumAbs");

function square(x) { return x * x; }
let sumSquares = foldLeft((a, b) => a + square(b), 0);
let sumAbs = foldLeft((a, b) => a + Math.abs(b), 0);
example("sumSquares(1, -2, 3)");
example("sumAbs(1, -2, 3)");


section("Unify sumSquares and sumAbs");

function map(f) {
    return (...args) => {
        const result = [];
        for (const arg of args) {
            result.push(f(arg));
        }
        return result;
    }
}
function compose(f, g) {
    return (...args) => f(g(...args));
}
sumSquares = compose(sum, map(square));
sumAbs = compose(sum, map(Math.abs));
example("sumSquares(1, -2, 3)");
example("sumAbs(1, -2, 3)");

section("diff");
let diff = dx => f => x => (f(x + dx) - f(x - dx)) / 2 / dx;

let dsin = diff(1e-7)(Math.sin);
for (let i = 0; i < 10; i++) {
    println(i + " " + Math.cos(i) + " " + dsin(i) + " " + Math.abs(Math.cos(i) - dsin(i)));
}


section("curry");

const curry = f => a => b => f(a, b);

const addC = curry((a, b) => a + b);
const add10 = addC(10);
example("addC(10)(20)");
example("add10(20)");


section("uncurry");

const uncurry = f => (a, b) => f(a)(b);
const addU = uncurry(a => b => a + b);
example("addU(10, 20)");


section("mCurry");

let mCurry = f => a => (...args) => f(...[a, ...args]);

let sub = mCurry(function(a, b, c) { return a - b - c; });
let sub10 = sub(10);
example("sub(10)(20, 30)");
example("sub10(20, 30)");
