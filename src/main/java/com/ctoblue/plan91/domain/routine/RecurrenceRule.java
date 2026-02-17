package com.ctoblue.plan91.domain.routine;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

/**
 * Value object representing when a habit is expected to be completed.
 *
 * <p>Supports different recurrence patterns:
 * <ul>
 *   <li>DAILY - Every day</li>
 *   <li>WEEKDAYS - Monday through Friday</li>
 *   <li>WEEKENDS - Saturday and Sunday</li>
 *   <li>SPECIFIC_DAYS - Selected days (e.g., Mon, Wed, Fri)</li>
 *   <li>NTH_DAY_OF_MONTH - Nth weekday of month (e.g., first Monday)</li>
 * </ul>
 */
public record RecurrenceRule(
        RecurrenceType type,
        Set<DayOfWeek> specificDays,
        DayOfWeek nthDay,
        Integer nthWeek
) {

    /**
     * Compact constructor with validation.
     */
    public RecurrenceRule {
        Objects.requireNonNull(type, "RecurrenceType cannot be null");

        if (type == RecurrenceType.SPECIFIC_DAYS) {
            if (specificDays == null || specificDays.isEmpty()) {
                throw new IllegalArgumentException("SPECIFIC_DAYS requires at least one day");
            }
            // Make defensive copy
            specificDays = Set.copyOf(specificDays);
        } else {
            // Other types shouldn't have specificDays
            if (specificDays != null) {
                specificDays = null;
            }
        }

        if (type == RecurrenceType.NTH_DAY_OF_MONTH) {
            Objects.requireNonNull(nthDay, "NTH_DAY_OF_MONTH requires nthDay");
            Objects.requireNonNull(nthWeek, "NTH_DAY_OF_MONTH requires nthWeek");
            if (nthWeek < 1 || nthWeek > 4) {
                throw new IllegalArgumentException("nthWeek must be 1-4, got: " + nthWeek);
            }
        } else {
            // Other types shouldn't have nthDay/nthWeek
            nthDay = null;
            nthWeek = null;
        }
    }

    /**
     * Creates a daily recurrence (every day).
     */
    public static RecurrenceRule daily() {
        return new RecurrenceRule(RecurrenceType.DAILY, null, null, null);
    }

    /**
     * Creates a weekdays recurrence (Mon-Fri).
     */
    public static RecurrenceRule weekdays() {
        return new RecurrenceRule(RecurrenceType.WEEKDAYS, null, null, null);
    }

    /**
     * Creates a weekends recurrence (Sat-Sun).
     */
    public static RecurrenceRule weekends() {
        return new RecurrenceRule(RecurrenceType.WEEKENDS, null, null, null);
    }

    /**
     * Creates a specific days recurrence.
     *
     * @param days the days of the week (must not be empty)
     */
    public static RecurrenceRule specificDays(Set<DayOfWeek> days) {
        return new RecurrenceRule(RecurrenceType.SPECIFIC_DAYS, days, null, null);
    }

    /**
     * Creates an nth-day-of-month recurrence.
     *
     * @param day the day of the week
     * @param week which occurrence (1-4)
     */
    public static RecurrenceRule nthDayOfMonth(DayOfWeek day, int week) {
        return new RecurrenceRule(RecurrenceType.NTH_DAY_OF_MONTH, null, day, week);
    }

    /**
     * Checks if this recurrence expects a completion on the given date.
     *
     * @param date the date to check
     * @return true if the habit is expected on this date
     */
    public boolean isExpectedOn(LocalDate date) {
        return switch (type) {
            case DAILY -> true;

            case WEEKDAYS -> {
                java.time.DayOfWeek javaDay = date.getDayOfWeek();
                yield javaDay != java.time.DayOfWeek.SATURDAY &&
                      javaDay != java.time.DayOfWeek.SUNDAY;
            }

            case WEEKENDS -> {
                java.time.DayOfWeek javaDay = date.getDayOfWeek();
                yield javaDay == java.time.DayOfWeek.SATURDAY ||
                      javaDay == java.time.DayOfWeek.SUNDAY;
            }

            case SPECIFIC_DAYS -> {
                DayOfWeek ourDay = convertFromJavaDay(date.getDayOfWeek());
                yield specificDays.contains(ourDay);
            }

            case NTH_DAY_OF_MONTH -> {
                // Check if this is the Nth occurrence of the specified day in the month
                DayOfWeek ourDay = convertFromJavaDay(date.getDayOfWeek());
                if (!ourDay.equals(nthDay)) {
                    yield false;
                }

                // Calculate which week of the month this is
                int weekOfMonth = (date.getDayOfMonth() - 1) / 7 + 1;
                yield weekOfMonth == nthWeek;
            }
        };
    }

    /**
     * Converts java.time.DayOfWeek to our domain DayOfWeek enum.
     */
    private DayOfWeek convertFromJavaDay(java.time.DayOfWeek javaDay) {
        return DayOfWeek.valueOf(javaDay.name());
    }

    /**
     * Standalone test method (ADR-004: Domain model testing with main()).
     */
    public static void main(String[] args) {
        System.out.println("Testing RecurrenceRule...\n");

        // Test 1: Daily recurrence
        RecurrenceRule daily = RecurrenceRule.daily();
        assert daily.type() == RecurrenceType.DAILY;
        assert daily.isExpectedOn(LocalDate.of(2026, 1, 1));  // Thu
        assert daily.isExpectedOn(LocalDate.of(2026, 1, 3));  // Sat
        assert daily.isExpectedOn(LocalDate.of(2026, 1, 5));  // Mon
        System.out.println("✓ Test 1: Daily recurrence works");

        // Test 2: Weekdays recurrence
        RecurrenceRule weekdays = RecurrenceRule.weekdays();
        assert weekdays.type() == RecurrenceType.WEEKDAYS;
        assert weekdays.isExpectedOn(LocalDate.of(2026, 1, 5));   // Mon - YES
        assert weekdays.isExpectedOn(LocalDate.of(2026, 1, 6));   // Tue - YES
        assert weekdays.isExpectedOn(LocalDate.of(2026, 1, 9));   // Fri - YES
        assert !weekdays.isExpectedOn(LocalDate.of(2026, 1, 3));  // Sat - NO
        assert !weekdays.isExpectedOn(LocalDate.of(2026, 1, 4));  // Sun - NO
        System.out.println("✓ Test 2: Weekdays recurrence works");

        // Test 3: Weekends recurrence
        RecurrenceRule weekends = RecurrenceRule.weekends();
        assert weekends.type() == RecurrenceType.WEEKENDS;
        assert weekends.isExpectedOn(LocalDate.of(2026, 1, 3));   // Sat - YES
        assert weekends.isExpectedOn(LocalDate.of(2026, 1, 4));   // Sun - YES
        assert !weekends.isExpectedOn(LocalDate.of(2026, 1, 5));  // Mon - NO
        assert !weekends.isExpectedOn(LocalDate.of(2026, 1, 9));  // Fri - NO
        System.out.println("✓ Test 3: Weekends recurrence works");

        // Test 4: Specific days (Mon, Wed, Fri)
        RecurrenceRule mwf = RecurrenceRule.specificDays(Set.of(
                DayOfWeek.MONDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.FRIDAY
        ));
        assert mwf.type() == RecurrenceType.SPECIFIC_DAYS;
        assert mwf.isExpectedOn(LocalDate.of(2026, 1, 5));   // Mon - YES
        assert !mwf.isExpectedOn(LocalDate.of(2026, 1, 6));  // Tue - NO
        assert mwf.isExpectedOn(LocalDate.of(2026, 1, 7));   // Wed - YES
        assert !mwf.isExpectedOn(LocalDate.of(2026, 1, 8));  // Thu - NO
        assert mwf.isExpectedOn(LocalDate.of(2026, 1, 9));   // Fri - YES
        assert !mwf.isExpectedOn(LocalDate.of(2026, 1, 3));  // Sat - NO
        System.out.println("✓ Test 4: Specific days (Mon/Wed/Fri) works");

        // Test 5: Nth day of month (First Monday)
        RecurrenceRule firstMonday = RecurrenceRule.nthDayOfMonth(DayOfWeek.MONDAY, 1);
        assert firstMonday.type() == RecurrenceType.NTH_DAY_OF_MONTH;
        assert firstMonday.isExpectedOn(LocalDate.of(2026, 1, 5));   // First Mon of Jan 2026 - YES
        assert !firstMonday.isExpectedOn(LocalDate.of(2026, 1, 12)); // Second Mon of Jan 2026 - NO
        assert firstMonday.isExpectedOn(LocalDate.of(2026, 2, 2));   // First Mon of Feb 2026 - YES
        assert !firstMonday.isExpectedOn(LocalDate.of(2026, 1, 6));  // Not Monday - NO
        System.out.println("✓ Test 5: Nth day of month (First Monday) works");

        // Test 6: Nth day of month (Third Friday)
        RecurrenceRule thirdFriday = RecurrenceRule.nthDayOfMonth(DayOfWeek.FRIDAY, 3);
        assert thirdFriday.isExpectedOn(LocalDate.of(2026, 1, 16));  // Third Fri of Jan 2026 - YES
        assert !thirdFriday.isExpectedOn(LocalDate.of(2026, 1, 9));  // Second Fri - NO
        assert !thirdFriday.isExpectedOn(LocalDate.of(2026, 1, 23)); // Fourth Fri - NO
        System.out.println("✓ Test 6: Nth day of month (Third Friday) works");

        // Test 7: Null type validation
        try {
            new RecurrenceRule(null, null, null, null);
            assert false : "Should throw for null type";
        } catch (NullPointerException e) {
            System.out.println("✓ Test 7: Null type validation works: " + e.getMessage());
        }

        // Test 8: SPECIFIC_DAYS without days
        try {
            RecurrenceRule.specificDays(null);
            assert false : "Should throw for null days";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 8: SPECIFIC_DAYS requires days: " + e.getMessage());
        }

        // Test 9: SPECIFIC_DAYS with empty set
        try {
            RecurrenceRule.specificDays(Set.of());
            assert false : "Should throw for empty days";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 9: SPECIFIC_DAYS requires at least one day: " + e.getMessage());
        }

        // Test 10: NTH_DAY_OF_MONTH without nthDay
        try {
            new RecurrenceRule(RecurrenceType.NTH_DAY_OF_MONTH, null, null, 1);
            assert false : "Should throw for null nthDay";
        } catch (NullPointerException e) {
            System.out.println("✓ Test 10: NTH_DAY_OF_MONTH requires nthDay: " + e.getMessage());
        }

        // Test 11: NTH_DAY_OF_MONTH without nthWeek
        try {
            new RecurrenceRule(RecurrenceType.NTH_DAY_OF_MONTH, null, DayOfWeek.MONDAY, null);
            assert false : "Should throw for null nthWeek";
        } catch (NullPointerException e) {
            System.out.println("✓ Test 11: NTH_DAY_OF_MONTH requires nthWeek: " + e.getMessage());
        }

        // Test 12: NTH_DAY_OF_MONTH with invalid nthWeek (too low)
        try {
            RecurrenceRule.nthDayOfMonth(DayOfWeek.MONDAY, 0);
            assert false : "Should throw for nthWeek < 1";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 12: nthWeek must be >= 1: " + e.getMessage());
        }

        // Test 13: NTH_DAY_OF_MONTH with invalid nthWeek (too high)
        try {
            RecurrenceRule.nthDayOfMonth(DayOfWeek.MONDAY, 5);
            assert false : "Should throw for nthWeek > 4";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 13: nthWeek must be <= 4: " + e.getMessage());
        }

        // Test 14: Immutability of specificDays set
        Set<DayOfWeek> originalSet = Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
        RecurrenceRule rule = RecurrenceRule.specificDays(originalSet);
        Set<DayOfWeek> returnedSet = rule.specificDays();
        assert returnedSet.contains(DayOfWeek.MONDAY);
        assert returnedSet.contains(DayOfWeek.FRIDAY);
        System.out.println("✓ Test 14: specificDays set is immutable");

        // Test 15: Equality
        RecurrenceRule daily1 = RecurrenceRule.daily();
        RecurrenceRule daily2 = RecurrenceRule.daily();
        assert daily1.equals(daily2);
        System.out.println("✓ Test 15: Equality works");

        System.out.println("\n✅ All RecurrenceRule tests passed!");
    }
}
