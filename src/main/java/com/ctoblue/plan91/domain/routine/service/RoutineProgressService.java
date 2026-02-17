package com.ctoblue.plan91.domain.routine.service;

import com.ctoblue.plan91.domain.habitentry.HabitEntry;
import com.ctoblue.plan91.domain.routine.Routine;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Domain service for calculating routine progress and statistics.
 *
 * <p>Stateless service that provides operations for:
 * <ul>
 *   <li>Calculating compliance rates</li>
 *   <li>Finding missed days</li>
 *   <li>Calculating overall progress</li>
 *   <li>Checking if routine is on track</li>
 * </ul>
 *
 * <p>This is a domain service (not application service) - pure domain logic with no infrastructure dependencies.
 */
public class RoutineProgressService {

    private final RecurrenceCalculatorService recurrenceCalculator;

    /**
     * Creates a new RoutineProgressService.
     */
    public RoutineProgressService() {
        this.recurrenceCalculator = new RecurrenceCalculatorService();
    }

    /**
     * Calculates the compliance rate for a routine up to a given date.
     *
     * <p>Compliance rate = (completed days / expected days) * 100
     *
     * @param routine the routine
     * @param entries the habit entries for this routine
     * @param upToDate calculate compliance up to this date (inclusive)
     * @return compliance rate as percentage (0-100)
     */
    public double calculateComplianceRate(
            Routine routine,
            List<HabitEntry> entries,
            LocalDate upToDate
    ) {
        if (routine == null) {
            throw new IllegalArgumentException("Routine cannot be null");
        }
        if (entries == null) {
            throw new IllegalArgumentException("Entries cannot be null");
        }
        if (upToDate == null) {
            throw new IllegalArgumentException("UpToDate cannot be null");
        }

        int expectedDays = countExpectedDays(routine, upToDate);
        if (expectedDays == 0) {
            return 100.0;  // No days expected yet, perfect compliance
        }

        // Count completed days (entries that fall on expected dates)
        Set<LocalDate> entryDates = entries.stream()
                .map(HabitEntry::getDate)
                .collect(Collectors.toSet());

        List<LocalDate> expectedDates = recurrenceCalculator.findExpectedDates(
                routine.getRecurrenceRule(),
                routine.getStartDate(),
                upToDate
        );

        long completedCount = expectedDates.stream()
                .filter(entryDates::contains)
                .count();

        return (completedCount * 100.0) / expectedDays;
    }

    /**
     * Calculates how many days were expected from start to a given date.
     *
     * @param routine the routine
     * @param upToDate calculate expected days up to this date (inclusive)
     * @return number of expected days based on recurrence rule
     */
    public int countExpectedDays(Routine routine, LocalDate upToDate) {
        if (routine == null) {
            throw new IllegalArgumentException("Routine cannot be null");
        }
        if (upToDate == null) {
            throw new IllegalArgumentException("UpToDate cannot be null");
        }

        LocalDate start = routine.getStartDate();
        LocalDate effectiveEnd = upToDate.isBefore(start) ? start : upToDate;

        return recurrenceCalculator.countExpectedDates(
                routine.getRecurrenceRule(),
                start,
                effectiveEnd
        );
    }

    /**
     * Calculates overall progress toward the 91-day goal.
     *
     * <p>Progress = (days completed / 91) * 100
     *
     * @param routine the routine
     * @param entries the habit entries
     * @return progress as percentage (0-100)
     */
    public double calculateOverallProgress(Routine routine, List<HabitEntry> entries) {
        if (routine == null) {
            throw new IllegalArgumentException("Routine cannot be null");
        }
        if (entries == null) {
            throw new IllegalArgumentException("Entries cannot be null");
        }

        // Use streak's total completions for accurate count
        int totalCompletions = routine.getStreak().totalCompletions();

        return Math.min(100.0, (totalCompletions * 100.0) / 91.0);
    }

