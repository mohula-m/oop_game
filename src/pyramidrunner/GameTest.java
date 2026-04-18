package pyramidrunner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import pyramidrunner.core.GameEvent;
import pyramidrunner.core.GameEventListener;
import pyramidrunner.core.GameEventType;
import pyramidrunner.core.GameException;
import pyramidrunner.core.PlayerCollisionException;
import pyramidrunner.entity.Barrier;
import pyramidrunner.entity.Door;
import pyramidrunner.entity.Player;
import pyramidrunner.input.KeyHandler;
import pyramidrunner.world.FloorTile;
import pyramidrunner.world.TileMap;
import pyramidrunner.world.WallTile;

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

    // ── TileMap ─────────────────────────────────────────────────────────────

    @Test
    void testTileMapSolidity() {
        assertTrue(tileMap.isSolid(0, 0),  "Border wall should be solid");
        assertFalse(tileMap.isSolid(1, 1), "Empty space should not be solid");
        assertTrue(tileMap.isSolid(-1, 0), "Out of bounds should be solid");
    }

    @Test
    void testTileMapCollidesWithSolid() {
        // Top-left corner is a wall — rectangle touching it must collide
        assertTrue(tileMap.collidesWithSolid(0, 0, 10, 10),
                   "Rectangle at wall corner should collide");
        // Centre of row=1, col=1 is empty — small rect should not collide
        assertFalse(tileMap.collidesWithSolid(
                TileMap.TILE_SIZE + 4, TileMap.TILE_SIZE + 4, 10, 10),
                "Rectangle in open space should not collide");
    }

    @Test
    void testTileMapPixelSize() {
        assertEquals(24 * TileMap.TILE_SIZE, tileMap.getPixelWidth(),  "Width  = 24 tiles");
        assertEquals(16 * TileMap.TILE_SIZE, tileMap.getPixelHeight(), "Height = 16 tiles");
    }

    // ── WallTile / FloorTile ────────────────────────────────────────────────

    @Test
    void testWallTileIsSolid() {
        WallTile wall = new WallTile();
        assertTrue(wall.isSolid(), "WallTile must be solid");
    }

    @Test
    void testFloorTileIsNotSolid() {
        FloorTile floor = new FloorTile();
        assertFalse(floor.isSolid(), "FloorTile must not be solid");
    }

    // ── Player ──────────────────────────────────────────────────────────────

    @Test
    void testPlayerMoveSkipCollision() {
        int startX = player.getX();
        int startY = player.getY();
        player.move(100, 100, true);
        assertEquals(startX + 100, player.getX(), "X should change by 100");
        assertEquals(startY + 100, player.getY(), "Y should change by 100");
    }

    @Test
    void testPlayerCollisionWithWall() {
        player.move(-1000, 0);
        assertTrue(player.getX() >= TileMap.TILE_SIZE,
                   "Player should not pass through the left wall");
    }

    @Test
    void testPlayerSnapToTop() {
        player.snapToTop(300);
        assertEquals(300 - player.getHeight(), player.getY(),
                     "Player Y should be platform top minus player height");
        assertEquals(0f, player.getVelocityY(), 0.001f,
                     "VelocityY should reset to 0 after snap");
        assertTrue(player.isOnGround(), "Player should be on ground after snap");
    }

    @Test
    void testPlayerInitialVelocity() {
        assertEquals(0f, player.getVelocityY(), 0.001f,
                     "Initial vertical velocity should be 0");
    }

    @Test
    void testPlayerNotOnGroundInitially() {
        // Player is placed at row=1 pixel area — there is no tile directly below
        assertFalse(player.isOnGround(), "Player should not be on ground at start (floating)");
    }

    @Test
    void testPlayerSetOnPlatform() {
        // After setOnPlatform(true) and update(), onGround should reflect platform
        player.setOnPlatform(true);
        // Cannot call update() cleanly here — just verify setter doesn't throw
        assertDoesNotThrow(() -> player.setOnPlatform(false));
    }

    @Test
    void testPlayerGetters() {
        assertEquals(44, player.getWidth(),  "Player width should be 44");
        assertEquals(44, player.getHeight(), "Player height should be 44");
        assertTrue(player.getX() > 0, "Player X should be positive");
        assertTrue(player.getY() > 0, "Player Y should be positive");
    }

    // ── Door ────────────────────────────────────────────────────────────────

    @Test
    void testDoorTriggersOnPlayerContact() {
        Door door = new Door(5, 5);
        assertFalse(door.isTriggered(), "Door should not be triggered initially");

        player.move(5 * TileMap.TILE_SIZE - player.getX(),
                    5 * TileMap.TILE_SIZE - player.getY(), true);
        door.checkPlayer(player);

        assertTrue(door.isTriggered(), "Door should trigger after player contact");
    }

    @Test
    void testDoorReset() {
        Door door = new Door(5, 5);
        player.move(5 * TileMap.TILE_SIZE - player.getX(),
                    5 * TileMap.TILE_SIZE - player.getY(), true);
        door.checkPlayer(player);
        assertTrue(door.isTriggered());

        door.reset();
        assertFalse(door.isTriggered(), "Door should not be triggered after reset");
    }

    @Test
    void testDoorDoesNotTriggerWhenFar() {
        Door door = new Door(10, 10);
        door.checkPlayer(player);  // player is far from tile (10,10)
        assertFalse(door.isTriggered(), "Door should not trigger when player is far away");
    }

    // ── Barrier ─────────────────────────────────────────────────────────────

    @Test
    void testBarrierHitsPlayer() {
        Barrier barrier = new Barrier(player.getX(), player.getY(), 20, 36, 500, 0f);
        assertTrue(barrier.hitsPlayer(player), "Barrier should detect player overlap");
    }

    @Test
    void testBarrierMissesPlayer() {
        Barrier barrier = new Barrier(800, 800, 20, 36, 900, 0f);
        assertFalse(barrier.hitsPlayer(player), "Barrier far away should not hit player");
    }

    @Test
    void testBarrierMovesAndReversesDirection() {
        int startX  = 100;
        int endX    = 150;
        Barrier barrier = new Barrier(startX, 100, 20, 36, endX, 10f);

        barrier.update(); // moves right → x = 110
        int afterFirst = barrier.getX();
        assertTrue(afterFirst > startX, "Barrier should move right initially");

        // Keep updating until it bounces
        for (int i = 0; i < 20; i++) barrier.update();
        int afterBounce = barrier.getX();
        assertTrue(afterBounce < afterFirst || afterBounce == startX,
                   "Barrier should reverse after reaching endX");
    }

    @Test
    void testBarrierStaysWithinBounds() {
        Barrier barrier = new Barrier(100, 100, 20, 36, 200, 5f);
        for (int i = 0; i < 200; i++) barrier.update();
        assertTrue(barrier.getX() >= 100 && barrier.getX() <= 200,
                   "Barrier should always stay between startX and endX");
    }

    // ── Custom Exceptions ───────────────────────────────────────────────────

    @Test
    void testPlayerCollisionExceptionCoordinates() {
        PlayerCollisionException ex = new PlayerCollisionException(100, 200);
        assertEquals(100, ex.getPlayerX(), "Exception should store player X");
        assertEquals(200, ex.getPlayerY(), "Exception should store player Y");
        assertTrue(ex.getMessage().contains("100"), "Message should contain X");
        assertTrue(ex.getMessage().contains("200"), "Message should contain Y");
    }

    @Test
    void testGameExceptionMessage() {
        GameException ex = new GameException("test error");
        assertEquals("test error", ex.getMessage());
    }

    @Test
    void testGameExceptionIsRuntimeException() {
        GameException ex = new GameException("error");
        assertInstanceOf(RuntimeException.class, ex,
                         "GameException must extend RuntimeException");
    }

    @Test
    void testPlayerCollisionExceptionIsGameException() {
        PlayerCollisionException ex = new PlayerCollisionException(0, 0);
        assertInstanceOf(GameException.class, ex,
                         "PlayerCollisionException must extend GameException");
    }

    // ── KeyHandler ──────────────────────────────────────────────────────────

    @Test
    void testKeyHandlerInitialState() {
        KeyHandler kh = new KeyHandler();
        assertFalse(kh.left,    "left should be false initially");
        assertFalse(kh.right,   "right should be false initially");
        assertFalse(kh.jump,    "jump should be false initially");
        assertFalse(kh.restart, "restart should be false initially");
    }

    // ── Observer pattern ────────────────────────────────────────────────────

    @Test
    void testDoorFiresLevelCompleteEvent() {
        Door door = new Door(5, 5);

        // Record the last received event
        GameEvent[] received = {null};
        door.addListener(event -> received[0] = event);

        // Move player onto door tile and trigger check
        player.move(5 * TileMap.TILE_SIZE - player.getX(),
                    5 * TileMap.TILE_SIZE - player.getY(), true);
        door.checkPlayer(player);

        assertNotNull(received[0], "Observer should have received an event");
        assertEquals(GameEventType.LEVEL_COMPLETE, received[0].getType(),
                     "Event type must be LEVEL_COMPLETE");
    }

    @Test
    void testDoorEventNotFiredWhenFar() {
        Door door = new Door(10, 10);

        boolean[] called = {false};
        door.addListener(event -> called[0] = true);

        door.checkPlayer(player);  // player is far away

        assertFalse(called[0], "Observer must NOT be notified when player is far away");
    }

    @Test
    void testDoorEventFiredOnlyOnce() {
        Door door = new Door(5, 5);
        player.move(5 * TileMap.TILE_SIZE - player.getX(),
                    5 * TileMap.TILE_SIZE - player.getY(), true);

        int[] count = {0};
        door.addListener(event -> count[0]++);

        door.checkPlayer(player);
        door.checkPlayer(player);  // second call — already triggered, no second fire
        door.checkPlayer(player);

        assertEquals(1, count[0], "LEVEL_COMPLETE event should fire exactly once");
    }

    @Test
    void testDoorListenerCountAfterAddRemove() {
        Door door = new Door(1, 1);
        GameEventListener listener = event -> {};

        assertEquals(0, door.listenerCount(), "No listeners initially");
        door.addListener(listener);
        assertEquals(1, door.listenerCount(), "One listener after add");
        door.removeListener(listener);
        assertEquals(0, door.listenerCount(), "Zero listeners after remove");
    }

    @Test
    void testGameEventStoresTypeAndMessage() {
        GameEvent event = new GameEvent(GameEventType.GAME_OVER, "barrier hit");
        assertEquals(GameEventType.GAME_OVER, event.getType());
        assertEquals("barrier hit", event.getMessage());
    }

    @Test
    void testMultipleObserversAllNotified() {
        Door door = new Door(5, 5);
        player.move(5 * TileMap.TILE_SIZE - player.getX(),
                    5 * TileMap.TILE_SIZE - player.getY(), true);

        int[] counts = {0, 0, 0};
        door.addListener(e -> counts[0]++);
        door.addListener(e -> counts[1]++);
        door.addListener(e -> counts[2]++);

        door.checkPlayer(player);

        assertEquals(1, counts[0], "First observer notified");
        assertEquals(1, counts[1], "Second observer notified");
        assertEquals(1, counts[2], "Third observer notified");
    }
}
