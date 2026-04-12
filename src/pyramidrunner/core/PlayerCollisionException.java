package pyramidrunner.core;

/**
 * Thrown when the player collides with a lethal barrier.
 * Extends {@link GameException} — demonstrates inheritance in exception hierarchy.
 */
public class PlayerCollisionException extends GameException {

    /** Player X position when the collision occurred. */
    private final int playerX;
    /** Player Y position when the collision occurred. */
    private final int playerY;

    /**
     * @param playerX player's X position at collision
     * @param playerY player's Y position at collision
     */
    public PlayerCollisionException(int playerX, int playerY) {
        super("Player hit a barrier at (" + playerX + ", " + playerY + ")");
        this.playerX = playerX;
        this.playerY = playerY;
    }

    /** @return X position of the player at the moment of collision */
    public int getPlayerX() { return playerX; }

    /** @return Y position of the player at the moment of collision */
    public int getPlayerY() { return playerY; }
}
