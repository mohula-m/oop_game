package pyramidrunner.core;

/**
 * Immutable value object that carries information about a game event.
 *
 * <p>Part of the Observer design pattern — passed from a <em>Subject</em>
 * (e.g. {@code Door}) to every registered {@link GameEventListener}
 * whenever a noteworthy game event occurs.</p>
 *
 * <p>Encapsulation: all fields are {@code private final}; access is
 * provided only through getters.</p>
 */
public final class GameEvent {

    /** Category of the event. */
    private final GameEventType type;

    /** Human-readable description of what happened. */
    private final String message;

    /**
     * Constructs a new game event.
     *
     * @param type    the kind of event that occurred
     * @param message a short description (used for logging / display)
     */
    public GameEvent(GameEventType type, String message) {
        this.type    = type;
        this.message = message;
    }

    /** @return the category of this event */
    public GameEventType getType()    { return type; }

    /** @return human-readable event description */
    public String        getMessage() { return message; }
}
