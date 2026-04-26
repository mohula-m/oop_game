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
 * Stores and loads the top 5 best completion times.
 *
 * Singleton pattern - only one instance exists in the whole program.
 * Scores are saved to scores.dat using Java serialization.
 * If the file doesn't exist yet, starts with an empty list.
 */
public final class ScoreManager {

    private static final Logger LOGGER     = Logger.getLogger(ScoreManager.class.getName());
    private static final String FILE_PATH  = "scores.dat";
    private static final int    MAX_SCORES = 5;

    // Singleton - only one instance allowed
    private static ScoreManager instance;

    /**
     * Returns the single instance (creates it on first call).
     *
     * @return the shared ScoreManager
     */
    public static ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }

    /** Only used in unit tests to reset state between test runs. */
    public static void resetForTesting() {
        instance = null;
    }

    // list of scores kept in memory (also saved to file)
    private final List<HighScore> scores;

    // private constructor - part of Singleton pattern
    private ScoreManager() {
        scores = loadFromDisk();
        LOGGER.info("ScoreManager ready, loaded " + scores.size() + " scores.");
    }

    /**
     * Adds a score, keeps only top 5 sorted by time, saves to disk.
     *
     * @param score the completed run to save
     */
    public void addScore(HighScore score) {
        scores.add(score);
        // lambda comparator - sort fastest first
        scores.sort(Comparator.comparingLong(HighScore::getTimeMs));
        if (scores.size() > MAX_SCORES) {
            scores.subList(MAX_SCORES, scores.size()).clear();
        }
        saveToDisk();
        LOGGER.info("New score: " + score);
    }

    /**
     * Returns the top scores list (read-only).
     *
     * @return unmodifiable list ordered fastest first
     */
    public List<HighScore> getTopScores() {
        return Collections.unmodifiableList(scores);
    }

    /** @return how many scores are currently stored */
    public int size() { return scores.size(); }

    /** Removes all scores from memory (does not delete the file). */
    public void clearScores() { scores.clear(); }

    // -- Serialization --

    /** Writes the score list to scores.dat using Java object serialization. */
    private void saveToDisk() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(new ArrayList<>(scores));
            LOGGER.fine("Saved scores to " + FILE_PATH);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not save scores: " + e.getMessage());
        }
    }

    /** Reads scores.dat back into memory. Returns empty list if file is missing. */
    @SuppressWarnings("unchecked")
    private List<HighScore> loadFromDisk() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            List<HighScore> loaded = (List<HighScore>) ois.readObject();
            LOGGER.fine("Loaded scores from " + FILE_PATH);
            return loaded;
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Could not load scores: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
