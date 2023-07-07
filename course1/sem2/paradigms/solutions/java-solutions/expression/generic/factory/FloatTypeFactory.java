package expression.generic.factory;

import expression.generic.parser.type.FloatType;
import expression.generic.parser.type.TypeInterface;
public class FloatTypeFactory extends TypeFactory {
    @Override
    public TypeInterface<?> createType() {
        return new FloatType();
    }
}
