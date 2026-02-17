package com.ctoblue.plan91.application.usecase.habit;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntity;
import com.ctoblue.plan91.adapter.out.persistence.entity.HabitPractitionerEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitJpaRepository;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitPractitionerJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Use case for copying a public habit to a practitioner's library.
 *
 * <p>This use case:
 * <ul>
 *   <li>Validates the source habit exists and is public</li>
 *   <li>Checks the practitioner doesn't already own this habit</li>
 *   <li>Creates a new habit with source_habit_id reference</li>
 *   <li>Preserves all configuration from the source habit</li>
 * </ul>
 *
 * <p>Epic 06: Social Features - Habit copying
 */
@Service
public class CopyHabitUseCase {

    private final HabitJpaRepository habitRepository;
    private final HabitPractitionerJpaRepository practitionerRepository;

    public CopyHabitUseCase(
            HabitJpaRepository habitRepository,
            HabitPractitionerJpaRepository practitionerRepository) {
        this.habitRepository = habitRepository;
        this.practitionerRepository = practitionerRepository;
    }

    /**
     * Copies a public habit to a practitioner's library.
     *
     * @param sourceHabitId the ID of the habit to copy
     * @param practitionerId the ID of the practitioner copying the habit
     * @return the newly created habit
     * @throws IllegalArgumentException if source habit not found, not public, or already copied
     */
    @Transactional
    public HabitEntity execute(String sourceHabitId, String practitionerId) {
        // 1. Validate source habit exists
        UUID sourceId = UUID.fromString(sourceHabitId);
        HabitEntity sourceHabit = habitRepository.findById(sourceId)
                .orElseThrow(() -> new IllegalArgumentException("Source habit not found: " + sourceHabitId));

        // 2. Validate source habit is public
        if (!sourceHabit.getIsPublic()) {
            throw new IllegalArgumentException("Cannot copy a non-public habit");
        }

        // 3. Validate practitioner exists
        UUID practId = UUID.fromString(practitionerId);
        HabitPractitionerEntity practitioner = practitionerRepository.findById(practId)
                .orElseThrow(() -> new IllegalArgumentException("Practitioner not found: " + practitionerId));

        // 4. Check if practitioner already owns this habit (original or copy)
        // Can't copy a habit you created or already copied
        if (sourceHabit.getCreator().getId().equals(practId)) {
            throw new IllegalArgumentException("Cannot copy your own habit");
        }

        // Check if already copied this habit
        boolean alreadyCopied = habitRepository.findByCreatorId(practId).stream()
                .anyMatch(h -> h.getSourceHabit() != null &&
                               h.getSourceHabit().getId().equals(sourceId));
        if (alreadyCopied) {
            throw new IllegalArgumentException("You have already copied this habit");
        }

        // 5. Create new habit with all configuration from source
        HabitEntity newHabit = HabitEntity.builder()
                .creator(practitioner)
                .name(sourceHabit.getName())
                .description(sourceHabit.getDescription())
                .trackingType(sourceHabit.getTrackingType())
                .numericUnitName(sourceHabit.getNumericUnitName())
                .numericMinValue(sourceHabit.getNumericMinValue())
                .numericMaxValue(sourceHabit.getNumericMaxValue())
                .numericTarget(sourceHabit.getNumericTarget())
                .isPublic(false)  // Copies are private by default
                .isPrivate(false)
                .sourceHabit(sourceHabit)  // Reference to original
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        // 6. Save and return
        return habitRepository.save(newHabit);
    }
}
