package pyramidrunner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import pyramidrunner.entity.Door;
import pyramidrunner.entity.Player;
import pyramidrunner.input.KeyHandler;
import pyramidrunner.world.TileMap;

public class GameTest {

    private TileMap tileMap;
    private KeyHandler keyHandler;
    private Player player;

    @BeforeEach
    void setUp() {
        tileMap = new TileMap();
        keyHandler = new KeyHandler();
        // Place player in open space (col 1, row 1 in pixels)
        player = new Player(TileMap.TILE_SIZE + 2, TileMap.TILE_SIZE + 2, keyHandler, tileMap);
    }

    // Test 1: TileMap — walls are solid, floor is not
    @Test
    void testTileMapSolidity() {

        assertTrue(tileMap.isSolid(0, 0), "Border wall should be solid");

        assertFalse(tileMap.isSolid(1, 1), "Empty space should not be solid");

        assertTrue(tileMap.isSolid(-1, 0), "Out of bounds should be solid");
    }

    // Test 2: Player move with collision skip
    @Test
    void testPlayerMoveSkipCollision() {
        int startX = player.getX();
        int startY = player.getY();
        // Move with skipCollision=true — should always move, even into walls
        player.move(100, 100, true);
        assertEquals(startX + 100, player.getX(), "X should change with skip collision");
        assertEquals(startY + 100, player.getY(), "Y should change with skip collision");
    }

    // Test 3: Player cannot walk through walls
    @Test
    void testPlayerCollisionWithWall() {
        // Move player far to the left — should be blocked by left wall (col=0)
        int startX = player.getX();
        player.move(-1000, 0);
        // Player should NOT have x < TILE_SIZE (wall occupies col 0)
        assertTrue(player.getX() >= TileMap.TILE_SIZE,
                   "Player should not pass through the left wall");
    }

    // Test 4: Door triggers when player overlaps
    @Test
    void testDoorTriggersOnPlayerContact() {
        // Place door at tile (5, 5)
        Door door = new Door(5, 5);
        assertFalse(door.isTriggered(), "Door should not be triggered initially");

        // Move player onto the door (skip collision so we can place freely)
        player.move(5 * TileMap.TILE_SIZE - player.getX(),
                    5 * TileMap.TILE_SIZE - player.getY(), true);

        door.checkPlayer(player);
        assertTrue(door.isTriggered(), "Door should be triggered after player contact");
    }
}
