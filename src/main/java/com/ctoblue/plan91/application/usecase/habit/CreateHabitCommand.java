package com.ctoblue.plan91.application.usecase.habit;

import com.ctoblue.plan91.domain.habit.TrackingType;

/**
 * Command to create a new habit.
 *
 * @param practitionerId the ID of the practitioner creating the habit
 * @param name the habit name
 * @param description optional description
 * @param trackingType BOOLEAN or NUMERIC
 * @param numericUnit unit name for numeric habits (e.g., "pages", "miles")
 * @param numericMin minimum value for numeric habits
 * @param numericMax maximum value for numeric habits
 * @param numericTarget target value for numeric habits
 * @param isPublic can others see this habit?
 * @param isPrivate only visible to creator?
 */
public record CreateHabitCommand(
        String practitionerId,
        String name,
        String description,
        TrackingType trackingType,
        String numericUnit,
        Integer numericMin,
        Integer numericMax,
        Integer numericTarget,
        boolean isPublic,
        boolean isPrivate
) {
}
