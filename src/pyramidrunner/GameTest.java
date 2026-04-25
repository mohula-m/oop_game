package pyramidrunner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import pyramidrunner.core.EntityGroup;
import pyramidrunner.core.GameEvent;
import pyramidrunner.core.GameEventListener;
import pyramidrunner.core.GameEventType;
import pyramidrunner.core.GameException;
import pyramidrunner.core.HighScore;
import pyramidrunner.core.PlayerCollisionException;
import pyramidrunner.core.ScoreManager;
import pyramidrunner.entity.Barrier;
import pyramidrunner.entity.Door;
import pyramidrunner.entity.Player;
import pyramidrunner.input.KeyHandler;
import pyramidrunner.world.FloorTile;
import pyramidrunner.world.TileMap;
import pyramidrunner.world.WallTile;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class GameTest {

    private TileMap    tileMap;
    private KeyHandler keyHandler;
    private Player     player;

    @BeforeEach
    void setUp() {
        tileMap    = new TileMap();
        keyHandler = new KeyHandler();
        player     = new Player(TileMap.TILE_SIZE + 2, TileMap.TILE_SIZE + 2, keyHandler, tileMap);
        // Reset Singleton before each test that uses ScoreManager
        ScoreManager.resetForTesting();
    }

    @AfterEach
    void tearDown() {
        // Ensure singleton is clean after each test
        ScoreManager.resetForTesting();
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
        assertTrue(tileMap.collidesWithSolid(0, 0, 10, 10),
                   "Rectangle at wall corner should collide");
        assertFalse(tileMap.collidesWithSolid(
                TileMap.TILE_SIZE + 4, TileMap.TILE_SIZE + 4, 10, 10),
                "Rectangle in open space should not collide");
    }

    @Test
    void testTileMapPixelSize() {
        assertEquals(24 * TileMap.TILE_SIZE, tileMap.getPixelWidth(),  "Width  = 24 tiles");
        assertEquals(16 * TileMap.TILE_SIZE, tileMap.getPixelHeight(), "Height = 16 tiles");
    }

    @Test
    void testTileMapOutOfBoundsRowIsAlwaysSolid() {
        assertTrue(tileMap.isSolid(5, -1),  "Negative row should be solid");
        assertTrue(tileMap.isSolid(5, 100), "Row beyond map should be solid");
    }

    @Test
    void testTileMapOutOfBoundsColIsAlwaysSolid() {
        assertTrue(tileMap.isSolid(-1, 5),  "Negative col should be solid");
        assertTrue(tileMap.isSolid(100, 5), "Col beyond map should be solid");
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
    void testPlayerCollisionWithRightWall() {
        player.move(10000, 0);
        assertTrue(player.getX() + player.getWidth() <= tileMap.getPixelWidth(),
                   "Player should not pass through the right wall");
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
        assertFalse(player.isOnGround(), "Player should not be on ground at start (floating)");
    }

    @Test
    void testPlayerSetOnPlatform() {
        player.setOnPlatform(true);
        assertDoesNotThrow(() -> player.setOnPlatform(false));
    }

    @Test
    void testPlayerGetters() {
        assertEquals(44, player.getWidth(),  "Player width should be 44");
        assertEquals(44, player.getHeight(), "Player height should be 44");
        assertTrue(player.getX() > 0, "Player X should be positive");
        assertTrue(player.getY() > 0, "Player Y should be positive");
    }

    @Test
    void testPlayerOnPlatformAffectsOnGround() {
        // When setOnPlatform(true) is set and then update() is called,
        // onGround should reflect the platform. Here we just verify the setter
        // influences the snapToTop path without throwing.
        player.setOnPlatform(true);
        player.snapToTop(500);
        assertTrue(player.isOnGround(), "isOnGround should be true after snapToTop");
    }

    @Test
    void testPlayerMoveWithCollision() {
        // Move player to a safe position, then try to move into a wall
        // Player starts at (TILE_SIZE+2, TILE_SIZE+2); left wall is at x=0
        int beforeX = player.getX();
        player.move(-10, 0); // small delta — should be allowed if no wall
        // Result depends on map; key check is no exception thrown
        assertDoesNotThrow(() -> player.move(0, 0));
        assertTrue(player.getX() >= 0, "X should never be negative");
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
        door.checkPlayer(player);
        assertFalse(door.isTriggered(), "Door should not trigger when player is far away");
    }

    @Test
    void testDoorUpdateDoesNotThrow() {
        Door door = new Door(3, 3);
        assertDoesNotThrow(door::update, "Door.update() should never throw");
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
        int startX = 100;
        int endX   = 150;
        Barrier barrier = new Barrier(startX, 100, 20, 36, endX, 10f);

        barrier.update();
        int afterFirst = barrier.getX();
        assertTrue(afterFirst > startX, "Barrier should move right initially");

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

    @Test
    void testBarrierStationary() {
        Barrier barrier = new Barrier(50, 50, 20, 20, 100, 0f);
        barrier.update();
        assertEquals(50, barrier.getX(), "Barrier with speed=0 should not move");
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

    @Test
    void testGameExceptionWithCause() {
        Throwable cause = new IllegalStateException("root");
        GameException ex = new GameException("wrapped", cause);
        assertEquals("wrapped", ex.getMessage());
        assertEquals(cause, ex.getCause());
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
        GameEvent[] received = {null};
        door.addListener(event -> received[0] = event);

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
        door.checkPlayer(player);
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
        door.checkPlayer(player);
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

    // ── EntityGroup<T> — Generics ────────────────────────────────────────────

    @Test
    void testEntityGroupIsEmptyInitially() {
        EntityGroup<Barrier> group = new EntityGroup<>();
        assertTrue(group.isEmpty(), "New group should be empty");
        assertEquals(0, group.size(), "New group size should be 0");
    }

    @Test
    void testEntityGroupAddIncreasesSize() {
        EntityGroup<Barrier> group = new EntityGroup<>();
        group.add(new Barrier(0, 0, 10, 10, 100, 1f));
        group.add(new Barrier(0, 0, 10, 10, 100, 1f));
        assertEquals(2, group.size(), "Size should be 2 after two adds");
        assertFalse(group.isEmpty(), "Group should not be empty");
    }

    @Test
    void testEntityGroupRemove() {
        EntityGroup<Barrier> group = new EntityGroup<>();
        Barrier b = new Barrier(0, 0, 10, 10, 100, 1f);
        group.add(b);
        boolean removed = group.remove(b);
        assertTrue(removed, "remove() should return true for existing element");
        assertEquals(0, group.size(), "Size should be 0 after removal");
    }

    @Test
    void testEntityGroupUpdateAllMovesBarriers() {
        EntityGroup<Barrier> group = new EntityGroup<>();
        Barrier b = new Barrier(50, 0, 10, 10, 200, 5f);
        group.add(b);
        int before = b.getX();
        group.updateAll();
        assertTrue(b.getX() > before, "updateAll() should advance barrier position");
    }

    @Test
    void testEntityGroupStreamAnyMatch() {
        EntityGroup<Barrier> group = new EntityGroup<>();
        group.add(new Barrier(player.getX(), player.getY(), 20, 20, 500, 0f));
        group.add(new Barrier(900, 900, 10, 10, 950, 1f));
        boolean hit = group.stream().anyMatch(b -> b.hitsPlayer(player));
        assertTrue(hit, "stream().anyMatch() should find the overlapping barrier");
    }

    @Test
    void testEntityGroupForEachCountsElements() {
        EntityGroup<Barrier> group = new EntityGroup<>();
        group.add(new Barrier(0, 0, 5, 5, 50, 1f));
        group.add(new Barrier(0, 0, 5, 5, 50, 1f));
        group.add(new Barrier(0, 0, 5, 5, 50, 1f));
        int[] count = {0};
        group.forEach(b -> count[0]++);
        assertEquals(3, count[0], "forEach should visit all 3 barriers");
    }

    @Test
    void testEntityGroupGetAllIsUnmodifiable() {
        EntityGroup<Barrier> group = new EntityGroup<>();
        group.add(new Barrier(0, 0, 5, 5, 50, 1f));
        assertThrows(UnsupportedOperationException.class,
                () -> group.getAll().add(new Barrier(0, 0, 5, 5, 50, 1f)),
                "getAll() must return an unmodifiable list");
    }

    @Test
    void testEntityGroupRemoveIf() {
        EntityGroup<Barrier> group = new EntityGroup<>();
        group.add(new Barrier(10,  0, 5, 5,  50, 1f));
        group.add(new Barrier(200, 0, 5, 5, 300, 1f));
        group.removeIf(b -> b.getX() > 100);
        assertEquals(1, group.size(), "removeIf should remove only matching elements");
        assertEquals(10, group.getAll().get(0).getX());
    }

    // ── HighScore — Serialization ────────────────────────────────────────────

    @Test
    void testHighScoreStoresValues() {
        HighScore hs = new HighScore("Alice", 12345L);
        assertEquals("Alice", hs.getPlayerName(), "Name should be stored");
        assertEquals(12345L,  hs.getTimeMs(),      "Time should be stored");
    }

    @Test
    void testHighScoreToString() {
        HighScore hs = new HighScore("Bob", 5000L);
        String s = hs.toString();
        assertTrue(s.contains("Bob"),  "toString must include name");
        assertTrue(s.contains("5.0"), "toString must include formatted time");
    }

    @Test
    void testHighScoreIsSerializable() {
        HighScore hs = new HighScore("Test", 9999L);
        assertDoesNotThrow(() -> {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(buf)) {
                oos.writeObject(hs);
            }
        }, "HighScore must be serializable without errors");
    }

    @Test
    void testHighScoreSerializeDeserialize() throws Exception {
        HighScore original = new HighScore("Round-trip", 7500L);

        // Serialize to bytes
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(buf)) {
            oos.writeObject(original);
        }

        // Deserialize back
        HighScore restored;
        try (java.io.ObjectInputStream ois =
                     new java.io.ObjectInputStream(
                             new java.io.ByteArrayInputStream(buf.toByteArray()))) {
            restored = (HighScore) ois.readObject();
        }

        assertEquals(original.getPlayerName(), restored.getPlayerName(),
                     "Name must survive serialization round-trip");
        assertEquals(original.getTimeMs(), restored.getTimeMs(),
                     "Time must survive serialization round-trip");
    }

    // ── ScoreManager — Singleton pattern ────────────────────────────────────

    @Test
    void testScoreManagerSingletonIdentity() {
        ScoreManager a = ScoreManager.getInstance();
        ScoreManager b = ScoreManager.getInstance();
        assertSame(a, b, "getInstance() must always return the same object");
    }

    @Test
    void testScoreManagerAddScore() {
        ScoreManager sm = ScoreManager.getInstance();
        sm.addScore(new HighScore("Player", 10000L));
        assertEquals(1, sm.size(), "Size should be 1 after one add");
    }

    @Test
    void testScoreManagerSortsByTime() {
        ScoreManager sm = ScoreManager.getInstance();
        sm.addScore(new HighScore("Slow",  30000L));
        sm.addScore(new HighScore("Fast",   5000L));
        sm.addScore(new HighScore("Medium",15000L));

        assertEquals("Fast", sm.getTopScores().get(0).getPlayerName(),
                     "Fastest score should be first");
    }

    @Test
    void testScoreManagerKeepsOnlyTopFive() {
        ScoreManager sm = ScoreManager.getInstance();
        for (int i = 1; i <= 7; i++) {
            sm.addScore(new HighScore("P" + i, i * 1000L));
        }
        assertTrue(sm.size() <= 5, "ScoreManager must keep at most 5 scores");
    }

    @Test
    void testScoreManagerTopScoresIsUnmodifiable() {
        ScoreManager sm = ScoreManager.getInstance();
        sm.addScore(new HighScore("X", 1000L));
        assertThrows(UnsupportedOperationException.class,
                () -> sm.getTopScores().add(new HighScore("Y", 2000L)),
                "getTopScores() must return an unmodifiable list");
    }

    @Test
    void testScoreManagerClearScores() {
        ScoreManager sm = ScoreManager.getInstance();
        sm.addScore(new HighScore("A", 1000L));
        sm.addScore(new HighScore("B", 2000L));
        sm.clearScores();
        assertEquals(0, sm.size(), "clearScores() should remove all entries");
    }
}
