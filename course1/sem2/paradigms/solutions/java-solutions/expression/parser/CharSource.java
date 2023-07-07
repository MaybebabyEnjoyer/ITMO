package expression.parser;

public interface CharSource {
    boolean hasNext();
    char next();
    IllegalArgumentException error(String message);
}
