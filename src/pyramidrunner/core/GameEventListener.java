package pyramidrunner.core;

/**
 * Observer interface for the game-event notification system.
 *
 * <p><b>Design pattern — Observer:</b> any class that wants to react to
 * in-game events (level complete, game over, …) implements this interface
 * and registers itself with a {@link GameEventSource}.  The source calls
 * {@link #onGameEvent(GameEvent)} whenever an event fires, completely
 * decoupling the event producer from its consumers.</p>
 *
 * <p>Example implementors: {@code GamePanel} (updates UI state),
 * future leaderboard or analytics classes.</p>
 */
@FunctionalInterface
public interface GameEventListener {

    /**
     * Called by the Subject when a game event occurs.
     *
     * @param event the event that was fired; never {@code null}
     */
    void onGameEvent(GameEvent event);
}
