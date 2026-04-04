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

public class GamePanel extends JPanel implements Runnable {

    private static final int FPS = 90;

    private Thread gameThread;
    private final KeyHandler keyHandler = new KeyHandler();
    private final TileMap tileMap;
    private final Player player;
    private final Door door;

    public GamePanel() {
        tileMap = new TileMap();
        // Player starts bottom-left, just above the floor (row 14, col 1)
        player = new Player(TileMap.TILE_SIZE + 2, 14 * TileMap.TILE_SIZE - 44, keyHandler, tileMap);
        // Door at top-right corner (row 1, col 22)
        door   = new Door(22, 1);

        setPreferredSize(new Dimension(tileMap.getPixelWidth(), tileMap.getPixelHeight()));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        addKeyListener(keyHandler);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long now = System.nanoTime();
            delta += (now - lastTime) / drawInterval;
            lastTime = now;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        if (!door.isTriggered()) {
            player.update();
        }
        door.checkPlayer(player);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        tileMap.draw(g2);
        door.draw(g2);
        player.draw(g2);

        if (door.isTriggered()) {
            // Semi-transparent overlay
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, getWidth(), getHeight());
            // "Level Complete" text
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            String msg = "LEVEL COMPLETE!";
            int msgX = (getWidth()  - g2.getFontMetrics().stringWidth(msg)) / 2;
            int msgY = getHeight() / 2;
            g2.drawString(msg, msgX, msgY);
        }

        g2.dispose();
    }
}
