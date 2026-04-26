package pyramidrunner.core;

/**
 * Carries information about a game event from a Subject to its Observers.
 *
 * Immutable - all fields are set in the constructor and never change.
 * Part of the Observer pattern: Door creates a GameEvent and passes it
 * to all registered GameEventListeners.
 */
public final class GameEvent {

    private final GameEventType type;
    private final String message;

    /**
     * Creates a new game event.
     *
     * @param type    what kind of event happened
     * @param message short description (used for logging)
     */
    public GameEvent(GameEventType type, String message) {
        this.type    = type;
        this.message = message;
    }

    /** @return the type of this event */
    public GameEventType getType()    { return type; }

    /** @return human-readable description */
    public String        getMessage() { return message; }
}
