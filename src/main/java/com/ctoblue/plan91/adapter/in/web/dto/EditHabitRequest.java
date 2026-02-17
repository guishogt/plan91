package com.ctoblue.plan91.adapter.in.web.dto;

/**
 * DTO for editing an existing habit.
 *
 * <p>All fields are optional. Only non-null fields will be updated.
 */
public record EditHabitRequest(
        String name,
        String description,
        String numericUnit,
        Integer numericMin,
        Integer numericMax,
        Integer numericTarget,
        Boolean isPublic,
        Boolean isPrivate
) {
}
