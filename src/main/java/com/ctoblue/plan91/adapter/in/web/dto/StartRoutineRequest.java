package com.ctoblue.plan91.adapter.in.web.dto;

import com.ctoblue.plan91.domain.routine.RecurrenceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO for starting a new routine.
 *
 * <p>This is sent by the client when starting a routine.
 */
public record StartRoutineRequest(
        @NotBlank(message = "Practitioner ID is required")
        String practitionerId,

        @NotBlank(message = "Habit ID is required")
        String habitId,

        @NotNull(message = "Recurrence type is required")
        RecurrenceType recurrenceType,

        Set<String> specificDays,
        String nthDay,
        Integer nthWeek,
        LocalDate startDate,

        @Min(value = 1, message = "Target days must be at least 1")
        Integer targetDays  // How many completions needed (default: 91)
) {
}
