package com.ctoblue.plan91.domain.habitpractitioner;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

/**
 * Aggregate root representing a person practicing habits in Plan 91.
 *
 * <p>A HabitPractitioner is someone who:
 * <ul>
 *   <li>Creates habits</li>
 *   <li>Starts 91-day routines</li>
 *   <li>Completes habit entries</li>
 * </ul>
 *
 * <p>This is an aggregate root that controls all invariants related to
 * practitioner identity and authentication.
 */
public class HabitPractitioner {

    // Identity
    private final HabitPractitionerId id;

    // Personal info
    private final String firstName;
    private final String lastName;
    private final Email email;

    // Auth & tracking
    private final String auth0Id;
    private Instant lastLogin;           // Mutable for tracking
    private String lastLoginIp;          // Mutable for tracking
    private final String originalTimezone;

    // Audit timestamps
    private final Instant createdAt;
    private Instant updatedAt;          // Mutable for updates

    /**
     * Full constructor for creating a HabitPractitioner with all fields.
     * Use the factory method {@link #create} for new practitioners.
     *
     * @param id the unique identifier
     * @param firstName the first name (1-100 characters)
     * @param lastName the last name (1-100 characters)
     * @param email the email address
     * @param auth0Id the Auth0 provider ID
     * @param originalTimezone the user's timezone (e.g., "America/Los_Angeles")
     * @param createdAt when the practitioner was created
     */
    public HabitPractitioner(
            HabitPractitionerId id,
            String firstName,
            String lastName,
            Email email,
            String auth0Id,
            String originalTimezone,
            Instant createdAt) {

        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.firstName = validateName(firstName, "First name");
        this.lastName = validateName(lastName, "Last name");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.auth0Id = validateAuth0Id(auth0Id);
        this.originalTimezone = validateTimezone(originalTimezone);
        this.createdAt = validateCreatedAt(createdAt);
        this.updatedAt = createdAt;
        this.lastLogin = null;
        this.lastLoginIp = null;
    }

    /**
     * Factory method to create a new HabitPractitioner with generated ID and current timestamp.
     *
     * @param firstName the first name
     * @param lastName the last name
     * @param email the email address
     * @param auth0Id the Auth0 provider ID
     * @param originalTimezone the user's timezone
     * @return a new HabitPractitioner
     */
    public static HabitPractitioner create(
            String firstName,
            String lastName,
            Email email,
            String auth0Id,
            String originalTimezone) {

        return new HabitPractitioner(
                HabitPractitionerId.generate(),
                firstName,
                lastName,
                email,
                auth0Id,
                originalTimezone,
                Instant.now()
        );
    }

    /**
     * Records a login event for this practitioner.
     *
     * @param ipAddress the IP address of the login
     */
    public void recordLogin(String ipAddress) {
        if (ipAddress == null || ipAddress.isBlank()) {
            throw new IllegalArgumentException("IP address cannot be null or blank");
        }
        this.lastLogin = Instant.now();
        this.lastLoginIp = ipAddress.trim();
        this.updatedAt = Instant.now();
    }

    /**
     * Returns the full name (first name + last name).
     *
     * @return the full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Checks if this practitioner has ever logged in.
     *
     * @return true if lastLogin is not null
     */
    public boolean hasLoggedIn() {
        return lastLogin != null;
    }

    /**
     * Gets the timezone as a ZoneId object.
     *
     * @return the ZoneId
     */
    public ZoneId getZoneId() {
        return ZoneId.of(originalTimezone);
    }

    // Validation helper methods

