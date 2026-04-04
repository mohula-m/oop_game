package pyramidrunner.entity;

import pyramidrunner.input.KeyHandler;
import pyramidrunner.world.TileMap;

import java.awt.Color;
import java.awt.Graphics2D;

// Inheritance: Player extends Entity
public class Player extends Entity {

    private static final float GRAVITY       = 0.5f;
    private static final float JUMP_FORCE    = -14f;
    private static final float MAX_FALL_SPEED = 16f;

    private final int speed;

    // Aggregation
    private final KeyHandler keyHandler;
    // Aggregation
    private final TileMap tileMap;

    private float velocityY = 0;
    private boolean onGround = false;

    public Player(int x, int y, KeyHandler keyHandler, TileMap tileMap) {
        super(x, y, 44, 44);
        this.speed = 4;
        this.keyHandler = keyHandler;
        this.tileMap = tileMap;
    }

    // Overriding
    @Override
    public void update() {
        // Check if standing on something
        onGround = tileMap.collidesWithSolid(x, y + 1, width, height);

        // Jump only when on the ground
        if (keyHandler.jump && onGround) {
            velocityY = JUMP_FORCE;
        }

        // Gravity pulls down every frame
        velocityY += GRAVITY;
        if (velocityY > MAX_FALL_SPEED) velocityY = MAX_FALL_SPEED;

        // Horizontal movement
        int dx = 0;
        if (keyHandler.left)  dx -= speed;
        if (keyHandler.right) dx += speed;
        move(dx, 0);

        // Vertical movement with collision
        int dy = (int) velocityY;
        if (tileMap.collidesWithSolid(x, y + dy, width, height)) {
            // Slide pixel-by-pixel to the exact edge
            int step = dy > 0 ? 1 : -1;
            while (!tileMap.collidesWithSolid(x, y + step, width, height)) {
                y += step;
            }
            velocityY = 0;
        } else {
            y += dy;
        }
    }

    // Overloading:
    public void move(int dx, int dy) {
        if (!tileMap.collidesWithSolid(x + dx, y, width, height)) x += dx;
        if (!tileMap.collidesWithSolid(x, y + dy, width, height)) y += dy;
    }

    // Overloading:
    public void move(int dx, int dy, boolean skipCollision) {
        if (skipCollision) {
            x += dx;
            y += dy;
        } else {
            move(dx, dy);
        }
    }

    public boolean isOnGround() { return onGround; }
    public float getVelocityY() { return velocityY; }

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
