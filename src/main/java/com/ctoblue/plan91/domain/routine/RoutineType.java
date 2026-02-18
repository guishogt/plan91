package com.ctoblue.plan91.domain.routine;

/**
 * Enum representing the type of a routine.
 *
 * <ul>
 *   <li>ROUTINE - Goal-oriented with one-strike rule and target days</li>
 *   <li>TRACKER - Streak tracking without penalties, runs indefinitely</li>
 * </ul>
 */
public enum RoutineType {
    /**
     * Goal-oriented routine with one-strike rule.
     * Has target days (default 91) and can become COMPLETED or ABANDONED.
     */
    ROUTINE,

    /**
     * Simple tracker without penalties.
     * Tracks streaks but never abandons on missed days.
     * Runs indefinitely with no target or end date.
     */
    TRACKER
}
