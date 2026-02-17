package com.ctoblue.plan91.domain.habitentry;

import com.ctoblue.plan91.domain.routine.RoutineId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository port for HabitEntry persistence.
 *
 * <p>Defines operations for storing and retrieving habit entries.
 * Implementation will be provided by the infrastructure layer.
 *
 * <p>Key queries support:
 * <ul>
 *   <li>Finding entries by routine</li>
 *   <li>Finding entry for specific date (one-per-date enforcement)</li>
 *   <li>Finding entries in date range (for reporting)</li>
 * </ul>
 */
public interface HabitEntryRepository {

    /**
     * Saves a habit entry (create or update).
     *
     * @param entry the entry to save
     * @return the saved entry
     */
    HabitEntry save(HabitEntry entry);

    /**
     * Finds an entry by ID.
     *
     * @param id the entry ID
     * @return Optional containing the entry if found
     */
    Optional<HabitEntry> findById(HabitEntryId id);

    /**
     * Finds all entries for a routine.
     *
     * @param routineId the routine ID
     * @return list of entries (may be empty), ordered by date ascending
     */
    List<HabitEntry> findByRoutine(RoutineId routineId);

    /**
     * Finds an entry for a specific routine and date.
     *
     * <p>Used to enforce one-entry-per-date-per-routine rule.
     *
     * @param routineId the routine ID
     * @param date the date
     * @return Optional containing the entry if found
     */
    Optional<HabitEntry> findByRoutineAndDate(RoutineId routineId, LocalDate date);

    /**
     * Finds all entries for a routine in a date range.
     *
     * <p>Used for reporting and statistics.
     *
     * @param routineId the routine ID
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of entries (may be empty), ordered by date ascending
     */
    List<HabitEntry> findByRoutineInDateRange(
            RoutineId routineId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Checks if an entry exists for a routine and date.
     *
     * @param routineId the routine ID
     * @param date the date
     * @return true if an entry exists
     */
    boolean existsForRoutineAndDate(RoutineId routineId, LocalDate date);

    /**
     * Deletes an entry.
     *
     * <p>Entries are always deletable (design decision Option B).
     *
     * @param id the entry ID
     */
    void delete(HabitEntryId id);
}
