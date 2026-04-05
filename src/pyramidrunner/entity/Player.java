package pyramidrunner.entity;

import pyramidrunner.input.KeyHandler;
import pyramidrunner.world.TileMap;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * The player character — demonstrates Inheritance (extends Entity),
 * Overriding (update, draw), and Overloading (move).
 *
 * <p>Uses Aggregation for {@link KeyHandler} and {@link TileMap}:
 * both are passed in from outside and exist independently of the Player.</p>
 */
public class Player extends Entity {

    private static final float GRAVITY        = 0.5f;
    private static final float JUMP_FORCE     = -14f;
    private static final float MAX_FALL_SPEED = 16f;

    /** Horizontal movement speed in pixels per frame. Encapsulated. */
    private final int speed;

    // Aggregation: received via constructor, exist independently of Player
    private final KeyHandler keyHandler;
    private final TileMap    tileMap;

    /** Current vertical velocity. Positive = falling down. */
    private float   velocityY  = 0;
    /** True when the player's feet are touching a solid tile. */
    private boolean onGround   = false;
    /** True when GamePanel confirms the player is standing on a moving platform. */
    private boolean onPlatform = false;

    /**
     * Creates a player at the given pixel position.
     *
     * @param x          starting X in pixels
     * @param y          starting Y in pixels
     * @param keyHandler keyboard state source (aggregation)
     * @param tileMap    collision source (aggregation)
     */
    public Player(int x, int y, KeyHandler keyHandler, TileMap tileMap) {
        super(x, y, 44, 44);
        this.speed      = 4;
        this.keyHandler = keyHandler;
        this.tileMap    = tileMap;
    }

    /**
     * Updates player physics and input each frame.
     * Overriding Entity.update() — run-time polymorphism.
     */
    @Override
    public void update() {
        // Standing on a tile OR on a moving platform (onPlatform set by GamePanel each frame)
        onGround = tileMap.collidesWithSolid(x, y + 1, width, height) || onPlatform;

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

    /**
     * Moves the player by the given delta, respecting tile collision.
     * Overloading — compile-time polymorphism (2-argument variant).
     *
     * @param dx horizontal delta in pixels
     * @param dy vertical delta in pixels
     */
    public void move(int dx, int dy) {
        if (!tileMap.collidesWithSolid(x + dx, y, width, height)) x += dx;
        if (!tileMap.collidesWithSolid(x, y + dy, width, height)) y += dy;
    }

    /**
     * Moves the player by the given delta, optionally skipping collision checks.
     * Overloading — compile-time polymorphism (3-argument variant).
     * Used for debug, tests, and platform carry logic.
     *
     * @param dx            horizontal delta in pixels
     * @param dy            vertical delta in pixels
     * @param skipCollision if true, collision is ignored
     */
    public void move(int dx, int dy, boolean skipCollision) {
        if (skipCollision) {
            x += dx;
            y += dy;
        } else {
            move(dx, dy);
        }
    }

    public boolean isOnGround()  { return onGround; }
    public float  getVelocityY() { return velocityY; }

    /**
     * Called by GamePanel each frame to tell the player whether
     * they are standing on a moving platform.
     * Resets to false automatically at the start of each update cycle.
     *
     * @param onPlatform true if the player is on a moving platform
     */
    public void setOnPlatform(boolean onPlatform) {
        this.onPlatform = onPlatform;
    }

    /**
     * Snaps the player to the top surface of a moving platform.
     * Called by GamePanel when landing is detected.
     *
     * @param platformTopY the Y coordinate of the platform's top edge
     */
    public void snapToTop(int platformTopY) {
        this.y         = platformTopY - height;
        this.velocityY = 0;
        this.onGround  = true;
    }

    /**
     * Draws the player as a solid black square.
     * Overriding Entity.draw(g) — run-time polymorphism.
     *
     * @param g the Graphics2D context
     */
    @Override
    public void draw(Graphics2D g) {
        draw(g, Color.BLACK);
    }

    /**
     * Draws the player with a custom colour.
     * Overriding Entity.draw(g, color) — run-time polymorphism.
     *
     * @param g     the Graphics2D context
     * @param color fill colour to use
     */
    @Override
    public void draw(Graphics2D g, Color color) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width - 1, height - 1);
    }
}
