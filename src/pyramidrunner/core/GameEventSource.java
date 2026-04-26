package pyramidrunner.core;

/**
 * Subject (publisher) interface for the Observer pattern.
 *
 * Implemented by game objects that fire events (currently Door).
 * Listeners can subscribe/unsubscribe at any time without the source
 * needing to know anything about them.
 */
public interface GameEventSource {

    /**
     * Registers a listener to receive future events from this source.
     *
     * @param listener the observer to add
     */
    void addListener(GameEventListener listener);

    /**
     * Removes a previously registered listener.
     *
     * @param listener the observer to remove
     */
    void removeListener(GameEventListener listener);
}
