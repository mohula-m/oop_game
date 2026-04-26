package pyramidrunner.entity;

import pyramidrunner.core.Drawable;
import pyramidrunner.core.Updatable;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Base class for all game objects (player, door, barrier).
 * Uses Generalization - everything in the game inherits from here.
 *
 * Implements Drawable and Updatable interfaces so every subclass
 * is forced to provide draw() and update() methods (Abstraction).
 *
 * Fields are protected so subclasses can use x, y, width, height directly,
 * but outside code can only read them via getters (Encapsulation).
 */
public abstract class Entity implements Drawable, Updatable {

    // protected so subclasses like Player, Door, Barrier can access directly
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    /**
     * Sets the position and size of this entity.
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

    // Abstract methods - every subclass must override these (polymorphism)
    @Override public abstract void update();
    @Override public abstract void draw(Graphics2D g);
    @Override public abstract void draw(Graphics2D g, Color color); // overloaded version

    /**
     * Simple AABB collision check between this entity and another.
     * Used by Door.checkPlayer() and Barrier.hitsPlayer().
     *
     * @param other the other entity to check against
     * @return true if bounding boxes overlap
     */
    public boolean intersects(Entity other) {
        return x < other.x + other.width
            && x + width  > other.x
            && y < other.y + other.height
            && y + height > other.y;
    }

    public int getX()      { return x; }
    public int getY()      { return y; }
    public int getWidth()  { return width; }
    public int getHeight() { return height; }
}
