package pyramidrunner.world;

import pyramidrunner.core.EntityGroup;
import pyramidrunner.entity.Barrier;

/**
 * Level 2 — "Rising".
 *
 * <p>Platforms zigzag across the map.  One barrier patrols the open middle
 * section — players must time their jumps carefully.</p>
 *
 * <p>Inheritance: extends {@link Level}.</p>
 */
public class Level2 extends Level {

    // Zigzag layout: left → right → left → right, ending at top-right door.
    private static final int[][] MAP = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},  // row  0 — ceiling
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  1
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  2 — cols 18-21
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,1},  // row  3
        {1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,1},  // row  4 — cols 10-14
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  5
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  6 — barrier zone
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,1},  // row  7 — cols 16-20
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  8
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row  9
        {1,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1},  // row 10 — cols  7-11
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row 11
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row 12
        {1,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row 13 — cols  2-6
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},  // row 14
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},  // row 15 — floor
    };

    @Override public int    getLevelNumber()  { return 2; }
    @Override public String getLevelName()    { return "Rising"; }
    @Override public String getDescription()  { return "1 barrier — watch your step"; }
    @Override public int[][] getMap()         { return MAP; }
    @Override public int    getDoorCol()      { return 22; }
    @Override public int    getDoorRow()      { return 1; }

    /**
     * One barrier patrols the open zone between rows 6-7,
     * forcing the player to time their crossing from the left platforms
     * to the right platforms.
     */
    @Override
    public void setupBarriers(EntityGroup<Barrier> group, int ts) {
        // Patrols the wide open space in the middle of the map
        group.add(new Barrier(3 * ts, 6 * ts - 36, 20, 36, 19 * ts, 3.0f));
    }
}
