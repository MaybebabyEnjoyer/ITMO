package expression.generic.factory;
import expression.generic.parser.type.TypeInterface;

public abstract class TypeFactory {
    public abstract TypeInterface<?> createType();
}
