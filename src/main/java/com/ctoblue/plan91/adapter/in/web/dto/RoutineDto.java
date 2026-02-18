package com.ctoblue.plan91.adapter.in.web.dto;

import com.ctoblue.plan91.domain.habit.TrackingType;
import com.ctoblue.plan91.domain.routine.RecurrenceType;
import com.ctoblue.plan91.domain.routine.RoutineStatus;
import com.ctoblue.plan91.domain.routine.RoutineType;

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
        TrackingType trackingType,
        String numericUnit,
        String practitionerId,
        RoutineType routineType,  // ROUTINE or TRACKER
        RecurrenceType recurrenceType,
        Set<String> specificDays,
        String nthDay,
        Integer nthWeek,
        Integer targetDays,  // null for TRACKER
        LocalDate startDate,
        LocalDate expectedEndDate,  // null for TRACKER
        RoutineStatus status,
        int currentStreak,
        int longestStreak,
        int totalCompletions,
        boolean hasUsedStrike,
        LocalDate lastCompletionDate
) {
}
