package pyramidrunner.core;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Interface for all objects that can be rendered on screen.
 * Enforces compile-time polymorphism via overloaded draw() methods.
 */
public interface Drawable {

    /** Draw with default appearance. */
    void draw(Graphics2D g);

    /** Draw with a specific color override. */
    void draw(Graphics2D g, Color color);
}
