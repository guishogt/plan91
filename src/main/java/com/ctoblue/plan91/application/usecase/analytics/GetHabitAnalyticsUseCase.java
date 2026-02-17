package com.ctoblue.plan91.application.usecase.analytics;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntryEntity;
import com.ctoblue.plan91.adapter.out.persistence.entity.RoutineEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitEntryJpaRepository;
import com.ctoblue.plan91.adapter.out.persistence.repository.RoutineJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Use case for getting habit-based analytics aggregation.
 *
 * <p>Epic 08: Analytics & Statistics (PLAN91-089)
 * Groups analytics by habit to show comparative performance.
 */
@Service
public class GetHabitAnalyticsUseCase {

    private final RoutineJpaRepository routineRepository;
    private final HabitEntryJpaRepository entryRepository;

    public GetHabitAnalyticsUseCase(
            RoutineJpaRepository routineRepository,
            HabitEntryJpaRepository entryRepository) {
        this.routineRepository = routineRepository;
        this.entryRepository = entryRepository;
    }

    /**
     * Gets analytics grouped by habit for a practitioner.
     *
     * @param practitionerId the practitioner's ID
     * @return habit-based analytics
     */
    @Transactional(readOnly = true)
    public HabitAnalyticsSummary execute(String practitionerId) {
        UUID id = UUID.fromString(practitionerId);

        // Get all routines for this practitioner
        List<RoutineEntity> routines = routineRepository.findByPractitionerId(id);

        // Group routines by habit
        Map<UUID, List<RoutineEntity>> routinesByHabit = routines.stream()
                .collect(Collectors.groupingBy(r -> r.getHabit().getId()));

        // Calculate analytics for each habit
        List<HabitStatistics> habitStats = new ArrayList<>();

        for (Map.Entry<UUID, List<RoutineEntity>> entry : routinesByHabit.entrySet()) {
            UUID habitId = entry.getKey();
            List<RoutineEntity> habitRoutines = entry.getValue();

            if (!habitRoutines.isEmpty()) {
                RoutineEntity firstRoutine = habitRoutines.get(0);
                HabitStatistics stats = calculateHabitStatistics(
                        habitId,
                        firstRoutine.getHabit().getName(),
                        habitRoutines
                );
                habitStats.add(stats);
            }
        }

        // Sort by total completions descending
        habitStats.sort((a, b) -> Integer.compare(b.totalCompletions(), a.totalCompletions()));

        return new HabitAnalyticsSummary(habitStats);
    }

    private HabitStatistics calculateHabitStatistics(
            UUID habitId,
            String habitName,
            List<RoutineEntity> routines) {

        int totalRoutines = routines.size();
        int activeRoutines = (int) routines.stream()
                .filter(r -> r.getStatus().toString().equals("ACTIVE"))
                .count();

        int totalCompletions = routines.stream()
                .mapToInt(r -> r.getStreak().getTotalCompletions())
                .sum();

        int longestStreak = routines.stream()
                .mapToInt(r -> r.getStreak().getLongestStreak())
                .max()
                .orElse(0);

        int currentStreak = routines.stream()
                .filter(r -> r.getStatus().toString().equals("ACTIVE"))
                .mapToInt(r -> r.getStreak().getCurrentStreak())
                .max()
                .orElse(0);

        // Calculate average completion rate
        double avgCompletionRate = calculateAverageCompletionRate(routines);

        return new HabitStatistics(
                habitId.toString(),
                habitName,
                totalRoutines,
                activeRoutines,
                totalCompletions,
                currentStreak,
                longestStreak,
                avgCompletionRate
        );
    }

    private double calculateAverageCompletionRate(List<RoutineEntity> routines) {
        if (routines.isEmpty()) {
            return 0.0;
        }

        LocalDate today = LocalDate.now();
        double totalRate = 0.0;
        int count = 0;

        for (RoutineEntity routine : routines) {
            long daysElapsed = java.time.temporal.ChronoUnit.DAYS.between(routine.getStartDate(), today) + 1;
            if (daysElapsed > 0 && daysElapsed <= 91) {
                int completions = routine.getStreak().getTotalCompletions();
                double rate = (completions * 100.0) / daysElapsed;
                totalRate += rate;
                count++;
            }
        }

        return count > 0 ? totalRate / count : 0.0;
    }

    // DTOs

    public record HabitAnalyticsSummary(
            List<HabitStatistics> habits
    ) {}

    public record HabitStatistics(
            String habitId,
            String habitName,
            int totalRoutines,
            int activeRoutines,
            int totalCompletions,
            int currentStreak,
            int longestStreak,
            double avgCompletionRate
    ) {}
}
