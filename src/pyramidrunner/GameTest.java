package pyramidrunner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import pyramidrunner.core.PlayerCollisionException;
import pyramidrunner.entity.Barrier;
import pyramidrunner.entity.Door;
import pyramidrunner.entity.Player;
import pyramidrunner.input.KeyHandler;
import pyramidrunner.world.TileMap;

public class GameTest {

    private TileMap    tileMap;
    private KeyHandler keyHandler;
    private Player     player;

    @BeforeEach
    void setUp() {
        tileMap    = new TileMap();
        keyHandler = new KeyHandler();
        player     = new Player(TileMap.TILE_SIZE + 2, TileMap.TILE_SIZE + 2, keyHandler, tileMap);
    }

    // ── Test 1 ──────────────────────────────────────────────────────────────
    /** TileMap correctly marks walls solid and floor not solid. */
    @Test
    void testTileMapSolidity() {
        assertTrue(tileMap.isSolid(0, 0),   "Border wall should be solid");
        assertFalse(tileMap.isSolid(1, 1),  "Empty space should not be solid");
        assertTrue(tileMap.isSolid(-1, 0),  "Out of bounds should be solid");
    }

    // ── Test 2 ──────────────────────────────────────────────────────────────
    /** move() with skipCollision=true always changes coordinates. */
    @Test
    void testPlayerMoveSkipCollision() {
        int startX = player.getX();
        int startY = player.getY();
        player.move(100, 100, true);
        assertEquals(startX + 100, player.getX(), "X should change by 100");
        assertEquals(startY + 100, player.getY(), "Y should change by 100");
    }

    // ── Test 3 ──────────────────────────────────────────────────────────────
    /** Player cannot walk through the left border wall. */
    @Test
    void testPlayerCollisionWithWall() {
        player.move(-1000, 0);
        assertTrue(player.getX() >= TileMap.TILE_SIZE,
                   "Player should not pass through the left wall");
    }

    // ── Test 4 ──────────────────────────────────────────────────────────────
    /** Door triggers when the player overlaps it. */
    @Test
    void testDoorTriggersOnPlayerContact() {
        Door door = new Door(5, 5);
        assertFalse(door.isTriggered(), "Door should not be triggered initially");

        player.move(5 * TileMap.TILE_SIZE - player.getX(),
                    5 * TileMap.TILE_SIZE - player.getY(), true);
        door.checkPlayer(player);

        assertTrue(door.isTriggered(), "Door should trigger after player contact");
    }

    // ── Test 5 ──────────────────────────────────────────────────────────────
    /** Door.reset() clears the triggered flag. */
    @Test
    void testDoorReset() {
        Door door = new Door(5, 5);
        player.move(5 * TileMap.TILE_SIZE - player.getX(),
                    5 * TileMap.TILE_SIZE - player.getY(), true);
        door.checkPlayer(player);
        assertTrue(door.isTriggered(), "Door should be triggered before reset");

        door.reset();
        assertFalse(door.isTriggered(), "Door should not be triggered after reset");
    }

    // ── Test 6 ──────────────────────────────────────────────────────────────
    /** Barrier detects overlap with the player. */
    @Test
    void testBarrierHitsPlayer() {
        // Place barrier exactly on the player's position
        Barrier barrier = new Barrier(player.getX(), player.getY(), 20, 36, 500, 0f);
        assertTrue(barrier.hitsPlayer(player), "Barrier should detect player overlap");
    }

    // ── Test 7 ──────────────────────────────────────────────────────────────
    /** Barrier does not hit player when far away. */
    @Test
    void testBarrierMissesPlayer() {
        Barrier barrier = new Barrier(800, 800, 20, 36, 900, 0f);
        assertFalse(barrier.hitsPlayer(player), "Barrier far away should not hit player");
    }

    // ── Test 8 ──────────────────────────────────────────────────────────────
    /** PlayerCollisionException carries correct coordinates. */
    @Test
    void testPlayerCollisionExceptionCoordinates() {
        PlayerCollisionException ex = new PlayerCollisionException(100, 200);
        assertEquals(100, ex.getPlayerX(), "Exception should store player X");
        assertEquals(200, ex.getPlayerY(), "Exception should store player Y");
        assertTrue(ex.getMessage().contains("100"), "Message should contain X");
        assertTrue(ex.getMessage().contains("200"), "Message should contain Y");
    }
}
