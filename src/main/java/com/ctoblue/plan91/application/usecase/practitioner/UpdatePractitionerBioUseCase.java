package com.ctoblue.plan91.application.usecase.practitioner;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitPractitionerEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitPractitionerJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Use case for updating a practitioner's bio.
 *
 * <p>Epic 06: Social Features - Practitioner profiles
 */
@Service
public class UpdatePractitionerBioUseCase {

    private final HabitPractitionerJpaRepository practitionerRepository;

    public UpdatePractitionerBioUseCase(HabitPractitionerJpaRepository practitionerRepository) {
        this.practitionerRepository = practitionerRepository;
    }

    /**
     * Updates a practitioner's bio.
     *
     * @param practitionerId the practitioner's ID
     * @param bio the new bio (max 500 characters, can be null/empty to clear)
     * @return the updated practitioner entity
     * @throws IllegalArgumentException if practitioner not found or bio too long
     */
    @Transactional
    public HabitPractitionerEntity execute(String practitionerId, String bio) {
        // 1. Validate practitioner exists
        UUID id = UUID.fromString(practitionerId);
        HabitPractitionerEntity practitioner = practitionerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Practitioner not found: " + practitionerId));

        // 2. Validate bio length
        if (bio != null && bio.length() > 500) {
            throw new IllegalArgumentException("Bio must be at most 500 characters, got: " + bio.length());
        }

        // 3. Update bio
        String trimmedBio = (bio == null || bio.trim().isEmpty()) ? null : bio.trim();
        practitioner.setBio(trimmedBio);
        practitioner.setUpdatedAt(Instant.now());

        // 4. Save and return
        return practitionerRepository.save(practitioner);
    }
}
