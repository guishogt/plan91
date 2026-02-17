package com.ctoblue.plan91.adapter.in.web.dto;

import com.ctoblue.plan91.domain.habit.TrackingType;

import java.time.Instant;

/**
 * DTO for habit responses.
 *
 * <p>Used when returning habit data to clients.
 */
public record HabitDto(
        String id,
        String creatorId,
        String name,
        String description,
        TrackingType trackingType,
        String numericUnit,
        Integer numericMin,
        Integer numericMax,
        Integer numericTarget,
        boolean isPublic,
        boolean isPrivate,
        String sourceHabitId,
        Instant createdAt,
        Instant updatedAt
) {
}
