package com.ctoblue.plan91.domain.routine;

/**
 * Enum representing days of the week for recurrence rules.
 *
 * <p>This is our domain enum, separate from java.time.DayOfWeek,
 * to avoid coupling domain logic to Java time APIs.
 */
public enum DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
}
