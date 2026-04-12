import pyramidrunner.core.PlayerCollisionException;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Main game panel — handles the game loop, update logic, and rendering.
 *
 * <p>Implements {@link Runnable} so the game loop runs on a dedicated thread.</p>
 *
 * <p>Composition: GamePanel creates and owns TileMap, Player, Door,
 * and the list of Barriers.</p>
 */
public class GamePanel extends JPanel implements Runnable {

    /** Target frames per second. */
    private static final int FPS = 90;

    /** Player starting X in pixels. */
    private static final int PLAYER_START_X = TileMap.TILE_SIZE + 2;
    /** Player starting Y in pixels (row 14, just above the floor). */
    private static final int PLAYER_START_Y = 14 * TileMap.TILE_SIZE - 44;

    private Thread gameThread;

    // Composition: all game objects created and owned here
    private final KeyHandler   keyHandler = new KeyHandler();
    private final TileMap      tileMap;
    private       Player       player;
    private final Door         door;
    private final List<Barrier> barriers;

    /** True when a barrier has hit the player. Stops updates, shows overlay. */
    private boolean gameOver = false;

    /**
     * Constructs the game panel and initialises all game objects.
     */
    public GamePanel() {
        tileMap  = new TileMap();
        player   = new Player(PLAYER_START_X, PLAYER_START_Y, keyHandler, tileMap);
        door     = new Door(22, 1);
        barriers = createBarriers();

        setPreferredSize(new Dimension(tileMap.getPixelWidth(), tileMap.getPixelHeight()));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        addKeyListener(keyHandler);
    }

    /**
     * Creates and positions all barriers in the level.
     *
     * @return list of configured Barrier objects
     */
    private List<Barrier> createBarriers() {
        int ts = TileMap.TILE_SIZE;
        List<Barrier> list = new ArrayList<>();

        // Row 4 — tall barrier patrolling the upper section
        list.add(new Barrier(5 * ts, 4 * ts - 36, 20, 36, 16 * ts, 2.5f));

        // Row 8 — wide barrier in the mid section
        list.add(new Barrier(3 * ts, 8 * ts - 36, 20, 36, 18 * ts, 2f));

        // Row 12 — fast narrow barrier near the floor
        list.add(new Barrier(6 * ts, 12 * ts - 36, 16, 36, 14 * ts, 3.5f));

        return list;
    }

    /**
     * Resets the game to its initial state after Game Over.
     * Recreates the player and resets door and gameOver flag.
     */
    private void resetGame() {
        player   = new Player(PLAYER_START_X, PLAYER_START_Y, keyHandler, tileMap);
        door.reset();
        gameOver = false;
    }

    /**
     * Starts the game loop on a new thread.
     */
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
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

    /**
     * Advances the game by one frame.
     *
     * <ul>
     *   <li>If Game Over — waits for R to restart.</li>
     *   <li>Otherwise — updates barriers, player, checks collisions.</li>
     * </ul>
     */
    private void update() {
        // Restart on R when game is over or level is complete
        if ((gameOver || door.isTriggered()) && keyHandler.restart) {
            resetGame();
            return;
        }

        if (gameOver || door.isTriggered()) return;

        // Update all barriers
        for (Barrier b : barriers) {
            b.update();
        }

        // Update player
        player.update();

        // Check barrier collisions — throws PlayerCollisionException on hit
        try {
            for (Barrier b : barriers) {
                if (b.hitsPlayer(player)) {
                    throw new PlayerCollisionException(player.getX(), player.getY());
                }
            }
        } catch (PlayerCollisionException e) {
            System.err.println("Game Over: " + e.getMessage());
            gameOver = true;
        }

        // Check if player reached the exit door
        door.checkPlayer(player);
    }

    /**
     * Renders the current frame:
     * map → barriers → door → player → overlay.
     *
     * @param g Swing graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        tileMap.draw(g2);

        for (Barrier b : barriers) {
            b.draw(g2);
        }

        door.draw(g2);
        player.draw(g2);

        // Game Over overlay
        if (gameOver) {
            drawOverlay(g2, Color.RED, "GAME OVER", "Press R to restart");
        }

        // Level Complete overlay
        if (door.isTriggered()) {
            drawOverlay(g2, Color.YELLOW, "LEVEL COMPLETE!", "Press R to play again");
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
        // Dark background
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Title
        g2.setColor(color);
        g2.setFont(new Font("Arial", Font.BOLD, 52));
        int titleX = (getWidth() - g2.getFontMetrics().stringWidth(title)) / 2;
        g2.drawString(title, titleX, getHeight() / 2 - 20);

        // Subtitle
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 22));
        int subX = (getWidth() - g2.getFontMetrics().stringWidth(subtitle)) / 2;
        g2.drawString(subtitle, subX, getHeight() / 2 + 30);
    }
}
