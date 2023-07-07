"use strict";

lecture("1. Types and Functions");
chapter("Types");
section("Variables are typeless");

let a = 1;
println("a ->", a);

a = "Hello";
println("a ->", a);

section("Values are typed");
let as = ["'Hello'", 1, 1.1, true, false, [1, 2, 3], new Array(1, 2, 3), null, undefined];
for (let i = 0; i < as.length; i++) {
    println("a =", as[i]);
    println("    typeof(a) ->", typeof(as[i]));
}

section("Ordinary comparison");
println("'1' == '1' ->", '1' == '1');
println("'1' == 1 ->", '1' == 1);
println("'1.0' == 1 ->", '1.0' == 1);
println("undefined == undefined ->", undefined == undefined);
println("undefined == null ->", undefined == null);
println("null == null ->", null == null);
println("0 == [] ->", 0 == []);

section("Strict comparison");
println("'1' === '1' ->", '1' === '1');
println("'1' === 1 ->", '1' === 1);
println("undefined === undefined ->", undefined === undefined);
println("undefined === null ->", undefined === null);
println("null === null ->", null === null);
println("0 === [] ->", 0 === []);

section("Calculations");
println("2 + 3 ->", 2 + 3);
println("2.1 + 3.1 ->", 2.1 + 3.1);
println("'2.1' + '3.1' ->", '2.1' + '3.1');

section("Arrays");
as = [10, 20, 30];
println("as -> [" + as +"]");
println("as[2] -> ", as[2]);
println("as[3] -> ", as[3]);
println('as["2"] -> ', as["2"]);

section("Arrays are mutable");
example("as = new Array(10, 20, 30)");
example("as.push(40)");
example("as");
example("as.pop()");
example("as");
example("as.unshift(50)");
example("as");
example("as.shift()");
example("as");
