package com.ctoblue.plan91.adapter.in.web.controller;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitPractitionerEntity;
import com.ctoblue.plan91.application.usecase.practitioner.GetPractitionerProfileUseCase;
import com.ctoblue.plan91.application.usecase.practitioner.UpdatePractitionerBioUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for practitioner profiles.
 *
 * <p>Endpoints:
 * <ul>
 *   <li>GET /api/practitioners/{id} - Get practitioner profile</li>
 *   <li>PUT /api/practitioners/{id}/bio - Update bio</li>
 * </ul>
 *
 * <p>Epic 06: Social Features - Practitioner profiles
 */
@RestController
@RequestMapping("/api/practitioners")
public class PractitionerController {

    private final GetPractitionerProfileUseCase getProfileUseCase;
    private final UpdatePractitionerBioUseCase updateBioUseCase;

    public PractitionerController(
            GetPractitionerProfileUseCase getProfileUseCase,
            UpdatePractitionerBioUseCase updateBioUseCase) {
        this.getProfileUseCase = getProfileUseCase;
        this.updateBioUseCase = updateBioUseCase;
    }

    /**
     * Gets a practitioner's public profile.
     *
     * @param id the practitioner's ID
     * @return the practitioner profile
     */
    @GetMapping("/{id}")
    public ResponseEntity<HabitPractitionerEntity> getProfile(@PathVariable String id) {
        HabitPractitionerEntity practitioner = getProfileUseCase.execute(id);
        return ResponseEntity.ok(practitioner);
    }

    /**
     * Updates a practitioner's bio.
     *
     * @param id the practitioner's ID
     * @param request the bio update request
     * @return the updated practitioner
     */
    @PutMapping("/{id}/bio")
    public ResponseEntity<HabitPractitionerEntity> updateBio(
            @PathVariable String id,
            @RequestBody BioUpdateRequest request) {
        HabitPractitionerEntity practitioner = updateBioUseCase.execute(id, request.bio());
        return ResponseEntity.ok(practitioner);
    }

    /**
     * DTO for bio update requests.
     */
    public record BioUpdateRequest(String bio) {
    }
}
