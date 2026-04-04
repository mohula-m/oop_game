package pyramidrunner.entity;

import pyramidrunner.core.Drawable;
import pyramidrunner.core.Updatable;
import java.awt.Color;
import java.awt.Graphics2D;

// Generalization: Entity is the abstract base for all game objects
// Implements Drawable and Updatable interfaces — forces every subclass to provide draw() and update()
public abstract class Entity implements Drawable, Updatable {

    protected int x, y;
    protected int width, height;

    public Entity(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    public abstract void update();

    // overload
    public abstract void draw(Graphics2D g);
    public abstract void draw(Graphics2D g, Color color);

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
