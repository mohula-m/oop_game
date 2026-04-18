package pyramidrunner.core;

/**
 * Enumerates the types of game events that can be fired through
 * the Observer notification system.
 *
 * <p>Used by {@link GameEvent} to classify what happened, and by
 * {@link GameEventListener} implementations to decide how to react.</p>
 */
public enum GameEventType {

    /** The player reached the exit door — level is complete. */
    LEVEL_COMPLETE,

    /** The player was hit by a barrier — game over. */
    GAME_OVER
}
