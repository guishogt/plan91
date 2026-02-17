package com.ctoblue.plan91.application.usecase.routine;

import com.ctoblue.plan91.adapter.out.persistence.entity.RoutineEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.RoutineJpaRepository;
import com.ctoblue.plan91.domain.routine.RoutineStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Use case for querying routines.
 *
 * <p>This use case supports:
 * <ul>
 *   <li>Getting all routines for a practitioner</li>
 *   <li>Getting active routines</li>
 *   <li>Getting routines scheduled for a specific date</li>
 *   <li>Getting a single routine by ID</li>
 * </ul>
 */
@Service
public class QueryRoutinesUseCase {

    private final RoutineJpaRepository routineRepository;

    public QueryRoutinesUseCase(RoutineJpaRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    /**
     * Gets all routines for a practitioner.
     *
     * @param practitionerId the practitioner's ID
     * @return list of all routines
     */
    @Transactional(readOnly = true)
    public List<RoutineEntity> getAllRoutines(String practitionerId) {
        UUID id = UUID.fromString(practitionerId);
        return routineRepository.findByPractitionerId(id);
    }

    /**
     * Gets active routines for a practitioner.
     *
     * @param practitionerId the practitioner's ID
     * @return list of active routines with habits eagerly loaded
     */
    @Transactional(readOnly = true)
    public List<RoutineEntity> getActiveRoutines(String practitionerId) {
        UUID id = UUID.fromString(practitionerId);
        return routineRepository.findActiveRoutinesWithHabit(id);
    }

    /**
     * Gets routines by status for a practitioner.
     *
     * @param practitionerId the practitioner's ID
     * @param status the routine status
     * @return list of routines with the given status
     */
    @Transactional(readOnly = true)
    public List<RoutineEntity> getRoutinesByStatus(String practitionerId, RoutineStatus status) {
        UUID id = UUID.fromString(practitionerId);
        return routineRepository.findByPractitionerIdAndStatus(id, status);
    }

    /**
     * Gets active routines expected to be completed on a specific date.
     *
     * @param practitionerId the practitioner's ID
     * @param date the date to check
     * @return list of routines scheduled for this date
     */
    @Transactional(readOnly = true)
    public List<RoutineEntity> getRoutinesForDate(String practitionerId, LocalDate date) {
        UUID id = UUID.fromString(practitionerId);
        return routineRepository.findActiveRoutinesForDate(id, date);
    }

    /**
     * Gets a routine by ID.
     *
     * @param routineId the routine's ID
     * @return the routine entity
     * @throws IllegalArgumentException if routine not found
     */
    @Transactional(readOnly = true)
    public RoutineEntity getRoutineById(String routineId) {
        UUID id = UUID.fromString(routineId);
        return routineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Routine not found: " + routineId));
    }
}
