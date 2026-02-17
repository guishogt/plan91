package com.ctoblue.plan91.domain.routine.service;

import com.ctoblue.plan91.domain.routine.DayOfWeek;
import com.ctoblue.plan91.domain.routine.RecurrenceRule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Domain service for calculating expected dates based on recurrence rules.
 *
 * <p>Stateless service that provides operations for:
 * <ul>
 *   <li>Finding all expected dates in a range</li>
 *   <li>Finding next/previous expected dates</li>
 *   <li>Counting expected dates</li>
 * </ul>
 *
 * <p>This is a domain service (not application service) - pure domain logic with no infrastructure dependencies.
 */
public class RecurrenceCalculatorService {

    /**
     * Finds all expected dates for a recurrence rule in a date range.
     *
     * @param recurrenceRule the recurrence rule
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of expected dates, ordered ascending
     */
    public List<LocalDate> findExpectedDates(
            RecurrenceRule recurrenceRule,
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (recurrenceRule == null) {
            throw new IllegalArgumentException("RecurrenceRule cannot be null");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        List<LocalDate> expectedDates = new ArrayList<>();
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            if (recurrenceRule.isExpectedOn(current)) {
                expectedDates.add(current);
            }
            current = current.plusDays(1);
        }

        return expectedDates;
    }

    /**
     * Finds the next expected date after a given date.
     *
     * <p>Searches up to 60 days ahead (reasonable limit).
     *
     * @param recurrenceRule the recurrence rule
     * @param afterDate find next expected date after this
     * @return the next expected date, or empty if none within 60 days
     */
    public Optional<LocalDate> findNextExpectedDate(
            RecurrenceRule recurrenceRule,
            LocalDate afterDate
    ) {
        if (recurrenceRule == null) {
            throw new IllegalArgumentException("RecurrenceRule cannot be null");
        }
        if (afterDate == null) {
            throw new IllegalArgumentException("After date cannot be null");
        }

        LocalDate searchLimit = afterDate.plusDays(60);
        LocalDate current = afterDate.plusDays(1);

        while (!current.isAfter(searchLimit)) {
            if (recurrenceRule.isExpectedOn(current)) {
                return Optional.of(current);
            }
            current = current.plusDays(1);
        }

        return Optional.empty();
    }

    /**
     * Finds the previous expected date before a given date.
     *
     * <p>Searches up to 60 days back (reasonable limit).
     *
     * @param recurrenceRule the recurrence rule
     * @param beforeDate find previous expected date before this
     * @return the previous expected date, or empty if none within 60 days
     */
    public Optional<LocalDate> findPreviousExpectedDate(
            RecurrenceRule recurrenceRule,
            LocalDate beforeDate
    ) {
        if (recurrenceRule == null) {
            throw new IllegalArgumentException("RecurrenceRule cannot be null");
        }
        if (beforeDate == null) {
            throw new IllegalArgumentException("Before date cannot be null");
        }

        LocalDate searchLimit = beforeDate.minusDays(60);
        LocalDate current = beforeDate.minusDays(1);

        while (!current.isBefore(searchLimit)) {
            if (recurrenceRule.isExpectedOn(current)) {
                return Optional.of(current);
            }
            current = current.minusDays(1);
        }

        return Optional.empty();
    }

    /**
     * Counts how many expected dates fall in a range.
     *
     * @param recurrenceRule the recurrence rule
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return count of expected dates
     */
    public int countExpectedDates(
            RecurrenceRule recurrenceRule,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return findExpectedDates(recurrenceRule, startDate, endDate).size();
    }

