package com.ctoblue.plan91.application.usecase.habit;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitJpaRepository;
import com.ctoblue.plan91.domain.habit.NumericConfig;
import com.ctoblue.plan91.domain.habit.TrackingType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Use case for editing an existing habit.
 *
 * <p>This use case:
 * <ul>
 *   <li>Validates the habit exists</li>
 *   <li>Updates only the fields provided in the command</li>
 *   <li>Validates numeric configuration if changed</li>
 *   <li>Validates visibility rules</li>
 * </ul>
 */
@Service
public class EditHabitUseCase {

    private final HabitJpaRepository habitRepository;

    public EditHabitUseCase(HabitJpaRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    /**
     * Edits an existing habit.
     *
     * @param command the edit command
     * @return the updated habit entity
     * @throws IllegalArgumentException if habit not found or validation fails
     */
    @Transactional
    public HabitEntity execute(EditHabitCommand command) {
        // 1. Validate habit exists
        UUID habitId = UUID.fromString(command.habitId());
        HabitEntity habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Habit not found: " + habitId));

        // 2. Update name if provided
        if (command.name() != null && !command.name().isBlank()) {
            habit.setName(command.name());
        }

        // 3. Update description if provided
        if (command.description() != null) {
            habit.setDescription(command.description());
        }

        // 4. Update numeric configuration if provided
        if (habit.getTrackingType() == TrackingType.NUMERIC) {
            boolean numericChanged = false;

            if (command.numericUnit() != null) {
                habit.setNumericUnitName(command.numericUnit());
                numericChanged = true;
            }
            if (command.numericMin() != null) {
                habit.setNumericMinValue(command.numericMin());
                numericChanged = true;
            }
            if (command.numericMax() != null) {
                habit.setNumericMaxValue(command.numericMax());
                numericChanged = true;
            }
            if (command.numericTarget() != null) {
                habit.setNumericTarget(command.numericTarget());
                numericChanged = true;
            }

            // Validate numeric config if changed
            if (numericChanged) {
                new NumericConfig(
                        habit.getNumericUnitName(),
                        habit.getNumericMinValue(),
                        habit.getNumericMaxValue(),
                        habit.getNumericTarget()
                );
            }
        }

        // 5. Update visibility if provided
        if (command.isPublic() != null) {
            habit.setIsPublic(command.isPublic());
        }
        if (command.isPrivate() != null) {
            habit.setIsPrivate(command.isPrivate());
        }

        // 6. Validate visibility rules
        if (habit.getIsPublic() && habit.getIsPrivate()) {
            throw new IllegalArgumentException("Habit cannot be both public and private");
        }

        // 7. Update timestamp
        habit.setUpdatedAt(Instant.now());

        // 8. Save and return
        return habitRepository.save(habit);
    }
}
