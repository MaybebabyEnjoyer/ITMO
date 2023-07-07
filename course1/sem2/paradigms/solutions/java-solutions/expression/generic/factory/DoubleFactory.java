package expression.generic.factory;

import expression.generic.parser.type.DoubleType;
import expression.generic.parser.type.TypeInterface;
public class DoubleFactory extends TypeFactory {
    @Override
    public TypeInterface<?> createType() {
        return new DoubleType();
    }
}
