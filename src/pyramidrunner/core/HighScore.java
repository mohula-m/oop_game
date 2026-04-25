package pyramidrunner.core;

import java.io.Serializable;

/**
 * Immutable record of a single level completion.
 *
 * <p><b>Serialization:</b> implements {@link Serializable} so that instances
 * can be written to / read from disk by {@link ScoreManager}.  The explicit
 * {@code serialVersionUID} guards against class-change invalidation.</p>
 *
 * <p>Encapsulation: all fields are {@code private final}; access via getters only.</p>
 */
public final class HighScore implements Serializable {

    /** Guards against serialization / deserialization version mismatch. */
    private static final long serialVersionUID = 1L;

    /** Display name of the player (or "Player" by default). */
    private final String playerName;

    /** Time taken to complete the level, in milliseconds. */
    private final long timeMs;

    /**
     * Creates a new high-score entry.
     *
     * @param playerName display name shown on the scoreboard
     * @param timeMs     completion time in milliseconds
     */
    public HighScore(String playerName, long timeMs) {
        this.playerName = playerName;
        this.timeMs     = timeMs;
    }

    /** @return display name of the player */
    public String getPlayerName() { return playerName; }

    /** @return level-completion time in milliseconds */
    public long   getTimeMs()     { return timeMs; }

    /**
     * Returns a human-readable string in the form {@code "PlayerName – 12.3 s"}.
     *
     * @return formatted score string
     */
    @Override
    public String toString() {
        return playerName + " – " + String.format("%.1f s", timeMs / 1000.0);
    }
}
