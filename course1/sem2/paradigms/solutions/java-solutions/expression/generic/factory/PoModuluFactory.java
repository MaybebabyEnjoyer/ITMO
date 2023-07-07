package expression.generic.factory;

import expression.generic.parser.type.PoModuluType;
import expression.generic.parser.type.TypeInterface;
public class PoModuluFactory extends TypeFactory {
    private final int MOD;
    public PoModuluFactory(int mod) {
        this.MOD = mod;
    }

    @Override
    public TypeInterface<?> createType() {
        return new PoModuluType(MOD);
    }
}
