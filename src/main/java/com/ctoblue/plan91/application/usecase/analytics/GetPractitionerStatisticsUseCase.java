package com.ctoblue.plan91.application.usecase.analytics;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntryEntity;
import com.ctoblue.plan91.adapter.out.persistence.entity.RoutineEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitEntryJpaRepository;
import com.ctoblue.plan91.adapter.out.persistence.repository.RoutineJpaRepository;
import com.ctoblue.plan91.domain.routine.RoutineStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Use case for getting overall practitioner statistics across all habits.
 *
 * <p>Epic 08: Analytics & Statistics (PLAN91-088)
 */
@Service
public class GetPractitionerStatisticsUseCase {

    private final RoutineJpaRepository routineRepository;
    private final HabitEntryJpaRepository entryRepository;

    public GetPractitionerStatisticsUseCase(
            RoutineJpaRepository routineRepository,
            HabitEntryJpaRepository entryRepository) {
        this.routineRepository = routineRepository;
        this.entryRepository = entryRepository;
    }

    /**
     * Gets comprehensive statistics for a practitioner across all their routines.
     *
     * @param practitionerId the practitioner's ID
     * @return aggregate statistics
     */
    @Transactional(readOnly = true)
    public PractitionerStatistics execute(String practitionerId) {
        UUID id = UUID.fromString(practitionerId);

        // Get all active routines
        List<RoutineEntity> activeRoutines = routineRepository.findByPractitionerIdAndStatus(id, RoutineStatus.ACTIVE);

        // Get all routines (including completed)
        List<RoutineEntity> allRoutines = routineRepository.findByPractitionerId(id);

        // Calculate statistics
        return calculateStatistics(id, activeRoutines, allRoutines);
    }

    private PractitionerStatistics calculateStatistics(
            UUID practitionerId,
            List<RoutineEntity> activeRoutines,
            List<RoutineEntity> allRoutines) {

        LocalDate today = LocalDate.now();

        // Active routines count
        int activeRoutinesCount = activeRoutines.size();
        int totalRoutinesCount = allRoutines.size();
        int completedRoutinesCount = (int) allRoutines.stream()
                .filter(r -> r.getStatus() == RoutineStatus.COMPLETED)
                .count();

        // Total completions across all routines
        int totalCompletions = allRoutines.stream()
                .mapToInt(r -> r.getStreak().getTotalCompletions())
                .sum();

        // Longest streak across all routines
        int longestStreak = allRoutines.stream()
                .mapToInt(r -> r.getStreak().getLongestStreak())
                .max()
                .orElse(0);

        // Current active streak (max among active routines)
        int currentStreak = activeRoutines.stream()
                .mapToInt(r -> r.getStreak().getCurrentStreak())
                .max()
                .orElse(0);

        // Overall completion rate for active routines
        double overallCompletionRate = calculateOverallCompletionRate(activeRoutines);

        // Recent activity (last 7 days)
        int last7DaysCompletions = 0;
        int last30DaysCompletions = 0;

        for (RoutineEntity routine : allRoutines) {
            List<HabitEntryEntity> entries = entryRepository.findByRoutineIdOrderByDateAsc(routine.getId());

            for (HabitEntryEntity entry : entries) {
                if (Boolean.TRUE.equals(entry.getCompleted())) {
                    long daysAgo = ChronoUnit.DAYS.between(entry.getDate(), today);
                    if (daysAgo <= 7) {
                        last7DaysCompletions++;
                    }
                    if (daysAgo <= 30) {
                        last30DaysCompletions++;
                    }
                }
            }
        }

        // Consistency score
        String consistencyScore = calculateConsistencyScore(overallCompletionRate);

        // Total days practiced (unique dates across all entries)
        long totalDaysPracticed = entryRepository.findByRoutineIdIn(
                allRoutines.stream().map(RoutineEntity::getId).toList()
        ).stream()
                .filter(e -> Boolean.TRUE.equals(e.getCompleted()))
                .map(HabitEntryEntity::getDate)
                .distinct()
                .count();

        return new PractitionerStatistics(
                practitionerId.toString(),
                new RoutinesSummary(activeRoutinesCount, totalRoutinesCount, completedRoutinesCount),
                new StreaksSummary(currentStreak, longestStreak, totalCompletions),
                new ActivitySummary(last7DaysCompletions, last30DaysCompletions, (int) totalDaysPracticed),
                overallCompletionRate,
                consistencyScore
        );
    }

    private double calculateOverallCompletionRate(List<RoutineEntity> activeRoutines) {
        if (activeRoutines.isEmpty()) {
            return 0.0;
        }

        LocalDate today = LocalDate.now();
        int totalExpected = 0;
        int totalCompleted = 0;

        for (RoutineEntity routine : activeRoutines) {
            long daysElapsed = ChronoUnit.DAYS.between(routine.getStartDate(), today) + 1;
            if (daysElapsed > 0 && daysElapsed <= 91) {
                totalExpected += (int) daysElapsed;
                totalCompleted += routine.getStreak().getTotalCompletions();
            }
        }

        return totalExpected > 0 ? (totalCompleted * 100.0) / totalExpected : 0.0;
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

    // DTOs

    public record PractitionerStatistics(
            String practitionerId,
            RoutinesSummary routines,
            StreaksSummary streaks,
            ActivitySummary activity,
            double overallCompletionRate,
            String consistencyScore
    ) {}

    public record RoutinesSummary(
            int active,
            int total,
            int completed
    ) {}

    public record StreaksSummary(
            int currentLongest,
            int allTimeLongest,
            int totalCompletions
    ) {}

    public record ActivitySummary(
            int last7Days,
            int last30Days,
            int totalDaysPracticed
    ) {}
}
