package com.ctoblue.plan91.application.usecase.routine;

import com.ctoblue.plan91.domain.routine.RecurrenceType;
import com.ctoblue.plan91.domain.routine.Routine;

import java.time.LocalDate;
import java.util.Set;

/**
 * Command to start a new routine.
 *
 * @param practitionerId the practitioner starting the routine
 * @param habitId the habit to practice
 * @param recurrenceType daily, weekdays, weekends, specific days, etc.
 * @param specificDays for SPECIFIC_DAYS type (e.g., Monday, Wednesday, Friday)
 * @param nthDay for NTH_DAY_OF_MONTH type
 * @param nthWeek for NTH_DAY_OF_MONTH type (1-4)
 * @param startDate when to start (default: today)
 * @param targetDays how many completions needed (default: 91)
 */
public record StartRoutineCommand(
        String practitionerId,
        String habitId,
        RecurrenceType recurrenceType,
        Set<String> specificDays,  // Day names: "MONDAY", "WEDNESDAY", etc.
        String nthDay,  // Day name for NTH_DAY_OF_MONTH
        Integer nthWeek,  // Week number (1-4)
        LocalDate startDate,
        Integer targetDays  // How many completions needed (default: 91)
) {
    public StartRoutineCommand {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (targetDays == null) {
            targetDays = Routine.DEFAULT_TARGET_DAYS;
        }
    }
}
