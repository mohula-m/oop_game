package pyramidrunner.core;

/**
 * Base custom exception for all game-related errors.
 * Extends {@link RuntimeException} — demonstrates custom exception hierarchy.
 */
public class GameException extends RuntimeException {

    /**
     * @param message description of the error
     */
    public GameException(String message) {
        super(message);
    }

    /**
     * @param message description of the error
     * @param cause   the underlying cause
     */
    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
}
