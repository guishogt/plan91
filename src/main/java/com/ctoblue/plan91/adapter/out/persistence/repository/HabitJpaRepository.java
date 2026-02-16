package com.ctoblue.plan91.adapter.out.persistence.repository;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for HabitEntity (habit definitions).
 */
@Repository
public interface HabitJpaRepository extends JpaRepository<HabitEntity, UUID> {

    /**
     * Finds all public habits.
     *
     * @return list of public habits
     */
    List<HabitEntity> findByIsPublicTrue();

    /**
     * Finds all habits created by a specific practitioner.
     *
     * @param creatorId the creator's ID
     * @return list of habits
     */
    List<HabitEntity> findByCreatorId(UUID creatorId);

    /**
     * Finds all public habits created by a specific practitioner.
     *
     * @param creatorId the creator's ID
     * @return list of public habits
     */
    List<HabitEntity> findByCreatorIdAndIsPublicTrue(UUID creatorId);

    /**
     * Searches habits by name (case-insensitive, contains).
     *
     * @param name the search term
     * @return list of matching habits
     */
    List<HabitEntity> findByNameContainingIgnoreCase(String name);
}
