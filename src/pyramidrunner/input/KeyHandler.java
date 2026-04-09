package pyramidrunner.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean left, right, jump, restart;

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP, KeyEvent.VK_SPACE -> jump    = true;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT                  -> left    = true;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT                 -> right   = true;
            case KeyEvent.VK_R                                    -> restart = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP, KeyEvent.VK_SPACE -> jump    = false;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT                  -> left    = false;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT                 -> right   = false;
            case KeyEvent.VK_R                                    -> restart = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
