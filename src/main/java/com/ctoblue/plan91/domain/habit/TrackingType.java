package com.ctoblue.plan91.domain.habit;

/**
 * Enum representing the type of tracking for a habit.
 *
 * <ul>
 *   <li>BOOLEAN - Yes/No tracking (did it or didn't do it)</li>
 *   <li>NUMERIC - Numeric tracking with units (e.g., 1500 meters, 10 pages, 30 minutes)</li>
 * </ul>
 */
public enum TrackingType {
    /**
     * Boolean tracking: Yes/No, did it or didn't.
     * Example: "Pray Rosary" - either you did it or you didn't.
     */
    BOOLEAN,

    /**
     * Numeric tracking: Track a quantity with a unit.
     * Example: "Swimming" - track meters swam (1500 meters)
     * Example: "Reading" - track pages read (10 pages)
     */
    NUMERIC
}