    /**
     * Identifies missed days (expected but no entry) up to a given date.
     *
     * <p>Only counts past and today - does not count future expected days as missed.
     *
     * @param routine the routine
     * @param entries the habit entries
     * @param upToDate check for misses up to this date (inclusive)
     * @return list of dates that were expected but missed
     */
    public List<LocalDate> findMissedDays(
            Routine routine,
            List<HabitEntry> entries,
            LocalDate upToDate
    ) {
        if (routine == null) {
            throw new IllegalArgumentException("Routine cannot be null");
        }
        if (entries == null) {
            throw new IllegalArgumentException("Entries cannot be null");
        }
        if (upToDate == null) {
            throw new IllegalArgumentException("UpToDate cannot be null");
        }

        Set<LocalDate> entryDates = entries.stream()
                .map(HabitEntry::getDate)
                .collect(Collectors.toSet());

        List<LocalDate> expectedDates = recurrenceCalculator.findExpectedDates(
                routine.getRecurrenceRule(),
                routine.getStartDate(),
                upToDate
        );

        return expectedDates.stream()
                .filter(date -> !entryDates.contains(date))
                .collect(Collectors.toList());
    }

    /**
     * Checks if the routine is on track (no missed days without strike).
     *
     * <p>A routine is on track if:
     * <ul>
     *   <li>It has no missed days, OR</li>
     *   <li>It has used its strike for the only missed day</li>
     * </ul>
     *
     * @param routine the routine
     * @param entries the habit entries
     * @param asOfDate check status as of this date
     * @return true if on track, false if has unexcused missed days
     */
    public boolean isOnTrack(
            Routine routine,
            List<HabitEntry> entries,
            LocalDate asOfDate
    ) {
        if (routine == null) {
            throw new IllegalArgumentException("Routine cannot be null");
        }
        if (entries == null) {
            throw new IllegalArgumentException("Entries cannot be null");
        }
        if (asOfDate == null) {
            throw new IllegalArgumentException("AsOfDate cannot be null");
        }

        List<LocalDate> missedDays = findMissedDays(routine, entries, asOfDate);

        if (missedDays.isEmpty()) {
            return true;  // No missed days, on track
        }

        if (missedDays.size() == 1 && routine.getStreak().hasUsedStrike()) {
            return true;  // One miss, but strike used
        }

        return false;  // Has unexcused missed days
    }

