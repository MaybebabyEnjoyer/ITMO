"use strict";

chapter("Vector and matrix operations");

section("Scalar operations");
const addS = (a, b) => a + b;
const subtractS = (a, b) => a - b;
const multiplyS = (a, b) => a * b;
example("addS(2, 3)");
example("subtractS(2, 3)");
example("multiplyS(2, 3)");

section("Vector operations");
function transpose(matrix) {
    const result = [];
    for (let i = 0; i < matrix[0].length; i++) {
        const row = [];
        for (let j = 0; j < matrix.length; j++) {
            row.push(matrix[j][i]);
        }
        result.push(row);
    }
    return result;
}

const apply = f => args => f(...args);
const zipWith = f => (...args) => apply(map(apply(f)))(transpose(args));
const sumV = v => sum(...v);

const addV = zipWith(addS);
const subtractV = zipWith(subtractS);
const multiplyV = zipWith(multiplyS);
const scalar = compose(sumV, multiplyV);
example("addV([1, 2, 3], [4, 5, 6])");
example("subtractV([1, 2, 3], [4, 5, 6])");
example("multiplyV([1, 2, 3], [4, 5, 6])");
example("scalar([1, 2, 3], [4, 5, 6])");

section("Matrix operations");
function multiplyM(a, b) {
    return apply(map(ar => apply(map(curry(scalar)(ar)))(transpose(b))))(a);
}
const addM = zipWith(addV);
const subtractM = zipWith(subtractV);
example("addM([[1, 2], [3, 4]], [[5, 6], [7, 8]])[0]");
example("addM([[1, 2], [3, 4]], [[5, 6], [7, 8]])[1]");
example("subtractM([[1, 2], [3, 4]], [[5, 6], [7, 8]])[0]");
example("subtractM([[1, 2], [3, 4]], [[5, 6], [7, 8]])[1]");
example("transpose([[1, 2], [3, 4]])[0]");
example("transpose([[1, 2], [3, 4]])[1]");
example("multiplyM([[1, 2], [3, 4]], [[5, 6], [7, 8]])[0]");
example("multiplyM([[1, 2], [3, 4]], [[5, 6], [7, 8]])[1]");
