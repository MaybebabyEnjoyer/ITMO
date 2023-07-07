package expression.exceptions;

public class LogIllegalArgumentException extends ExpressionException {
    public LogIllegalArgumentException(String arg) {
        super("Logarithm by illegal argument " + arg);
    }
}
