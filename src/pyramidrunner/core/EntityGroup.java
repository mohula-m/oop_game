package pyramidrunner.core;

import pyramidrunner.entity.Entity;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A generic, type-safe container for homogeneous groups of game entities.
 *
 * <p><b>Generics — bounded type parameter:</b> {@code T extends Entity} guarantees
 * that every element in the group has {@link Entity#update()} and
 * {@link Drawable#draw(Graphics2D)} available, so the group can call those
 * operations on all members without casting.</p>
 *
 * <p>This class replaces raw {@code List<Barrier>} in {@code GamePanel} with a
 * reusable, self-contained unit that handles update, draw, and stream operations
 * for any {@link Entity} subtype.</p>
 *
 * <p>Encapsulation: the internal list is {@code private}; callers access
 * contents only through the provided methods.</p>
 *
 * @param <T> the concrete {@link Entity} subtype stored in this group
 */
public class EntityGroup<T extends Entity> {

    /** Internal storage — never exposed directly. */
    private final List<T> entities = new ArrayList<>();

    // ── Mutation ───────────────────────────────────────────────────────────

    /**
     * Adds an entity to this group.
     *
     * @param entity entity to add; must not be {@code null}
     */
    public void add(T entity) {
        entities.add(entity);
    }

    /**
     * Removes a specific entity from this group.
     *
     * @param entity entity to remove
     * @return {@code true} if the entity was present and removed
     */
    public boolean remove(T entity) {
        return entities.remove(entity);
    }

    /**
     * Removes all entities that satisfy the given predicate.
     *
     * <p>Example: {@code group.removeIf(b -> b.getX() > 1000)}</p>
     *
     * @param predicate test applied to each element
     */
    public void removeIf(Predicate<T> predicate) {
        entities.removeIf(predicate);
    }

    // ── Bulk operations ────────────────────────────────────────────────────

    /**
     * Calls {@link Entity#update()} on every entity in this group.
     * Equivalent to {@code entities.forEach(Entity::update)} but semantically named.
     */
    public void updateAll() {
        entities.forEach(Updatable::update);
    }

    /**
     * Calls {@link Drawable#draw(Graphics2D)} on every entity in this group.
     *
     * @param g the Graphics2D context
     */
    public void drawAll(Graphics2D g) {
        entities.forEach(e -> e.draw(g));
    }

    /**
     * Applies {@code action} to every entity in this group.
     * Delegates to {@link List#forEach} — works with lambda expressions and
     * method references.
     *
     * @param action action to perform on each element
     */
    public void forEach(Consumer<T> action) {
        entities.forEach(action);
    }

    // ── Query ──────────────────────────────────────────────────────────────

    /**
     * Returns a sequential {@link Stream} over the entities.
     * Callers can use it with {@code anyMatch}, {@code filter}, {@code map}, etc.
     *
     * @return stream of the stored entities
     */
    public Stream<T> stream() {
        return entities.stream();
    }

    /**
     * Returns an unmodifiable view of the internal list.
     *
     * @return read-only list of entities
     */
    public List<T> getAll() {
        return Collections.unmodifiableList(entities);
    }

    /** @return number of entities currently in this group */
    public int size() { return entities.size(); }

    /** @return {@code true} if this group contains no entities */
    public boolean isEmpty() { return entities.isEmpty(); }
}
