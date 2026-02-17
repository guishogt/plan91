package com.ctoblue.plan91.application.usecase.routine;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntryEntity;
import com.ctoblue.plan91.adapter.out.persistence.entity.RoutineEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitEntryJpaRepository;
import com.ctoblue.plan91.adapter.out.persistence.repository.RoutineJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Use case for getting calendar data for a routine.
 *
 * <p>Returns all entries for a specific month, along with scheduled days
 * based on the routine's recurrence pattern.
 *
 * <p>Epic 07: Routine Progress & Visualization
 */
@Service
public class GetCalendarDataUseCase {

    private final RoutineJpaRepository routineRepository;
    private final HabitEntryJpaRepository entryRepository;

    public GetCalendarDataUseCase(
            RoutineJpaRepository routineRepository,
            HabitEntryJpaRepository entryRepository) {
        this.routineRepository = routineRepository;
        this.entryRepository = entryRepository;
    }

    /**
     * Gets calendar data for a routine for a specific month.
     *
     * @param routineId the routine's ID
     * @param yearMonth the year-month (e.g., "2026-02")
     * @return calendar data
     * @throws IllegalArgumentException if routine not found
     */
    @Transactional(readOnly = true)
    public CalendarData execute(String routineId, String yearMonth) {
        // 1. Parse year-month
        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDate firstDay = ym.atDay(1);
        LocalDate lastDay = ym.atEndOfMonth();

        // 2. Get routine
        UUID id = UUID.fromString(routineId);
        RoutineEntity routine = routineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Routine not found: " + routineId));

        // 3. Get entries for this month
        List<HabitEntryEntity> entries = entryRepository.findByRoutineIdAndDateBetween(id, firstDay, lastDay);

        // 4. Calculate scheduled days for this month based on recurrence
        List<LocalDate> scheduledDays = calculateScheduledDays(routine, firstDay, lastDay);

        // 5. Calculate stats
        long completedDays = entries.stream().filter(HabitEntryEntity::getCompleted).count();
        double completionRate = scheduledDays.isEmpty() ? 0 : (completedDays * 100.0) / scheduledDays.size();

        // 6. Build calendar data
        List<CalendarEntry> calendarEntries = entries.stream()
                .map(e -> new CalendarEntry(
                        e.getDate(),
                        e.getCompleted(),
                        e.getValue(),
                        e.getNotes()
                ))
                .collect(Collectors.toList());

        return new CalendarData(
                routineId,
                yearMonth,
                calendarEntries,
                scheduledDays,
                new MonthStats(
                        ym.lengthOfMonth(),
                        (int) completedDays,
                        completionRate
                )
        );
    }

    /**
     * Calculates which days in the month are scheduled based on recurrence rule.
     * Simplified version - returns all days in range for now.
     * TODO: Implement full recurrence pattern logic (DAILY, WEEKDAYS, SPECIFIC_DAYS, etc.)
     */
    private List<LocalDate> calculateScheduledDays(RoutineEntity routine, LocalDate start, LocalDate end) {
        List<LocalDate> scheduled = new ArrayList<>();

        // Only include days within the routine's active period
        LocalDate routineStart = routine.getStartDate();
        LocalDate routineEnd = routine.getExpectedEndDate();

        LocalDate current = start.isBefore(routineStart) ? routineStart : start;
        LocalDate last = end.isAfter(routineEnd) ? routineEnd : end;

        while (!current.isAfter(last)) {
            // TODO: Check if current day matches recurrence pattern
            // For now, include all days (assumes DAILY recurrence)
            scheduled.add(current);
            current = current.plusDays(1);
        }

        return scheduled;
    }

    // DTOs

    public record CalendarData(
            String routineId,
            String yearMonth,
            List<CalendarEntry> entries,
            List<LocalDate> scheduledDays,
            MonthStats stats
    ) {}

    public record CalendarEntry(
            LocalDate date,
            boolean completed,
            Integer value,
            String notes
    ) {}

    public record MonthStats(
            int totalDays,
            int completedDays,
            double completionRate
    ) {}
}
