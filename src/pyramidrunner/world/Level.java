package pyramidrunner.world;

import pyramidrunner.core.EntityGroup;
import pyramidrunner.entity.Barrier;

/**
 * Abstract base class for a game level.
 *
 * <p><b>Inheritance / Generalization:</b> each concrete level
 * ({@link Level1}, {@link Level2}, {@link Level3}) extends this class and
 * provides its own tile layout, door position, and barrier configuration.
 * {@code GamePanel} works against {@code Level} (the abstraction), never
 * against the concrete subclasses — run-time polymorphism in action.</p>
 */
public abstract class Level {

    /**
     * @return 1-based level number shown to the player
     */
    public abstract int getLevelNumber();

    /**
     * @return short display name (e.g. "Tutorial")
     */
    public abstract String getLevelName();

    /**
     * @return brief description shown on the menu
     */
    public abstract String getDescription();

    /**
     * @return the tile layout: {@code 1} = solid wall, {@code 0} = empty space
     */
    public abstract int[][] getMap();

    /** @return column of the exit door (tile coordinates) */
    public abstract int getDoorCol();

    /** @return row of the exit door (tile coordinates) */
    public abstract int getDoorRow();

    /**
     * Populates {@code group} with the barriers for this level.
     * Called by {@code GamePanel} when loading the level.
     *
     * @param group    empty barrier group to fill
     * @param tileSize pixel size of one tile ({@link TileMap#TILE_SIZE})
     */
    public abstract void setupBarriers(EntityGroup<Barrier> group, int tileSize);
}
