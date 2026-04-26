package pyramidrunner.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the persistent high-score leaderboard.
 *
 * <p><b>Design pattern — Singleton:</b> exactly one {@code ScoreManager} exists
 * for the lifetime of the application.  Clients obtain it via
 * {@link #getInstance()} — the constructor is private.</p>
 *
 * <p><b>Serialization:</b> the leaderboard is stored as a serialized
 * {@code List<HighScore>} on disk ({@value #FILE_PATH}).  If the file is absent
 * or corrupt the manager silently starts with an empty list.</p>
 *
 * <p>Generics: the internal list is typed {@code List<HighScore>}, and
 * {@link Collections#sort} is used with a lambda {@link Comparator}.</p>
 */
public final class ScoreManager {

    private static final Logger LOGGER    = Logger.getLogger(ScoreManager.class.getName());
    private static final String FILE_PATH = "scores.dat";
    private static final int    MAX_SCORES = 5;

    // ── Singleton ──────────────────────────────────────────────────────────

    /** The single shared instance. Lazily initialised; guarded by class-load. */
    private static ScoreManager instance;

    /**
     * Returns the application-wide {@code ScoreManager} instance.
     * Creates it on the first call (lazy initialisation).
     *
     * @return the singleton instance
     */
    public static ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }

    /**
     * Resets the singleton — intended for unit tests only.
     * Calling this in production code produces undefined behaviour.
     */
    public static void resetForTesting() {
        instance = null;
    }

    // ── State ──────────────────────────────────────────────────────────────

    /** In-memory leaderboard; loaded from disk on construction. */
    private final List<HighScore> scores;

    /** Private constructor — enforces Singleton. */
    private ScoreManager() {
        scores = loadFromDisk();
        LOGGER.info("ScoreManager initialised with " + scores.size() + " stored scores.");
    }

    // ── Public API ─────────────────────────────────────────────────────────

    /**
     * Adds a new score to the leaderboard, keeps only the fastest
     * {@value #MAX_SCORES} entries, and persists the updated list to disk.
     *
     * @param score the score to record; must not be {@code null}
     */
    public void addScore(HighScore score) {
        scores.add(score);
        // Lambda comparator: sort ascending by time (fastest first)
        scores.sort(Comparator.comparingLong(HighScore::getTimeMs));
        if (scores.size() > MAX_SCORES) {
            scores.subList(MAX_SCORES, scores.size()).clear();
        }
        saveToDisk();
        LOGGER.info("Score recorded: " + score);
    }

    /**
     * Returns an unmodifiable view of the current top scores (fastest first).
     *
     * @return read-only ordered list of high scores
     */
    public List<HighScore> getTopScores() {
        return Collections.unmodifiableList(scores);
    }

    /** @return number of scores currently on the leaderboard */
    public int size() { return scores.size(); }

    /** Clears all in-memory scores (does not touch the file). */
    public void clearScores() { scores.clear(); }

    // ── Serialization (file I/O) ───────────────────────────────────────────

    /**
     * Serializes the leaderboard to {@value #FILE_PATH}.
     * Logs a SEVERE message and continues silently on failure.
     */
    private void saveToDisk() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(new ArrayList<>(scores));
            LOGGER.fine("Scores saved to " + FILE_PATH);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save scores: " + e.getMessage());
        }
    }

    /**
     * Deserializes the leaderboard from {@value #FILE_PATH}.
     * Returns an empty list if the file does not exist or cannot be read.
     *
     * @return loaded list of high scores, or an empty list on failure
     */
    @SuppressWarnings("unchecked")
    private List<HighScore> loadFromDisk() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            List<HighScore> loaded = (List<HighScore>) ois.readObject();
            LOGGER.fine("Scores loaded from " + FILE_PATH);
            return loaded;
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Failed to load scores: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
