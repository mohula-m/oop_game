import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Application entry point.
 *
 * <p>Creates the main window, shows {@link MenuPanel} initially, and
 * switches to {@link GamePanel} when the player selects a level.
 * When the game ends (ESC or all levels complete), it swaps back to
 * the menu — all within the same {@link JFrame}.</p>
 */
public class Main {

    private static JFrame window;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            window = new JFrame("Pyramid Runner");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);

            showMenu();

            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }

    /** Replaces the window content with the level-selection menu. */
    public static void showMenu() {
        MenuPanel menu = new MenuPanel(Main::startGame);
        window.setContentPane(menu);
        window.pack();
        window.setLocationRelativeTo(null);
        // Request focus so keyboard events land on the panel immediately
        SwingUtilities.invokeLater(menu::requestFocusInWindow);
    }

    /**
     * Replaces the window content with a {@link GamePanel} starting at
     * the chosen level, then kicks off the game loop.
     *
     * @param level 1-based level number (1 = Tutorial, 2 = Rising, 3 = Expert)
     */
    public static void startGame(int level) {
        GamePanel game = new GamePanel(level, Main::showMenu);
        window.setContentPane(game);
        window.pack();
        window.setLocationRelativeTo(null);
        game.startGameThread();
        SwingUtilities.invokeLater(game::requestFocusInWindow);
    }
}
