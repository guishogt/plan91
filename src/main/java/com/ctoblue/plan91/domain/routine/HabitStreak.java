package com.ctoblue.plan91.domain.routine;

import java.time.LocalDate;

/**
 * Value object representing streak tracking for a routine.
 *
 * <p>Immutable value object that tracks:
 * <ul>
 *   <li>Current consecutive completions</li>
 *   <li>Longest streak achieved</li>
 *   <li>Total completions all-time</li>
 *   <li>Strike usage (one-strike rule)</li>
 *   <li>Last completion date</li>
 * </ul>
 *
 * <p>Supports the one-strike rule:
 * <ul>
 *   <li>First miss: Use strike, preserve streak</li>
 *   <li>Second miss: Break streak, abandon routine</li>
 * </ul>
 */
public record HabitStreak(
        int currentStreak,
        int longestStreak,
        int totalCompletions,
        boolean hasUsedStrike,
        LocalDate strikeDate,
        LocalDate lastCompletionDate
) {

    /**
     * Compact constructor with validation.
     */
    public HabitStreak {
        if (currentStreak < 0) {
            throw new IllegalArgumentException("Current streak cannot be negative, got: " + currentStreak);
        }
        if (longestStreak < 0) {
            throw new IllegalArgumentException("Longest streak cannot be negative, got: " + longestStreak);
        }
        if (totalCompletions < 0) {
            throw new IllegalArgumentException("Total completions cannot be negative, got: " + totalCompletions);
        }
        if (currentStreak > longestStreak) {
            throw new IllegalArgumentException("Current streak (" + currentStreak + ") cannot exceed longest streak (" + longestStreak + ")");
        }
    }

    /**
     * Creates an initial streak (all zeros, no strike).
     *
     * @return a new HabitStreak with zero values
     */
    public static HabitStreak initial() {
        return new HabitStreak(0, 0, 0, false, null, null);
    }

    /**
     * Returns a new streak with incremented values for a completion.
     *
     * @param completionDate the date of completion
     * @return a new HabitStreak with updated values
     */
    public HabitStreak incrementStreak(LocalDate completionDate) {
        int newCurrent = currentStreak + 1;
        int newLongest = Math.max(longestStreak, newCurrent);
        return new HabitStreak(
                newCurrent,
                newLongest,
                totalCompletions + 1,
                hasUsedStrike,
                strikeDate,
                completionDate
        );
    }

    /**
     * Returns a new streak with strike used (one-strike rule).
     * Preserves current streak but marks strike as used.
     *
     * @param date the date the strike was used
     * @return a new HabitStreak with strike marked
     * @throws IllegalStateException if strike already used
     */
    public HabitStreak useStrike(LocalDate date) {
        if (hasUsedStrike) {
            throw new IllegalStateException("Strike already used on " + strikeDate);
        }
        return new HabitStreak(
                currentStreak,  // Preserve streak
                longestStreak,
                totalCompletions,
                true,
                date,
                lastCompletionDate
        );
    }

    /**
     * Returns a new streak with current streak reset to zero.
     * Preserves longest streak and total completions.
     *
     * @return a new HabitStreak with reset current streak
     */
    public HabitStreak resetStreak() {
        return new HabitStreak(
                0,
                longestStreak,
                totalCompletions,
                hasUsedStrike,
                strikeDate,
                lastCompletionDate
        );
    }

    /**
     * Standalone test method (ADR-004: Domain model testing with main()).
     */
    public static void main(String[] args) {
        System.out.println("Testing HabitStreak...\n");

        // Test 1: Initial streak
        HabitStreak initial = HabitStreak.initial();
        assert initial.currentStreak() == 0;
        assert initial.longestStreak() == 0;
        assert initial.totalCompletions() == 0;
        assert !initial.hasUsedStrike();
        assert initial.strikeDate() == null;
        assert initial.lastCompletionDate() == null;
        System.out.println("✓ Test 1: Initial streak: " + initial);

        // Test 2: Increment streak
        LocalDate day1 = LocalDate.of(2026, 1, 1);
        HabitStreak afterDay1 = initial.incrementStreak(day1);
        assert afterDay1.currentStreak() == 1;
        assert afterDay1.longestStreak() == 1;
        assert afterDay1.totalCompletions() == 1;
        assert afterDay1.lastCompletionDate().equals(day1);
        System.out.println("✓ Test 2: After first completion: " + afterDay1);

        // Test 3: Increment again
        LocalDate day2 = LocalDate.of(2026, 1, 2);
        HabitStreak afterDay2 = afterDay1.incrementStreak(day2);
        assert afterDay2.currentStreak() == 2;
        assert afterDay2.longestStreak() == 2;
        assert afterDay2.totalCompletions() == 2;
        System.out.println("✓ Test 3: After second completion: " + afterDay2);

        // Test 4: Build up a streak
        HabitStreak streak5 = afterDay2;
        for (int i = 3; i <= 5; i++) {
            streak5 = streak5.incrementStreak(LocalDate.of(2026, 1, i));
        }
        assert streak5.currentStreak() == 5;
        assert streak5.longestStreak() == 5;
        assert streak5.totalCompletions() == 5;
        System.out.println("✓ Test 4: After 5 days: " + streak5);

        // Test 5: Use strike (first miss)
        LocalDate missDate = LocalDate.of(2026, 1, 6);
        HabitStreak withStrike = streak5.useStrike(missDate);
        assert withStrike.currentStreak() == 5;  // Preserved
        assert withStrike.longestStreak() == 5;
        assert withStrike.totalCompletions() == 5;
        assert withStrike.hasUsedStrike();
        assert withStrike.strikeDate().equals(missDate);
        System.out.println("✓ Test 5: After using strike: " + withStrike);

        // Test 6: Continue after strike
        LocalDate day7 = LocalDate.of(2026, 1, 7);
        HabitStreak afterStrike = withStrike.incrementStreak(day7);
        assert afterStrike.currentStreak() == 6;
        assert afterStrike.longestStreak() == 6;
        assert afterStrike.hasUsedStrike();  // Still true
        System.out.println("✓ Test 6: Continue after strike: " + afterStrike);

        // Test 7: Reset streak
        HabitStreak reset = afterStrike.resetStreak();
        assert reset.currentStreak() == 0;
        assert reset.longestStreak() == 6;  // Preserved
        assert reset.totalCompletions() == 6;  // Preserved
        assert reset.hasUsedStrike();  // Preserved
        System.out.println("✓ Test 7: After reset: " + reset);

        // Test 8: Rebuild after reset
        HabitStreak rebuild = reset.incrementStreak(LocalDate.of(2026, 1, 10));
        assert rebuild.currentStreak() == 1;
        assert rebuild.longestStreak() == 6;  // Previous longest preserved
        assert rebuild.totalCompletions() == 7;  // Incremented
        System.out.println("✓ Test 8: Rebuild after reset: " + rebuild);

        // Test 9: New longest streak
        HabitStreak newLongest = rebuild;
        for (int i = 11; i <= 17; i++) {
            newLongest = newLongest.incrementStreak(LocalDate.of(2026, 1, i));
        }
        assert newLongest.currentStreak() == 8;
        assert newLongest.longestStreak() == 8;  // New longest!
        System.out.println("✓ Test 9: New longest streak: " + newLongest);

        // Test 10: Negative current streak validation
        try {
            new HabitStreak(-1, 0, 0, false, null, null);
            assert false : "Should throw for negative current";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 10: Negative current validation works: " + e.getMessage());
        }

        // Test 11: Negative longest streak validation
        try {
            new HabitStreak(0, -1, 0, false, null, null);
            assert false : "Should throw for negative longest";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 11: Negative longest validation works: " + e.getMessage());
        }

        // Test 12: Negative total completions validation
        try {
            new HabitStreak(0, 0, -1, false, null, null);
            assert false : "Should throw for negative total";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 12: Negative total validation works: " + e.getMessage());
        }

        // Test 13: Current > longest validation
        try {
            new HabitStreak(10, 5, 10, false, null, null);
            assert false : "Should throw for current > longest";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 13: Current > longest validation works: " + e.getMessage());
        }

        // Test 14: Cannot use strike twice
        try {
            withStrike.useStrike(LocalDate.of(2026, 1, 8));
            assert false : "Should throw when strike already used";
        } catch (IllegalStateException e) {
            System.out.println("✓ Test 14: Cannot use strike twice: " + e.getMessage());
        }

        // Test 15: Immutability check
        HabitStreak original = HabitStreak.initial();
        HabitStreak modified = original.incrementStreak(LocalDate.now());
        assert original.currentStreak() == 0 : "Original should not change";
        assert modified.currentStreak() == 1 : "Modified should be different";
        System.out.println("✓ Test 15: Immutability preserved");

        System.out.println("\n✅ All HabitStreak tests passed!");
    }
}
