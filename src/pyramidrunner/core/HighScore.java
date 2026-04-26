package pyramidrunner.core;

import java.io.Serializable;
import java.util.Locale;

/**
 * Stores one completed run: player name + time in milliseconds.
 *
 * Implements Serializable so ScoreManager can write it to a .dat file
 * and read it back later. serialVersionUID prevents issues if the class
 * changes between runs.
 */
public final class HighScore implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String playerName;
    private final long   timeMs;

    /**
     * @param playerName name shown on the scoreboard
     * @param timeMs     time to complete the level in milliseconds
     */
    public HighScore(String playerName, long timeMs) {
        this.playerName = playerName;
        this.timeMs     = timeMs;
    }

    /** @return player display name */
    public String getPlayerName() { return playerName; }

    /** @return completion time in milliseconds */
    public long   getTimeMs()     { return timeMs; }

    /** Returns something like "Player - 12.3 s" */
    @Override
    public String toString() {
        // Locale.US ensures decimal point is "." regardless of system locale
        return playerName + " – " + String.format(Locale.US, "%.1f s", timeMs / 1000.0);
    }
}
