import pyramidrunner.core.EntityGroup;
import pyramidrunner.core.GameEvent;
import pyramidrunner.core.GameEventListener;
import pyramidrunner.core.GameEventType;
import pyramidrunner.core.HighScore;
import pyramidrunner.core.PlayerCollisionException;
import pyramidrunner.core.ScoreManager;
import pyramidrunner.entity.Barrier;
import pyramidrunner.entity.Door;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main game panel — handles the game loop, update logic, and rendering.
 *
 * <p>Implements {@link Runnable} so the game loop runs on a dedicated thread.</p>
 *
 * <p><b>Generics:</b> barriers are stored in an {@link EntityGroup}{@code <Barrier>}
 * — a bounded generic container ({@code T extends Entity}) that provides type-safe
 * bulk {@code updateAll()}, {@code drawAll()}, and {@code stream()} operations.</p>
 *
 * <p><b>Design pattern — Observer:</b> {@code GamePanel} implements
 * {@link GameEventListener} and registers itself with {@link Door} (Subject).
 * On level completion the door fires {@link GameEventType#LEVEL_COMPLETE} and
 * {@code GamePanel} records the run time via the {@link ScoreManager} Singleton.</p>
 *
 * <p><b>Design pattern — Singleton:</b> {@link ScoreManager#getInstance()} provides
 * the single application-wide score repository; the panel never constructs it
 * directly.</p>
 *
 * <p><b>Serialization:</b> completed runs are saved to disk as
 * {@link HighScore} objects (implements {@link java.io.Serializable})
 * through {@link ScoreManager}.</p>
 *
 * <p>Composition: GamePanel creates and owns TileMap, Player, Door,
 * and the EntityGroup of Barriers.</p>
 */
public class GamePanel extends JPanel implements Runnable, GameEventListener {

    /** Logger for this class — three levels used: INFO, FINE, SEVERE. */
    private static final Logger LOGGER = Logger.getLogger(GamePanel.class.getName());

    /** Target frames per second. */
    private static final int FPS = 90;

    /** Player starting X in pixels. */
    private static final int PLAYER_START_X = TileMap.TILE_SIZE + 2;
    /** Player starting Y in pixels (row 14, just above the floor). */
    private static final int PLAYER_START_Y = 14 * TileMap.TILE_SIZE - 44;

    private Thread gameThread;

    // Composition: all game objects created and owned here
    private final KeyHandler           keyHandler = new KeyHandler();
    private final TileMap              tileMap;
    private       Player               player;
    private final Door                 door;
    private final EntityGroup<Barrier> barriers;

    /** Singleton score repository — obtained once, reused throughout. */
    private final ScoreManager scoreManager = ScoreManager.getInstance();

    /** System time (ms) when the current round started — used to compute score. */
    private long roundStartTime;

    /** Elapsed ms of the last completed run — shown in the overlay. */
    private long lastRunTimeMs = 0;

    /** True when a barrier has hit the player. Stops updates, shows overlay. */
    private boolean gameOver = false;

    /**
     * Constructs the game panel, initialises all game objects, and
     * registers this panel as an Observer of the exit door.
     */
    public GamePanel() {
        tileMap  = new TileMap();
        player   = new Player(PLAYER_START_X, PLAYER_START_Y, keyHandler, tileMap);
        door     = new Door(22, 1);
        barriers = createBarriers();

        // Observer registration: GamePanel listens for door events
        door.addListener(this);

        roundStartTime = System.currentTimeMillis();

        setPreferredSize(new Dimension(tileMap.getPixelWidth(), tileMap.getPixelHeight()));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        addKeyListener(keyHandler);

        LOGGER.info("GamePanel initialised — " + barriers.size() + " barriers loaded.");
    }

    // ── Observer callback ──────────────────────────────────────────────────

    /**
     * Called by the door (Subject) when a game event fires.
     *
     * <p>On {@link GameEventType#LEVEL_COMPLETE}: calculates elapsed time,
     * creates a {@link HighScore} (Serializable), and persists it via the
     * {@link ScoreManager} Singleton.</p>
     *
     * @param event the event received from the Subject
     */
    @Override
    public void onGameEvent(GameEvent event) {
        if (event.getType() == GameEventType.LEVEL_COMPLETE) {
            lastRunTimeMs = System.currentTimeMillis() - roundStartTime;
            HighScore hs = new HighScore("Player", lastRunTimeMs);
            scoreManager.addScore(hs);
            LOGGER.info("Observer notified — Level complete! Time: "
                        + hs + " | " + event.getMessage());
        } else if (event.getType() == GameEventType.GAME_OVER) {
            LOGGER.info("Observer notified — Game over! " + event.getMessage());
        }
    }

    // ── Game object setup ──────────────────────────────────────────────────

    /**
     * Creates and returns an {@link EntityGroup}{@code <Barrier>} containing
     * all level barriers.
     *
     * @return populated generic barrier group
     */
    private EntityGroup<Barrier> createBarriers() {
        int ts = TileMap.TILE_SIZE;
        EntityGroup<Barrier> group = new EntityGroup<>();
        group.add(new Barrier(5  * ts, 4  * ts - 36, 20, 36, 16 * ts, 2.5f));
        group.add(new Barrier(3  * ts, 8  * ts - 36, 20, 36, 18 * ts, 2.0f));
        group.add(new Barrier(6  * ts, 12 * ts - 36, 16, 36, 14 * ts, 3.5f));
        return group;
    }

    /**
     * Resets the game to its initial state and restarts the timer.
     */
    private void resetGame() {
        player         = new Player(PLAYER_START_X, PLAYER_START_Y, keyHandler, tileMap);
        door.reset();
        gameOver       = false;
        roundStartTime = System.currentTimeMillis();
        LOGGER.info("Game reset — new round started.");
    }

    // ── Thread / game loop ─────────────────────────────────────────────────

    /**
     * Starts the game loop on a new thread.
     */
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
        LOGGER.info("Game thread started.");
    }

    /**
     * Game loop — runs at 90 FPS using a delta-time accumulator.
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

    // ── Update ─────────────────────────────────────────────────────────────

    /**
     * Advances the game by one frame.
     */
    private void update() {
        if ((gameOver || door.isTriggered()) && keyHandler.restart) {
            resetGame();
            return;
        }
        if (gameOver || door.isTriggered()) return;

        barriers.updateAll();

        LOGGER.fine("Player pos: x=" + player.getX() + " y=" + player.getY());

        player.update();

        boolean hit = barriers.stream().anyMatch(b -> b.hitsPlayer(player));
        if (hit) {
            try {
                throw new PlayerCollisionException(player.getX(), player.getY());
            } catch (PlayerCollisionException e) {
                LOGGER.log(Level.SEVERE, "Barrier collision: " + e.getMessage());
                gameOver = true;
            }
        }

        if (door.isTriggered()) return;
        door.checkPlayer(player);
    }

    // ── Rendering ──────────────────────────────────────────────────────────

    /**
     * Renders the current frame.
     *
     * @param g Swing graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        tileMap.draw(g2);
        barriers.drawAll(g2);
        door.draw(g2);
        player.draw(g2);

        if (gameOver) {
            drawOverlay(g2, Color.RED, "GAME OVER", "Press R to restart");
        }
        if (door.isTriggered()) {
            String timeStr = String.format("%.1f s", lastRunTimeMs / 1000.0);
            drawOverlay(g2, Color.YELLOW, "LEVEL COMPLETE!", "Time: " + timeStr + "   Press R to play again");
        }

        g2.dispose();
    }

    /**
     * Draws a semi-transparent overlay with a centred title and subtitle.
     *
     * @param g2       Graphics2D context
     * @param color    title text colour
     * @param title    main message
     * @param subtitle smaller hint below the title
     */
    private void drawOverlay(Graphics2D g2, Color color, String title, String subtitle) {
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(color);
        g2.setFont(new Font("Arial", Font.BOLD, 52));
        int titleX = (getWidth() - g2.getFontMetrics().stringWidth(title)) / 2;
        g2.drawString(title, titleX, getHeight() / 2 - 20);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 22));
        int subX = (getWidth() - g2.getFontMetrics().stringWidth(subtitle)) / 2;
        g2.drawString(subtitle, subX, getHeight() / 2 + 30);
    }
}