    private String validateName(String name, String fieldName) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or blank");
        }
        String trimmed = name.trim();
        if (trimmed.length() < 1 || trimmed.length() > 100) {
            throw new IllegalArgumentException(fieldName + " must be 1-100 characters, got: " + trimmed.length());
        }
        return trimmed;
    }

    private String validateAuth0Id(String auth0Id) {
        if (auth0Id == null || auth0Id.isBlank()) {
            throw new IllegalArgumentException("Auth0 ID cannot be null or blank");
        }
        return auth0Id.trim();
    }

    private String validateTimezone(String timezone) {
        if (timezone == null || timezone.isBlank()) {
            throw new IllegalArgumentException("Timezone cannot be null or blank");
        }
        // Validate it's a valid timezone ID
        try {
            ZoneId.of(timezone);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timezone: " + timezone, e);
        }
        return timezone;
    }

    private Instant validateCreatedAt(Instant createdAt) {
        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt cannot be null");
        }
        if (createdAt.isAfter(Instant.now().plusSeconds(1))) {
            throw new IllegalArgumentException("CreatedAt cannot be in the future");
        }
        return createdAt;
    }

    // Getters

    public HabitPractitionerId getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Email getEmail() {
        return email;
    }

    public String getAuth0Id() {
        return auth0Id;
    }

    public Instant getLastLogin() {
        return lastLogin;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public String getOriginalTimezone() {
        return originalTimezone;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // equals, hashCode, toString based on ID (aggregate root identity)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HabitPractitioner that = (HabitPractitioner) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "HabitPractitioner{" +
                "id=" + id +
                ", name='" + getFullName() + '\'' +
                ", email=" + email +
                ", timezone='" + originalTimezone + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    /**
     * Standalone test method (ADR-004: Domain model testing with main()).
     */
    public static void main(String[] args) {
        System.out.println("Testing HabitPractitioner...\n");

        // Test 1: Create new practitioner via factory method
        HabitPractitioner practitioner1 = HabitPractitioner.create(
                "Luis",
                "Martinez",
                new Email("luis@example.com"),
                "auth0|123456",
                "America/Los_Angeles"
        );
        assert practitioner1 != null : "Created practitioner should not be null";
        assert practitioner1.getId() != null : "ID should be generated";
        assert practitioner1.getFullName().equals("Luis Martinez") : "Full name should be correct";
        assert practitioner1.getEmail().value().equals("luis@example.com") : "Email should match";
        assert !practitioner1.hasLoggedIn() : "New practitioner should not have logged in";
        System.out.println("✓ Test 1: Create practitioner: " + practitioner1.getFullName());

        // Test 2: Full constructor
        HabitPractitionerId id = HabitPractitionerId.generate();
        Instant now = Instant.now();
        HabitPractitioner practitioner2 = new HabitPractitioner(
                id,
                "Jane",
                "Doe",
                new Email("jane@example.com"),
                "auth0|789012",
                "Europe/London",
                now
        );
        assert practitioner2.getId().equals(id) : "ID should match";
        assert practitioner2.getFirstName().equals("Jane") : "First name should match";
        assert practitioner2.getLastName().equals("Doe") : "Last name should match";
        System.out.println("✓ Test 2: Full constructor works");

        // Test 3: Record login
        practitioner1.recordLogin("192.168.1.1");
        assert practitioner1.hasLoggedIn() : "Should have logged in";
        assert practitioner1.getLastLogin() != null : "Last login should be set";
        assert practitioner1.getLastLoginIp().equals("192.168.1.1") : "IP should match";
        System.out.println("✓ Test 3: Record login works");

        // Test 4: Record login updates timestamp
        Instant updatedBefore = practitioner1.getUpdatedAt();
        try {
            Thread.sleep(10); // Small delay to ensure time difference
        } catch (InterruptedException e) {
            // Ignore
        }
        practitioner1.recordLogin("10.0.0.1");
        assert practitioner1.getUpdatedAt().isAfter(updatedBefore) : "UpdatedAt should be updated";
        assert practitioner1.getLastLoginIp().equals("10.0.0.1") : "IP should be updated";
        System.out.println("✓ Test 4: Record login updates timestamp");

        // Test 5: Get full name
        assert practitioner1.getFullName().equals("Luis Martinez");
        assert practitioner2.getFullName().equals("Jane Doe");
        System.out.println("✓ Test 5: Get full name works");

        // Test 6: Get ZoneId
        ZoneId zoneId = practitioner1.getZoneId();
        assert zoneId.getId().equals("America/Los_Angeles") : "ZoneId should match";
        System.out.println("✓ Test 6: Get ZoneId works");

        // Test 7: Null ID validation
        try {
            new HabitPractitioner(null, "John", "Smith", new Email("john@example.com"),
                    "auth0|456", "UTC", Instant.now());
            assert false : "Should throw for null ID";
        } catch (NullPointerException e) {
            System.out.println("✓ Test 7: Null ID validation works: " + e.getMessage());
        }

        // Test 8: Null first name validation
        try {
            HabitPractitioner.create(null, "Smith", new Email("test@example.com"),
                    "auth0|456", "UTC");
            assert false : "Should throw for null first name";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 8: Null first name validation works: " + e.getMessage());
        }

        // Test 9: Blank first name validation
        try {
            HabitPractitioner.create("   ", "Smith", new Email("test@example.com"),
                    "auth0|456", "UTC");
            assert false : "Should throw for blank first name";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 9: Blank first name validation works: " + e.getMessage());
        }

        // Test 10: First name too long
        try {
            String longName = "A".repeat(101);
            HabitPractitioner.create(longName, "Smith", new Email("test@example.com"),
                    "auth0|456", "UTC");
            assert false : "Should throw for too long first name";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 10: First name too long validation works: " + e.getMessage());
        }

        // Test 11: Null last name validation
        try {
            HabitPractitioner.create("John", null, new Email("test@example.com"),
                    "auth0|456", "UTC");
            assert false : "Should throw for null last name";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 11: Null last name validation works: " + e.getMessage());
        }

        // Test 12: Null email validation
        try {
            HabitPractitioner.create("John", "Smith", null, "auth0|456", "UTC");
            assert false : "Should throw for null email";
        } catch (NullPointerException e) {
            System.out.println("✓ Test 12: Null email validation works: " + e.getMessage());
        }

        // Test 13: Null Auth0 ID validation
        try {
            HabitPractitioner.create("John", "Smith", new Email("test@example.com"),
                    null, "UTC");
            assert false : "Should throw for null Auth0 ID";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 13: Null Auth0 ID validation works: " + e.getMessage());
        }

        // Test 14: Blank Auth0 ID validation
        try {
            HabitPractitioner.create("John", "Smith", new Email("test@example.com"),
                    "   ", "UTC");
            assert false : "Should throw for blank Auth0 ID";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 14: Blank Auth0 ID validation works: " + e.getMessage());
        }

        // Test 15: Null timezone validation
        try {
            HabitPractitioner.create("John", "Smith", new Email("test@example.com"),
                    "auth0|456", null);
            assert false : "Should throw for null timezone";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 15: Null timezone validation works: " + e.getMessage());
        }

        // Test 16: Invalid timezone validation
        try {
            HabitPractitioner.create("John", "Smith", new Email("test@example.com"),
                    "auth0|456", "Invalid/Timezone");
            assert false : "Should throw for invalid timezone";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 16: Invalid timezone validation works: " + e.getMessage());
        }

        // Test 17: Null createdAt validation
        try {
            new HabitPractitioner(HabitPractitionerId.generate(), "John", "Smith",
                    new Email("test@example.com"), "auth0|456", "UTC", null);
            assert false : "Should throw for null createdAt";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 17: Null createdAt validation works: " + e.getMessage());
        }

        // Test 18: Future createdAt validation
        try {
            Instant future = Instant.now().plusSeconds(3600);
            new HabitPractitioner(HabitPractitionerId.generate(), "John", "Smith",
                    new Email("test@example.com"), "auth0|456", "UTC", future);
            assert false : "Should throw for future createdAt";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 18: Future createdAt validation works: " + e.getMessage());
        }

        // Test 19: Null IP in recordLogin validation
        try {
            practitioner1.recordLogin(null);
            assert false : "Should throw for null IP";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 19: Null IP validation works: " + e.getMessage());
        }

        // Test 20: Blank IP in recordLogin validation
        try {
            practitioner1.recordLogin("   ");
            assert false : "Should throw for blank IP";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 20: Blank IP validation works: " + e.getMessage());
        }

        // Test 21: Equality based on ID
        HabitPractitioner p1 = HabitPractitioner.create("A", "B", new Email("a@b.com"), "auth0|1", "UTC");
        HabitPractitioner p2 = HabitPractitioner.create("A", "B", new Email("a@b.com"), "auth0|1", "UTC");
        assert !p1.equals(p2) : "Different IDs should not be equal";
        HabitPractitioner p3 = new HabitPractitioner(p1.getId(), "C", "D", new Email("c@d.com"), "auth0|2", "UTC", Instant.now());
        assert p1.equals(p3) : "Same ID should be equal";
        System.out.println("✓ Test 21: Equality based on ID works");

        // Test 22: HashCode based on ID
        assert p1.hashCode() == p3.hashCode() : "Same ID should have same hashCode";
        System.out.println("✓ Test 22: HashCode based on ID works");

        // Test 23: toString()
        String str = practitioner1.toString();
        assert str.contains("Luis Martinez") : "toString should contain name";
        assert str.contains("luis@example.com") : "toString should contain email";
        System.out.println("✓ Test 23: toString() works: " + str);

        // Test 24: Whitespace trimming in names
        HabitPractitioner p4 = HabitPractitioner.create("  John  ", "  Smith  ",
                new Email("john@example.com"), "auth0|999", "UTC");
        assert p4.getFirstName().equals("John") : "Should trim first name";
        assert p4.getLastName().equals("Smith") : "Should trim last name";
        System.out.println("✓ Test 24: Whitespace trimming works");

        System.out.println("\n✅ All HabitPractitioner tests passed!");
    }
}
