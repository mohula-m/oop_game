package pyramidrunner.entity;

import java.awt.Color;
import java.awt.Graphics2D;

// Generalization: Entity is the abstract base for all game objects
public abstract class Entity {

    protected int x, y;
    protected int width, height;

    public Entity(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Overriding: subclasses must provide their own update logic
    public abstract void update();

    // Compile-time polymorphism (overloading): two draw signatures
    public abstract void draw(Graphics2D g);
    public abstract void draw(Graphics2D g, Color color);

    public int getX()      { return x; }
    public int getY()      { return y; }
    public int getWidth()  { return width; }
    public int getHeight() { return height; }
}
