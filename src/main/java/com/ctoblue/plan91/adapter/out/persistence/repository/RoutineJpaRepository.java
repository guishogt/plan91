package com.ctoblue.plan91.adapter.out.persistence.repository;

import com.ctoblue.plan91.adapter.out.persistence.entity.RoutineEntity;
import com.ctoblue.plan91.domain.routine.RoutineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for RoutineEntity (91-day commitments).
 */
@Repository
public interface RoutineJpaRepository extends JpaRepository<RoutineEntity, UUID> {

    /**
     * Finds all routines for a specific practitioner.
     *
     * @param practitionerId the practitioner's ID
     * @return list of routines
     */
    List<RoutineEntity> findByPractitionerId(UUID practitionerId);

    /**
     * Finds all active routines for a specific practitioner.
     *
     * @param practitionerId the practitioner's ID
     * @return list of active routines
     */
    List<RoutineEntity> findByPractitionerIdAndStatus(UUID practitionerId, RoutineStatus status);

    /**
     * Finds all active routines for a practitioner (optimized query).
     *
     * @param practitionerId the practitioner's ID
     * @return list of active routines with habit eagerly loaded
     */
    @Query("SELECT r FROM RoutineEntity r JOIN FETCH r.habit WHERE r.practitioner.id = :practitionerId AND r.status = 'ACTIVE'")
    List<RoutineEntity> findActiveRoutinesWithHabit(@Param("practitionerId") UUID practitionerId);

    /**
     * Finds all routines for a specific habit.
     *
     * @param habitId the habit's ID
     * @return list of routines
     */
    List<RoutineEntity> findByHabitId(UUID habitId);

    /**
     * Counts all routines for a specific habit.
     *
     * @param habitId the habit's ID
     * @return count of routines
     */
    long countByHabitId(UUID habitId);

    /**
     * Finds routines that are expected to be completed on a specific date.
     * This is a complex query that would need custom logic for recurrence rules.
     *
     * @param practitionerId the practitioner's ID
     * @param date the date to check
     * @return list of routines expected on this date
     */
    @Query("SELECT r FROM RoutineEntity r WHERE r.practitioner.id = :practitionerId " +
            "AND r.status = 'ACTIVE' " +
            "AND :date >= r.startDate " +
            "AND :date <= r.expectedEndDate")
    List<RoutineEntity> findActiveRoutinesForDate(
            @Param("practitionerId") UUID practitionerId,
            @Param("date") LocalDate date
    );

    /**
     * Finds a routine by ID with habit and practitioner eagerly loaded.
     *
     * @param id the routine's ID
     * @return the routine with relationships loaded
     */
    @Query("SELECT r FROM RoutineEntity r JOIN FETCH r.habit JOIN FETCH r.practitioner WHERE r.id = :id")
    java.util.Optional<RoutineEntity> findByIdWithRelations(@Param("id") UUID id);
}
