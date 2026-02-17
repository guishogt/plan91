package com.ctoblue.plan91.adapter.in.web.controller;

import com.ctoblue.plan91.application.usecase.analytics.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for analytics and statistics.
 *
 * <p>Epic 08: Analytics & Statistics (PLAN91-092, PLAN91-093)
 * <p>Endpoints:
 * <ul>
 *   <li>GET /api/analytics/practitioners/{id}/statistics - Overall practitioner statistics</li>
 *   <li>GET /api/analytics/practitioners/{id}/habits - Habit-based analytics</li>
 *   <li>GET /api/analytics/practitioners/{id}/dashboard - Dashboard summary</li>
 *   <li>GET /api/analytics/charts/completion-trend - Completion trend chart data</li>
 *   <li>GET /api/analytics/charts/habit-comparison - Habit comparison chart data</li>
 *   <li>GET /api/analytics/charts/weekly-aggregation - Weekly aggregation chart data</li>
 *   <li>GET /api/analytics/charts/numeric-progress/{routineId} - Numeric progress chart data</li>
 *   <li>GET /api/analytics/heatmap - Activity heatmap data</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final GetPractitionerStatisticsUseCase getPractitionerStatisticsUseCase;
    private final GetHabitAnalyticsUseCase getHabitAnalyticsUseCase;
    private final GetChartDataUseCase getChartDataUseCase;
    private final GetHeatmapDataUseCase getHeatmapDataUseCase;

    public AnalyticsController(
            GetPractitionerStatisticsUseCase getPractitionerStatisticsUseCase,
            GetHabitAnalyticsUseCase getHabitAnalyticsUseCase,
            GetChartDataUseCase getChartDataUseCase,
            GetHeatmapDataUseCase getHeatmapDataUseCase) {
        this.getPractitionerStatisticsUseCase = getPractitionerStatisticsUseCase;
        this.getHabitAnalyticsUseCase = getHabitAnalyticsUseCase;
        this.getChartDataUseCase = getChartDataUseCase;
        this.getHeatmapDataUseCase = getHeatmapDataUseCase;
    }

    /**
     * Gets overall statistics for a practitioner.
     *
     * @param practitionerId the practitioner's ID
     * @return practitioner statistics
     */
    @GetMapping("/practitioners/{practitionerId}/statistics")
    public ResponseEntity<GetPractitionerStatisticsUseCase.PractitionerStatistics> getPractitionerStatistics(
            @PathVariable String practitionerId) {
        GetPractitionerStatisticsUseCase.PractitionerStatistics stats =
                getPractitionerStatisticsUseCase.execute(practitionerId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Gets habit-based analytics for a practitioner.
     *
     * @param practitionerId the practitioner's ID
     * @return habit analytics
     */
    @GetMapping("/practitioners/{practitionerId}/habits")
    public ResponseEntity<GetHabitAnalyticsUseCase.HabitAnalyticsSummary> getHabitAnalytics(
            @PathVariable String practitionerId) {
        GetHabitAnalyticsUseCase.HabitAnalyticsSummary analytics =
                getHabitAnalyticsUseCase.execute(practitionerId);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Gets dashboard summary combining key statistics and recent activity.
     *
     * @param practitionerId the practitioner's ID
     * @return dashboard summary
     */
    @GetMapping("/practitioners/{practitionerId}/dashboard")
    public ResponseEntity<DashboardSummary> getDashboardSummary(
            @PathVariable String practitionerId) {
        // Combine multiple analytics into a single dashboard view
        GetPractitionerStatisticsUseCase.PractitionerStatistics stats =
                getPractitionerStatisticsUseCase.execute(practitionerId);

        GetHabitAnalyticsUseCase.HabitAnalyticsSummary habitAnalytics =
                getHabitAnalyticsUseCase.execute(practitionerId);

        DashboardSummary summary = new DashboardSummary(stats, habitAnalytics);
        return ResponseEntity.ok(summary);
    }

    /**
     * Gets completion trend chart data for line charts.
     *
     * @param practitionerId the practitioner's ID
     * @param startDate the start date (optional, defaults to 30 days ago)
     * @param endDate the end date (optional, defaults to today)
     * @return chart data
     */
    @GetMapping("/charts/completion-trend")
    public ResponseEntity<GetChartDataUseCase.LineChartData> getCompletionTrend(
            @RequestParam String practitionerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        GetChartDataUseCase.LineChartData chartData =
                getChartDataUseCase.getCompletionTrendData(practitionerId, startDate, endDate);
        return ResponseEntity.ok(chartData);
    }

    /**
     * Gets habit comparison chart data for bar charts.
     *
     * @param practitionerId the practitioner's ID
     * @return chart data
     */
    @GetMapping("/charts/habit-comparison")
    public ResponseEntity<GetChartDataUseCase.BarChartData> getHabitComparison(
            @RequestParam String practitionerId) {
        GetChartDataUseCase.BarChartData chartData =
                getChartDataUseCase.getHabitComparisonData(practitionerId);
        return ResponseEntity.ok(chartData);
    }

    /**
     * Gets weekly aggregation chart data.
     *
     * @param practitionerId the practitioner's ID
     * @param weeks number of weeks to include (default 8)
     * @return chart data
     */
    @GetMapping("/charts/weekly-aggregation")
    public ResponseEntity<GetChartDataUseCase.BarChartData> getWeeklyAggregation(
            @RequestParam String practitionerId,
            @RequestParam(defaultValue = "8") int weeks) {
        GetChartDataUseCase.BarChartData chartData =
                getChartDataUseCase.getWeeklyAggregationData(practitionerId, weeks);
        return ResponseEntity.ok(chartData);
    }

    /**
     * Gets numeric progress chart data for routines with numeric tracking.
     *
     * @param routineId the routine's ID
     * @return chart data
     */
    @GetMapping("/charts/numeric-progress/{routineId}")
    public ResponseEntity<GetChartDataUseCase.LineChartData> getNumericProgress(
            @PathVariable String routineId) {
        GetChartDataUseCase.LineChartData chartData =
                getChartDataUseCase.getNumericProgressData(routineId);
        return ResponseEntity.ok(chartData);
    }

    /**
     * Gets activity heatmap data.
     *
     * @param practitionerId the practitioner's ID
     * @param startDate the start date (optional)
     * @param endDate the end date (optional)
     * @return heatmap data
     */
    @GetMapping("/heatmap")
    public ResponseEntity<GetHeatmapDataUseCase.HeatmapData> getHeatmap(
            @RequestParam String practitionerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        GetHeatmapDataUseCase.HeatmapData heatmapData;

        if (startDate != null && endDate != null) {
            heatmapData = getHeatmapDataUseCase.execute(practitionerId, startDate, endDate);
        } else {
            // Default to past year
            heatmapData = getHeatmapDataUseCase.getPastYearHeatmap(practitionerId);
        }

        return ResponseEntity.ok(heatmapData);
    }

    // DTOs

    /**
     * Dashboard summary combining multiple analytics views.
     */
    public record DashboardSummary(
            GetPractitionerStatisticsUseCase.PractitionerStatistics statistics,
            GetHabitAnalyticsUseCase.HabitAnalyticsSummary habitAnalytics
    ) {}
}
