package com.ctoblue.plan91.application.usecase.practitioner;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitPractitionerEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitPractitionerJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case for retrieving a practitioner's public profile.
 *
 * <p>Epic 06: Social Features - Practitioner profiles
 */
@Service
public class GetPractitionerProfileUseCase {

    private final HabitPractitionerJpaRepository practitionerRepository;

    public GetPractitionerProfileUseCase(HabitPractitionerJpaRepository practitionerRepository) {
        this.practitionerRepository = practitionerRepository;
    }

    /**
     * Gets a practitioner's profile by ID.
     *
     * @param practitionerId the practitioner's ID
     * @return the practitioner entity
     * @throws IllegalArgumentException if practitioner not found
     */
    @Transactional(readOnly = true)
    public HabitPractitionerEntity execute(String practitionerId) {
        UUID id = UUID.fromString(practitionerId);
        return practitionerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Practitioner not found: " + practitionerId));
    }
}
