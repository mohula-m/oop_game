package pyramidrunner.entity;

import pyramidrunner.core.GameEvent;
import pyramidrunner.core.GameEventListener;
import pyramidrunner.core.GameEventSource;
import pyramidrunner.core.GameEventType;
import pyramidrunner.world.TileMap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * The exit door that ends the level when the player steps into it.
 *
 * <p><b>Design pattern — Observer / Subject:</b> {@code Door} implements
 * {@link GameEventSource}.  Any number of {@link GameEventListener}s can
 * register with {@link #addListener}.  The moment the player touches the
 * door, {@code Door} fires a {@link GameEventType#LEVEL_COMPLETE} event to
 * every registered listener — without knowing anything about them.</p>
 *
 * <p>Inheritance: {@code Door} extends {@link Entity}.</p>
 */
// Inheritance: Door extends Entity
public class Door extends Entity implements GameEventSource {

    private static final Color COLOR_DOOR   = new Color(255, 195, 0);
    private static final Color COLOR_BORDER = new Color(180, 130, 0);

    /** Whether the player has already reached this door. */
    private boolean triggered = false;

    /**
     * Registered observers — notified once when the door is triggered.
     * Composition: Door owns its listener list.
     */
    private final List<GameEventListener> listeners = new ArrayList<>();

    /**
     * Places the door at the given tile coordinates.
     *
     * @param tileCol column index (0-based)
     * @param tileRow row    index (0-based)
     */
    public Door(int tileCol, int tileRow) {
        super(tileCol * TileMap.TILE_SIZE, tileRow * TileMap.TILE_SIZE,
              TileMap.TILE_SIZE, TileMap.TILE_SIZE);
    }

    // ── GameEventSource ────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Registers {@code listener} to be notified when the door is triggered.
     */
    @Override
    public void addListener(GameEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     * Removes {@code listener} from the notification list.
     */
    @Override
    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all registered observers about {@code event}.
     *
     * @param event the event to broadcast
     */
    private void notifyListeners(GameEvent event) {
        // Lambda: iterate and notify every observer in one expression
        listeners.forEach(l -> l.onGameEvent(event));
    }

    // ── Domain logic ───────────────────────────────────────────────────────

    /**
     * Checks whether the player overlaps this door.
     * If so, marks it as triggered and fires a
     * {@link GameEventType#LEVEL_COMPLETE} event.
     *
     * @param player the player entity to test
     */
    public void checkPlayer(Player player) {
        if (!triggered && intersects(player)) {
            triggered = true;
            notifyListeners(new GameEvent(
                    GameEventType.LEVEL_COMPLETE,
                    "Player reached the exit door at (" + x + ", " + y + ")"));
        }
    }

    /** @return {@code true} if the player has already reached this door */
    public boolean isTriggered() {
        return triggered;
    }

    /** Resets the door to its initial un-triggered state (used on game restart). */
    public void reset() {
        triggered = false;
    }

    /** @return number of currently registered observers (useful for testing) */
    public int listenerCount() {
        return listeners.size();
    }

    @Override
    public void update() {
        // Door is static — nothing to update each frame
    }

    @Override
    public void draw(Graphics2D g) {
        draw(g, COLOR_DOOR);
    }

    @Override
    public void draw(Graphics2D g, Color color) {
        // Fill
        g.setColor(color);
        g.fillRect(x, y, width, height);
        // Border
        g.setColor(COLOR_BORDER);
        g.drawRect(x, y, width - 1, height - 1);
        g.drawRect(x + 2, y + 2, width - 5, height - 5);
        // Label
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("EXIT", x + 4, y + height / 2 + 6);
    }
}
