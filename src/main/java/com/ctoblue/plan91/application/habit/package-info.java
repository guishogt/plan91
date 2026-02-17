/**
 * Habit use cases - Core habit management and tracking operations.
 *
 * <p>Use cases in this package:
 * <ul>
 *   <li>{@code CreateHabitUseCase} - Create new habit</li>
 *   <li>{@code EditHabitUseCase} - Update habit details</li>
 *   <li>{@code DeleteHabitUseCase} - Delete habit</li>
 *   <li>{@code CompleteHabitUseCase} - Mark habit as complete for today</li>
 *   <li>{@code AddHabitEntryUseCase} - Add completion entry with note/value</li>
 *   <li>{@code GetHabitDetailsUseCase} - Retrieve habit with entries</li>
 *   <li>{@code GetTodaysHabitsUseCase} - Get habits expected today</li>
 * </ul>
 *
 * <p>These use cases orchestrate the habit domain logic and coordinate
 * with repositories for persistence.
 *
 * @since 0.1.0
 */
package com.ctoblue.plan91.application.habit;
