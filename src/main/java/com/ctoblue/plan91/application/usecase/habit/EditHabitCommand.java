package com.ctoblue.plan91.application.usecase.habit;

/**
 * Command to edit an existing habit.
 *
 * <p>All fields except habitId are optional. Only non-null fields will be updated.
 *
 * @param habitId the habit to edit (required)
 * @param name new name (optional)
 * @param description new description (optional)
 * @param numericUnit new unit name for numeric habits (optional)
 * @param numericMin new minimum value (optional)
 * @param numericMax new maximum value (optional)
 * @param numericTarget new target value (optional)
 * @param isPublic new public visibility (optional)
 * @param isPrivate new private visibility (optional)
 */
public record EditHabitCommand(
        String habitId,
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