    /**
     * Standalone test method (ADR-004: Domain model testing with main()).
     */
    public static void main(String[] args) {
        System.out.println("Testing RecurrenceCalculatorService...\n");

        RecurrenceCalculatorService service = new RecurrenceCalculatorService();

        // Test 1: DAILY recurrence - find expected dates in a week
        RecurrenceRule daily = RecurrenceRule.daily();
        LocalDate startWeek = LocalDate.of(2026, 2, 2);  // Monday
        LocalDate endWeek = LocalDate.of(2026, 2, 8);    // Sunday
        List<LocalDate> dailyDates = service.findExpectedDates(daily, startWeek, endWeek);
        assert dailyDates.size() == 7 : "DAILY should have 7 days in a week";
        System.out.println("✓ Test 1: DAILY has 7 expected dates in a week");

        // Test 2: WEEKDAYS recurrence - find expected dates in a week
        RecurrenceRule weekdays = RecurrenceRule.weekdays();
        List<LocalDate> weekdayDates = service.findExpectedDates(weekdays, startWeek, endWeek);
        assert weekdayDates.size() == 5 : "WEEKDAYS should have 5 days in a week, got: " + weekdayDates.size();
        System.out.println("✓ Test 2: WEEKDAYS has 5 expected dates in a week");

        // Test 3: WEEKENDS recurrence - find expected dates in a week
        RecurrenceRule weekends = RecurrenceRule.weekends();
        List<LocalDate> weekendDates = service.findExpectedDates(weekends, startWeek, endWeek);
        assert weekendDates.size() == 2 : "WEEKENDS should have 2 days in a week";
        System.out.println("✓ Test 3: WEEKENDS has 2 expected dates in a week");

        // Test 4: SPECIFIC_DAYS (Mon, Wed, Fri)
        RecurrenceRule mwf = RecurrenceRule.specificDays(Set.of(
                DayOfWeek.MONDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.FRIDAY
        ));
        List<LocalDate> mwfDates = service.findExpectedDates(mwf, startWeek, endWeek);
        assert mwfDates.size() == 3 : "Mon/Wed/Fri should have 3 days in a week";
        assert mwfDates.get(0).equals(LocalDate.of(2026, 2, 2));  // Monday
        assert mwfDates.get(1).equals(LocalDate.of(2026, 2, 4));  // Wednesday
        assert mwfDates.get(2).equals(LocalDate.of(2026, 2, 6));  // Friday
        System.out.println("✓ Test 4: SPECIFIC_DAYS (Mon/Wed/Fri) works");

        // Test 5: NTH_DAY_OF_MONTH (First Monday) in February 2026
        RecurrenceRule firstMonday = RecurrenceRule.nthDayOfMonth(DayOfWeek.MONDAY, 1);
        LocalDate febStart = LocalDate.of(2026, 2, 1);
        LocalDate febEnd = LocalDate.of(2026, 2, 28);
        List<LocalDate> firstMondayDates = service.findExpectedDates(firstMonday, febStart, febEnd);
        assert firstMondayDates.size() == 1 : "Should have exactly 1 first Monday in February";
        assert firstMondayDates.get(0).equals(LocalDate.of(2026, 2, 2));  // First Monday is Feb 2
        System.out.println("✓ Test 5: NTH_DAY_OF_MONTH (First Monday) works");

        // Test 6: Count expected dates (DAILY in a week)
        int count = service.countExpectedDates(daily, startWeek, endWeek);
        assert count == 7;
        System.out.println("✓ Test 6: countExpectedDates works");

        // Test 7: Find next expected date (DAILY)
        LocalDate today = LocalDate.of(2026, 2, 5);
        Optional<LocalDate> nextDaily = service.findNextExpectedDate(daily, today);
        assert nextDaily.isPresent();
        assert nextDaily.get().equals(LocalDate.of(2026, 2, 6));
        System.out.println("✓ Test 7: findNextExpectedDate (DAILY) works");

        // Test 8: Find next expected date (WEEKDAYS from Friday)
        LocalDate friday = LocalDate.of(2026, 2, 6);  // Friday
        Optional<LocalDate> nextWeekday = service.findNextExpectedDate(weekdays, friday);
        assert nextWeekday.isPresent();
        assert nextWeekday.get().equals(LocalDate.of(2026, 2, 9));  // Next Monday
        System.out.println("✓ Test 8: findNextExpectedDate (WEEKDAYS) skips weekend");

        // Test 9: Find next expected date (WEEKENDS from Friday)
        Optional<LocalDate> nextWeekend = service.findNextExpectedDate(weekends, friday);
        assert nextWeekend.isPresent();
        assert nextWeekend.get().equals(LocalDate.of(2026, 2, 7));  // Saturday
        System.out.println("✓ Test 9: findNextExpectedDate (WEEKENDS) works");

        // Test 10: Find previous expected date (DAILY)
        Optional<LocalDate> prevDaily = service.findPreviousExpectedDate(daily, today);
        assert prevDaily.isPresent();
        assert prevDaily.get().equals(LocalDate.of(2026, 2, 4));
        System.out.println("✓ Test 10: findPreviousExpectedDate (DAILY) works");

        // Test 11: Find previous expected date (WEEKDAYS from Monday)
        LocalDate monday = LocalDate.of(2026, 2, 9);  // Monday
        Optional<LocalDate> prevWeekday = service.findPreviousExpectedDate(weekdays, monday);
        assert prevWeekday.isPresent();
        assert prevWeekday.get().equals(LocalDate.of(2026, 2, 6));  // Previous Friday
        System.out.println("✓ Test 11: findPreviousExpectedDate (WEEKDAYS) skips weekend");

        // Test 12: Single day range
        List<LocalDate> singleDay = service.findExpectedDates(daily, today, today);
        assert singleDay.size() == 1;
        System.out.println("✓ Test 12: Single day range works");

        // Test 13: Null recurrence rule validation
        try {
            service.findExpectedDates(null, startWeek, endWeek);
            assert false : "Should throw for null recurrence rule";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 13: Null recurrence rule validation works: " + e.getMessage());
        }

        // Test 14: Null start date validation
        try {
            service.findExpectedDates(daily, null, endWeek);
            assert false : "Should throw for null start date";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 14: Null start date validation works: " + e.getMessage());
        }

        // Test 15: Null end date validation
        try {
            service.findExpectedDates(daily, startWeek, null);
            assert false : "Should throw for null end date";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 15: Null end date validation works: " + e.getMessage());
        }

        // Test 16: Start after end validation
        try {
            service.findExpectedDates(daily, endWeek, startWeek);
            assert false : "Should throw for start after end";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 16: Start after end validation works: " + e.getMessage());
        }

        // Test 17: Cross month boundary (WEEKDAYS)
        LocalDate janEnd = LocalDate.of(2026, 1, 30);  // Friday
        Optional<LocalDate> nextAfterJan = service.findNextExpectedDate(weekdays, janEnd);
        assert nextAfterJan.isPresent();
        assert nextAfterJan.get().equals(LocalDate.of(2026, 2, 2));  // Monday in February
        System.out.println("✓ Test 17: findNextExpectedDate crosses month boundary");

        // Test 18: NTH_DAY_OF_MONTH spanning multiple months
        RecurrenceRule thirdFriday = RecurrenceRule.nthDayOfMonth(DayOfWeek.FRIDAY, 3);
        LocalDate twoMonths = LocalDate.of(2026, 2, 1);
        LocalDate twoMonthsEnd = LocalDate.of(2026, 3, 31);
        List<LocalDate> thirdFridays = service.findExpectedDates(thirdFriday, twoMonths, twoMonthsEnd);
        assert thirdFridays.size() == 2 : "Should have 2 third Fridays in Feb and March";
        System.out.println("✓ Test 18: NTH_DAY_OF_MONTH across months works");

        System.out.println("\n✅ All RecurrenceCalculatorService tests passed!");
    }
}
