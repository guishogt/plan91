package com.ctoblue.plan91.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * DTO for completing a habit entry.
 *
 * <p>This is sent by the client when marking a habit as complete for a specific date.
 */
public record CompleteEntryRequest(
        @NotBlank(message = "Routine ID is required")
        String routineId,

        LocalDate date,
        Integer value,
        String notes
) {
}
