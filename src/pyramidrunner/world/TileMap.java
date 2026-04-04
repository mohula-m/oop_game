package pyramidrunner.world;

import java.awt.Graphics2D;

public class TileMap {

    public static final int TILE_SIZE = 48;

    // 0 = empty, 1 = solid   (24 cols x 16 rows)
    private static final int[][] MAP = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},  // row  0 — ceiling
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  1
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  2
        {1,0,0,1,1,1,1,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,1},  // row  3 — high platforms
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  4
        {1,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,1,1,1,0,0,1},  // row  5 — mid-high platforms
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  6
        {1,0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,1},  // row  7 — mid platforms
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  8
        {1,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,0,1},  // row  9 — mid-low platforms
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row 10
        {1,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,1},  // row 11 — low platforms
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row 12
        {1,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,1,1,0,1},  // row 13 — near-floor platforms
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row 14
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},  // row 15 — floor
    };

    // Composition: TileMap OWNS Tile[][] — tiles are created inside TileMap
    // and do not exist outside of it (their lifecycle is bound to TileMap)
    private final Tile[][] tiles;
    private final int rows;
    private final int cols;

    public TileMap() {
        this.rows = MAP.length;
        this.cols = MAP[0].length;
        this.tiles = new Tile[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tiles[r][c] = (MAP[r][c] == 1) ? new WallTile() : new FloorTile();
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
