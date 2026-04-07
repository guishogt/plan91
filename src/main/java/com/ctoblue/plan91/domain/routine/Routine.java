package com.ctoblue.plan91.domain.routine;

import com.ctoblue.plan91.domain.habit.HabitId;
import com.ctoblue.plan91.domain.habitpractitioner.HabitPractitionerId;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Aggregate root representing a commitment to a habit for a target number of days.
 *
 * <p>A Routine is YOUR commitment to practice a habit (default 91 days, configurable):
 * <ul>
 *   <li>Links to a Habit (what) and HabitPractitioner (who)</li>
 *   <li>Defines when it's expected (RecurrenceRule)</li>
 *   <li>Tracks streak with one-strike rule (HabitStreak)</li>
 *   <li>Manages N-day cycle (start, end, completion)</li>
 * </ul>
 *
 * <p>Routine is separate from Habit:
 * <ul>
 *   <li>Habit = Definition ("Swimming")</li>
 *   <li>Routine = YOUR commitment to swimming for N days</li>
 * </ul>
 *
 * <p>Two types of routines:
 * <ul>
 *   <li>ROUTINE: Goal-oriented with one-strike rule (default)</li>
 *   <li>TRACKER: Streak tracking without penalties, runs indefinitely</li>
 * </ul>
 *
 * <p>Business rules for ROUTINE type:
 * <ul>
 *   <li>N-day cycle: Configurable (default 91), fixed from startDate</li>
 *   <li>One-strike rule: First miss uses strike, second abandons</li>
 *   <li>Expected days only: Can only complete on recurrence days</li>
 *   <li>One per day: Cannot complete same day twice</li>
 *   <li>Active only: Can only complete ACTIVE routines</li>
 * </ul>
 *
 * <p>Business rules for TRACKER type:
 * <ul>
 *   <li>No target days or end date - runs indefinitely</li>
 *   <li>No one-strike rule - never abandons</li>
 *   <li>Tracks streaks and completions normally</li>
 * </ul>
 */
public class Routine {

    // Identity
    private final RoutineId id;

    // Links
    private final HabitId habitId;
    private final HabitPractitionerId practitionerId;

    // Type (ROUTINE or TRACKER)
    private final RoutineType type;

    // Recurrence (when is it expected?)
    private final RecurrenceRule recurrenceRule;

    // The N-day cycle (default 91, null for TRACKER)
    private final Integer targetDays;         // How many completions needed (null for TRACKER)
    private final LocalDate startDate;        // Immutable
    private final LocalDate expectedEndDate;  // startDate + (targetDays - 1) days (null for TRACKER)
    private LocalDate completedAt;            // When reached target days (null if ongoing, always null for TRACKER)

    // Streak tracking
    private HabitStreak streak;

    // Status
    private RoutineStatus status;

    // Timestamps
    private final Instant createdAt;
    private Instant updatedAt;

    /**
     * Default target days for a routine.
     */
    public static final int DEFAULT_TARGET_DAYS = 91;

