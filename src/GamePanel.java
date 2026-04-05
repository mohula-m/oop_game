import pyramidrunner.entity.Door;
import pyramidrunner.entity.MovingPlatform;
import pyramidrunner.entity.Player;
import pyramidrunner.input.KeyHandler;
import pyramidrunner.world.TileMap;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.util.ArrayList;

/**
 * Main game panel — handles the game loop, update logic, and rendering.
 *
 * <p>Implements {@link Runnable} so the game loop runs on a dedicated thread
 * (demonstrating multi-threading).</p>
 *
 * <p>Composition: GamePanel creates and owns TileMap, Player, Door, and
 * the list of MovingPlatforms.</p>
 */
public class GamePanel extends JPanel implements Runnable {

    /** Target frames per second. */
    private static final int FPS = 90;

    private Thread gameThread;

    // Composition: all game objects are created and owned here
    private final KeyHandler    keyHandler = new KeyHandler();
    private final TileMap       tileMap;
    private final Player        player;
    private final Door          door;
    private final List<MovingPlatform> platforms;

    /**
     * Constructs the game panel, initialises all game objects,
     * and sets up keyboard input.
     */
    public GamePanel() {
        tileMap   = new TileMap();

        // Player starts bottom-left, just above the floor (row 14, col 1)
        player    = new Player(TileMap.TILE_SIZE + 2, 14 * TileMap.TILE_SIZE - 44, keyHandler, tileMap);

        // Exit door placed in the top-right area (row 1, col 22)
        door      = new Door(22, 1);

        platforms = createPlatforms();

        setPreferredSize(new Dimension(tileMap.getPixelWidth(), tileMap.getPixelHeight()));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        addKeyListener(keyHandler);
    }

    /**
     * Builds the list of moving platforms placed across the level.
     * Encapsulated in its own method to keep the constructor clean.
     *
     * @return list of configured MovingPlatform objects
     */
    private List<MovingPlatform> createPlatforms() {
        int ts = TileMap.TILE_SIZE;
        List<MovingPlatform> list = new ArrayList<>();

        // Row 6 — wide platform crossing the middle gap
        list.add(new MovingPlatform(3 * ts, 6 * ts, 3 * ts, 15 * ts, 2f));

        // Row 10 — medium platform in the lower section
        list.add(new MovingPlatform(4 * ts, 10 * ts, 2 * ts, 17 * ts, 1.5f));

        // Row 12 — fast platform near the floor
        list.add(new MovingPlatform(2 * ts, 12 * ts, 3 * ts, 12 * ts, 3f));

        return list;
    }

    /**
     * Starts the game loop on a new thread.
     * The thread calls {@link #run()} which drives update and render.
     */
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Game loop body — runs at a fixed 90 FPS using a delta-time accumulator.
     * Calls {@link #update()} and {@link #repaint()} once per logical frame.
     */
    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double delta        = 0;
        long   lastTime     = System.nanoTime();

        while (gameThread != null) {
            long now = System.nanoTime();
            delta   += (now - lastTime) / drawInterval;
            lastTime = now;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    /**
     * Advances the game state by one frame:
     * <ol>
     *   <li>Updates each moving platform.</li>
     *   <li>Carries the player horizontally when standing on a platform.</li>
     *   <li>Updates the player (input, gravity, tile collision).</li>
     *   <li>Snaps player to any platform they just landed on.</li>
     *   <li>Checks if the player reached the exit door.</li>
     * </ol>
     */
    private void update() {
        if (!door.isTriggered()) {

            // 1. Update platforms, carry player horizontally and flag onPlatform
            boolean playerOnPlatform = false;
            for (MovingPlatform p : platforms) {
                p.update();
                if (p.isEntityOnTop(player)) {
                    player.move(p.getDeltaX(), 0, true);
                    playerOnPlatform = true;
                }
            }
            // Tell the player BEFORE update() so onGround is correct during jump check
            player.setOnPlatform(playerOnPlatform);

            // 2. Update player physics and tile collision
            player.update();

            // 3. Snap player to platform surface if they just landed on one
            for (MovingPlatform p : platforms) {
                if (p.isPlayerLanding(player)) {
                    player.snapToTop(p.getY());
                }
            }
        }

        door.checkPlayer(player);
    }

    /**
     * Renders the current frame:
     * map → moving platforms → door → player → overlay (if level complete).
     *
     * @param g the Graphics context provided by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        tileMap.draw(g2);

        for (MovingPlatform p : platforms) {
            p.draw(g2);
        }

        door.draw(g2);
        player.draw(g2);

        if (door.isTriggered()) {
            // Semi-transparent dark overlay
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, getWidth(), getHeight());
            // Centred "LEVEL COMPLETE!" message
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            String msg  = "LEVEL COMPLETE!";
            int    msgX = (getWidth() - g2.getFontMetrics().stringWidth(msg)) / 2;
            int    msgY = getHeight() / 2;
            g2.drawString(msg, msgX, msgY);
        }

        g2.dispose();
    }
}
