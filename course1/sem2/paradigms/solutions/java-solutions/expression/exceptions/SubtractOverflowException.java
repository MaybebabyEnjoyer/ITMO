package expression.exceptions;

public class SubtractOverflowException extends OperateOverflowException {
    public SubtractOverflowException(int a, int b) {
        super("subtract", a + " - " + b);
    }
}
