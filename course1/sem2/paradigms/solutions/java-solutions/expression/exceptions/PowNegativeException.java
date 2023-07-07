package expression.exceptions;

public class PowNegativeException extends OperateOverflowException {
    public PowNegativeException(int a) {
        super("pow", "10^" + a);
    }
}
