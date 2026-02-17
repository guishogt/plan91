package com.ctoblue.plan91.domain.routine;

import com.ctoblue.plan91.domain.habit.HabitId;
import com.ctoblue.plan91.domain.habitentry.HabitEntry;
import com.ctoblue.plan91.domain.habitpractitioner.HabitPractitionerId;
import com.ctoblue.plan91.domain.routine.service.RoutineProgressService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Comprehensive end-to-end tests for the one-strike rule.
 *
 * <p>The one-strike rule is a critical business rule:
 * <ul>
 *   <li>First miss on expected day: Use strike, preserve streak</li>
 *   <li>Second miss on expected day: Break streak, abandon routine</li>
 *   <li>Misses on non-expected days: No penalty</li>
 * </ul>
 *
 * <p>This test class verifies the rule works correctly across all components:
 * Routine, HabitStreak, RecurrenceRule, and RoutineProgressService.
 */
public class OneStrikeRuleTest {

    /**
     * Comprehensive tests for the one-strike rule.
     */
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("ONE-STRIKE RULE COMPREHENSIVE TESTS");
        System.out.println("===========================================\n");

        testPerfectRecord();
        testOneMissWithStrike();
        testTwoMissesAbandonment();
        testNonExpectedDayMiss();
        testStrikeUsageThenRecovery();
        testMultipleRecurrencePatterns();
        testMissOnLastDay();
        testProgressTrackingDuringStrikes();