    /**
     * Full constructor. Use factory method {@link #start} for new routines.
     */
    public Routine(
            RoutineId id,
            HabitId habitId,
            HabitPractitionerId practitionerId,
            RoutineType type,
            RecurrenceRule recurrenceRule,
            Integer targetDays,
            LocalDate startDate,
            LocalDate expectedEndDate,
            LocalDate completedAt,
            HabitStreak streak,
            RoutineStatus status,
            Instant createdAt) {

        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.habitId = Objects.requireNonNull(habitId, "HabitId cannot be null");
        this.practitionerId = Objects.requireNonNull(practitionerId, "PractitionerId cannot be null");
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.recurrenceRule = Objects.requireNonNull(recurrenceRule, "RecurrenceRule cannot be null");
        this.startDate = Objects.requireNonNull(startDate, "StartDate cannot be null");

        if (type == RoutineType.TRACKER) {
            // TRACKER: no target days or end date
            this.targetDays = null;
            this.expectedEndDate = null;
        } else {
            // ROUTINE: requires target days and end date
            this.expectedEndDate = Objects.requireNonNull(expectedEndDate, "ExpectedEndDate cannot be null for ROUTINE type");
            if (targetDays == null || targetDays < 1) {
                throw new IllegalArgumentException("Target days must be at least 1 for ROUTINE type, got: " + targetDays);
            }
            this.targetDays = targetDays;
        }

        this.completedAt = completedAt;
        this.streak = Objects.requireNonNull(streak, "Streak cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
        this.updatedAt = createdAt;
    }

    /**
     * Factory method to start a new routine with default 91 days.
     *
     * @param habitId the habit to practice
     * @param practitionerId who is practicing
     * @param recurrenceRule when to practice
     * @param startDate when to start
     * @return a new Routine with ACTIVE status
     */
    public static Routine start(
            HabitId habitId,
            HabitPractitionerId practitionerId,
            RecurrenceRule recurrenceRule,
            LocalDate startDate) {
        return start(habitId, practitionerId, RoutineType.ROUTINE, recurrenceRule, startDate, DEFAULT_TARGET_DAYS);
    }

    /**
     * Factory method to start a new routine with custom target days.
     *
     * @param habitId the habit to practice
     * @param practitionerId who is practicing
     * @param recurrenceRule when to practice
     * @param startDate when to start
     * @param targetDays how many completions needed (default 91)
     * @return a new Routine with ACTIVE status
     */
    public static Routine start(
            HabitId habitId,
            HabitPractitionerId practitionerId,
            RecurrenceRule recurrenceRule,
            LocalDate startDate,
            int targetDays) {
        return start(habitId, practitionerId, RoutineType.ROUTINE, recurrenceRule, startDate, targetDays);
    }

    /**
     * Factory method to start a new routine or tracker.
     *
     * @param habitId the habit to practice
     * @param practitionerId who is practicing
     * @param type ROUTINE (goal-oriented) or TRACKER (no penalties)
     * @param recurrenceRule when to practice
     * @param startDate when to start
     * @param targetDays how many completions needed (ignored for TRACKER)
     * @return a new Routine with ACTIVE status
     */
    public static Routine start(
            HabitId habitId,
            HabitPractitionerId practitionerId,
            RoutineType type,
            RecurrenceRule recurrenceRule,
            LocalDate startDate,
            Integer targetDays) {

        if (type == RoutineType.TRACKER) {
            return new Routine(
                    RoutineId.generate(),
                    habitId,
                    practitionerId,
                    type,
                    recurrenceRule,
                    null,  // no target days
                    startDate,
                    null,  // no end date
                    null,
                    HabitStreak.initial(),
                    RoutineStatus.ACTIVE,
                    Instant.now()
            );
        } else {
            int days = (targetDays != null) ? targetDays : DEFAULT_TARGET_DAYS;
            return new Routine(
                    RoutineId.generate(),
                    habitId,
                    practitionerId,
                    type,
                    recurrenceRule,
                    days,
                    startDate,
                    startDate.plusDays(days - 1),
                    null,
                    HabitStreak.initial(),
                    RoutineStatus.ACTIVE,
                    Instant.now()
            );
        }
    }

    /**
     * Factory method to start a new tracker (no penalties, runs indefinitely).
     *
     * @param habitId the habit to track
     * @param practitionerId who is tracking
     * @param recurrenceRule when to track
     * @param startDate when to start
     * @return a new Tracker with ACTIVE status
     */
    public static Routine startTracker(
            HabitId habitId,
            HabitPractitionerId practitionerId,
            RecurrenceRule recurrenceRule,
            LocalDate startDate) {
        return start(habitId, practitionerId, RoutineType.TRACKER, recurrenceRule, startDate, null);
    }

    /**
     * Records a completion on the given date.
     *
     * @param date the completion date
     * @throws IllegalStateException if routine not ACTIVE
     * @throws IllegalArgumentException if date invalid or not expected
     */
    public void recordCompletion(LocalDate date) {
        Objects.requireNonNull(date, "Completion date cannot be null");

        // Validate can complete
        if (status != RoutineStatus.ACTIVE) {
            throw new IllegalStateException("Can only complete ACTIVE routines, status is: " + status);
        }

        // No restriction on past dates - allow backfilling history

        if (streak.lastCompletionDate() != null && date.equals(streak.lastCompletionDate())) {
            throw new IllegalArgumentException("Already completed on: " + date);
        }

        // Check if expected day
        if (!recurrenceRule.isExpectedOn(date)) {
            throw new IllegalArgumentException("Not an expected day: " + date);
        }

        // Update streak
        streak = streak.incrementStreak(date);
        updatedAt = Instant.now();

        // Check if completed target days (based on total completions, not calendar date)
        // TRACKER type never completes - it runs indefinitely
        if (type == RoutineType.ROUTINE && targetDays != null && streak.totalCompletions() >= targetDays) {
            status = RoutineStatus.COMPLETED;
            completedAt = date;
        }
    }

    /**
     * Records a miss on the given date (one-strike rule for ROUTINE type).
     * TRACKER type ignores misses - no penalties.
     *
     * @param date the missed date
     */
    public void recordMiss(LocalDate date) {
        Objects.requireNonNull(date, "Miss date cannot be null");

        if (status != RoutineStatus.ACTIVE) {
            return; // Only active routines can accumulate misses
        }

        // TRACKER type has no penalties - just reset current streak but stay active
        if (type == RoutineType.TRACKER) {
            streak = streak.resetStreak();
            updatedAt = Instant.now();
            return;
        }

        // Only count misses on expected days (ROUTINE type)
        if (!recurrenceRule.isExpectedOn(date)) {
            return; // Not an expected day, no penalty
        }

        if (!streak.hasUsedStrike()) {
            // First miss: use strike
            streak = streak.useStrike(date);
            updatedAt = Instant.now();
        } else {
            // Second miss: abandon routine
            streak = streak.resetStreak();
            status = RoutineStatus.ABANDONED;
            updatedAt = Instant.now();
        }
    }

    /**
     * Pauses the routine.
     *
     * @throws IllegalStateException if not ACTIVE
     */
    public void pause() {
        if (status != RoutineStatus.ACTIVE) {
            throw new IllegalStateException("Can only pause ACTIVE routines");
        }
        status = RoutineStatus.PAUSED;
        updatedAt = Instant.now();
    }

    /**
     * Resumes a paused routine.
     *
     * @throws IllegalStateException if not PAUSED
     */
    public void resume() {
        if (status != RoutineStatus.PAUSED) {
            throw new IllegalStateException("Can only resume PAUSED routines");
        }
        status = RoutineStatus.ACTIVE;
        updatedAt = Instant.now();
    }

    /**
     * Abandons the routine.
     */
    public void abandon() {
        status = RoutineStatus.ABANDONED;
        updatedAt = Instant.now();
    }

    /**
     * Gets days remaining until expected end date.
     * Returns null for TRACKER type (no end date).
     *
     * @return days remaining (can be negative if past end date), or null for TRACKER
     */
    public Integer getDaysRemaining() {
        if (expectedEndDate == null) {
            return null; // TRACKER has no end date
        }
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), expectedEndDate);
    }

    /**
     * Gets the target number of completions for this routine.
     *
     * @return the target days (default 91), or null for TRACKER
     */
    public Integer getTargetDays() {
        return targetDays;
    }

    /**
     * Gets total days in the cycle.
     *
     * @return the target days, or null for TRACKER
     */
    public Integer getTotalDays() {
        return targetDays;
    }

    /**
     * Gets the type of this routine.
     *
     * @return ROUTINE or TRACKER
     */
    public RoutineType getType() {
        return type;
    }

    /**
     * Checks if this is a tracker (no penalties, indefinite).
     */
    public boolean isTracker() {
        return type == RoutineType.TRACKER;
    }

    /**
     * Checks if routine is active.
     */
    public boolean isActive() {
        return status == RoutineStatus.ACTIVE;
    }

    /**
     * Checks if routine is completed.
     */
    public boolean isCompleted() {
        return status == RoutineStatus.COMPLETED;
    }

    /**
     * Checks if routine is abandoned.
     */
    public boolean isAbandoned() {
        return status == RoutineStatus.ABANDONED;
    }

    /**
     * Checks if routine is paused.
     */
    public boolean isPaused() {
        return status == RoutineStatus.PAUSED;
    }

    /**
     * Checks if owned by practitioner.
     */
    public boolean isOwnedBy(HabitPractitionerId pid) {
        return practitionerId.equals(pid);
    }

    // Getters

    public RoutineId getId() {
        return id;
    }

    public HabitId getHabitId() {
        return habitId;
    }

    public HabitPractitionerId getPractitionerId() {
        return practitionerId;
    }

    public RecurrenceRule getRecurrenceRule() {
        return recurrenceRule;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getExpectedEndDate() {
        return expectedEndDate;
    }

    public LocalDate getCompletedAt() {
        return completedAt;
    }

    public HabitStreak getStreak() {
        return streak;
    }

    public RoutineStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // equals, hashCode, toString based on ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Routine routine = (Routine) o;
        return Objects.equals(id, routine.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String daysInfo = (type == RoutineType.TRACKER)
                ? "tracker"
                : getDaysRemaining() + "/" + targetDays;
        return "Routine{" +
                "id=" + id +
                ", type=" + type +
                ", habit=" + habitId +
                ", status=" + status +
                ", streak=" + streak.currentStreak() +
                ", days=" + daysInfo +
                '}';
    }

    /**
     * Standalone test method (ADR-004: Domain model testing with main()).
     */
    public static void main(String[] args) {
        System.out.println("Testing Routine...\n");

        // Setup
        HabitId habitId = HabitId.generate();
        HabitPractitionerId practitionerId = HabitPractitionerId.generate();
        RecurrenceRule daily = RecurrenceRule.daily();
        LocalDate startDate = LocalDate.of(2026, 1, 1);

        // Test 1: Start new routine
        Routine routine = Routine.start(habitId, practitionerId, daily, startDate);
        assert routine != null;
        assert routine.getId() != null;
        assert routine.isActive();
        assert routine.getStartDate().equals(startDate);
        assert routine.getExpectedEndDate().equals(startDate.plusDays(90));  // 91 days total (0-90)
        assert routine.getStreak().currentStreak() == 0;
        assert routine.getTotalDays() == 91;
        System.out.println("✓ Test 1: Start new routine: " + routine);

        // Test 2: Record first completion
        LocalDate day1 = startDate;
        routine.recordCompletion(day1);
        assert routine.getStreak().currentStreak() == 1;
        assert routine.getStreak().totalCompletions() == 1;
        assert routine.isActive();
        System.out.println("✓ Test 2: First completion recorded");

        // Test 3: Record multiple completions
        for (int i = 1; i <= 10; i++) {
            routine.recordCompletion(startDate.plusDays(i));
        }
        assert routine.getStreak().currentStreak() == 11;
        assert routine.getStreak().longestStreak() == 11;
        System.out.println("✓ Test 3: Multiple completions: streak = " + routine.getStreak().currentStreak());

        // Test 4: Record miss (first strike)
        LocalDate missDate1 = startDate.plusDays(11);
        routine.recordMiss(missDate1);
        assert routine.getStreak().hasUsedStrike();
        assert routine.getStreak().currentStreak() == 11; // Preserved
        assert routine.isActive(); // Still active
        System.out.println("✓ Test 4: First miss uses strike, streak preserved");

        // Test 5: Continue after strike
        routine.recordCompletion(startDate.plusDays(12));
        assert routine.getStreak().currentStreak() == 12;
        assert routine.isActive();
        System.out.println("✓ Test 5: Continue after strike");

        // Test 6: Second miss abandons
        LocalDate missDate2 = startDate.plusDays(13);
        routine.recordMiss(missDate2);
        assert routine.isAbandoned();
        assert routine.getStreak().currentStreak() == 0; // Reset
        System.out.println("✓ Test 6: Second miss abandons routine");

        // Test 7: Cannot complete abandoned routine
        try {
            routine.recordCompletion(startDate.plusDays(14));
            assert false : "Should not complete abandoned routine";
        } catch (IllegalStateException e) {
            System.out.println("✓ Test 7: Cannot complete abandoned routine: " + e.getMessage());
        }

        // Test 8: Complete 91 days
        Routine longRoutine = Routine.start(habitId, practitionerId, daily, startDate);
        for (int i = 0; i < 91; i++) {
            longRoutine.recordCompletion(startDate.plusDays(i));
        }
        assert longRoutine.isCompleted() : "Should be completed after 91 completions";
        assert longRoutine.getCompletedAt() != null;
        assert longRoutine.getStreak().totalCompletions() == 91;
        System.out.println("✓ Test 8: Completing 91 days marks as COMPLETED");

        // Test 9: Recurrence rule (weekdays only)
        RecurrenceRule weekdays = RecurrenceRule.weekdays();
        Routine weekdayRoutine = Routine.start(habitId, practitionerId, weekdays, LocalDate.of(2026, 1, 5)); // Monday
        weekdayRoutine.recordCompletion(LocalDate.of(2026, 1, 5)); // Mon - OK
        weekdayRoutine.recordCompletion(LocalDate.of(2026, 1, 6)); // Tue - OK
        System.out.println("✓ Test 9: Weekday completions work");

        // Test 10: Cannot complete on non-expected day
        try {
            weekdayRoutine.recordCompletion(LocalDate.of(2026, 1, 10)); // Sat - NOT expected
            assert false : "Should not complete on Saturday";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 10: Cannot complete non-expected day: " + e.getMessage());
        }

        // Test 11: Miss on non-expected day does nothing
        int streakBefore = weekdayRoutine.getStreak().currentStreak();
        weekdayRoutine.recordMiss(LocalDate.of(2026, 1, 10)); // Sat - not expected, no penalty
        assert weekdayRoutine.getStreak().currentStreak() == streakBefore;
        assert weekdayRoutine.isActive();
        System.out.println("✓ Test 11: Miss on non-expected day has no effect");

        // Test 12: Cannot complete same day twice
        Routine r12 = Routine.start(habitId, practitionerId, daily, startDate);
        r12.recordCompletion(startDate);
        try {
            r12.recordCompletion(startDate);
            assert false : "Should not complete same day twice";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 12: Cannot complete same day twice: " + e.getMessage());
        }

        // Test 13: Cannot complete before start date
        try {
            Routine r13 = Routine.start(habitId, practitionerId, daily, startDate);
            r13.recordCompletion(startDate.minusDays(1));
            assert false : "Should not complete before start";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 13: Cannot complete before start date: " + e.getMessage());
        }

        // Test 14: Pause and resume
        Routine r14 = Routine.start(habitId, practitionerId, daily, startDate);
        r14.pause();
        assert r14.isPaused();
        r14.resume();
        assert r14.isActive();
        System.out.println("✓ Test 14: Pause and resume work");

        // Test 15: Cannot pause non-active
        try {
            r14.pause();
            r14.pause(); // Already paused
            assert false : "Should not pause non-active";
        } catch (IllegalStateException e) {
            System.out.println("✓ Test 15: Cannot pause non-active: " + e.getMessage());
        }

        // Test 16: Abandon
        Routine r16 = Routine.start(habitId, practitionerId, daily, startDate);
        r16.abandon();
        assert r16.isAbandoned();
        System.out.println("✓ Test 16: Abandon works");

        // Test 17: isOwnedBy
        Routine r17 = Routine.start(habitId, practitionerId, daily, startDate);
        assert r17.isOwnedBy(practitionerId);
        HabitPractitionerId other = HabitPractitionerId.generate();
        assert !r17.isOwnedBy(other);
        System.out.println("✓ Test 17: isOwnedBy works");

        // Test 18: Target days validation
        try {
            new Routine(RoutineId.generate(), habitId, practitionerId, RoutineType.ROUTINE, daily, 91,
                    startDate, startDate.plusDays(91), null, HabitStreak.initial(),
                    RoutineStatus.ACTIVE, Instant.now());
            assert false : "Should enforce 90 days between start and end (91 days total)";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 18: Target days validation works: " + e.getMessage());
        }

        // Test 19: Null validations
        try {
            Routine.start(null, practitionerId, daily, startDate);
            assert false : "Should throw for null habitId";
        } catch (NullPointerException e) {
            System.out.println("✓ Test 19: Null habitId validation works: " + e.getMessage());
        }

        // Test 20: Equality based on ID
        Routine r20a = Routine.start(habitId, practitionerId, daily, startDate);
        Routine r20b = Routine.start(habitId, practitionerId, daily, startDate);
        assert !r20a.equals(r20b) : "Different IDs should not be equal";
        Routine r20c = new Routine(r20a.getId(), habitId, practitionerId, RoutineType.ROUTINE, daily, 91, startDate,
                startDate.plusDays(90), null, HabitStreak.initial(), RoutineStatus.ACTIVE, Instant.now());
        assert r20a.equals(r20c) : "Same ID should be equal";
        System.out.println("✓ Test 20: Equality based on ID works");

        // Test 21: Custom target days
        Routine customDays = Routine.start(habitId, practitionerId, daily, startDate, 30);
        assert customDays.getTargetDays() == 30;
        assert customDays.getTotalDays() == 30;
        assert customDays.getExpectedEndDate().equals(startDate.plusDays(29));
        System.out.println("✓ Test 21: Custom target days (30) works");

        // Test 22: Complete custom target
        Routine shortRoutine = Routine.start(habitId, practitionerId, daily, startDate, 5);
        for (int i = 0; i < 5; i++) {
            shortRoutine.recordCompletion(startDate.plusDays(i));
        }
        assert shortRoutine.isCompleted() : "Should be completed after 5 completions";
        System.out.println("✓ Test 22: Custom target completion works");

        System.out.println("\n✅ All Routine tests passed!");
    }
}
