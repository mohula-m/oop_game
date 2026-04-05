package pyramidrunner.entity;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * A solid platform that moves horizontally between two X positions.
 * Extends Entity (inheritance), uses Drawable and Updatable via Entity.
 *
 * <p>Demonstrates run-time polymorphism — stored as Entity, behaves as MovingPlatform.</p>
 */
public class MovingPlatform extends Entity {

    private static final Color COLOR_FILL   = new Color(160, 90, 30);
    private static final Color COLOR_BORDER = new Color(100, 55, 15);
    private static final int   HEIGHT       = 14;

    private final int   startX;
    private final int   endX;
    private final float speed;

    private int direction = 1;  // 1 = right, -1 = left
    private int deltaX    = 0;  // pixels moved this frame (for carrying the player)

    /**
     * Creates a moving platform.
     *
     * @param x             starting X position in pixels
     * @param y             Y position in pixels (fixed row)
     * @param platformWidth width in pixels
     * @param endX          rightmost X the platform travels to
     * @param speed         pixels per frame
     */
    public MovingPlatform(int x, int y, int platformWidth, int endX, float speed) {
        super(x, y, platformWidth, HEIGHT);
        this.startX = x;
        this.endX   = endX;
        this.speed  = speed;
    }

    /**
     * Moves the platform one step and records how far it moved this frame.
     * Overriding Entity.update() — run-time polymorphism.
     */
    @Override
    public void update() {
        int prevX = x;
        x += (int)(direction * speed);
        if (x >= endX)   { x = endX;   direction = -1; }
        if (x <= startX) { x = startX; direction =  1; }
        deltaX = x - prevX;
    }

    /**
     * Returns true if the given entity is standing on top of this platform.
     * Used by GamePanel to carry the player with the platform.
     *
     * @param entity the entity to check
     * @return true if entity's feet are resting on this platform's surface
     */
    public boolean isEntityOnTop(Entity entity) {
        int entityBottom = entity.getY() + entity.getHeight();
        boolean horizontalOverlap = entity.getX() + entity.getWidth() > x
                                 && entity.getX() < x + width;
        boolean restingOnSurface  = entityBottom >= y - 3
                                 && entityBottom <= y + 5;
        return horizontalOverlap && restingOnSurface;
    }

    /**
     * Returns true if the given player has just landed on this platform from above.
     * Checks overlap and that the player is moving downward.
     *
     * @param player the player to check
     * @return true if the player is landing on this platform
     */
    public boolean isPlayerLanding(Player player) {
        int playerBottom = player.getY() + player.getHeight();
        boolean horizontalOverlap = player.getX() + player.getWidth() > x
                                 && player.getX() < x + width;
        boolean overlapsTop = playerBottom >= y
                           && playerBottom <= y + height + 6;
        return horizontalOverlap && overlapsTop && player.getVelocityY() >= 0;
    }

    /** @return pixels moved horizontally this frame */
    public int getDeltaX() { return deltaX; }

    /** Draws the platform with default brown colour. Overriding Entity.draw(). */
    @Override
    public void draw(Graphics2D g) {
        draw(g, COLOR_FILL);
    }

    /** Draws the platform with a custom colour. Overloading draw(). */
    @Override
    public void draw(Graphics2D g, Color color) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(COLOR_BORDER);
        g.drawRect(x, y, width - 1, height - 1);
    }
}
