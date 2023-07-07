package expression.exceptions;

public class DivideOverflowException extends OperateOverflowException {
    public DivideOverflowException(int a, int b) {
        super("divide", a + " / " + b);
    }
}
