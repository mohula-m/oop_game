import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GamePanel gamePanel = new GamePanel();

            JFrame window = new JFrame("Pyramid Runner");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);
            window.add(gamePanel);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);

            gamePanel.startGameThread();
        });
    }
}
