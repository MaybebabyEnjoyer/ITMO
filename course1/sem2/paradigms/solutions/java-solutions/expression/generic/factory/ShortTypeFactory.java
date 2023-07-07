package expression.generic.factory;

import expression.generic.parser.type.ShortType;
import expression.generic.parser.type.TypeInterface;
public class ShortTypeFactory extends TypeFactory {
    @Override
    public TypeInterface<?> createType() {
        return new ShortType();
    }
}
