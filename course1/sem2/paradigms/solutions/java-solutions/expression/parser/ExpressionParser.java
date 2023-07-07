package expression.parser;

import expression.*;

public class ExpressionParser extends BaseParser implements TripleParser {

    @Override
    public BaseExpression parse(String expression) {
        setSource(new StringSource(Format(expression)));
        return parseExpression();
    }

    BaseExpression parseExpression() {
        return parseMinPriority();
    }

    private BaseExpression parseMinPriority() {
        BaseExpression result = parseAddSub();
        while (true) {
            if (take('g')) {
                expect("cd");
                result = new Gcd(result, parseAddSub());
            } else if (take('l')) {
                expect("cm");
                result = new Lcm(result, parseAddSub());
            } else if (take('s')) {
                expect("et");
                result = new Set(result, parseAddSub());
            } else if (take('c')) {
                expect("lear");
                result = new Clear(result, parseAddSub());
            } else {
                return result;
            }
        }
    }

    private BaseExpression parseAddSub() {
        BaseExpression result = parseMulDiv();
        while (true) {
            if (take('+')) {
                result = new Add(result, parseMulDiv());
            } else if (take('-')) {
                result = new Subtract(result, parseMulDiv());
            } else {
                return result;
            }
        }
    }

    private BaseExpression parseMulDiv() {
        BaseExpression result = parseUnary();
        while (true) {
            if (take('*')) {
                result = new Multiply(result, parseUnary());
            } else if (take('/')) {
                result = new Divide(result, parseUnary());
            } else {
                return result;
            }
        }
    }

    private BaseExpression parseUnary() {
        if (take('-')) {
            if (take('(')) {
                BaseExpression result = parseExpression();
                expect(')');
                return new UnaryMinus(result);
            } else {
                return UnaryMinus.getUnaryMinus(parsePrimary());
            }
        } else if (take('r')) {
            expect("everse");
            if (take('(')) {
                BaseExpression result = parseExpression();
                expect(')');
                return new Reverse(result);
            } else if (take('-')) {
                if (take('(')) {
                    BaseExpression result = parseExpression();
                    expect(')');
                    return new Reverse(new UnaryMinus(result));
                } else {
                    return new Reverse(UnaryMinus.getUnaryMinus(parsePrimary()));
                }
            } else {
                return Reverse.getReverse(parsePrimary());
            }
        } else if (take('c')) {
            expect("ount");
            if (take('(')) {
                BaseExpression result = parseExpression();
                expect(')');
                return new Count(result);
            } else if (take('-')) {
                if (take('(')) {
                    BaseExpression result = parseExpression();
                    expect(')');
                    return new Count(new UnaryMinus(result));
                } else {
                    return new Count(UnaryMinus.getUnaryMinus(parsePrimary()));
                }
            } else {
                return Count.getCount(parsePrimary());
            }
        } else {
            return parsePrimary();
        }
    }

    private BaseExpression parsePrimary() {
        if (take('(')) {
            BaseExpression result = parseExpression();
            expect(')');
            return result;
        } else if (between('0', '9')) {
            return parseConst();
        } else if (between('x', 'z')) {
            return parseVariable();
        } else if (take('-')) {
            return new UnaryMinus(parsePrimary());
        } else if (take('r')) {
            expect("everse");
            return new Reverse(parsePrimary());
        } else if (take('c')) {
            expect("ount");
            return new Count(parsePrimary());
        } else {
            throw error("Unexpected symbol");
        }
    }

    private BaseExpression parseConst() {
        StringBuilder sb = new StringBuilder();
        while (between('0', '9')) {
            sb.append(ch);
            take();
        }
        if (sb.toString().equals("2147483648")) {
            return new Const(Integer.MIN_VALUE);
        } else {
            return new Const(Integer.parseInt(sb.toString()));
        }
    }

    private BaseExpression parseVariable() {
        char var = ch;
        take();
        return new Variable(String.valueOf(var));
    }

    private String Format(String expression) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            if (!Character.isWhitespace(expression.charAt(i))) {
                sb.append(expression.charAt(i));
            }
        }
        return sb.toString();
    }
}
