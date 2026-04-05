package pyramidrunner.entity;

import pyramidrunner.core.Drawable;
import pyramidrunner.core.Updatable;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Abstract base class for all game objects — demonstrates Generalization.
 *
 * <p>Implements {@link pyramidrunner.core.Drawable} and {@link pyramidrunner.core.Updatable}
 * interfaces, enforcing that every concrete entity provides both rendering
 * and update logic (Abstraction).</p>
 *
 * <p>All positional fields are {@code protected} so subclasses can access them
 * directly without getters — Encapsulation: external code uses getters only.</p>
 */
public abstract class Entity implements Drawable, Updatable {

    /** X position in pixels (left edge). Encapsulated — only subclasses access directly. */
    protected int x;
    /** Y position in pixels (top edge). Encapsulated — only subclasses access directly. */
    protected int y;
    /** Width in pixels. Encapsulated. */
    protected int width;
    /** Height in pixels. Encapsulated. */
    protected int height;

    /**
     * Initialises position and size.
     *
     * @param x      left edge in pixels
     * @param y      top edge in pixels
     * @param width  width in pixels
     * @param height height in pixels
     */
    public Entity(int x, int y, int width, int height) {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
    }

    /**
     * Advances this entity's state by one game frame.
     * Overriding required by Updatable — run-time polymorphism.
     */
    @Override
    public abstract void update();

    /**
     * Draws this entity with its default appearance.
     * Overriding required by Drawable — run-time polymorphism.
     *
     * @param g the Graphics2D context
     */
    @Override
    public abstract void draw(Graphics2D g);

    /**
     * Draws this entity with a custom colour override.
     * Overloading draw() — compile-time polymorphism.
     *
     * @param g     the Graphics2D context
     * @param color colour to use instead of the default
     */
    @Override
    public abstract void draw(Graphics2D g, Color color);

    /**
     * Returns true if this entity's bounding box overlaps another entity's.
     * Shared collision utility available to all subclasses.
     *
     * @param other the entity to test against
     * @return true if the two bounding boxes intersect
     */
    public boolean intersects(Entity other) {
        return x < other.x + other.width
            && x + width  > other.x
            && y < other.y + other.height
            && y + height > other.y;
    }

    /** @return X position (left edge) in pixels */
    public int getX()      { return x; }
    /** @return Y position (top edge) in pixels */
    public int getY()      { return y; }
    /** @return width in pixels */
    public int getWidth()  { return width; }
    /** @return height in pixels */
    public int getHeight() { return height; }
}
