import pyramidrunner.entity.Player;
import pyramidrunner.input.KeyHandler;
import pyramidrunner.world.TileMap;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class GamePanel extends JPanel implements Runnable {

    private static final int FPS = 60;

    private Thread gameThread;
    private final KeyHandler keyHandler = new KeyHandler();
    private final TileMap tileMap;
    private final Player player;

    public GamePanel() {
        tileMap = new TileMap();
        // Start player just above the floor (row 10, col 1)
        player  = new Player(TileMap.TILE_SIZE + 2, 10 * TileMap.TILE_SIZE - 44, keyHandler, tileMap);

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
        player.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        tileMap.draw(g2);
        player.draw(g2);

        g2.dispose();
    }
}
