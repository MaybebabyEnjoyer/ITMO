"use strict"

const expressionConstructor = (constructor, evaluate, toString, prefix = toString, postfix = toString) => {
    constructor.prototype.evaluate = evaluate
    constructor.prototype.toString = toString
    constructor.prototype.prefix = prefix
    constructor.prototype.postfix = postfix
    return constructor
}

const Const = expressionConstructor(
    function(value) { this.value = value },
    function() { return this.value },
    function() { return this.value.toString() }
)


const varInd = {"x": 0, "y": 1, "z": 2}

const Variable = expressionConstructor(
    function(name) {
        this.name = name
        this.ind = varInd[name]
    },
    function(...args) { return args[this.ind] },
    function() { return this.name }
)

const Operation = expressionConstructor(
    function(...args) { this.args = args },
    function(...vars) { return this.op(...this.args.map(val => val.evaluate(...vars))) },
    function() { return this.args.join(" ") + " " + this.opName },
    function() { return "(" + this.opName + " " + this.args.map(val => val.prefix()).join(" ") + ")" },
    function() { return "(" + this.args.map(val => val.postfix()).join(" ") + " " + this.opName + ")" }
)

const parseOperation = {}

function operationConstructor(op, opName) {
    const constructor = function(...args) {
        Operation.call(this, ...args)
    }
    constructor.prototype = Object.create(Operation.prototype)
    constructor.prototype.op = op
    constructor.prototype.opName = opName
    parseOperation[opName] = constructor
    return constructor
}

const Negate = operationConstructor(x => -x, "negate")
const Add = operationConstructor((x, y) => x + y, "+")
const Subtract = operationConstructor((x, y) => x - y, "-")
const Multiply = operationConstructor((x, y) => x * y, "*")
const Divide = operationConstructor((x, y) => x / y, "/")

const Exp = operationConstructor(Math.exp, "exp")
const Ln = operationConstructor(Math.log, "ln")

const Sumexp = operationConstructor((...args) => args.reduce((acc, x) => acc + Math.exp(x), 0), "sumexp")
const LSE = operationConstructor((...args) => Math.log(Sumexp.prototype.op(...args)), "lse")

const Sum = operationConstructor((...args) => args.reduce((acc, x) => acc + x, 0), "sum")
const Avg = operationConstructor((...args) => Sum.prototype.op(...args) / args.length, "avg")

const Meansq = operationConstructor((...args) => args.reduce((acc, x) => acc + x * x, 0) / args.length, "meansq")
const RMS = operationConstructor((...args) => Math.sqrt(Meansq.prototype.op(...args)), "rms")


const applyOperation = (op, stack) => [
    ...stack.slice(0, -parseOperation[op].prototype.op.length),
    new parseOperation[op](...stack.slice(-parseOperation[op].prototype.op.length)),]

const parse = expr => expr.split(" ").filter(t => t.length > 0).reduce((stack, t) =>
    (t in parseOperation ? applyOperation(t, stack) : t in varInd ? [...stack, new Variable(t)] :
        [...stack, new Const(+t)]), []).pop()

function CustomError(message, index) {
    this.message = message
    this.index = index
}
CustomError.prototype = Object.create(Error.prototype)

function BracketError(message, index) {
    CustomError.call(this, message, index)
}
BracketError.prototype = Object.create(CustomError.prototype)

function UnexpectedTokenError(message, index) {
    CustomError.call(this, message, index)
}
UnexpectedTokenError.prototype = Object.create(CustomError.prototype)

function OperationError(message, index) {
    CustomError.call(this, message, index)
}
OperationError.prototype = Object.create(CustomError.prototype)

function ArgError(message, index) {
    CustomError.call(this, message, index)
}
ArgError.prototype = Object.create(CustomError.prototype)

function InvalidTokenError(message, index) {
    CustomError.call(this, message, index)
}
InvalidTokenError.prototype = Object.create(CustomError.prototype)



const Parser = (expression, isSuffix = false) => {
    const source = new Source(expression.trim())
    const tokenMap = {
        "(": () => parseBrackets(source, getExpression, isSuffix),
        ...Object.fromEntries(Object.entries(varInd).map(([k]) => [k, () => new Variable(k)])),
        ...Object.fromEntries(Object.entries(parseOperation).map(([k]) => [k, () => {
            throw new OperationError(`Unexpected operation: ${source.next()}`, source.getCurrentPosition() - k.length)
        }])),
    }

    const getExpression = () => {
        const token = source.next()
        if (!isNaN(+token)) {
            return new Const(+token)
        } else if (tokenMap[token]) {
            return tokenMap[token]()
        }
        throw new InvalidTokenError(`Invalid token: ${token}`, source.getCurrentPosition() - token.length)
    }

    const result = getExpression()
    if (!source.isEoF()) {
        throw new UnexpectedTokenError(`Unexpected token: ${source.next()}`, source.getCurrentPosition())
    }
    return result
}


const parseBrackets = (source, getExpr, isSuffix = false) => {
    const getOp = () => {
        const token = source.checkNextToken()
        if (!(token in parseOperation)) {
            throw new OperationError(`Expected operation but found ${token}`, source.getCurrentPosition())
        }
        return parseOperation[source.next()]
    }

    const checkBrackets = (op, args) => {
        if (op.prototype.op.length !== 0 && args.length !== op.prototype.op.length) {
            throw new ArgError(`Expected ${op.prototype.op.length} arguments but found ${args.length}`, source.getCurrentPosition())
        }
        if (source.checkNextToken() !== ")") {
            throw new BracketError("Expected ')'", source.getCurrentPosition())
        }
        source.next()
    }

    const parseArgs = () => {
        const args = []
        while (!(source.checkNextToken() in parseOperation || source.checkNextToken() === ")" || source.isEoF())) {
            args.push(getExpr())
        }
        return args
    }

    const op = isSuffix ? null : getOp()
    const args = parseArgs()
    const finalOp = isSuffix ? getOp() : op
    checkBrackets(finalOp, args)
    return new finalOp(...args)
}

const parsePrefix = expression => Parser(expression)
const parsePostfix = expression => Parser(expression, true)

function Source(expression) {
    let pos = 0
    const skipSpaces = () => { while (expression[pos] === " ") pos++ }
    const isDigit = c => c >= '0' && c <= '9'

    const next = () => {
        skipSpaces()
        const startPos = pos
        if (pos === expression.length) return undefined
        if (["(", ")"].includes(expression[pos])) return expression[pos++]
        if (isDigit(expression[pos]) || (expression[pos] === "-")) {
            let start = pos
            if (expression[pos] === "-") pos++
            while (isDigit(expression[pos])) pos++
            return expression.slice(start, pos)
        } else {
            const token = Object.keys(varInd).concat(Object.keys(parseOperation)).find(t => expression.startsWith(t, pos))
            if (token) {
                pos += token.length
                return token
            }
            throw new InvalidTokenError(`Unexpected token ${expression[pos]}`, startPos)
        }
    }

    let currentToken = next()
    this.checkNextToken = () => currentToken
    this.isEoF = () => currentToken === undefined
    this.next = () => {
        const token = this.checkNextToken()
        if (this.isEoF()) {
            throw new UnexpectedTokenError(`Unexpected end of expression`, pos)
        }
        currentToken = next()
        return token
    }
    this.getCurrentPosition = () => pos
}
