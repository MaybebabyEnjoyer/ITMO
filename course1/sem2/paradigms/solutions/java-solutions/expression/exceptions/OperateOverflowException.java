package expression.exceptions;

abstract class OperateOverflowException extends ExpressionException {
    public OperateOverflowException(String operation, String arg) {
        super("Overflow in " + operation + " with argument " + arg);
    }
}
