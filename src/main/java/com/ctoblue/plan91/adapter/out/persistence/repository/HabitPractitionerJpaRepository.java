package com.ctoblue.plan91.adapter.out.persistence.repository;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitPractitionerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for HabitPractitionerEntity (domain users).
 */
@Repository
public interface HabitPractitionerJpaRepository extends JpaRepository<HabitPractitionerEntity, UUID> {

    /**
     * Finds a practitioner by email address.
     *
     * @param email the email to search for
     * @return Optional containing the practitioner if found
     */
    Optional<HabitPractitionerEntity> findByEmail(String email);

    /**
     * Finds a practitioner by Auth0 ID.
     *
     * @param auth0Id the Auth0 ID
     * @return Optional containing the practitioner if found
     */
    Optional<HabitPractitionerEntity> findByAuth0Id(String auth0Id);

    /**
     * Finds a practitioner by user ID.
     *
     * @param userId the user entity ID
     * @return Optional containing the practitioner if found
     */
    Optional<HabitPractitionerEntity> findByUserId(UUID userId);
}
