package pyramidrunner.world;

import java.awt.Color;
import java.awt.Graphics2D;

// Inheritance: WallTile extends Tile
public class WallTile extends Tile {

    private static final Color FILL   = new Color(70, 45, 15);
    private static final Color BORDER = Color.BLACK;

    public WallTile() {
        super(true); // solid = true
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public void draw(Graphics2D g, int x, int y, int size) {
        g.setColor(FILL);
        g.fillRect(x, y, size, size);
        g.setColor(BORDER);
        g.drawRect(x, y, size, size);
    }
}
