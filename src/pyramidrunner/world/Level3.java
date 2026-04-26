package pyramidrunner.world;

import pyramidrunner.core.EntityGroup;
import pyramidrunner.entity.Barrier;

/**
 * Level 3 — "Expert".
 *
 * <p>Uses the original Pyramid Runner map with four barriers patrolling
 * at different heights.  The barriers move faster than in earlier levels.</p>
 *
 * <p>Inheritance: extends {@link Level}.</p>
 */
public class Level3 extends Level {

    @Override public int    getLevelNumber()  { return 3; }
    @Override public String getLevelName()    { return "Expert"; }
    @Override public String getDescription()  { return "4 barriers — good luck!"; }
    @Override public int[][] getMap()         { return TileMap.DEFAULT_MAP; }
    @Override public int    getDoorCol()      { return 22; }
    @Override public int    getDoorRow()      { return 1; }

    /**
     * Four barriers at different heights, each with a distinct patrol range
     * and speed, creating a gauntlet the player must navigate.
     */
    @Override
    public void setupBarriers(EntityGroup<Barrier> group, int ts) {
        group.add(new Barrier(5  * ts, 4  * ts - 36, 20, 36, 20 * ts, 2.5f));
        group.add(new Barrier(3  * ts, 8  * ts - 36, 20, 36, 18 * ts, 2.0f));
        group.add(new Barrier(2  * ts, 12 * ts - 36, 16, 36, 14 * ts, 3.5f));
        group.add(new Barrier(3  * ts, 10 * ts - 36, 20, 36, 19 * ts, 4.0f));
    }
}
