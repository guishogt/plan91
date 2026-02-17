package com.ctoblue.plan91.domain.habittracker;

/**
 * Value object representing a validated email address.
 * Immutable and self-validating. Email is normalized to lowercase and trimmed.
 */
public record Email(String value) {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final int MAX_LENGTH = 255;

    /**
     * Compact constructor with validation and normalization.
     */
    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        // Normalize: trim and lowercase
        value = value.trim().toLowerCase();

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Email cannot exceed " + MAX_LENGTH + " characters");
        }

        if (!value.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * Standalone test method (ADR-004: Domain model testing with main()).
     */
    public static void main(String[] args) {
        System.out.println("Testing Email...\n");

        // Test 1: Valid email
        Email email1 = new Email("john@example.com");
        assert email1.value().equals("john@example.com") : "Valid email should work";
        System.out.println("✓ Test 1: Valid email: " + email1);

        // Test 2: Uppercase normalization
        Email email2 = new Email("JANE@EXAMPLE.COM");
        assert email2.value().equals("jane@example.com") : "Should normalize to lowercase";
        System.out.println("✓ Test 2: Normalized uppercase: " + email2);

        // Test 3: Mixed case normalization
        Email email3 = new Email("Bob.Smith@Example.COM");
        assert email3.value().equals("bob.smith@example.com") : "Should normalize mixed case";
        System.out.println("✓ Test 3: Normalized mixed case: " + email3);

        // Test 4: Whitespace trimming
        Email email4 = new Email("  alice@example.com  ");
        assert email4.value().equals("alice@example.com") : "Should trim whitespace";
        System.out.println("✓ Test 4: Trimmed whitespace: " + email4);

        // Test 5: Valid email with plus
        Email email5 = new Email("user+tag@example.com");
        assert email5.value().equals("user+tag@example.com") : "Plus sign should be valid";
        System.out.println("✓ Test 5: Email with plus: " + email5);

        // Test 6: Valid email with dash and underscore
        Email email6 = new Email("user_name-test@example.com");
        assert email6.value().equals("user_name-test@example.com") : "Dash and underscore should be valid";
        System.out.println("✓ Test 6: Email with dash/underscore: " + email6);

        // Test 7: Valid email with subdomain
        Email email7 = new Email("user@mail.example.com");
        assert email7.value().equals("user@mail.example.com") : "Subdomain should be valid";
        System.out.println("✓ Test 7: Email with subdomain: " + email7);

        // Test 8: Equality
        Email email8a = new Email("TEST@example.com");
        Email email8b = new Email("test@example.com");
        assert email8a.equals(email8b) : "Normalized emails should be equal";
        System.out.println("✓ Test 8: Equality after normalization");

        // Test 9: Null email
        try {
            new Email(null);
            assert false : "Should throw for null";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 9: Null validation works: " + e.getMessage());
        }

        // Test 10: Empty email
        try {
            new Email("");
            assert false : "Should throw for empty";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 10: Empty validation works: " + e.getMessage());
        }

        // Test 11: Blank email (whitespace only)
        try {
            new Email("   ");
            assert false : "Should throw for blank";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 11: Blank validation works: " + e.getMessage());
        }

        // Test 12: Invalid email - no @
        try {
            new Email("notanemail");
            assert false : "Should throw for no @";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 12: Invalid (no @) validation works: " + e.getMessage());
        }

        // Test 13: Invalid email - no domain
        try {
            new Email("user@");
            assert false : "Should throw for no domain";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 13: Invalid (no domain) validation works: " + e.getMessage());
        }

        // Test 14: Invalid email - no local part
        try {
            new Email("@example.com");
            assert false : "Should throw for no local part";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 14: Invalid (no local part) validation works: " + e.getMessage());
        }

        // Test 15: Invalid email - no TLD
        try {
            new Email("user@domain");
            assert false : "Should throw for no TLD";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 15: Invalid (no TLD) validation works: " + e.getMessage());
        }

        // Test 16: Invalid email - spaces in address
        try {
            new Email("user name@example.com");
            assert false : "Should throw for spaces";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 16: Invalid (spaces) validation works: " + e.getMessage());
        }

        // Test 17: Too long email
        try {
            String longEmail = "a".repeat(250) + "@example.com";
            new Email(longEmail);
            assert false : "Should throw for too long email";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 17: Max length validation works: " + e.getMessage());
        }

        // Test 18: toString()
        Email email18 = new Email("test@example.com");
        assert email18.toString().equals("test@example.com") : "toString should return email value";
        System.out.println("✓ Test 18: toString() works correctly");

        System.out.println("\n✅ All Email tests passed!");
    }
}
