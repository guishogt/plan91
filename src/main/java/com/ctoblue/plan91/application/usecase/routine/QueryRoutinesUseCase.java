package com.ctoblue.plan91.application.usecase.routine;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntryEntity;
import com.ctoblue.plan91.adapter.out.persistence.entity.RoutineEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitEntryJpaRepository;
import com.ctoblue.plan91.adapter.out.persistence.repository.RoutineJpaRepository;
import com.ctoblue.plan91.domain.routine.RecurrenceRule;
import com.ctoblue.plan91.domain.routine.RecurrenceType;
import com.ctoblue.plan91.domain.routine.RoutineStatus;
import com.ctoblue.plan91.domain.routine.RoutineType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final HabitEntryJpaRepository entryRepository;

    public QueryRoutinesUseCase(RoutineJpaRepository routineRepository, HabitEntryJpaRepository entryRepository) {
        this.routineRepository = routineRepository;
        this.entryRepository = entryRepository;
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
     * Gets active routines for a practitioner with streaks recalculated.
     *
     * @param practitionerId the practitioner's ID
     * @return list of active routines with habits eagerly loaded and streaks up-to-date
     */
    @Transactional
    public List<RoutineEntity> getActiveRoutines(String practitionerId) {
        UUID id = UUID.fromString(practitionerId);
        List<RoutineEntity> routines = routineRepository.findActiveRoutinesWithHabit(id);

        // Recalculate streaks for each routine
        for (RoutineEntity routine : routines) {
            recalculateStreak(routine);
        }

        return routines;
    }

    /**
     * Recalculates the streak for a routine based on actual entries.
     * Checks for missed expected days and applies one-strike rule.
     */
    private void recalculateStreak(RoutineEntity routine) {
        if (routine.getStatus() != RoutineStatus.ACTIVE) {
            return; // Only recalculate active routines
        }

        LocalDate lastCompletion = routine.getStreak().getLastCompletionDate();
        if (lastCompletion == null) {
            // No completions yet - check if missed any days since start
            lastCompletion = routine.getStartDate().minusDays(1);
        }

        LocalDate today = LocalDate.now();

        // Build recurrence rule to check expected days
        RecurrenceRule recurrenceRule = buildRecurrenceRule(routine);

        // Get all completed entries for this routine
        List<HabitEntryEntity> entries = entryRepository.findByRoutineIdAndDateBetween(
                routine.getId(), lastCompletion.plusDays(1), today);
        Set<LocalDate> completedDates = entries.stream()
                .filter(e -> Boolean.TRUE.equals(e.getCompleted()))
                .map(HabitEntryEntity::getDate)
                .collect(Collectors.toSet());

        // Check each day from lastCompletion+1 to yesterday (today might still be completed)
        LocalDate checkUntil = today.minusDays(1);
        int missedDays = 0;

        for (LocalDate date = lastCompletion.plusDays(1); !date.isAfter(checkUntil); date = date.plusDays(1)) {
            // Only count misses on expected days
            if (recurrenceRule.isExpectedOn(date) && !completedDates.contains(date)) {
                missedDays++;
            }
        }

        // Apply one-strike rule for ROUTINE type
        if (missedDays > 0 && routine.getRoutineType() == RoutineType.ROUTINE) {
            var streak = routine.getStreak();

            if (!streak.getHasUsedStrike()) {
                // First miss: use strike
                streak.setHasUsedStrike(true);
                streak.setStrikeDate(lastCompletion.plusDays(1));

                if (missedDays > 1) {
                    // Multiple misses: abandon
                    streak.setCurrentStreak(0);
                    routine.setStatus(RoutineStatus.ABANDONED);
                }
            } else {
                // Already used strike: any miss abandons
                streak.setCurrentStreak(0);
                routine.setStatus(RoutineStatus.ABANDONED);
            }

            routineRepository.save(routine);
        } else if (missedDays > 0 && routine.getRoutineType() == RoutineType.TRACKER) {
            // TRACKER: just reset streak, don't abandon
            routine.getStreak().setCurrentStreak(0);
            routineRepository.save(routine);
        }
    }

    /**
     * Builds a RecurrenceRule from the routine's embedded recurrence data.
     */
    private RecurrenceRule buildRecurrenceRule(RoutineEntity routine) {
        var embedded = routine.getRecurrenceRule();
        RecurrenceType type = embedded.getType();

        return switch (type) {
            case DAILY -> RecurrenceRule.daily();
            case WEEKDAYS -> RecurrenceRule.weekdays();
            case WEEKENDS -> RecurrenceRule.weekends();
            case SPECIFIC_DAYS -> {
                String days = embedded.getSpecificDays();
                if (days != null && !days.isBlank()) {
                    Set<com.ctoblue.plan91.domain.routine.DayOfWeek> daySet =
                            java.util.Arrays.stream(days.split(","))
                                    .map(String::trim)
                                    .map(com.ctoblue.plan91.domain.routine.DayOfWeek::valueOf)
                                    .collect(Collectors.toSet());
                    yield RecurrenceRule.specificDays(daySet);
                }
                yield RecurrenceRule.daily(); // fallback
            }
            default -> RecurrenceRule.daily();
        };
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
        return routineRepository.findByPractitionerIdAndStatusWithHabit(id, status);
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
     * Gets a routine by ID with relationships eagerly loaded.
     *
     * @param routineId the routine's ID
     * @return the routine entity with habit and practitioner loaded
     * @throws IllegalArgumentException if routine not found
     */
    @Transactional(readOnly = true)
    public RoutineEntity getRoutineById(String routineId) {
        UUID id = UUID.fromString(routineId);
        return routineRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new IllegalArgumentException("Routine not found: " + routineId));
    }
}
