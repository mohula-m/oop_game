package pyramidrunner.entity;

import pyramidrunner.input.KeyHandler;
import pyramidrunner.world.TileMap;

import java.awt.Color;
import java.awt.Graphics2D;

// Inheritance: Player extends Entity
public class Player extends Entity {

    private final int speed;
    private final KeyHandler keyHandler;
    private final TileMap tileMap;

    public Player(int x, int y, KeyHandler keyHandler, TileMap tileMap) {
        super(x, y, 44, 44);
        this.speed = 4;
        this.keyHandler = keyHandler;
        this.tileMap = tileMap;
    }

    // Overriding: concrete implementation of Entity.update()
    @Override
    public void update() {
        int dx = 0, dy = 0;
        if (keyHandler.up)    dy -= speed;
        if (keyHandler.down)  dy += speed;
        if (keyHandler.left)  dx -= speed;
        if (keyHandler.right) dx += speed;
        move(dx, dy);
    }

    // Overloading: move by delta with collision check
    public void move(int dx, int dy) {
        if (!tileMap.collidesWithSolid(x + dx, y, width, height)) x += dx;
        if (!tileMap.collidesWithSolid(x, y + dy, width, height)) y += dy;
    }

    // Overloading: move by delta, optionally skipping collision (useful for tests/debug)
    public void move(int dx, int dy, boolean skipCollision) {
        if (skipCollision) {
            x += dx;
            y += dy;
        } else {
            move(dx, dy);
        }
    }

    // Overriding: draw as solid black square
    @Override
    public void draw(Graphics2D g) {
        draw(g, Color.BLACK);
    }

    // Overriding: draw with custom color
    @Override
    public void draw(Graphics2D g, Color color) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width - 1, height - 1);
    }
}
