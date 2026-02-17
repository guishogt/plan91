
/**
 * Habit domain - Core domain with habit tracking and streak calculation.
 *
 * <p>This is the heart of Plan 91's business logic. This package contains:
 * <ul>
 *   <li>{@code Habit} - Aggregate root representing a habit to track</li>
 *   <li>{@code HabitEntry} - Entity representing a single completion</li>
 *   <li>{@code HabitStreak} - Value object encapsulating streak state</li>
 *   <li>{@code RecurrenceRule} - Value object for scheduling (daily, weekdays, etc.)</li>
 *   <li>{@code NumericConfig} - Value object for numeric tracking configuration</li>
 *   <li>{@code StreakCalculationService} - Domain service for streak logic</li>
 *   <li>{@code RecurrenceEvaluationService} - Domain service for recurrence rules</li>
 *   <li>{@code HabitRepository} - Port (interface) for habit persistence</li>
 * </ul>
 *
 * <p><b>Core Business Rules:</b>
 * <ul>
 *   <li>91-day commitment cycles</li>
 *   <li>One-strike forgiveness rule (miss once, but not twice)</li>
 *   <li>Flexible recurrence rules</li>
 *   <li>Boolean or numeric tracking</li>
 *   <li>Timezone-aware date calculations</li>
 * </ul>
 *
 * @since 0.1.0
 */
package com.ctoblue.plan91.domain.habit;
