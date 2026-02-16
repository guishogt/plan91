package com.ctoblue.plan91.adapter.out.persistence.repository;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for HabitEntryEntity (daily completions).
 */
@Repository
public interface HabitEntryJpaRepository extends JpaRepository<HabitEntryEntity, UUID> {

    /**
     * Finds all entries for a specific routine.
     *
     * @param routineId the routine's ID
     * @return list of entries
     */
    List<HabitEntryEntity> findByRoutineId(UUID routineId);

    /**
     * Finds all entries for a specific routine, ordered by date ascending.
     *
     * @param routineId the routine's ID
     * @return list of entries ordered by date
     */
    List<HabitEntryEntity> findByRoutineIdOrderByDateAsc(UUID routineId);

    /**
     * Finds an entry for a specific routine on a specific date.
     *
     * @param routineId the routine's ID
     * @param date the date
     * @return Optional containing the entry if found
     */
    Optional<HabitEntryEntity> findByRoutineIdAndDate(UUID routineId, LocalDate date);

    /**
     * Finds all entries for a routine between two dates (inclusive).
     *
     * @param routineId the routine's ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of entries
     */
    List<HabitEntryEntity> findByRoutineIdAndDateBetween(
            UUID routineId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Counts entries for a specific routine.
     *
     * @param routineId the routine's ID
     * @return count of entries
     */
    long countByRoutineId(UUID routineId);

    /**
     * Checks if an entry exists for a routine on a specific date.
     *
     * @param routineId the routine's ID
     * @param date the date
     * @return true if entry exists
     */
    boolean existsByRoutineIdAndDate(UUID routineId, LocalDate date);

    /**
     * Finds all entries for a practitioner on a specific date.
     *
     * @param practitionerId the practitioner's ID
     * @param date the date
     * @return list of entries
     */
    @Query("SELECT e FROM HabitEntryEntity e WHERE e.routine.practitioner.id = :practitionerId AND e.date = :date")
    List<HabitEntryEntity> findByPractitionerAndDate(
            @Param("practitionerId") UUID practitionerId,
            @Param("date") LocalDate date
    );

    /**
     * Finds the latest entry for a routine.
     *
     * @param routineId the routine's ID
     * @return Optional containing the latest entry
     */
    Optional<HabitEntryEntity> findFirstByRoutineIdOrderByDateDesc(UUID routineId);

    /**
     * Finds all entries for multiple routines.
     *
     * @param routineIds the list of routine IDs
     * @return list of entries
     */
    List<HabitEntryEntity> findByRoutineIdIn(List<UUID> routineIds);
}
