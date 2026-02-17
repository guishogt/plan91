package com.ctoblue.plan91.application.usecase.routine;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntryEntity;
import com.ctoblue.plan91.adapter.out.persistence.entity.RoutineEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitEntryJpaRepository;
import com.ctoblue.plan91.adapter.out.persistence.repository.RoutineJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Use case for getting routine progress analytics and statistics.
 *
 * <p>Epic 07: Routine Progress & Visualization
 */
@Service
public class GetRoutineAnalyticsUseCase {

    private final RoutineJpaRepository routineRepository;
    private final HabitEntryJpaRepository entryRepository;

    public GetRoutineAnalyticsUseCase(
            RoutineJpaRepository routineRepository,
            HabitEntryJpaRepository entryRepository) {
        this.routineRepository = routineRepository;
        this.entryRepository = entryRepository;
    }

    /**
     * Gets comprehensive analytics for a routine.
     *
     * @param routineId the routine's ID
     * @return analytics data
     * @throws IllegalArgumentException if routine not found
     */
    @Transactional(readOnly = true)
    public RoutineAnalytics execute(String routineId) {
        // 1. Get routine
        UUID id = UUID.fromString(routineId);
        RoutineEntity routine = routineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Routine not found: " + routineId));

        // 2. Get all entries for this routine
        List<HabitEntryEntity> entries = entryRepository.findByRoutineIdOrderByDateAsc(id);

        // 3. Calculate analytics
        return calculateAnalytics(routine, entries);
    }

    private RoutineAnalytics calculateAnalytics(RoutineEntity routine, List<HabitEntryEntity> entries) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = routine.getStartDate();
        LocalDate endDate = routine.getExpectedEndDate();

        // Progress metrics
        int totalDays = 91;
        int daysCompleted = entries.size();
        double percentage = (daysCompleted * 100.0) / totalDays;
        int daysRemaining = totalDays - daysCompleted;

        // Days elapsed since start
        long daysElapsed = ChronoUnit.DAYS.between(startDate, today) + 1;
        if (daysElapsed < 0) daysElapsed = 0;
        if (daysElapsed > totalDays) daysElapsed = totalDays;

        // Completion rate (percentage of eligible days completed)
        double completionRate = daysElapsed > 0 ? (daysCompleted * 100.0) / daysElapsed : 0;

        // Weekly average
        long weeksElapsed = Math.max(1, daysElapsed / 7);
        double weeklyAverage = (double) daysCompleted / weeksElapsed;

        // Consistency score (A+ to F based on completion rate)
        String consistencyScore = calculateConsistencyScore(completionRate);

        // Pace calculation
        String pace = calculatePace(daysCompleted, daysElapsed, totalDays);

        // Days until completion
        long daysUntilCompletion = ChronoUnit.DAYS.between(today, endDate);

        return new RoutineAnalytics(
                routine.getId().toString(),
                new ProgressMetrics(daysCompleted, totalDays, percentage, daysRemaining, (int) daysElapsed),
                new StreakMetrics(
                        routine.getStreak().getCurrentStreak(),
                        routine.getStreak().getLongestStreak(),
                        routine.getStreak().getTotalCompletions(),
                        routine.getStreak().getHasUsedStrike()
                ),
                completionRate,
                weeklyAverage,
                consistencyScore,
                pace,
                daysUntilCompletion
        );
    }

    private String calculateConsistencyScore(double completionRate) {
        if (completionRate >= 95) return "A+";
        if (completionRate >= 90) return "A";
        if (completionRate >= 85) return "A-";
        if (completionRate >= 80) return "B+";
        if (completionRate >= 75) return "B";
        if (completionRate >= 70) return "B-";
        if (completionRate >= 65) return "C+";
        if (completionRate >= 60) return "C";
        return "F";
    }

    private String calculatePace(int completed, long elapsed, int total) {
        if (elapsed == 0) return "on_track";

        double expectedCompleted = (double) elapsed;  // Should complete once per day
        double actual = completed;

        if (actual >= expectedCompleted * 0.95) return "on_track";
        if (actual >= expectedCompleted * 0.80) return "slightly_behind";
        return "behind";
    }

    // DTOs

    public record RoutineAnalytics(
            String routineId,
            ProgressMetrics progress,
            StreakMetrics streaks,
            double completionRate,
            double weeklyAverage,
            String consistencyScore,
            String pace,
            long daysUntilCompletion
    ) {}

    public record ProgressMetrics(
            int daysCompleted,
            int totalDays,
            double percentage,
            int daysRemaining,
            int daysElapsed
    ) {}

    public record StreakMetrics(
            int current,
            int longest,
            int totalCompletions,
            boolean hasUsedStrike
    ) {}
}
