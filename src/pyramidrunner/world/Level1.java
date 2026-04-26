package pyramidrunner.world;

import pyramidrunner.core.EntityGroup;
import pyramidrunner.entity.Barrier;

/**
 * Level 1 — "Tutorial".
 *
 * <p>A simple ascending staircase from the bottom-left to the top-right.
 * No barriers — the player gets used to jumping mechanics.</p>
 *
 * <p>Inheritance: extends {@link Level}.</p>
 */
public class Level1 extends Level {

    // 0 = empty, 1 = solid  (24 cols × 16 rows)
    // Platforms form a clean zigzag staircase leading to the door at (22, 1).
    private static final int[][] MAP = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},  // row  0 — ceiling
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  1
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  2
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,1},  // row  3 — cols 19-21
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  4
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,1},  // row  5 — cols 15-18
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  6
        {1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,1},  // row  7 — cols 11-14
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  8
        {1,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  9 — cols  7-10
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row 10
        {1,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row 11 — cols  4-7
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row 12
        {1,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row 13 — cols  2-5
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row 14
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},  // row 15 — floor
    };

    @Override public int    getLevelNumber()  { return 1; }
    @Override public String getLevelName()    { return "Tutorial"; }
    @Override public String getDescription()  { return "No barriers — learn the ropes"; }
    @Override public int[][] getMap()         { return MAP; }
    @Override public int    getDoorCol()      { return 22; }
    @Override public int    getDoorRow()      { return 1; }

    /** Level 1 has no barriers. */
    @Override
    public void setupBarriers(EntityGroup<Barrier> group, int ts) {
        // intentionally empty — no barriers on the tutorial level
    }
}
