package pyramidrunner.entity;

import pyramidrunner.world.TileMap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

// Inheritance: Door extends Entity
public class Door extends Entity {

    private static final Color COLOR_DOOR   = new Color(255, 195, 0);
    private static final Color COLOR_BORDER = new Color(180, 130, 0);

    private boolean triggered = false;

    // Place door by tile coordinates — Door(col, row)
    public Door(int tileCol, int tileRow) {
        super(tileCol * TileMap.TILE_SIZE, tileRow * TileMap.TILE_SIZE,
              TileMap.TILE_SIZE, TileMap.TILE_SIZE);
    }

    // Call every frame — checks if player touched the door
    public void checkPlayer(Player player) {
        if (!triggered && intersects(player)) {
            triggered = true;
        }
    }

    public boolean isTriggered() {
        return triggered;
    }

    @Override
    public void update() {
        // Door is static — nothing to update
    }

    @Override
    public void draw(Graphics2D g) {
        draw(g, COLOR_DOOR);
    }

    @Override
    public void draw(Graphics2D g, Color color) {
        // Fill
        g.setColor(color);
        g.fillRect(x, y, width, height);
        // Border
        g.setColor(COLOR_BORDER);
        g.drawRect(x, y, width - 1, height - 1);
        g.drawRect(x + 2, y + 2, width - 5, height - 5);
        // Label
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("EXIT", x + 4, y + height / 2 + 6);
    }
}
