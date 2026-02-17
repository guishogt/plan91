package com.ctoblue.plan91.domain.habitentry;

import java.util.UUID;

/**
 * Value object representing a unique identifier for a HabitEntry.
 * Immutable and self-validating.
 */
public record HabitEntryId(UUID value) {

    /**
     * Compact constructor with validation.
     */
    public HabitEntryId {
        if (value == null) {
            throw new IllegalArgumentException("HabitEntryId cannot be null");
        }
    }

    /**
     * Generates a new random HabitEntryId.
     *
     * @return a new HabitEntryId with a random UUID
     */
    public static HabitEntryId generate() {
        return new HabitEntryId(UUID.randomUUID());
    }

    /**
     * Creates a HabitEntryId from a UUID string.
     *
     * @param uuid the UUID string
     * @return a HabitEntryId with the parsed UUID
     * @throws IllegalArgumentException if the string is not a valid UUID
     */
    public static HabitEntryId from(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException("UUID string cannot be null or blank");
        }
        return new HabitEntryId(UUID.fromString(uuid));
    }

    @Override
    public String toString() {
        return value.toString();
    }

    /**
     * Standalone test method (ADR-004: Domain model testing with main()).
     */
    public static void main(String[] args) {
        System.out.println("Testing HabitEntryId...\n");

        // Test 1: Generate new ID
        HabitEntryId id1 = HabitEntryId.generate();
        assert id1 != null : "Generated ID should not be null";
        assert id1.value() != null : "Generated UUID should not be null";
        System.out.println("✓ Test 1: Generated ID: " + id1);

        // Test 2: Create from string
        String uuidString = "423e4567-e89b-12d3-a456-426614174000";
        HabitEntryId id2 = HabitEntryId.from(uuidString);
        assert id2.toString().equals(uuidString) : "ID should match UUID string";
        System.out.println("✓ Test 2: From string: " + id2);

        // Test 3: Equality
        HabitEntryId id3 = HabitEntryId.from(uuidString);
        assert id2.equals(id3) : "Same UUID should be equal";
        assert id2.hashCode() == id3.hashCode() : "Same UUID should have same hash";
        System.out.println("✓ Test 3: Equality test passed");

        // Test 4: Inequality
        HabitEntryId id4 = HabitEntryId.generate();
        assert !id1.equals(id4) : "Different UUIDs should not be equal";
        System.out.println("✓ Test 4: Inequality test passed");

        // Test 5: Null validation in constructor
        try {
            new HabitEntryId(null);
            assert false : "Should throw exception for null UUID";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 5: Null UUID validation works: " + e.getMessage());
        }

        // Test 6: Null validation in from()
        try {
            HabitEntryId.from(null);
            assert false : "Should throw exception for null string";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 6: Null string validation works: " + e.getMessage());
        }

        // Test 7: Blank validation in from()
        try {
            HabitEntryId.from("   ");
            assert false : "Should throw exception for blank string";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 7: Blank string validation works: " + e.getMessage());
        }

        // Test 8: Invalid UUID format
        try {
            HabitEntryId.from("not-a-uuid");
            assert false : "Should throw exception for invalid UUID format";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 8: Invalid UUID format validation works: " + e.getMessage());
        }

        // Test 9: toString()
        String str = id2.toString();
        assert str.equals(uuidString) : "toString should return UUID string";
        System.out.println("✓ Test 9: toString() works correctly");

        System.out.println("\n✅ All HabitEntryId tests passed!");
    }
}
