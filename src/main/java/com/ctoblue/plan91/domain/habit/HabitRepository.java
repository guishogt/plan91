package com.ctoblue.plan91.domain.habit;

import com.ctoblue.plan91.domain.habitpractitioner.HabitPractitionerId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface (Port) for Habit aggregate.
 *
 * <p>This is a domain interface following the Hexagonal Architecture pattern.
 * The implementation will be in the infrastructure layer (Adapter).
 *
 * <p>Repository methods follow these conventions:
 * <ul>
 *   <li>save() - Persists or updates a habit</li>
 *   <li>findBy*() - Queries that return Optional or List</li>
 *   <li>existsBy*() - Boolean queries</li>
 *   <li>delete() - Removes a habit</li>
 * </ul>
 */
public interface HabitRepository {

    /**
     * Saves (creates or updates) a Habit.
     *
     * @param habit the habit to save
     * @return the saved habit
     */
    Habit save(Habit habit);

    /**
     * Finds a habit by its unique ID.
     *
     * @param id the habit ID
     * @return an Optional containing the habit if found
     */
    Optional<Habit> findById(HabitId id);

    /**
     * Finds all habits created by a specific practitioner.
     *
     * @param creatorId the practitioner ID
     * @return a list of habits (may be empty)
     */
    List<Habit> findByCreator(HabitPractitionerId creatorId);

    /**
     * Finds all public habits (that can be copied by others).
     * Useful for browsing available habits.
     *
     * @return a list of public habits (may be empty)
     */
    List<Habit> findPublicHabits();

    /**
     * Finds all habits that were copied from a specific source habit.
     * Useful for tracking how habits spread across practitioners.
     *
     * @param sourceHabitId the source habit ID
     * @return a list of copied habits (may be empty)
     */
    List<Habit> findCopiesOf(HabitId sourceHabitId);

    /**
     * Checks if a habit with the given name already exists for a practitioner.
     * Useful for preventing duplicate habit names per practitioner.
     *
     * @param name the habit name
     * @param creatorId the practitioner ID
     * @return true if a habit with this name exists for this practitioner
     */
    boolean existsByNameAndCreator(String name, HabitPractitionerId creatorId);

    /**
     * Deletes a habit by its ID.
     *
     * <p>Note: In production, this may need to check if the habit has
     * associated routines before allowing deletion, or cascade to related entities.
     *
     * @param id the habit ID to delete
     */
    void delete(HabitId id);
}
