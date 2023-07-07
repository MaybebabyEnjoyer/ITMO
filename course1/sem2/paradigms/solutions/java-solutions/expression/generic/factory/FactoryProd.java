package expression.generic.factory;

public class FactoryProd {
    public static TypeFactory getFactory(String type) {
        return switch (type) {
            case "i" -> new IntegerTypeFactory(true);
            case "d" -> new DoubleFactory();
            case "bi" -> new BigIntegerFactory();
            case "s" -> new ShortTypeFactory();
            case "f" -> new FloatTypeFactory();
            case "u" -> new IntegerTypeFactory(false);
            case "l" -> new LongFactory();
            case "p" -> new PoModuluFactory(10079);
            default -> null;
        };
    }
}
