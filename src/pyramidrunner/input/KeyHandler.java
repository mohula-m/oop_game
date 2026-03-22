package pyramidrunner.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean up, down, left, right;

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP    -> up    = true;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN  -> down  = true;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT  -> left  = true;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP    -> up    = false;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN  -> down  = false;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT  -> left  = false;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
