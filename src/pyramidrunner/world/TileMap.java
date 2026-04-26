package pyramidrunner.world;

import java.awt.Graphics2D;

/**
 * A 24×16 tile-based map.
 *
 * <p>Accepts any {@code int[][]} layout so each {@link Level} subclass
 * can supply its own map at construction time.  The no-arg constructor
 * uses the built-in {@link #DEFAULT_MAP} and keeps existing unit tests
 * working without changes.</p>
 *
 * <p>Composition: TileMap owns the {@code Tile[][]} array — tiles are
 * created here and do not exist outside it.</p>
 */
public class TileMap {

    public static final int TILE_SIZE = 48;

    /**
     * Default map layout — used by the no-arg constructor and by {@link Level3}.
     * {@code 0} = empty (floor tile), {@code 1} = solid (wall tile).
     */
    public static final int[][] DEFAULT_MAP = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},  // row  0 — ceiling
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  1
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  2
        {1,0,0,1,1,1,1,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,1},  // row  3 — high platforms
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1},  // row  4
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  5
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,1},  // row  6
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  7
        {1,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  8
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1},  // row  9
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row 10
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,1},  // row 11
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row 12
        {1,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1},  // row 13
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row 14
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},  // row 15 — floor
    };

    // Composition: TileMap owns Tile[][] — tiles are created and live here only
    private final Tile[][] tiles;
    private final int rows;
    private final int cols;

    /**
     * Creates a TileMap from the built-in {@link #DEFAULT_MAP}.
     * Used by unit tests and as a convenience constructor.
     */
    public TileMap() {
        this(DEFAULT_MAP);
    }

    /**
     * Creates a TileMap from a custom map layout.
     * Used by each {@link Level} subclass to supply its own design.
     *
     * @param map int[][] where 1 = solid wall, 0 = empty space
     */
    public TileMap(int[][] map) {
        this.rows  = map.length;
        this.cols  = map[0].length;
        this.tiles = new Tile[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tiles[r][c] = (map[r][c] == 1) ? new WallTile() : new FloorTile();
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

    /** Returns true if the given pixel rectangle overlaps any solid tile. */
    public boolean collidesWithSolid(int x, int y, int w, int h) {
        int left   = x / TILE_SIZE;
        int right  = (x + w - 1) / TILE_SIZE;
        int top    = y / TILE_SIZE;
        int bottom = (y + h - 1) / TILE_SIZE;
        return isSolid(left, top)  || isSolid(right, top)
            || isSolid(left, bottom) || isSolid(right, bottom);
    }

    public int getPixelWidth()  { return cols * TILE_SIZE; }
    public int getPixelHeight() { return rows * TILE_SIZE; }
}
