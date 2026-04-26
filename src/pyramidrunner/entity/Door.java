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
 * Exit door - the goal of each level.
 * When the player touches it, the level ends (LEVEL_COMPLETE event is fired).
 *
 * Extends Entity (inheritance).
 * Implements GameEventSource - Observer pattern, Door is the Subject.
 * Listeners register with addListener() and get notified when triggered.
 */
public class Door extends Entity implements GameEventSource {

    private static final Color COLOR_DOOR   = new Color(255, 195, 0);
    private static final Color COLOR_BORDER = new Color(180, 130, 0);

    private boolean triggered = false;

    // list of observers - notified when player reaches the door
    private final List<GameEventListener> listeners = new ArrayList<>();

    /**
     * Creates a door at tile position (col, row).
     *
     * @param tileCol column in tile coordinates
     * @param tileRow row in tile coordinates
     */
    public Door(int tileCol, int tileRow) {
        super(tileCol * TileMap.TILE_SIZE, tileRow * TileMap.TILE_SIZE,
              TileMap.TILE_SIZE, TileMap.TILE_SIZE);
    }

    @Override
    public void addListener(GameEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
    }

    // sends the event to all registered listeners
    private void notifyListeners(GameEvent event) {
        listeners.forEach(l -> l.onGameEvent(event));
    }

    /**
     * Call every frame - checks if player is touching the door.
     * Fires LEVEL_COMPLETE event once on first contact.
     *
     * @param player the player to check against
     */
    public void checkPlayer(Player player) {
        if (!triggered && intersects(player)) {
            triggered = true;
            notifyListeners(new GameEvent(
                    GameEventType.LEVEL_COMPLETE,
                    "Player reached the exit at (" + x + ", " + y + ")"));
        }
    }

    /** @return true if the player already reached this door */
    public boolean isTriggered() { return triggered; }

    /** Resets door state - called when restarting the game. */
    public void reset() { triggered = false; }

    /** @return how many listeners are currently registered */
    public int listenerCount() { return listeners.size(); }

    @Override
    public void update() {
        // door doesn't move, nothing to update
    }

    @Override
    public void draw(Graphics2D g) {
        draw(g, COLOR_DOOR);
    }

    @Override
    public void draw(Graphics2D g, Color color) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(COLOR_BORDER);
        g.drawRect(x, y, width - 1, height - 1);
        g.drawRect(x + 2, y + 2, width - 5, height - 5);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("EXIT", x + 4, y + height / 2 + 6);
    }
}
