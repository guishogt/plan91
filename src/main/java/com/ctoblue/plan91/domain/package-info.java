/**
 * Domain layer - Pure business logic with no framework dependencies.
 *
 * <p>This is the core of Plan 91's hexagonal architecture. The domain layer contains:
 * <ul>
 *   <li>Aggregates (User, Category, Habit)</li>
 *   <li>Entities (HabitEntry)</li>
 *   <li>Value Objects (UserId, Email, HabitStreak, RecurrenceRule)</li>
 *   <li>Domain Services (StreakCalculationService, RecurrenceEvaluationService)</li>
 *   <li>Repository Interfaces (Ports)</li>
 *   <li>Domain Events</li>
 * </ul>
 *
 * <p><b>Key Principles:</b>
 * <ul>
 *   <li>No Spring, JPA, or framework annotations</li>
 *   <li>Pure Java with business logic only</li>
 *   <li>All dependencies point inward to this layer</li>
 *   <li>Framework-agnostic and testable in isolation</li>
 * </ul>
 *
 * <p>Repository interfaces defined here are implemented in the infrastructure layer.
 *
 * @since 0.1.0
 */
package com.ctoblue.plan91.domain;
