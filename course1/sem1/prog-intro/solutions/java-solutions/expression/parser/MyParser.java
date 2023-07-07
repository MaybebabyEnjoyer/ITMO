package expression.parser;

import expression.*;
import expression.exceptions.*;

public class MyParser extends BaseParser implements TripleParser {

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
            if (take('s')) {
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
                result = new CheckedAdd(result, parseMulDiv());
            } else if (take('-')) {
                result = new CheckedSubtract(result, parseMulDiv());
            } else {
                return result;
            }
        }
    }

    private BaseExpression parseMulDiv() {
        BaseExpression result = parseUnary();
        while (true) {
            if (take('*')) {
                result = new CheckedMultiply(result, parseUnary());
            } else if (take('/')) {
                result = new CheckedDivide(result, parseUnary());
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
                return new CheckedNegate(result);
            } else {
                return UnaryMinus.getUnaryMinus(parsePrimary());
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
                    return new Count(new CheckedNegate(result));
                } else {
                    return new Count(UnaryMinus.getUnaryMinus(parsePrimary()));
                }
            } else {
                return Count.getCount(parsePrimary());
            }
        } else if (take('p')) {
            expect("ow10");
            if (take('(')) {
                BaseExpression result = parseExpression();
                expect(')');
                return new CheckedPow(result);
            } else if (take('-')) {
                if (take('(')) {
                    BaseExpression result = parseExpression();
                    expect(')');
                    return new CheckedPow(new CheckedNegate(result));
                } else {
                    return new CheckedPow(UnaryMinus.getUnaryMinus(parsePrimary()));
                }
            } else {
                return new CheckedPow(parsePrimary());
            }
        } else if(take('l')) {
            expect("og10");
            if (take('(')) {
                BaseExpression result = parseExpression();
                expect(')');
                return new CheckedLog(result);
            } else if (take('-')) {
                if (take('(')) {
                    BaseExpression result = parseExpression();
                    expect(')');
                    return new CheckedLog(new CheckedNegate(result));
                } else {
                    return new CheckedLog(UnaryMinus.getUnaryMinus(parsePrimary()));
                }
            } else {
                return new CheckedLog(parsePrimary());
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
            return new CheckedNegate(parsePrimary());
        } else if (take('c')) {
            expect("ount");
            return new Count(parsePrimary());
        } else if (take('p')) {
            expect("ow10");
            return new CheckedPow(parsePrimary());
        } else if(take('l')) {
            expect("og10");
            return new CheckedLog(parsePrimary());
        } else {
            throw error("Error while parsing, unexpected symbol: " + ch);
        }
    }

    private BaseExpression parseConst() {
        StringBuilder sb = new StringBuilder();
        while (between('0', '9')) {
            sb.append(ch);
            take();
        }
        if (sb.length() == 0) {
            throw error("Expected number");
        }
        if (sb.toString().equals("2147483648")) {
            return new Const(Integer.MIN_VALUE);
        } else {
            try {
                return new Const(Integer.parseInt(sb.toString()));
            } catch (NumberFormatException e) {
                throw error("Number is too big");
            }
        }
    }

    private BaseExpression parseVariable() {
        char var = ch;
        take();
        return new Variable(String.valueOf(var));
    }

    private String Format(String expression) {
        StringBuilder sb = new StringBuilder();
        int l = 0;
        int r = 0;
        int white = 0;
        int op = 0;
        setSource(new StringSource("XD"));
        for (int i = 0; i < expression.length(); i++) {
            if (!Character.isWhitespace(expression.charAt(i))) {
                sb.append(expression.charAt(i));
                if (expression.charAt(i) == '(') {
                    l++;
                }
                if (expression.charAt(i) == ')') {
                    r++;
                }
                if (operatorChecker(expression.charAt(i))) {
                    op++;
                }
                if (expression.charAt(i) == 's' ||
                        (expression.charAt(i) == 'c' && expression.charAt(i+1) == 'l')) {
                    if (Character.isDigit(expression.charAt(i - 1))) {
                        throw error("Expected whitespace after first operand, but found " + expression.charAt(i - 1));
                    }
                }
                if (unaryWhitespaceChecker(expression.charAt(i))) {
                    if (!Character.isWhitespace(expression.charAt(i + 3)) && notBrackets(expression.charAt(i + 3))) {
                        throw error("Expected whitespace or expression after multi-char unary operator, but found " + expression.charAt(i + 3));
                    }
                }
                if (!(checkBetween(expression.charAt(i)))) {
                    throw error("Invalid symbol found '" + expression.charAt(i) + "', please enter a valid expression OwO");
                }
            }
            else {
                white++;
            }
        }
        if (l != r) {
            throw error("Unexpected parenthesis balance, please ensure that all parentheses are closed UwU");
        }
        if (sb.toString().equals("2147483648")) {
            throw error("Number is too big");
        }
        if (white > 0 && op == 0) {
            throw error("Expected operator, but found whitespace");
        }
        return sb.toString();
    }


    private boolean customBetween(char a, char b, char c) {
        return b <= a  && a <= c;
    }

    private boolean operatorChecker(char a) {
        return customBetween(a, '*', '/') || a == 's' || a == 'c' ||
                a == 'g' || a == 'l' || a == 'p';
    }
    private boolean unaryWhitespaceChecker(char a) {
        return a == 'u' || a == 'w' || a == 'g';
    }

    private boolean notBrackets(char a) {
        return a != '(' && a != ')';
    }

    private boolean checkBetween(char a) {
        return ('(' <= a && a <= '9') || ('a' <= a && a <= 'z');
    }
}