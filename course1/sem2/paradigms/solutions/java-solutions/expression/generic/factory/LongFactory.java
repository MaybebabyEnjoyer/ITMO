package expression.generic.factory;
import expression.generic.parser.type.LongType;
import expression.generic.parser.type.TypeInterface;

public class LongFactory extends TypeFactory {
    @Override
    public TypeInterface<?> createType() {
        return new LongType();
    }
}
