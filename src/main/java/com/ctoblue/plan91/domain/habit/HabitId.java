package com.ctoblue.plan91.domain.habit;

import java.util.UUID;

/**
 * Value object representing a unique identifier for a Habit.
 * Immutable and self-validating.
 */
public record HabitId(UUID value) {

    /**
     * Compact constructor with validation.
     */
    public HabitId {
        if (value == null) {
            throw new IllegalArgumentException("HabitId cannot be null");
        }
    }

    /**
     * Generates a new random HabitId.
     *
     * @return a new HabitId with a random UUID
     */
    public static HabitId generate() {
        return new HabitId(UUID.randomUUID());
    }

    /**
     * Creates a HabitId from a UUID string.
     *
     * @param uuid the UUID string
     * @return a HabitId with the parsed UUID
     * @throws IllegalArgumentException if the string is not a valid UUID
     */
    public static HabitId from(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException("UUID string cannot be null or blank");
        }
        return new HabitId(UUID.fromString(uuid));
    }

    @Override
    public String toString() {
        return value.toString();
    }

    /**
     * Standalone test method (ADR-004: Domain model testing with main()).
     */
    public static void main(String[] args) {
        System.out.println("Testing HabitId...\n");

        // Test 1: Generate new ID
        HabitId id1 = HabitId.generate();
        assert id1 != null : "Generated ID should not be null";
        assert id1.value() != null : "Generated UUID should not be null";
        System.out.println("✓ Test 1: Generated ID: " + id1);

        // Test 2: Create from string
        String uuidString = "223e4567-e89b-12d3-a456-426614174000";
        HabitId id2 = HabitId.from(uuidString);
        assert id2.toString().equals(uuidString) : "ID should match UUID string";
        System.out.println("✓ Test 2: From string: " + id2);

        // Test 3: Equality
        HabitId id3 = HabitId.from(uuidString);
        assert id2.equals(id3) : "Same UUID should be equal";
        assert id2.hashCode() == id3.hashCode() : "Same UUID should have same hash";
        System.out.println("✓ Test 3: Equality test passed");

        // Test 4: Inequality
        HabitId id4 = HabitId.generate();
        assert !id1.equals(id4) : "Different UUIDs should not be equal";
        System.out.println("✓ Test 4: Inequality test passed");

        // Test 5: Null validation in constructor
        try {
            new HabitId(null);
            assert false : "Should throw exception for null UUID";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 5: Null UUID validation works: " + e.getMessage());
        }

        // Test 6: Null validation in from()
        try {
            HabitId.from(null);
            assert false : "Should throw exception for null string";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 6: Null string validation works: " + e.getMessage());
        }

        // Test 7: Blank validation in from()
        try {
            HabitId.from("   ");
            assert false : "Should throw exception for blank string";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 7: Blank string validation works: " + e.getMessage());
        }

        // Test 8: Invalid UUID format
        try {
            HabitId.from("not-a-uuid");
            assert false : "Should throw exception for invalid UUID format";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 8: Invalid UUID format validation works: " + e.getMessage());
        }

        // Test 9: toString()
        String str = id2.toString();
        assert str.equals(uuidString) : "toString should return UUID string";
        System.out.println("✓ Test 9: toString() works correctly");

        System.out.println("\n✅ All HabitId tests passed!");
    }
}
