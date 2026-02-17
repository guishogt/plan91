package com.ctoblue.plan91.adapter.in.web.dto;

import com.ctoblue.plan91.domain.routine.RecurrenceType;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO for updating an existing routine.
 *
 * <p>All fields are optional - only provided fields will be updated.
 */
public record UpdateRoutineRequest(
        LocalDate startDate,
        RecurrenceType recurrenceType,
        Set<String> specificDays,
        String nthDay,
        Integer nthWeek,

        @Min(value = 1, message = "Target days must be at least 1")
        Integer targetDays
) {
}
