package com.ctoblue.plan91.application.usecase.habit;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Use case for querying habits.
 *
 * <p>This use case supports:
 * <ul>
 *   <li>Getting all habits created by a practitioner</li>
 *   <li>Getting all public habits</li>
 *   <li>Searching habits by name</li>
 * </ul>
 */
@Service
public class QueryHabitsUseCase {

    private final HabitJpaRepository habitRepository;

    public QueryHabitsUseCase(HabitJpaRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    /**
     * Gets all habits created by a practitioner.
     *
     * @param practitionerId the practitioner's ID
     * @return list of habits created by this practitioner
     */
    @Transactional(readOnly = true)
    public List<HabitEntity> getHabitsForPractitioner(String practitionerId) {
        UUID id = UUID.fromString(practitionerId);
        return habitRepository.findByCreatorId(id);
    }

    /**
     * Gets all public habits (browsable by anyone).
     *
     * @return list of public habits
     */
    @Transactional(readOnly = true)
    public List<HabitEntity> getPublicHabits() {
        return habitRepository.findByIsPublicTrue();
    }

    /**
     * Searches habits by name (case-insensitive).
     *
     * @param searchTerm the search term
     * @return list of matching habits
     */
    @Transactional(readOnly = true)
    public List<HabitEntity> searchHabits(String searchTerm) {
        return habitRepository.findByNameContainingIgnoreCase(searchTerm);
    }

    /**
     * Gets a habit by ID.
     *
     * @param habitId the habit's ID
     * @return the habit entity
     * @throws IllegalArgumentException if habit not found
     */
    @Transactional(readOnly = true)
    public HabitEntity getHabitById(String habitId) {
        UUID id = UUID.fromString(habitId);
        return habitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habit not found: " + habitId));
    }
}
