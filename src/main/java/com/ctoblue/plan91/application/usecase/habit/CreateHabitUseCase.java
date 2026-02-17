package com.ctoblue.plan91.application.usecase.habit;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntity;
import com.ctoblue.plan91.adapter.out.persistence.entity.HabitPractitionerEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitJpaRepository;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitPractitionerJpaRepository;
import com.ctoblue.plan91.domain.habit.NumericConfig;
import com.ctoblue.plan91.domain.habit.TrackingType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Use case for creating a new habit.
 *
 * <p>This is an application service that orchestrates:
 * <ul>
 *   <li>Validation of input</li>
 *   <li>Creation of domain objects</li>
 *   <li>Persistence via repositories</li>
 * </ul>
 */
@Service
public class CreateHabitUseCase {

    private final HabitJpaRepository habitRepository;
    private final HabitPractitionerJpaRepository practitionerRepository;

    public CreateHabitUseCase(
            HabitJpaRepository habitRepository,
            HabitPractitionerJpaRepository practitionerRepository) {
        this.habitRepository = habitRepository;
        this.practitionerRepository = practitionerRepository;
    }

    /**
     * Creates a new habit.
     *
     * @param command the create command
     * @return the created habit entity
     * @throws IllegalArgumentException if practitioner not found or validation fails
     */
    @Transactional
    public HabitEntity execute(CreateHabitCommand command) {
        // 1. Validate practitioner exists
        UUID practitionerId = UUID.fromString(command.practitionerId());
        HabitPractitionerEntity practitioner = practitionerRepository.findById(practitionerId)
                .orElseThrow(() -> new IllegalArgumentException("Practitioner not found: " + practitionerId));

        // 2. Validate tracking configuration
        if (command.trackingType() == TrackingType.NUMERIC) {
            if (command.numericUnit() == null || command.numericUnit().isBlank()) {
                throw new IllegalArgumentException("Numeric unit is required for numeric tracking");
            }
            // Validate using domain NumericConfig
            new NumericConfig(
                    command.numericUnit(),
                    command.numericMin(),
                    command.numericMax(),
                    command.numericTarget()
            );
        }

        // 3. Validate visibility
        if (command.isPublic() && command.isPrivate()) {
            throw new IllegalArgumentException("Habit cannot be both public and private");
        }

        // 4. Create habit entity
        HabitEntity habit = HabitEntity.builder()
                .creator(practitioner)
                .name(command.name())
                .description(command.description())
                .trackingType(command.trackingType())
                .numericUnitName(command.numericUnit())
                .numericMinValue(command.numericMin())
                .numericMaxValue(command.numericMax())
                .numericTarget(command.numericTarget())
                .isPublic(command.isPublic())
                .isPrivate(command.isPrivate())
                .sourceHabit(null)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        // 5. Save and return
        return habitRepository.save(habit);
    }
}
