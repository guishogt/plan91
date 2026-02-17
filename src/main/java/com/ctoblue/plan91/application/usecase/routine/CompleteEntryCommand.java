package com.ctoblue.plan91.application.usecase.routine;

import java.time.LocalDate;

/**
 * Command to complete a habit entry for a specific date.
 *
 * @param routineId the routine to complete
 * @param date the date of completion (default: today)
 * @param value numeric value for numeric habits (null for boolean)
 * @param notes optional notes about the completion
 */
public record CompleteEntryCommand(
        String routineId,
        LocalDate date,
        Integer value,
        String notes
) {
    public CompleteEntryCommand {
        if (date == null) {
            date = LocalDate.now();
        }
    }
}
