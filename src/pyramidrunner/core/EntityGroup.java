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
 * Generic container for a group of game entities of the same type.
 *
 * The type parameter T extends Entity, so the group can call update()
 * and draw() on all elements without casting. This is the main point
 * of using generics here - type safety + reusability.
 *
 * Used in GamePanel as EntityGroup&lt;Barrier&gt; to manage all barriers.
 *
 * @param <T> any subclass of Entity (e.g. Barrier, Door, ...)
 */
public class EntityGroup<T extends Entity> {

    private final List<T> entities = new ArrayList<>();

    // -- Adding / removing --

    /** Adds an entity to the group. */
    public void add(T entity) {
        entities.add(entity);
    }

    /**
     * Removes a specific entity.
     *
     * @return true if it was found and removed
     */
    public boolean remove(T entity) {
        return entities.remove(entity);
    }

    /**
     * Removes all entities that match the predicate.
     * Example: group.removeIf(b -> b.getX() > 1000)
     *
     * @param predicate condition to test each entity
     */
    public void removeIf(Predicate<T> predicate) {
        entities.removeIf(predicate);
    }

    // -- Bulk operations --

    /** Calls update() on every entity in the group. */
    public void updateAll() {
        entities.forEach(Updatable::update);
    }

    /**
     * Calls draw(g) on every entity in the group.
     *
     * @param g graphics context
     */
    public void drawAll(Graphics2D g) {
        entities.forEach(e -> e.draw(g));
    }

    /**
     * Applies an action to every entity (works with lambdas).
     *
     * @param action what to do with each entity
     */
    public void forEach(Consumer<T> action) {
        entities.forEach(action);
    }

    // -- Queries --

    /**
     * Returns a Stream so you can use anyMatch, filter, etc.
     *
     * @return stream over the stored entities
     */
    public Stream<T> stream() {
        return entities.stream();
    }

    /**
     * Returns a read-only view of the list.
     *
     * @return unmodifiable list
     */
    public List<T> getAll() {
        return Collections.unmodifiableList(entities);
    }

    /** @return number of entities in this group */
    public int size() { return entities.size(); }

    /** @return true if the group is empty */
    public boolean isEmpty() { return entities.isEmpty(); }
}
