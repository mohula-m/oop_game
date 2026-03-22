package pyramidrunner.world;

import java.awt.Color;
import java.awt.Graphics2D;

// Inheritance: FloorTile extends Tile
public class FloorTile extends Tile {

    private static final Color FILL   = new Color(140, 90, 40);
    private static final Color BORDER = Color.BLACK;

    public FloorTile() {
        super(false); // solid = false
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public void draw(Graphics2D g, int x, int y, int size) {
        g.setColor(FILL);
        g.fillRect(x, y, size, size);
        g.setColor(BORDER);
        g.drawRect(x, y, size, size);
    }
}