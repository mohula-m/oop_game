package pyramidrunner.world;

import java.awt.Color;
import java.awt.Graphics2D;

public class Tile {

    private final boolean solid;
    private final Color color;

    public Tile(boolean solid) {
        this.solid = solid;
        this.color = solid ? new Color(70, 45, 15) : new Color(140, 90, 40);
    }

    public boolean isSolid() {
        return solid;
    }

    public void draw(Graphics2D g, int x, int y, int size) {
        g.setColor(color);
        g.fillRect(x, y, size, size);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, size, size);
    }
}
