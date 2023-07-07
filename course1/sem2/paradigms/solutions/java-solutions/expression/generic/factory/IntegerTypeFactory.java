package expression.generic.factory;

import expression.generic.parser.type.IntegerType;
import expression.generic.parser.type.TypeInterface;

public class IntegerTypeFactory extends TypeFactory {
    private final boolean isSigned;

    public IntegerTypeFactory(boolean isSigned) {this.isSigned = isSigned;}

    @Override
    public TypeInterface<?> createType() {
        return new IntegerType(isSigned);
    }
}
