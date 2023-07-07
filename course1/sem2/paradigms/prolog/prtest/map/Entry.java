package prtest.map;

import prtest.Value;

/**
 * Prolog-aware analogue to {@link java.util.Map.Entry}.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Entry {
    private final int key;
    private Value value;
    private Value struct;

    public Entry(final int key, final Value value) {
        this.key = key;
        setValue(value);
    }

    public void setValue(final Value value) {
        this.value = value;
        struct = Value.struct(",", key, value);
    }

    public int getKey() {
        return key;
    }

    public Value getValue() {
        return value;
    }

    public Value toValue() {
        return struct;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
