package expression.generic.parser.type;

import expression.exceptions.*;

// Create Checked unchecked operation module
public class IntegerType implements TypeInterface<Integer> {

    //Сюда еще можно было сразу занести операции по модулю и передавать 2 флага + сам модуль
    //Но я не уверен, как лучше реализовать это.
    private final boolean isCheckedInt;

    public IntegerType(boolean isCheckedInt) {
        this.isCheckedInt = isCheckedInt;
    }

    @Override
    public Integer add(Integer a, Integer b) {
        if (isCheckedInt) {
            if (a > 0 && b > 0 && a > Integer.MAX_VALUE - b) {
                throw new AddOverflowException(a, b);
            }
            if (a < 0 && b < 0 && a < Integer.MIN_VALUE - b) {
                throw new AddOverflowException(a, b);
            }
        }
        return a + b;
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        if (isCheckedInt) {
            if (a >= 0 && b < 0 && a > Integer.MAX_VALUE + b) {
                throw new SubtractOverflowException(a, b);
            }
            if (a < 0 && b > 0 && a < Integer.MIN_VALUE + b) {
                throw new SubtractOverflowException(a, b);
            }
        }
        return a - b;
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        if (isCheckedInt) {
            if (a != 0 && b != 0 && (a > 0 && b > 0 && a > Integer.MAX_VALUE / b || a < 0 && b < 0 && a <
                    Integer.MAX_VALUE / b || a > 0 && b < 0 &&
                    b < Integer.MIN_VALUE / a || a < 0 && b > 0 && a < Integer.MIN_VALUE / b)) {
                throw new MultiplyOverflowException(a, b);
            }
        }
        return a * b;
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        if (isCheckedInt) {
            if (a == Integer.MIN_VALUE && b == -1) {
                throw new DivideOverflowException(a, b);
            }
        }
        if (b == 0) {
            throw new DivideByZeroException(Integer.toString(b));
        }
        return a / b;
    }

    @Override
    public Integer negate(Integer a) {
        if (isCheckedInt) {
            if (a == Integer.MIN_VALUE) {
                throw new UnaryMinusOverflowException(a);
            }
        }
        return -a;
    }


    @Override
    public Integer count(Integer a) {
        return Integer.bitCount(a);
    }

    @Override
    public Integer min(Integer a, Integer b) {
        return Math.min(a, b);
    }

    @Override
    public Integer max(Integer a, Integer b) {
        return Math.max(a, b);
    }

    @Override
    public Integer parse(String s) {
        return Integer.parseInt(s);
    }

    @Override
    public Integer valueOf(int x) {
        return x;
    }

    @Override
    public Integer abs(Integer a) {
        if (isCheckedInt) {
            if (a == Integer.MIN_VALUE) {
                throw new UnaryMinusOverflowException(a);
            }
        }
        return Math.abs(a);
    }

    @Override
    public Integer square(Integer a) {
        if (isCheckedInt) {
            if (a > 0 && a > Integer.MAX_VALUE / a || a < 0 && a < Integer.MAX_VALUE / a) {
                throw new MultiplyOverflowException(a, a);
            }
        }
        return a * a;
    }

    @Override
    public Integer mod(Integer a, Integer b) {
        if (b == 0) {
            throw new DivideByZeroException(Integer.toString(b));
        }
        return a % b;
    }

}
