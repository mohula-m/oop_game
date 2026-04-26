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
import pyramidrunner.world.Level1;
import pyramidrunner.world.Level2;
import pyramidrunner.world.Level3;
import pyramidrunner.world.TileMap;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.logging.Logger;

/**
 * Main game panel — game loop, update logic, and rendering.
 *
 * <p>Supports three levels ({@link Level1}, {@link Level2}, {@link Level3}).
 * Completing a level advances to the next; finishing all three or pressing
 * ESC returns to {@link MenuPanel} via the {@code returnToMenu} callback.</p>
 *
 * <p><b>Generics:</b> barriers stored in {@link EntityGroup}{@code <Barrier>}.</p>
 * <p><b>Observer:</b> implements {@link GameEventListener}; {@link Door} is Subject.</p>
 * <p><b>Singleton:</b> scores persisted via {@link ScoreManager#getInstance()}.</p>
 * <p><b>Serialization:</b> run times saved as {@link HighScore} objects.</p>
 */
public class GamePanel extends JPanel implements Runnable, GameEventListener {

    private static final Logger LOGGER =
            Logger.getLogger(GamePanel.class.getName());

    private static final int FPS            = 90;
    private static final int PLAYER_START_X = TileMap.TILE_SIZE + 2;
    private static final int PLAYER_START_Y = 14 * TileMap.TILE_SIZE - 44;

    /**
     * All available levels — polymorphism: GamePanel works against the
     * abstract {@code pyramidrunner.world.Level} type, never against
     * concrete subclasses directly.
     */
    private static final pyramidrunner.world.Level[] ALL_LEVELS =
            { new Level1(), new Level2(), new Level3() };

    private Thread gameThread;

    // ── Input (shared across level loads) ─────────────────────────────────
    private final KeyHandler keyHandler = new KeyHandler();

    // ── Game world (rebuilt on every loadLevel call) ───────────────────────
    private TileMap              tileMap;
    private Player               player;
    private Door                 door;
    private EntityGroup<Barrier> barriers;

    // ── Level state ────────────────────────────────────────────────────────
    private int     currentLevelIndex = 0;
    private boolean gameOver          = false;

    // ── Scoring ────────────────────────────────────────────────────────────
    private final ScoreManager scoreManager = ScoreManager.getInstance();
    private long roundStartTime = 0;
    private long lastRunTimeMs  = 0;

    /** Invoked when the player presses ESC or completes all three levels. */
    private final Runnable returnToMenu;

    /**
     * @param startLevel   1-based level number to begin on (1, 2, or 3)
     * @param returnToMenu callback invoked to return to the menu screen
     */
    public GamePanel(int startLevel, Runnable returnToMenu) {
        this.returnToMenu      = returnToMenu;
        this.currentLevelIndex = Math.max(0, Math.min(startLevel - 1, ALL_LEVELS.length - 1));

        setPreferredSize(new Dimension(24 * TileMap.TILE_SIZE, 16 * TileMap.TILE_SIZE));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        addKeyListener(keyHandler);

        loadLevel(currentLevelIndex);
    }

    // ── Observer ───────────────────────────────────────────────────────────

    /**
     * Receives {@link GameEventType#LEVEL_COMPLETE} from the Door (Subject).
     * Records elapsed time as a {@link HighScore} via the Singleton {@link ScoreManager}.
     */
    @Override
    public void onGameEvent(GameEvent event) {
        if (event.getType() == GameEventType.LEVEL_COMPLETE) {
            lastRunTimeMs = System.currentTimeMillis() - roundStartTime;
            HighScore hs  = new HighScore("Player", lastRunTimeMs);
            scoreManager.addScore(hs);
            LOGGER.info("Level " + (currentLevelIndex + 1) + " complete — " + hs);
        }
    }

    // ── Level loading ──────────────────────────────────────────────────────

