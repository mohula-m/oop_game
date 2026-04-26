import pyramidrunner.world.Level1;
import pyramidrunner.world.Level2;
import pyramidrunner.world.Level3;
import pyramidrunner.world.Level;
import pyramidrunner.world.TileMap;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;

/**
 * Start-screen panel — lets the player choose which level to play.
 *
 * <p>Press <b>1</b>, <b>2</b>, or <b>3</b> to launch the corresponding level.
 * When a key is pressed, {@link #onLevelSelected} is called with the chosen
 * level number and the main window replaces this panel with a {@link GamePanel}.</p>
 */
public class MenuPanel extends JPanel implements KeyListener {

    private static final int W = 24 * TileMap.TILE_SIZE;
    private static final int H = 16 * TileMap.TILE_SIZE;

    private static final Color BG         = new Color(20,  20,  35);
    private static final Color TITLE_COL  = new Color(255, 195,   0);
    private static final Color KEY_COL    = new Color(255, 100,  80);
    private static final Color DESC_COL   = new Color(200, 200, 200);
    private static final Color HINT_COL   = new Color(120, 120, 120);

    /** Called with the chosen level number (1, 2, or 3) when the player presses a key. */
    private final Consumer<Integer> onLevelSelected;

    /** Level metadata used to render the descriptions. */
    private final Level[] levels = { new Level1(), new Level2(), new Level3() };

    /**
     * @param onLevelSelected callback invoked with the selected level number
     */
    public MenuPanel(Consumer<Integer> onLevelSelected) {
        this.onLevelSelected = onLevelSelected;
        setPreferredSize(new Dimension(W, H));
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2.setColor(BG);
        g2.fillRect(0, 0, W, H);

        // Title
        g2.setColor(TITLE_COL);
        g2.setFont(new Font("Arial", Font.BOLD, 72));
        String title = "PYRAMID RUNNER";
        int tx = (W - g2.getFontMetrics().stringWidth(title)) / 2;
        g2.drawString(title, tx, H / 2 - 160);

        // Subtitle
        g2.setColor(DESC_COL);
        g2.setFont(new Font("Arial", Font.PLAIN, 26));
        String sub = "Select a level to begin";
        int sx = (W - g2.getFontMetrics().stringWidth(sub)) / 2;
        g2.drawString(sub, sx, H / 2 - 100);

        // Level entries
        int startY = H / 2 - 40;
        int lineH  = 60;
        for (int i = 0; i < levels.length; i++) {
            Level lv = levels[i];
            int rowY = startY + i * lineH;

            // Key label  [1]
            g2.setColor(KEY_COL);
            g2.setFont(new Font("Monospaced", Font.BOLD, 32));
            String key = "[" + lv.getLevelNumber() + "]";
            g2.drawString(key, W / 2 - 240, rowY);

            // Level name
            g2.setColor(TITLE_COL);
            g2.setFont(new Font("Arial", Font.BOLD, 28));
            g2.drawString("Level " + lv.getLevelNumber() + " – " + lv.getLevelName(),
                          W / 2 - 145, rowY);

            // Description
            g2.setColor(DESC_COL);
            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.drawString(lv.getDescription(), W / 2 - 145, rowY + 25);
        }

        // Bottom hint
        g2.setColor(HINT_COL);
        g2.setFont(new Font("Arial", Font.ITALIC, 18));
        String hint = "W / ↑ / Space = jump     A/D = move     R = restart     ESC = menu";
        int hx = (W - g2.getFontMetrics().stringWidth(hint)) / 2;
        g2.drawString(hint, hx, H - 40);

        g2.dispose();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_1 -> onLevelSelected.accept(1);
            case KeyEvent.VK_2 -> onLevelSelected.accept(2);
            case KeyEvent.VK_3 -> onLevelSelected.accept(3);
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e)    {}
}
