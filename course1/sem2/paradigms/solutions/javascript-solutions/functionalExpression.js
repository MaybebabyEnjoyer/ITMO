"use strict"

const cnst = value => () => value

const one = cnst(1)
const two = cnst(2)

const constants = {
    "one": one,
    "two": two
}

const getVariable = ind => (...args) => args[ind]

const varInd = {
    "x": getVariable(0),
    "y": getVariable(1),
    "z": getVariable(2)
}

const variable = name => varInd[name]

const operate = operation => (...args) => (...vars) => operation(...args.map(arg => arg(...vars)))

const add = operate((a, b) => a + b)
const subtract = operate((a, b) => a - b)
const multiply = operate((a, b) => a * b)
const divide = operate((a, b) => a / b)
const negate = operate(a => -a)
const floor = operate(Math.floor)
const ceil = operate(Math.ceil)
const madd = operate((a, b, c) => a * b + c)
const sin = operate(Math.sin)
const cos = operate(Math.cos)

const argCompareN = (comparator) => operate((...args) => {
    return args.reduce((extremumIndex, value, index, array) => comparator(value, array[extremumIndex]) ? index : extremumIndex, 0);
})

const argMin3 = argCompareN((a, b) => a < b)
const argMax3 = argCompareN((a, b) => a > b)
const argMin5 = argCompareN((a, b) => a < b)
const argMax5 = argCompareN((a, b) => a > b)

const parseOperation = {
    "+": { operation: add, arguments: 2 },
    "-": { operation: subtract, arguments: 2 },
    "*": { operation: multiply, arguments: 2 },
    "/": { operation: divide, arguments: 2 },
    "negate": { operation: negate, arguments: 1 },
    "_": { operation: floor, arguments: 1 },
    "^": { operation: ceil, arguments: 1 },
    "*+": { operation: madd, arguments: 3 },
    "argMin3": { operation: argMin3, arguments: 3 },
    "argMax3": { operation: argMax3, arguments: 3 },
    "argMin5": { operation: argMin5, arguments: 5 },
    "argMax5": { operation: argMax5, arguments: 5 },
    "cos": { operation: cos, arguments: 1},
    "sin": { operation: sin, arguments: 1}
}

const applyOperation = (op, stack) => [...stack.slice(0, -parseOperation[op].arguments),
    parseOperation[op].operation(...stack.slice(-parseOperation[op].arguments))]

const parse = expr => expr.split(" ").filter(t => t.length > 0).reduce((stack, t) =>
    (t in parseOperation ? applyOperation(t, stack) : t in varInd ? [...stack, variable(t)] : t in constants ?
        [...stack, constants[t]] : [...stack, cnst(+t)]), []).pop()
