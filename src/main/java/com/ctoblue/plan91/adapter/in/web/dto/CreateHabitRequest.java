package com.ctoblue.plan91.adapter.in.web.dto;

import com.ctoblue.plan91.domain.habit.TrackingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating a new habit.
 *
 * <p>This is sent by the client when creating a habit.
 */
public record CreateHabitRequest(
        @NotBlank(message = "Practitioner ID is required")
        String practitionerId,

        @NotBlank(message = "Name is required")
        String name,

        String description,

        @NotNull(message = "Tracking type is required")
        TrackingType trackingType,

        String numericUnit,
        Integer numericMin,
        Integer numericMax,
        Integer numericTarget,

        boolean isPublic,
        boolean isPrivate
) {
}
