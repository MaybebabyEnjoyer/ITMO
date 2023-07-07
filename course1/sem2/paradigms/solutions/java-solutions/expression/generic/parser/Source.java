package expression.generic.parser;

public interface Source {
    IllegalArgumentException error(String message);
    boolean hasNext();
    char next();
}
