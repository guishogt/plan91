package com.ctoblue.plan91.domain.routine;

/**
 * Enum representing recurrence patterns for routines.
 *
 * <p>Defines when a habit is expected to be completed:
 * <ul>
 *   <li>DAILY - Every day</li>
 *   <li>WEEKDAYS - Monday through Friday</li>
 *   <li>WEEKENDS - Saturday and Sunday</li>
 *   <li>SPECIFIC_DAYS - Selected days (e.g., Mon, Wed, Fri)</li>
 *   <li>NTH_DAY_OF_MONTH - Specific weekday of month (e.g., first Monday)</li>
 * </ul>
 */
public enum RecurrenceType {
    /**
     * Expected every day.
     */
    DAILY,

    /**
     * Expected Monday through Friday.
     */
    WEEKDAYS,

    /**
     * Expected Saturday and Sunday.
     */
    WEEKENDS,

    /**
     * Expected on specific days of the week (e.g., Mon, Wed, Fri).
     * Requires specificDays to be set in RecurrenceRule.
     */
    SPECIFIC_DAYS,

    /**
     * Expected on the Nth occurrence of a weekday in each month.
     * Example: First Monday, Third Friday
     * Requires nthDay and nthWeek to be set in RecurrenceRule.
     */
    NTH_DAY_OF_MONTH
}
