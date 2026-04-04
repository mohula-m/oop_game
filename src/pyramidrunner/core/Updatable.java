package pyramidrunner.core;

/**
 * Interface for all objects that have game logic updated each frame.
 */
public interface Updatable {

    /** Called once per frame to advance the object's state. */
    void update();
}
