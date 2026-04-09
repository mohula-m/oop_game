package pyramidrunner.entity;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * A moving red barrier that triggers Game Over on player contact.
 * Extends Entity — demonstrates Inheritance and run-time Polymorphism.
 *
 * <p>Barriers patrol horizontally between two X positions.
 * If the player touches one, the game stops immediately.</p>
 */
public class Barrier extends Entity {

    private static final Color COLOR_FILL   = new Color(200, 30, 30);
    private static final Color COLOR_BORDER = new Color(120, 0, 0);
    private static final Color COLOR_STRIPE = new Color(255, 80, 80);

    private final int   startX;
    private final int   endX;
    private final float speed;
    private int direction = 1;

    /**
     * Creates a barrier that moves horizontally.
     *
     * @param x      starting X position in pixels
     * @param y      Y position in pixels
     * @param width  width in pixels
     * @param height height in pixels
     * @param endX   rightmost X the barrier travels to
     * @param speed  pixels per frame
     */
    public Barrier(int x, int y, int width, int height, int endX, float speed) {
        super(x, y, width, height);
        this.startX = x;
        this.endX   = endX;
        this.speed  = speed;
    }

    /**
     * Moves the barrier one step left or right, reversing at the endpoints.
     * Overriding Entity.update() — run-time polymorphism.
     */
    @Override
    public void update() {
        x += (int)(direction * speed);
        if (x >= endX)   { x = endX;   direction = -1; }
        if (x <= startX) { x = startX; direction =  1; }
    }

    /**
     * Returns true if this barrier's bounding box overlaps the player.
     *
     * @param player the player to check
     * @return true if contact detected
     */
    public boolean hitsPlayer(Player player) {
        return intersects(player);
    }

    /**
     * Draws the barrier as a red rectangle with a diagonal stripe.
     * Overriding Entity.draw(g) — run-time polymorphism.
     *
     * @param g the Graphics2D context
     */
    @Override
    public void draw(Graphics2D g) {
        draw(g, COLOR_FILL);
    }

    /**
     * Draws the barrier with a custom colour.
     * Overloading draw() — compile-time polymorphism.
     *
     * @param g     the Graphics2D context
     * @param color fill colour
     */
    @Override
    public void draw(Graphics2D g, Color color) {
        // Fill
        g.setColor(color);
        g.fillRect(x, y, width, height);
        // Diagonal warning stripe
        g.setColor(COLOR_STRIPE);
        g.drawLine(x, y, x + width, y + height);
        g.drawLine(x + width / 2, y, x + width, y + height / 2);
        // Border
        g.setColor(COLOR_BORDER);
        g.drawRect(x, y, width - 1, height - 1);
    }
}