        System.out.println("\n===========================================");
        System.out.println("✅ ALL ONE-STRIKE RULE TESTS PASSED!");
        System.out.println("===========================================");
    }

    /**
     * Test 1: Perfect record - complete all 91 days without any misses.
     */
    private static void testPerfectRecord() {
        System.out.println("Test 1: Perfect Record (91 days, no misses)");
        System.out.println("-------------------------------------------");

        LocalDate start = LocalDate.of(2026, 1, 1);
        Routine routine = Routine.start(
                HabitId.generate(),
                HabitPractitionerId.generate(),
                RecurrenceRule.daily(),
                start
        );

        // Complete all 91 days
        for (int i = 0; i < 91; i++) {
            routine.recordCompletion(start.plusDays(i));
        }

        // Verify final state
        assert routine.isCompleted() : "Routine should be completed";
        assert !routine.isActive() : "Routine should not be active";
        assert routine.getStreak().currentStreak() == 91 : "Current streak should be 91";
        assert routine.getStreak().longestStreak() == 91 : "Longest streak should be 91";
        assert routine.getStreak().totalCompletions() == 91 : "Total completions should be 91";
        assert !routine.getStreak().hasUsedStrike() : "Strike should not be used";

        System.out.println("✓ Perfect record: 91 days completed");
        System.out.println("✓ Status: " + routine.getStatus());
        System.out.println("✓ Streak: " + routine.getStreak().currentStreak() + " days");
        System.out.println("✓ Strike used: No\n");
    }

    /**
     * Test 2: One miss with strike - first miss uses strike, preserves streak.
     */
    private static void testOneMissWithStrike() {
        System.out.println("Test 2: One Miss With Strike");
        System.out.println("-------------------------------------------");

        LocalDate start = LocalDate.of(2026, 2, 1);  // Sunday
        Routine routine = Routine.start(
                HabitId.generate(),
                HabitPractitionerId.generate(),
                RecurrenceRule.daily(),
                start
        );

        // Complete 10 days
        for (int i = 0; i < 10; i++) {
            routine.recordCompletion(start.plusDays(i));
        }

        int streakBeforeMiss = routine.getStreak().currentStreak();
        assert streakBeforeMiss == 10 : "Streak should be 10 before miss";

        // Miss day 11 (first miss)
        LocalDate missDate = start.plusDays(10);
        routine.recordMiss(missDate);

        // Verify strike was used and streak preserved
        assert routine.getStreak().hasUsedStrike() : "Strike should be used";
        assert routine.getStreak().strikeDate().equals(missDate) : "Strike date should match miss date";
        assert routine.getStreak().currentStreak() == 10 : "Streak should be preserved at 10";
        assert routine.isActive() : "Routine should still be active";

        // Continue and complete more days
        for (int i = 11; i < 20; i++) {
            routine.recordCompletion(start.plusDays(i));
        }

        assert routine.getStreak().currentStreak() == 19 : "Streak should continue growing";
        assert routine.isActive() : "Routine should still be active";

        System.out.println("✓ First miss used strike");
        System.out.println("✓ Streak preserved at: " + streakBeforeMiss);
        System.out.println("✓ Continued to day 20, streak now: " + routine.getStreak().currentStreak());
        System.out.println("✓ Status: Still ACTIVE\n");
    }

    /**
     * Test 3: Two misses - second miss abandons routine.
     */
    private static void testTwoMissesAbandonment() {
        System.out.println("Test 3: Two Misses - Abandonment");
        System.out.println("-------------------------------------------");

        LocalDate start = LocalDate.of(2026, 3, 1);  // Sunday
        Routine routine = Routine.start(
                HabitId.generate(),
                HabitPractitionerId.generate(),
                RecurrenceRule.daily(),
                start
        );

        // Complete 5 days
        for (int i = 0; i < 5; i++) {
            routine.recordCompletion(start.plusDays(i));
        }

        // First miss (uses strike)
        LocalDate firstMiss = start.plusDays(5);
        routine.recordMiss(firstMiss);

        assert routine.getStreak().hasUsedStrike() : "Strike should be used";
        assert routine.getStreak().currentStreak() == 5 : "Streak preserved after first miss";
        assert routine.isActive() : "Should still be active after first miss";

        // Complete a few more days
        routine.recordCompletion(start.plusDays(6));
        routine.recordCompletion(start.plusDays(7));

        // Second miss (abandons routine)
        LocalDate secondMiss = start.plusDays(8);
        routine.recordMiss(secondMiss);

        // Verify abandonment
        assert routine.getStatus() == RoutineStatus.ABANDONED : "Should be abandoned after second miss";
        assert !routine.isActive() : "Should not be active";
        assert routine.getStreak().currentStreak() == 0 : "Streak should reset to 0";
        assert routine.getStreak().longestStreak() == 7 : "Longest streak should be preserved";

        System.out.println("✓ First miss: Strike used, streak preserved");
        System.out.println("✓ Second miss: Routine ABANDONED");
        System.out.println("✓ Current streak reset to: 0");
        System.out.println("✓ Longest streak preserved: 7\n");
    }

    /**
     * Test 4: Non-expected day miss - should have no effect.
     */
    private static void testNonExpectedDayMiss() {
        System.out.println("Test 4: Non-Expected Day Miss (No Penalty)");
        System.out.println("-------------------------------------------");

        LocalDate start = LocalDate.of(2026, 2, 2);  // Monday
        Routine routine = Routine.start(
                HabitId.generate(),
                HabitPractitionerId.generate(),
                RecurrenceRule.weekdays(),  // Mon-Fri only
                start
        );

        // Complete Mon-Fri (Feb 2-6)
        for (int i = 0; i < 5; i++) {
            routine.recordCompletion(start.plusDays(i));
        }

        assert routine.getStreak().currentStreak() == 5 : "Streak should be 5";

        // Miss Saturday (Feb 7) - not an expected day
        LocalDate saturday = start.plusDays(5);  // Saturday
        routine.recordMiss(saturday);

        // Verify no penalty
        assert !routine.getStreak().hasUsedStrike() : "Strike should NOT be used";
        assert routine.getStreak().currentStreak() == 5 : "Streak should remain 5";
        assert routine.isActive() : "Routine should still be active";

        // Miss Sunday (Feb 8) - also not expected
        LocalDate sunday = start.plusDays(6);  // Sunday
        routine.recordMiss(sunday);

        // Still no penalty
        assert !routine.getStreak().hasUsedStrike() : "Strike still should NOT be used";
        assert routine.getStreak().currentStreak() == 5 : "Streak still should be 5";

        // Continue on Monday (Feb 9)
        LocalDate monday = start.plusDays(7);  // Monday
        routine.recordCompletion(monday);

        assert routine.getStreak().currentStreak() == 6 : "Streak should grow to 6";

        System.out.println("✓ Missed Saturday: No penalty");
        System.out.println("✓ Missed Sunday: No penalty");
        System.out.println("✓ Continued Monday: Streak grew from 5 to 6");
        System.out.println("✓ Strike never used\n");
    }

    /**
     * Test 5: Strike usage then recovery - use strike early, complete 91 days.
     */
    private static void testStrikeUsageThenRecovery() {
        System.out.println("Test 5: Strike Usage Then Recovery");
        System.out.println("-------------------------------------------");

        LocalDate start = LocalDate.of(2026, 4, 1);  // Wednesday
        Routine routine = Routine.start(
                HabitId.generate(),
                HabitPractitionerId.generate(),
                RecurrenceRule.daily(),
                start
        );

        // Complete 10 days
        for (int i = 0; i < 10; i++) {
            routine.recordCompletion(start.plusDays(i));
        }

        // Miss day 11 (uses strike)
        routine.recordMiss(start.plusDays(10));
        assert routine.getStreak().hasUsedStrike() : "Strike should be used";

        // Complete remaining 81 days (11-91, but we already completed 0-9, so days 11-91 is 81 more days)
        for (int i = 11; i < 92; i++) {
            if (routine.isActive()) {
                routine.recordCompletion(start.plusDays(i));
            }
        }

        // Verify completion
        assert routine.isCompleted() : "Should reach completion despite early strike";
        assert routine.getStreak().totalCompletions() == 91 : "Should have 91 completions";

        System.out.println("✓ Used strike on day 11");
        System.out.println("✓ Continued and completed all 91 days");
        System.out.println("✓ Status: " + routine.getStatus());
        System.out.println("✓ Total completions: " + routine.getStreak().totalCompletions() + "\n");
    }

    /**
     * Test 6: Multiple recurrence patterns - verify one-strike works for each.
     */
    private static void testMultipleRecurrencePatterns() {
        System.out.println("Test 6: Multiple Recurrence Patterns");
        System.out.println("-------------------------------------------");

        LocalDate start = LocalDate.of(2026, 5, 4);  // Monday

        // Test WEEKDAYS
        Routine weekdaysRoutine = Routine.start(
                HabitId.generate(),
                HabitPractitionerId.generate(),
                RecurrenceRule.weekdays(),
                start
        );
        weekdaysRoutine.recordCompletion(start);  // Mon
        weekdaysRoutine.recordCompletion(start.plusDays(1));  // Tue
        weekdaysRoutine.recordMiss(start.plusDays(2));  // Wed miss - strike used
        assert weekdaysRoutine.getStreak().hasUsedStrike() : "WEEKDAYS: Strike should be used";
        assert weekdaysRoutine.isActive() : "WEEKDAYS: Should still be active";

        // Test SPECIFIC_DAYS (Mon, Wed, Fri)
        Routine mwfRoutine = Routine.start(
                HabitId.generate(),
                HabitPractitionerId.generate(),
                RecurrenceRule.specificDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)),
                start
        );
        mwfRoutine.recordCompletion(start);  // Mon
        mwfRoutine.recordMiss(start.plusDays(2));  // Wed miss - strike used
        assert mwfRoutine.getStreak().hasUsedStrike() : "MWF: Strike should be used";
        assert mwfRoutine.isActive() : "MWF: Should still be active";

        // Test DAILY
        Routine dailyRoutine = Routine.start(
                HabitId.generate(),
                HabitPractitionerId.generate(),
                RecurrenceRule.daily(),
                start
        );
        dailyRoutine.recordCompletion(start);
        dailyRoutine.recordCompletion(start.plusDays(1));
        dailyRoutine.recordMiss(start.plusDays(2));  // Miss - strike used
        assert dailyRoutine.getStreak().hasUsedStrike() : "DAILY: Strike should be used";
        assert dailyRoutine.isActive() : "DAILY: Should still be active";

        System.out.println("✓ WEEKDAYS: One-strike rule works correctly");
        System.out.println("✓ SPECIFIC_DAYS (M/W/F): One-strike rule works correctly");
        System.out.println("✓ DAILY: One-strike rule works correctly\n");
    }

    /**
     * Test 7: Miss on last day - verify strike works even at the end.
     */
    private static void testMissOnLastDay() {
        System.out.println("Test 7: Miss On Last Day");
        System.out.println("-------------------------------------------");

        LocalDate start = LocalDate.of(2026, 6, 1);
        Routine routine = Routine.start(
                HabitId.generate(),
                HabitPractitionerId.generate(),
                RecurrenceRule.daily(),
                start
        );

        // Complete 90 days
        for (int i = 0; i < 90; i++) {
            routine.recordCompletion(start.plusDays(i));
        }

        assert routine.getStreak().currentStreak() == 90 : "Should have 90-day streak";
        assert routine.isActive() : "Should still be active before day 91";

        // Miss day 91 (first miss ever, uses strike)
        routine.recordMiss(start.plusDays(90));

        assert routine.getStreak().hasUsedStrike() : "Strike should be used";
        assert routine.getStreak().currentStreak() == 90 : "Streak preserved at 90";
        assert routine.isActive() : "Should still be active (not completed yet)";

        // Complete day 92 to finish
        routine.recordCompletion(start.plusDays(91));

        assert routine.isCompleted() : "Should be completed after 91 completions";
        assert routine.getStreak().totalCompletions() == 91 : "Should have 91 total completions";

        System.out.println("✓ Completed 90 days");
        System.out.println("✓ Missed day 91 (strike used, streak preserved)");
        System.out.println("✓ Completed day 92 to finish");
        System.out.println("✓ Status: " + routine.getStatus() + "\n");
    }

    /**
     * Test 8: Progress tracking during strikes - verify service calculations.
     */
    private static void testProgressTrackingDuringStrikes() {
        System.out.println("Test 8: Progress Tracking During Strikes");
        System.out.println("-------------------------------------------");

        RoutineProgressService service = new RoutineProgressService();
        LocalDate start = LocalDate.of(2026, 7, 1);  // Wednesday

        Routine routine = Routine.start(
                HabitId.generate(),
                HabitPractitionerId.generate(),
                RecurrenceRule.daily(),
                start
        );

        List<HabitEntry> entries = new ArrayList<>();

        // Complete days 1-5
        for (int i = 0; i < 5; i++) {
            routine.recordCompletion(start.plusDays(i));
            entries.add(HabitEntry.recordBoolean(routine.getId(), start.plusDays(i), null));
        }

        // Check compliance before miss
        double complianceBefore = service.calculateComplianceRate(routine, entries, start.plusDays(4));
        assert complianceBefore == 100.0 : "Compliance should be 100% before miss";

        // Miss day 6 (uses strike)
        routine.recordMiss(start.plusDays(5));
        // Don't add entry for missed day

        // Check compliance after miss
        double complianceAfter = service.calculateComplianceRate(routine, entries, start.plusDays(5));
        assert complianceAfter < 100.0 : "Compliance should drop after miss";
        double expected = (5.0 / 6.0) * 100.0;  // 5 completed out of 6 expected
        assert Math.abs(complianceAfter - expected) < 0.01 : "Compliance should be ~83.33%";

        // Check isOnTrack with strike
        boolean onTrack = service.isOnTrack(routine, entries, start.plusDays(5));
        assert onTrack : "Should be on track with one miss and strike used";

        // Complete day 7
        routine.recordCompletion(start.plusDays(6));
        entries.add(HabitEntry.recordBoolean(routine.getId(), start.plusDays(6), null));

        // Miss day 8 (second miss - abandons)
        routine.recordMiss(start.plusDays(7));

        // Check not on track after abandonment
        boolean onTrackAfterAbandon = service.isOnTrack(routine, entries, start.plusDays(7));
        assert !onTrackAfterAbandon : "Should NOT be on track after two misses";

        System.out.println("✓ Compliance before miss: 100%");
        System.out.println("✓ Compliance after one miss: " + String.format("%.1f%%", complianceAfter));
        System.out.println("✓ On track with strike: Yes");
        System.out.println("✓ On track after second miss: No");
        System.out.println("✓ Progress calculations accurate\n");
    }
}
