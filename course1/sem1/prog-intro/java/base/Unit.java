package base;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Unit {
    @SuppressWarnings("InstantiationOfUtilityClass")
    public static final Unit INSTANCE = new Unit();

    private Unit() { }

    @Override
    public String toString() {
        return "unit";
    }
}
