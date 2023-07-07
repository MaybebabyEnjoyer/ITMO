package expression.generic.parser;

import expression.generic.parser.expressions.*;
import expression.generic.parser.type.TypeInterface;

import java.util.List;
import java.util.Map;

public class GenericParser<T> extends BaseParser implements TripleParser<T> {
    private String finalOperator = null;
    private final TypeInterface<T> workingMode;
    private static final Map<String, Integer> PRIORITY_MAP = Map.of(
            "max", 30,
            "min", 30,
            "+", 20,
            "-", 20,
            "*", 10,
            "/", 10,
            "mod", 10
    );
    private static final int MAX_PRIORITY = PRIORITY_MAP.values().stream().max(Integer::compare).get();
    private static final int MAX_OPERATOR_LENGTH = PRIORITY_MAP.keySet().stream().mapToInt(String::length).max().getAsInt();
    private static final int MIN_DIFFERENCE_BETWEEN_PRIORITIES = PRIORITY_MAP.values().stream().min(Integer::compare).get();
    private static final List<String> UNARIES = List.of(
            "abs",
            "square",
            "count"
    );

    public GenericParser(TypeInterface<T> workingMode) {
        this.workingMode = workingMode;
    }

    public GenericExpression<T> parse(String expression) {
        setSource(new StringSource(expression));
        return parseExpression(MAX_PRIORITY);
    }

    private GenericExpression<T> parseExpression(int priority) {
        if (priority == 0) {
            return parseMinPriority();
        } else {
            GenericExpression<T> result = parseExpression(priority - MIN_DIFFERENCE_BETWEEN_PRIORITIES);
            skipSpaces();
            while (!eof() && !test(')')) {
                if (finalOperator == null) {
                    takeOperator();
                }
                if (PRIORITY_MAP.get(finalOperator) == priority) {
                    String operator = finalOperator;
                    finalOperator = null;
                    result = constructExpression(operator, result, parseExpression(priority - MIN_DIFFERENCE_BETWEEN_PRIORITIES));
                    skipSpaces();
                } else {
                    break;
                }
            }
            return result;
        }
    }

    private void takeOperator() {
        StringBuilder sb = new StringBuilder();
        while (!eof() && !PRIORITY_MAP.containsKey(sb.toString()) && sb.length() <= MAX_OPERATOR_LENGTH) {
            sb.append(ch);
            take();
        }
        if (!PRIORITY_MAP.containsKey(sb.toString())) {
            throw error("Unknown operator: " + sb);
        } else {
            finalOperator = sb.toString();
        }
    }

    private GenericExpression<T> parseMinPriority() {
        skipSpaces();
        if (take('(')) {
            GenericExpression<T> result = parseExpression(MAX_PRIORITY);
            expect(')');
            return result;
        } else if (take('-')) {
            if (between('0', '9')) {
                return parseConst(true);
            } else {
                return new Negate<>(parseMinPriority(), workingMode);
            }
        } else if (between('0', '9')) {
            return parseConst(false);
        } else {
            StringBuilder sb = new StringBuilder();
            while (between('a', 'z') || between('0', '9')) {
                sb.append(ch);
                take();
            }
            if (UNARIES.contains(sb.toString())) {
                return constructExpression(sb.toString(), parseMinPriority());
            } else if (sb.toString().equals("x") || sb.toString().equals("y") || sb.toString().equals("z")) {
                return new Variable<>(sb.toString());
            } else {
                throw error("Unknown expression");
            }
        }
    }

    private GenericExpression<T> parseConst(boolean isNegative) {
        StringBuilder sb = new StringBuilder();
        if (isNegative) {
            sb.append('-');
        }
        while (between('0', '9') || ch == '.' || ch == ',') {
            sb.append(ch);
            take();
        }
        return new Const<>(workingMode.parse(sb.toString()));
    }

    private GenericExpression<T> constructExpression(String operator, GenericExpression<T> first, GenericExpression<T> second) {
        return switch (operator) {
            case "+" -> new Add<>(first, second, workingMode);
            case "-" -> new Subtract<>(first, second, workingMode);
            case "*" -> new Multiply<>(first, second, workingMode);
            case "/" -> new Divide<>(first, second, workingMode);
            case "mod" -> new Mod<>(first, second, workingMode);
            case "min" -> new Min<>(first, second, workingMode);
            case "max" -> new Max<>(first, second, workingMode);
            default -> throw error("Unknown operator: " + operator);
        };
    }

    private GenericExpression<T> constructExpression(String operator, GenericExpression<T> first) {
        return switch (operator) {
            case "count" -> new Count<>(first, workingMode);
            case "abs" -> new Abs<>(first, workingMode);
            case "square" -> new Square<>(first, workingMode);
            default -> throw error("Unknown operator: " + operator);
        };
    }
}
