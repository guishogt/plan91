package com.ctoblue.plan91.application.usecase.analytics;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntryEntity;
import com.ctoblue.plan91.adapter.out.persistence.entity.RoutineEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitEntryJpaRepository;
import com.ctoblue.plan91.adapter.out.persistence.repository.RoutineJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Use case for generating calendar heatmap data.
 *
 * <p>Epic 08: Analytics & Statistics (PLAN91-091)
 * Generates data for GitHub-style contribution heatmap showing activity intensity.
 */
@Service
public class GetHeatmapDataUseCase {

    private final RoutineJpaRepository routineRepository;
    private final HabitEntryJpaRepository entryRepository;

    public GetHeatmapDataUseCase(
            RoutineJpaRepository routineRepository,
            HabitEntryJpaRepository entryRepository) {
        this.routineRepository = routineRepository;
        this.entryRepository = entryRepository;
    }

    /**
     * Gets heatmap data for a practitioner showing daily activity intensity.
     *
     * @param practitionerId the practitioner's ID
     * @param startDate the start date
     * @param endDate the end date
     * @return heatmap data
     */
    @Transactional(readOnly = true)
    public HeatmapData execute(String practitionerId, LocalDate startDate, LocalDate endDate) {
        UUID id = UUID.fromString(practitionerId);

        // Get all routines
        List<RoutineEntity> routines = routineRepository.findByPractitionerId(id);
        if (routines.isEmpty()) {
            return new HeatmapData(List.of(), 0, 0);
        }

        // Get all entries for these routines
        List<UUID> routineIds = routines.stream().map(RoutineEntity::getId).toList();
        List<HabitEntryEntity> entries = entryRepository.findByRoutineIdIn(routineIds);

        // Count completions per day
        Map<LocalDate, Long> completionsByDate = entries.stream()
                .filter(e -> Boolean.TRUE.equals(e.getCompleted()))
                .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
                .collect(Collectors.groupingBy(HabitEntryEntity::getDate, Collectors.counting()));

        // Find max completions for intensity scaling
        int maxCompletions = completionsByDate.values().stream()
                .mapToInt(Long::intValue)
                .max()
                .orElse(1);

        // Create day records for each date
        List<DayRecord> days = new ArrayList<>();
        LocalDate current = startDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (!current.isAfter(endDate)) {
            int count = completionsByDate.getOrDefault(current, 0L).intValue();
            int intensity = calculateIntensity(count, maxCompletions);

            days.add(new DayRecord(
                    current.format(formatter),
                    count,
                    intensity,
                    current.getDayOfWeek().getValue()
            ));

            current = current.plusDays(1);
        }

        // Calculate total completions
        int totalCompletions = completionsByDate.values().stream()
                .mapToInt(Long::intValue)
                .sum();

        return new HeatmapData(days, totalCompletions, maxCompletions);
    }

    /**
     * Gets heatmap data for the past year (52 weeks).
     *
     * @param practitionerId the practitioner's ID
     * @return heatmap data for past year
     */
    @Transactional(readOnly = true)
    public HeatmapData getPastYearHeatmap(String practitionerId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(52);
        return execute(practitionerId, startDate, endDate);
    }

    /**
     * Calculates intensity level (0-4) based on completion count.
     * Similar to GitHub's contribution graph.
     *
     * @param count completion count for the day
     * @param max maximum completions in the dataset
     * @return intensity level (0-4)
     */
    private int calculateIntensity(int count, int max) {
        if (count == 0) return 0;
        if (max == 0) return 0;

        double ratio = (double) count / max;

        if (ratio >= 0.75) return 4;  // Very active
        if (ratio >= 0.50) return 3;  // Active
        if (ratio >= 0.25) return 2;  // Moderately active
        if (ratio > 0) return 1;      // Lightly active

        return 0;  // No activity
    }

    // DTOs

    public record HeatmapData(
            List<DayRecord> days,
            int totalCompletions,
            int maxCompletions
    ) {}

    public record DayRecord(
            String date,           // ISO format: yyyy-MM-dd
            int count,             // Number of completions
            int intensity,         // 0-4 intensity level
            int dayOfWeek          // 1-7 (Monday-Sunday)
    ) {}
}
