package expression.generic.factory;

import expression.generic.parser.type.BigIntegerType;
import expression.generic.parser.type.TypeInterface;
public class BigIntegerFactory extends TypeFactory {
    @Override
    public TypeInterface<?> createType() {
        return new BigIntegerType();
    }
}
