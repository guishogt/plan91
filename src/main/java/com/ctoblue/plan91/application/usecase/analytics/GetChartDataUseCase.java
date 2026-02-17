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
 * Use case for generating chart data formatted for Chart.js.
 *
 * <p>Epic 08: Analytics & Statistics (PLAN91-090)
 * Provides data in Chart.js compatible format for various visualizations.
 */
@Service
public class GetChartDataUseCase {

    private final RoutineJpaRepository routineRepository;
    private final HabitEntryJpaRepository entryRepository;

    public GetChartDataUseCase(
            RoutineJpaRepository routineRepository,
            HabitEntryJpaRepository entryRepository) {
        this.routineRepository = routineRepository;
        this.entryRepository = entryRepository;
    }

    /**
     * Gets completion trend data for line charts.
     * Shows daily completion count over the specified date range.
     *
     * @param practitionerId the practitioner's ID
     * @param startDate the start date
     * @param endDate the end date
     * @return chart data for line chart
     */
    @Transactional(readOnly = true)
    public LineChartData getCompletionTrendData(String practitionerId, LocalDate startDate, LocalDate endDate) {
        UUID id = UUID.fromString(practitionerId);

        // Get all routines
        List<RoutineEntity> routines = routineRepository.findByPractitionerId(id);
        if (routines.isEmpty()) {
            return new LineChartData(List.of(), List.of());
        }

        // Get all entries for these routines
        List<UUID> routineIds = routines.stream().map(RoutineEntity::getId).toList();
        List<HabitEntryEntity> entries = entryRepository.findByRoutineIdIn(routineIds);

        // Group completions by date
        Map<LocalDate, Long> completionsByDate = entries.stream()
                .filter(e -> Boolean.TRUE.equals(e.getCompleted()))
                .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
                .collect(Collectors.groupingBy(HabitEntryEntity::getDate, Collectors.counting()));

        // Create date labels and data points
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();

        LocalDate current = startDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");

        while (!current.isAfter(endDate)) {
            labels.add(current.format(formatter));
            data.add(completionsByDate.getOrDefault(current, 0L).intValue());
            current = current.plusDays(1);
        }

        return new LineChartData(labels, data);
    }

    /**
     * Gets habit comparison data for bar charts.
     * Shows total completions per habit.
     *
     * @param practitionerId the practitioner's ID
     * @return chart data for bar chart
     */
    @Transactional(readOnly = true)
    public BarChartData getHabitComparisonData(String practitionerId) {
        UUID id = UUID.fromString(practitionerId);

        // Get all routines
        List<RoutineEntity> routines = routineRepository.findByPractitionerId(id);

        // Group by habit and sum completions
        Map<String, Integer> completionsByHabit = routines.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getHabit().getName(),
                        Collectors.summingInt(r -> r.getStreak().getTotalCompletions())
                ));

        // Sort by completion count
        List<Map.Entry<String, Integer>> sorted = completionsByHabit.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)  // Top 10 habits
                .toList();

        List<String> labels = sorted.stream().map(Map.Entry::getKey).toList();
        List<Integer> data = sorted.stream().map(Map.Entry::getValue).toList();

        return new BarChartData(labels, data);
    }

    /**
     * Gets weekly aggregation data for bar charts.
     * Shows total completions per week over the past N weeks.
     *
     * @param practitionerId the practitioner's ID
     * @param weeks number of weeks to include
     * @return chart data for weekly bar chart
     */
    @Transactional(readOnly = true)
    public BarChartData getWeeklyAggregationData(String practitionerId, int weeks) {
        UUID id = UUID.fromString(practitionerId);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(weeks);

        // Get all routines
        List<RoutineEntity> routines = routineRepository.findByPractitionerId(id);
        if (routines.isEmpty()) {
            return new BarChartData(List.of(), List.of());
        }

        // Get all entries
        List<UUID> routineIds = routines.stream().map(RoutineEntity::getId).toList();
        List<HabitEntryEntity> entries = entryRepository.findByRoutineIdIn(routineIds);

        // Group by week
        Map<String, Integer> completionsByWeek = new LinkedHashMap<>();

        LocalDate weekStart = startDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");

        while (!weekStart.isAfter(endDate)) {
            LocalDate weekEnd = weekStart.plusDays(6);
            if (weekEnd.isAfter(endDate)) {
                weekEnd = endDate;
            }

            String label = weekStart.format(formatter) + " - " + weekEnd.format(formatter);
            LocalDate finalWeekStart = weekStart;
            LocalDate finalWeekEnd = weekEnd;

            int count = (int) entries.stream()
                    .filter(e -> Boolean.TRUE.equals(e.getCompleted()))
                    .filter(e -> !e.getDate().isBefore(finalWeekStart) && !e.getDate().isAfter(finalWeekEnd))
                    .count();

            completionsByWeek.put(label, count);
            weekStart = weekStart.plusWeeks(1);
        }

        return new BarChartData(
                new ArrayList<>(completionsByWeek.keySet()),
                new ArrayList<>(completionsByWeek.values())
        );
    }

    /**
     * Gets numeric habit progress data for line charts.
     * Shows the numeric value progression over time for habits with numeric tracking.
     *
     * @param routineId the routine's ID
     * @return chart data for line chart
     */
    @Transactional(readOnly = true)
    public LineChartData getNumericProgressData(String routineId) {
        UUID id = UUID.fromString(routineId);

        // Get all entries for this routine
        List<HabitEntryEntity> entries = entryRepository.findByRoutineIdOrderByDateAsc(id);

        // Filter for entries with numeric values
        List<HabitEntryEntity> numericEntries = entries.stream()
                .filter(e -> e.getValue() != null)
                .toList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");

        List<String> labels = numericEntries.stream()
                .map(e -> e.getDate().format(formatter))
                .toList();

        List<Integer> data = numericEntries.stream()
                .map(HabitEntryEntity::getValue)
                .toList();

        return new LineChartData(labels, data);
    }

    // DTOs

    public record LineChartData(
            List<String> labels,
            List<Integer> data
    ) {}

    public record BarChartData(
            List<String> labels,
            List<Integer> data
    ) {}
}
