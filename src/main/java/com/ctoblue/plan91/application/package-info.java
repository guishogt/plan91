/**
 * Application layer - Use cases and application services.
 *
 * <p>This layer orchestrates domain objects to fulfill use cases. It:
 * <ul>
 *   <li>Coordinates multiple aggregates</li>
 *   <li>Handles transactions</li>
 *   <li>Triggers domain events</li>
 *   <li>Enforces application-level business rules</li>
 * </ul>
 *
 * <p>Use cases are organized by domain area (user, category, habit, analytics).
 * Each use case represents a single user intention or system operation.
 *
 * <p><b>Examples:</b>
 * <ul>
 *   <li>{@code CreateHabitUseCase} - Create a new habit</li>
 *   <li>{@code CompleteHabitUseCase} - Mark habit as complete for today</li>
 *   <li>{@code CalculateHabitStatsUseCase} - Generate habit statistics</li>
 * </ul>
 *
 * <p>This layer depends on the domain layer only.
 *
 * @since 0.1.0
 */
package com.ctoblue.plan91.application;
