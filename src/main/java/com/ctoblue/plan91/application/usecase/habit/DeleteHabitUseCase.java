package com.ctoblue.plan91.application.usecase.habit;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitJpaRepository;
import com.ctoblue.plan91.adapter.out.persistence.repository.RoutineJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case for deleting a habit.
 *
 * <p>This use case:
 * <ul>
 *   <li>Validates the habit exists</li>
 *   <li>Checks if the habit is used in any active routines</li>
 *   <li>Deletes the habit if not in use</li>
 * </ul>
 *
 * <p>Note: Habits with active routines cannot be deleted to maintain data integrity.
 * Users should complete or cancel routines first.
 */
@Service
public class DeleteHabitUseCase {

    private final HabitJpaRepository habitRepository;
    private final RoutineJpaRepository routineRepository;

    public DeleteHabitUseCase(
            HabitJpaRepository habitRepository,
            RoutineJpaRepository routineRepository) {
        this.habitRepository = habitRepository;
        this.routineRepository = routineRepository;
    }

    /**
     * Deletes a habit.
     *
     * @param habitId the habit's ID
     * @throws IllegalArgumentException if habit not found
     * @throws IllegalStateException if habit is used in active routines
     */
    @Transactional
    public void execute(String habitId) {
        // 1. Validate habit exists
        UUID id = UUID.fromString(habitId);
        HabitEntity habit = habitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habit not found: " + habitId));

        // 2. Check if habit is used in any routines
        long routineCount = routineRepository.countByHabitId(id);
        if (routineCount > 0) {
            throw new IllegalStateException(
                    "Cannot delete habit: " + routineCount + " routine(s) are using this habit. " +
                    "Please complete or cancel all routines first."
            );
        }

        // 3. Delete the habit
        habitRepository.delete(habit);
    }
}
