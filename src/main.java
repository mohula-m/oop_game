import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Entry point - creates the window and manages the switch between menu and game.
 * showMenu() and startGame() swap the content pane within the same JFrame.
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
     * Replaces the window content with a GamePanel for the chosen level.
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
