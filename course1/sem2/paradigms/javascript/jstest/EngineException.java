package jstest;

/**
 * Thrown on test engine error.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class EngineException extends RuntimeException {
    public EngineException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
