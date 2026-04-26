package pyramidrunner.world;

import pyramidrunner.core.EntityGroup;
import pyramidrunner.entity.Barrier;

/**
 * Abstract base for a game level.
 *
 * Each concrete level (Level1, Level2, Level3) extends this and provides
 * its own tile map, door position, and barrier setup.
 * GamePanel only works with Level references - it never knows which
 * specific level it's running (polymorphism).
 */
public abstract class Level {

    /** @return 1-based level number shown to the player */
    public abstract int getLevelNumber();

    /** @return short name like "Tutorial" or "Expert" */
    public abstract String getLevelName();

    /** @return one-line description shown in the menu */
    public abstract String getDescription();

    /**
     * @return the tile grid: 1 = solid wall, 0 = passable space
     */
    public abstract int[][] getMap();

    /** @return column of the EXIT door (tile coordinates) */
    public abstract int getDoorCol();

    /** @return row of the EXIT door (tile coordinates) */
    public abstract int getDoorRow();

    /**
     * Fills the barrier group with this level's obstacles.
     * Called by GamePanel when the level loads.
     *
     * @param group    empty group to populate
     * @param tileSize pixel size of one tile
     */
    public abstract void setupBarriers(EntityGroup<Barrier> group, int tileSize);
}
