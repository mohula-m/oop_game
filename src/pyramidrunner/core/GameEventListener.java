package pyramidrunner.core;

/**
 * Observer interface for the game event system.
 *
 * Any class that wants to react to events (level complete, game over)
 * implements this interface and registers with a GameEventSource.
 * The source then calls onGameEvent() when something happens.
 *
 * Design pattern: Observer - listener doesn't know who fires the event,
 * and the source doesn't know who is listening.
 *
 * Marked @FunctionalInterface so it can also be used as a lambda.
 */
@FunctionalInterface
public interface GameEventListener {

    /**
     * Called when a game event is fired.
     *
     * @param event the event that occurred (never null)
     */
    void onGameEvent(GameEvent event);
}