    /**
     * Rebuilds the entire game world for the level at {@code index}.
     * Every object (tileMap, player, door, barriers) is replaced.
     *
     * @param index 0-based index into {@link #ALL_LEVELS}
     */
    private void loadLevel(int index) {
        currentLevelIndex = index;
        pyramidrunner.world.Level level = ALL_LEVELS[index];

        tileMap  = new TileMap(level.getMap());
        player   = new Player(PLAYER_START_X, PLAYER_START_Y, keyHandler, tileMap);
        door     = new Door(level.getDoorCol(), level.getDoorRow());
        barriers = new EntityGroup<>();
        level.setupBarriers(barriers, TileMap.TILE_SIZE);

        door.addListener(this);      // Observer: subscribe to door events
        gameOver       = false;
        roundStartTime = System.currentTimeMillis();

        LOGGER.info("Loaded Level " + level.getLevelNumber()
                    + " – " + level.getLevelName()
                    + "  (" + barriers.size() + " barriers)");
    }

    // ── Thread ─────────────────────────────────────────────────────────────

    /** Starts the 90-FPS game loop on a dedicated thread. */
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double delta        = 0;
        long   lastTime     = System.nanoTime();

        while (gameThread != null) {
            long now = System.nanoTime();
            delta   += (now - lastTime) / drawInterval;
            lastTime = now;
            if (delta >= 1) { update(); repaint(); delta--; }
        }
    }

    // ── Update ─────────────────────────────────────────────────────────────

    private void update() {

        // ESC → return to menu from anywhere
        if (keyHandler.escape) {
            gameThread = null;
            returnToMenu.run();
            return;
        }

        // Game over: R restarts the same level
        if (gameOver && keyHandler.restart) {
            loadLevel(currentLevelIndex);
            return;
        }

        // Level complete: R advances (or finishes the game)
        if (door.isTriggered() && keyHandler.restart) {
            int next = currentLevelIndex + 1;
            if (next >= ALL_LEVELS.length) {
                gameThread = null;
                returnToMenu.run();
            } else {
                loadLevel(next);
            }
            return;
        }

        if (gameOver || door.isTriggered()) return;

        // Normal frame ─────────────────────────────────────────────────────
        barriers.updateAll();
        LOGGER.fine("Player: x=" + player.getX() + " y=" + player.getY());
        player.update();

        // Lambda: check if any barrier hit the player
        boolean hit = barriers.stream().anyMatch(b -> b.hitsPlayer(player));
        if (hit) {
            try {
                throw new PlayerCollisionException(player.getX(), player.getY());
            } catch (PlayerCollisionException e) {
                LOGGER.log(java.util.logging.Level.SEVERE,
                           "Barrier collision: " + e.getMessage());
                gameOver = true;
            }
        }

        if (!door.isTriggered()) {
            door.checkPlayer(player);
        }
    }

    // ── Rendering ──────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        tileMap.draw(g2);
        barriers.drawAll(g2);
        door.draw(g2);
        player.draw(g2);

        drawHud(g2);

        if (gameOver) {
            drawOverlay(g2, Color.RED,
                    "GAME OVER",
                    "R — try again     ESC — menu");
        } else if (door.isTriggered()) {
            boolean last    = (currentLevelIndex == ALL_LEVELS.length - 1);
            String  timeStr = String.format("%.1f s", lastRunTimeMs / 1000.0);
            String  subMsg  = last
                    ? "YOU WIN ALL LEVELS!   R — back to menu"
                    : "Time: " + timeStr + "   R — Level " + (currentLevelIndex + 2) + "   ESC — menu";
            drawOverlay(g2, Color.YELLOW,
                    "LEVEL " + (currentLevelIndex + 1) + " COMPLETE!",
                    subMsg);
        }

        g2.dispose();
    }

    /** Small top-left HUD: level name + ESC hint. */
    private void drawHud(Graphics2D g2) {
        pyramidrunner.world.Level lv = ALL_LEVELS[currentLevelIndex];
        g2.setColor(new Color(255, 255, 255, 180));
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Level " + lv.getLevelNumber() + " – " + lv.getLevelName(), 10, 22);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.drawString("ESC = menu", 10, 40);
    }

    /** Semi-transparent centred overlay with title and subtitle. */
    private void drawOverlay(Graphics2D g2, Color color, String title, String subtitle) {
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(color);
        g2.setFont(new Font("Arial", Font.BOLD, 52));
        int tx = (getWidth() - g2.getFontMetrics().stringWidth(title)) / 2;
        g2.drawString(title, tx, getHeight() / 2 - 20);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 22));
        int sx = (getWidth() - g2.getFontMetrics().stringWidth(subtitle)) / 2;
        g2.drawString(subtitle, sx, getHeight() / 2 + 30);
    }
}
