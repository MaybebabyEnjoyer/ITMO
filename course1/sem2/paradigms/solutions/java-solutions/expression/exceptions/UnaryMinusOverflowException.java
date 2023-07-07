package expression.exceptions;

public class UnaryMinusOverflowException extends OperateOverflowException {
    public UnaryMinusOverflowException(int a) {
        super("unary minus", "-" + a);
    }
}
