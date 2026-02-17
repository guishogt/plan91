package com.ctoblue.plan91.adapter.in.web.dto;

import com.ctoblue.plan91.domain.routine.RecurrenceType;
import com.ctoblue.plan91.domain.routine.RoutineStatus;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO for routine responses.
 *
 * <p>Used when returning routine data to clients.
 */
public record RoutineDto(
        String id,
        String habitId,
        String habitName,
        String practitionerId,
        RecurrenceType recurrenceType,
        Set<String> specificDays,
        String nthDay,
        Integer nthWeek,
        int targetDays,
        LocalDate startDate,
        LocalDate expectedEndDate,
        RoutineStatus status,
        int currentStreak,
        int longestStreak,
        int totalCompletions,
        boolean hasUsedStrike,
        LocalDate lastCompletionDate
) {
}
