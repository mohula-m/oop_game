package pyramidrunner.world;

import java.awt.Graphics2D;

public class TileMap {

    public static final int TILE_SIZE = 48;

    // 0 = floor, 1 = wall
    private static final int[][] MAP = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1},
        {1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
        {1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
    };

    private final Tile[][] tiles;
    private final int rows;
    private final int cols;

    public TileMap() {
        this.rows = MAP.length;
        this.cols = MAP[0].length;
        this.tiles = new Tile[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tiles[r][c] = new Tile(MAP[r][c] == 1);
            }
        }
    }

    public void draw(Graphics2D g) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tiles[r][c].draw(g, c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE);
            }
        }
    }

    public boolean isSolid(int col, int row) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return true;
        return tiles[row][col].isSolid();
    }

    // Returns true if the given rectangle overlaps any solid tile
    public boolean collidesWithSolid(int x, int y, int w, int h) {
        int left   = x / TILE_SIZE;
        int right  = (x + w - 1) / TILE_SIZE;
        int top    = y / TILE_SIZE;
        int bottom = (y + h - 1) / TILE_SIZE;
        return isSolid(left, top) || isSolid(right, top)
            || isSolid(left, bottom) || isSolid(right, bottom);
    }

    public int getPixelWidth()  { return cols * TILE_SIZE; }
    public int getPixelHeight() { return rows * TILE_SIZE; }
}
