package com.ctoblue.plan91.adapter.in.web.dto;

import java.time.Instant;
import java.time.LocalDate;

/**
 * DTO for habit entry responses.
 *
 * <p>Used when returning habit entry data to clients.
 */
public record HabitEntryDto(
        String id,
        String routineId,
        LocalDate date,
        boolean completed,
        Integer value,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
}