    /**
     * Standalone test method (ADR-004: Domain model testing with main()).
     */
    public static void main(String[] args) {
        System.out.println("Testing RoutineProgressService...\n");

        RoutineProgressService service = new RoutineProgressService();

        // Setup test data
        LocalDate start = LocalDate.of(2026, 2, 1);  // Sunday
        Routine routine = Routine.start(
                com.ctoblue.plan91.domain.habit.HabitId.generate(),
                com.ctoblue.plan91.domain.habitpractitioner.HabitPractitionerId.generate(),
                com.ctoblue.plan91.domain.routine.RecurrenceRule.weekdays(),  // Mon-Fri only
                start
        );

        // Test 1: Count expected days (first week - Sun-Sat, so 5 weekdays: Mon 2, Tue 3, Wed 4, Thu 5, Fri 6)
        LocalDate endFirstWeek = LocalDate.of(2026, 2, 7);  // Friday
        int expected = service.countExpectedDays(routine, endFirstWeek);
        assert expected == 5 : "Should have 5 weekdays in first week, got: " + expected;
        System.out.println("✓ Test 1: countExpectedDays works (5 weekdays)");

        // Test 2: Compliance rate with no entries (0%)
        List<com.ctoblue.plan91.domain.habitentry.HabitEntry> noEntries = new ArrayList<>();
        double noCompliance = service.calculateComplianceRate(routine, noEntries, endFirstWeek);
        assert noCompliance == 0.0 : "Should be 0% with no entries, got: " + noCompliance;
        System.out.println("✓ Test 2: Compliance rate with no entries is 0%");

        // Test 3: Compliance rate with all entries (100%)
        List<com.ctoblue.plan91.domain.habitentry.HabitEntry> allEntries = new ArrayList<>();
        allEntries.add(com.ctoblue.plan91.domain.habitentry.HabitEntry.recordBoolean(
                routine.getId(), LocalDate.of(2026, 2, 2), null));  // Mon
        allEntries.add(com.ctoblue.plan91.domain.habitentry.HabitEntry.recordBoolean(
                routine.getId(), LocalDate.of(2026, 2, 3), null));  // Tue
        allEntries.add(com.ctoblue.plan91.domain.habitentry.HabitEntry.recordBoolean(
                routine.getId(), LocalDate.of(2026, 2, 4), null));  // Wed
        allEntries.add(com.ctoblue.plan91.domain.habitentry.HabitEntry.recordBoolean(
                routine.getId(), LocalDate.of(2026, 2, 5), null));  // Thu
        allEntries.add(com.ctoblue.plan91.domain.habitentry.HabitEntry.recordBoolean(
                routine.getId(), LocalDate.of(2026, 2, 6), null));  // Fri
        double fullCompliance = service.calculateComplianceRate(routine, allEntries, endFirstWeek);
        assert fullCompliance == 100.0 : "Should be 100% with all entries, got: " + fullCompliance;
        System.out.println("✓ Test 3: Compliance rate with all entries is 100%");

        // Test 4: Partial compliance (3 out of 5 = 60%)
        List<com.ctoblue.plan91.domain.habitentry.HabitEntry> partialEntries = new ArrayList<>();
        partialEntries.add(com.ctoblue.plan91.domain.habitentry.HabitEntry.recordBoolean(
                routine.getId(), LocalDate.of(2026, 2, 2), null));  // Mon
        partialEntries.add(com.ctoblue.plan91.domain.habitentry.HabitEntry.recordBoolean(
                routine.getId(), LocalDate.of(2026, 2, 4), null));  // Wed
        partialEntries.add(com.ctoblue.plan91.domain.habitentry.HabitEntry.recordBoolean(
                routine.getId(), LocalDate.of(2026, 2, 6), null));  // Fri
        double partialCompliance = service.calculateComplianceRate(routine, partialEntries, endFirstWeek);
        assert partialCompliance == 60.0 : "Should be 60% with 3/5 entries, got: " + partialCompliance;
        System.out.println("✓ Test 4: Partial compliance (60%) works");

        // Test 5: Find missed days
        List<LocalDate> missed = service.findMissedDays(routine, partialEntries, endFirstWeek);
        assert missed.size() == 2 : "Should have 2 missed days, got: " + missed.size();
        assert missed.contains(LocalDate.of(2026, 2, 3)) : "Should include Tuesday";
        assert missed.contains(LocalDate.of(2026, 2, 5)) : "Should include Thursday";
        System.out.println("✓ Test 5: findMissedDays identifies Tue and Thu");

        // Test 6: Overall progress (new routine = 0%)
        Routine newRoutine = Routine.start(
                com.ctoblue.plan91.domain.habit.HabitId.generate(),
                com.ctoblue.plan91.domain.habitpractitioner.HabitPractitionerId.generate(),
                com.ctoblue.plan91.domain.routine.RecurrenceRule.daily(),
                LocalDate.now()
        );
        double newProgress = service.calculateOverallProgress(newRoutine, noEntries);
        assert newProgress == 0.0 : "New routine should be 0% progress";
        System.out.println("✓ Test 6: Overall progress for new routine is 0%");

        // Test 7: Overall progress after completions
        // Complete 10 weekdays on the routine (Feb 2-6, Feb 9-13)
        routine.recordCompletion(LocalDate.of(2026, 2, 2));   // Mon week 1
        routine.recordCompletion(LocalDate.of(2026, 2, 3));   // Tue week 1
        routine.recordCompletion(LocalDate.of(2026, 2, 4));   // Wed week 1
        routine.recordCompletion(LocalDate.of(2026, 2, 5));   // Thu week 1
        routine.recordCompletion(LocalDate.of(2026, 2, 6));   // Fri week 1
        routine.recordCompletion(LocalDate.of(2026, 2, 9));   // Mon week 2
        routine.recordCompletion(LocalDate.of(2026, 2, 10));  // Tue week 2
        routine.recordCompletion(LocalDate.of(2026, 2, 11));  // Wed week 2
        routine.recordCompletion(LocalDate.of(2026, 2, 12));  // Thu week 2
        routine.recordCompletion(LocalDate.of(2026, 2, 13));  // Fri week 2
        double someProgress = service.calculateOverallProgress(routine, allEntries);
        assert someProgress > 0 && someProgress < 100 : "Should have some progress";
        System.out.println("✓ Test 7: Overall progress after 10 completions: " + someProgress + "%");

        // Test 8: isOnTrack with all entries (no missed days)
        Routine trackRoutine = Routine.start(
                com.ctoblue.plan91.domain.habit.HabitId.generate(),
                com.ctoblue.plan91.domain.habitpractitioner.HabitPractitionerId.generate(),
                com.ctoblue.plan91.domain.routine.RecurrenceRule.weekdays(),
                start
        );
        boolean perfectTrack = service.isOnTrack(trackRoutine, allEntries, endFirstWeek);
        assert perfectTrack : "Should be on track with all entries (no missed days)";
        System.out.println("✓ Test 8: isOnTrack with all entries is true");

        // Test 9: isOnTrack with one miss and strike
        Routine strikeRoutine = Routine.start(
                com.ctoblue.plan91.domain.habit.HabitId.generate(),
                com.ctoblue.plan91.domain.habitpractitioner.HabitPractitionerId.generate(),
                com.ctoblue.plan91.domain.routine.RecurrenceRule.weekdays(),
                start
        );
        // Record a miss to use strike
        strikeRoutine.recordMiss(LocalDate.of(2026, 2, 2));  // Monday missed, strike used
        List<com.ctoblue.plan91.domain.habitentry.HabitEntry> strikeEntries = new ArrayList<>();
        // No entry for Monday (missed), but have others
        strikeEntries.add(com.ctoblue.plan91.domain.habitentry.HabitEntry.recordBoolean(
                strikeRoutine.getId(), LocalDate.of(2026, 2, 3), null));  // Tue
        strikeEntries.add(com.ctoblue.plan91.domain.habitentry.HabitEntry.recordBoolean(
                strikeRoutine.getId(), LocalDate.of(2026, 2, 4), null));  // Wed
        strikeEntries.add(com.ctoblue.plan91.domain.habitentry.HabitEntry.recordBoolean(
                strikeRoutine.getId(), LocalDate.of(2026, 2, 5), null));  // Thu
        strikeEntries.add(com.ctoblue.plan91.domain.habitentry.HabitEntry.recordBoolean(
                strikeRoutine.getId(), LocalDate.of(2026, 2, 6), null));  // Fri
        boolean onTrackWithStrike = service.isOnTrack(strikeRoutine, strikeEntries, endFirstWeek);
        assert onTrackWithStrike : "Should be on track with one miss and strike used";
        System.out.println("✓ Test 9: isOnTrack with one miss and strike is true");

        // Test 10: Not on track with multiple misses
        List<com.ctoblue.plan91.domain.habitentry.HabitEntry> manyMisses = new ArrayList<>();
        manyMisses.add(com.ctoblue.plan91.domain.habitentry.HabitEntry.recordBoolean(
                trackRoutine.getId(), LocalDate.of(2026, 2, 2), null));  // Only Monday
        boolean notOnTrack = service.isOnTrack(trackRoutine, manyMisses, endFirstWeek);
        assert !notOnTrack : "Should not be on track with 4 missed days";
        System.out.println("✓ Test 10: isOnTrack with multiple misses is false");

        // Test 11: Compliance before start date
        double beforeStart = service.calculateComplianceRate(routine, noEntries, start.minusDays(5));
        assert beforeStart == 100.0 : "Should be 100% before start (no days expected yet)";
        System.out.println("✓ Test 11: Compliance before start date is 100%");

        // Test 12: Null routine validation
        try {
            service.calculateComplianceRate(null, noEntries, endFirstWeek);
            assert false : "Should throw for null routine";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 12: Null routine validation works: " + e.getMessage());
        }

        // Test 13: Null entries validation
        try {
            service.calculateComplianceRate(routine, null, endFirstWeek);
            assert false : "Should throw for null entries";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 13: Null entries validation works: " + e.getMessage());
        }

        // Test 14: Null date validation
        try {
            service.calculateComplianceRate(routine, noEntries, null);
            assert false : "Should throw for null date";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 14: Null date validation works: " + e.getMessage());
        }

        // Test 15: Overall progress caps at 100%
        Routine completedRoutine = Routine.start(
                com.ctoblue.plan91.domain.habit.HabitId.generate(),
                com.ctoblue.plan91.domain.habitpractitioner.HabitPractitionerId.generate(),
                com.ctoblue.plan91.domain.routine.RecurrenceRule.daily(),
                LocalDate.now().minusDays(100)
        );
        // Complete 92 days (will complete routine after 91, then try one more)
        for (int i = 0; i < 92; i++) {
            if (completedRoutine.isActive()) {
                completedRoutine.recordCompletion(completedRoutine.getStartDate().plusDays(i));
            }
        }
        double cappedProgress = service.calculateOverallProgress(completedRoutine, allEntries);
        assert cappedProgress == 100.0 : "Progress should cap at 100%, got: " + cappedProgress;
        System.out.println("✓ Test 15: Overall progress caps at 100%");

        System.out.println("\n✅ All RoutineProgressService tests passed!");
    }
}
