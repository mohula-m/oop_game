package pyramidrunner.core;

/**
 * Subject (publisher) interface for the Observer design pattern.
 *
 * <p>Any game object that can fire events implements this interface so that
 * interested parties ({@link GameEventListener} implementations) can
 * subscribe and unsubscribe at runtime without the source knowing anything
 * about the consumers — full decoupling.</p>
 */
public interface GameEventSource {

    /**
     * Registers an observer to receive future events from this source.
     *
     * @param listener the observer to add; ignored if already registered
     */
    void addListener(GameEventListener listener);

    /**
     * Unregisters a previously added observer.
     *
     * @param listener the observer to remove; no-op if not found
     */
    void removeListener(GameEventListener listener);
}
