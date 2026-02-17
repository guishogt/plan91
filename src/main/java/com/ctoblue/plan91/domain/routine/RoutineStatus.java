package com.ctoblue.plan91.domain.routine;

/**
 * Enum representing the status of a routine.
 *
 * <ul>
 *   <li>ACTIVE - Currently tracking, can complete entries</li>
 *   <li>PAUSED - Temporarily paused</li>
 *   <li>COMPLETED - Successfully finished 91 days</li>
 *   <li>ABANDONED - Gave up or streak broken (two misses)</li>
 * </ul>
 */
public enum RoutineStatus {
    /**
     * Currently tracking. Can record completions.
     * Only ACTIVE routines count towards the one-active-per-habit rule.
     */
    ACTIVE,

    /**
     * Temporarily paused.
     * User can resume later.
     */
    PAUSED,

    /**
     * Successfully completed 91 days.
     * User can start a new routine for the same habit if desired.
     */
    COMPLETED,

    /**
     * Gave up or broke the routine (two misses with one-strike rule).
     * User can start a new routine for the same habit if desired.
     */
    ABANDONED
}
