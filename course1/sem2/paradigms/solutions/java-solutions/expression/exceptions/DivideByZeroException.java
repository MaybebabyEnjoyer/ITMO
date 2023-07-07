package expression.exceptions;

public class DivideByZeroException extends ExpressionException {
    public DivideByZeroException(String arg) {
        super("Division by zero with argument " + arg);
    }
}
