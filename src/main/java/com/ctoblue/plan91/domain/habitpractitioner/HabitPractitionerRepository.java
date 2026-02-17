package com.ctoblue.plan91.domain.habitpractitioner;

import java.util.Optional;

/**
 * Repository interface (Port) for HabitPractitioner aggregate.
 *
 * <p>This is a domain interface following the Hexagonal Architecture pattern.
 * The implementation will be in the infrastructure layer (Adapter).
 *
 * <p>Repository methods follow these conventions:
 * <ul>
 *   <li>save() - Persists or updates a practitioner</li>
 *   <li>findBy*() - Queries that return Optional</li>
 *   <li>existsBy*() - Boolean queries</li>
 *   <li>delete() - Removes a practitioner</li>
 * </ul>
 */
public interface HabitPractitionerRepository {

    /**
     * Saves (creates or updates) a HabitPractitioner.
     *
     * @param practitioner the practitioner to save
     * @return the saved practitioner
     */
    HabitPractitioner save(HabitPractitioner practitioner);

    /**
     * Finds a practitioner by their unique ID.
     *
     * @param id the practitioner ID
     * @return an Optional containing the practitioner if found
     */
    Optional<HabitPractitioner> findById(HabitPractitionerId id);

    /**
     * Finds a practitioner by their email address.
     * Email is unique per practitioner.
     *
     * @param email the email address
     * @return an Optional containing the practitioner if found
     */
    Optional<HabitPractitioner> findByEmail(Email email);

    /**
     * Finds a practitioner by their Auth0 ID.
     * Auth0 ID is unique per practitioner.
     *
     * @param auth0Id the Auth0 provider ID
     * @return an Optional containing the practitioner if found
     */
    Optional<HabitPractitioner> findByAuth0Id(String auth0Id);

    /**
     * Checks if a practitioner with the given email already exists.
     * Useful for registration validation.
     *
     * @param email the email to check
     * @return true if a practitioner with this email exists
     */
    boolean existsByEmail(Email email);

    /**
     * Deletes a practitioner by their ID.
     *
     * <p>Note: In production, this may need to be a soft delete or
     * may cascade to related entities (habits, routines, entries).
     *
     * @param id the practitioner ID to delete
     */
    void delete(HabitPractitionerId id);
}
