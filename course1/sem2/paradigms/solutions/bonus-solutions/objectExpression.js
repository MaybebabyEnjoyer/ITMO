"use strict"

const exprBuilder = (ctor, methods) => {
    Object.assign(ctor.prototype, methods)
    return ctor
}

const Const = exprBuilder(function (value) {
    this.value = value
}, {
    isEvalConst: function () { return true },
    evaluate:   function () { return this.value },
    diff:       function () { return new Const(0)},
    simplify:   function () { return this },
    toString:   function () { return String(this.value) }
})

Const.ZERO = new Const(0)
Const.ONE  = new Const(1)
Const.TWO  = new Const(2)

const variable = (function () {
    const names = {"x": 0, "y": 1, "z": 2}

    const ctor = exprBuilder(function (name) {
        this.name = name
    }, {
        isEvalConst: function () { return false },
        evaluate:   function (...vs) { return vs[names[this.name]] },
        diff:       function (v) { return this.name === v ? Const.ONE : Const.ZERO },
        simplify:   function () { return this },
        toString:   function () { return this.name }
    })

    return {ctor, names}
})()

const Variable = variable.ctor

const operation = (function () {
    const signs = new Map()

    const Operation = exprBuilder(function (...args) {
        this.args = args
    }, {
        isEvalConst: function () { return this.args.every(arg => arg.isEvalConst()) }
    })

    const builder = (sign, fun, diff, simplify) => {
        function Atom(...args) {
            Operation.call(this, ...args)
        }
        Atom.prototype = Object.create(Operation.prototype)

        Atom.prototype.evaluate = function (...vs) {
            return fun(...this.args.map(arg => arg.evaluate(...vs)))
        }
        Atom.prototype.diff = function (v) {
            return diff(...this.args.map(arg => [arg, arg.diff(v)]).flat())
        }
        Atom.prototype.simplify = function () {
            const simplified = this.args.map(arg => arg.simplify())
            return simplified.every(arg => arg.isEvalConst())
                ? new Const(fun(...simplified.map(arg => arg.evaluate()))) : simplify(...simplified)
        }
        Atom.prototype.toString = function () {
            return this.args.join(" ") + " " + sign
        }

        signs.set(sign, {atom: Atom, argc: fun.length})
        return Atom
    }

    return {builder, signs}
})()

const opBuilder = operation.builder

const simplifying = (function () {
    const isMapN = f => n => expr => expr.isEvalConst() && f(expr.evaluate()) === n

    const isN       = isMapN(v => v)
    const isAbsN    = isMapN(v => Math.abs(v))

    const isZero    = isN(0)
    const isAbsOne  = isAbsN(1)

    const diffHypotTerm = (x, dx) => new Multiply(x, new Multiply(Const.TWO, dx))
    const diffHMeanTerm = (dx, x, l, r) => new Multiply(
        new Multiply(Const.TWO, dx),
        new Hypot(Const.ZERO, new Divide(x, new Add(l, r)))
    )

    return {isZero, isAbsOne, diffHypotTerm, diffHMeanTerm}
})()

const isZero    = simplifying.isZero
const isAbsOne  = simplifying.isAbsOne

const Negate = opBuilder("negate",
    c => -c, (c, cx) => new Negate(cx), cs => new Negate(cs))

const Add = opBuilder("+",
    (l, r) => l + r,
    (l, lx, r, rx) => new Add(lx, rx),
    (ls, rs) => isZero(ls) ? rs : isZero(rs) ? ls : new Add(ls, rs))

const Subtract = opBuilder("-",
    (l, r) => l - r,
    (l, lx, r, rx) => new Subtract(lx, rx),
    (ls, rs) => isZero(ls) ? new Negate(rs) : isZero(rs) ? ls : new Subtract(ls, rs))

const Multiply = opBuilder("*",
    (l, r) => l * r,
    (l, lx, r, rx) => new Add(new Multiply(lx, r), new Multiply(l, rx)),
    (ls, rs) => {
        if (isZero(ls) || isZero(rs))
            return Const.ZERO
        else if (isAbsOne(ls))
            return ls.evaluate() > 0 ? rs : new Negate(rs)
        else if (isAbsOne(rs))
            return rs.evaluate() > 0 ? ls : new Negate(ls)
        return new Multiply(ls, rs)
    })

const Divide = opBuilder("/",
    (l, r) => l / r,
    (l, lx, r, rx) => new Add(
        new Divide(lx, r),
        new Divide(
            new Multiply(new Negate(l), rx),
            new Multiply(r, r)
        )
    ),
    (ls, rs) => {
        if (isZero(ls))
            return Const.ZERO
        else if (isAbsOne(rs))
            return rs.evaluate() > 0 ? ls : new Negate(ls)
        return new Divide(ls, rs)
    })

const diffHypotTerm = simplifying.diffHypotTerm
const diffHMeanTerm = simplifying.diffHMeanTerm

const Hypot = opBuilder("hypot",
    (l, r) => l * l + r * r,
    (l, lx, r, rx) => new Add(diffHypotTerm(l, lx), diffHypotTerm(r, rx)),
    (ls, rs) => isZero(ls) ? new Hypot(Const.ZERO, rs) : isZero(rs)
        ? new Hypot(ls, Const.ZERO)
        : new Hypot(ls, rs))

const HMean = opBuilder("hmean",
    (l, r) => 2 / (1 / l + 1 / r),
    (l, lx, r, rx) => new Add(diffHMeanTerm(lx, r, l, r), diffHMeanTerm(rx, l, l, r)),
    (ls, rs) => new HMean(ls, rs))

const variables  = variable.names
const operations = operation.signs

const parse = source => {
    const ts = []
    source.split(/\s+/).forEach(t => {
        const info = operations.get(t)
        if (info !== undefined)
            ts.push(new info.atom(...ts.splice(-info.argc)))
        else if (variables.hasOwnProperty(t))
            ts.push(new Variable(t))
        else {
            const value = parseInt(t)
            if (!isNaN(value))
                ts.push(new Const(value))
        }
    })
    return ts.pop()
}
