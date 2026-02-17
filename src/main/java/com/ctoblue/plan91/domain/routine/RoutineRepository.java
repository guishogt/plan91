package com.ctoblue.plan91.domain.routine;

import com.ctoblue.plan91.domain.habit.HabitId;
import com.ctoblue.plan91.domain.habitpractitioner.HabitPractitionerId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for Routine aggregate persistence.
 *
 * <p>Defines operations for storing and retrieving routines.
 * Implementation will be provided by the infrastructure layer.
 *
 * <p>Key queries support:
 * <ul>
 *   <li>Finding active routine for a habit (one-active-per-habit rule)</li>
 *   <li>Finding all routines for a practitioner</li>
 *   <li>Finding all routines for a specific habit</li>
 * </ul>
 */
public interface RoutineRepository {

    /**
     * Saves a routine (create or update).
     *
     * @param routine the routine to save
     * @return the saved routine
     */
    Routine save(Routine routine);

    /**
     * Finds a routine by ID.
     *
     * @param id the routine ID
     * @return Optional containing the routine if found
     */
    Optional<Routine> findById(RoutineId id);

    /**
     * Finds all routines for a practitioner.
     *
     * @param practitionerId the practitioner ID
     * @return list of routines (may be empty)
     */
    List<Routine> findByPractitioner(HabitPractitionerId practitionerId);

    /**
     * Finds all routines for a specific habit.
     *
     * @param habitId the habit ID
     * @return list of routines (may be empty)
     */
    List<Routine> findByHabit(HabitId habitId);

    /**
     * Finds the active routine for a habit and practitioner.
     *
     * <p>Used to enforce the one-active-routine-per-habit rule.
     *
     * @param habitId the habit ID
     * @param practitionerId the practitioner ID
     * @return Optional containing the active routine if found
     */
    Optional<Routine> findActiveRoutine(HabitId habitId, HabitPractitionerId practitionerId);

    /**
     * Finds all active routines for a practitioner.
     *
     * @param practitionerId the practitioner ID
     * @return list of active routines (may be empty)
     */
    List<Routine> findActiveRoutinesByPractitioner(HabitPractitionerId practitionerId);

    /**
     * Checks if a practitioner has an active routine for a habit.
     *
     * @param habitId the habit ID
     * @param practitionerId the practitioner ID
     * @return true if an active routine exists
     */
    boolean hasActiveRoutine(HabitId habitId, HabitPractitionerId practitionerId);

    /**
     * Deletes a routine.
     *
     * @param id the routine ID
     */
    void delete(RoutineId id);
}
