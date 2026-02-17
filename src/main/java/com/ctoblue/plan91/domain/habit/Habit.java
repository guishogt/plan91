package com.ctoblue.plan91.domain.habit;

import com.ctoblue.plan91.domain.habitpractitioner.HabitPractitionerId;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate root representing a habit definition (the "WHAT" you want to do).
 *
 * <p>A Habit defines:
 * <ul>
 *   <li>What the habit is (name, description)</li>
 *   <li>How it's tracked (boolean vs numeric)</li>
 *   <li>Who can see it (public vs private)</li>
 *   <li>Where it came from (original vs copied)</li>
 * </ul>
 *
 * <p>Habit is separate from Routine:
 * <ul>
 *   <li>Habit = Definition/template ("Swimming", "Read 10 pages")</li>
 *   <li>Routine = Your 91-day commitment to that habit</li>
 * </ul>
 *
 * <p>This separation allows:
 * <ul>
 *   <li>Sharing habit definitions between practitioners</li>
 *   <li>Copying public habits</li>
 *   <li>Multiple routines for the same habit over time</li>
 * </ul>
 */
public class Habit {

    // Identity
    private final HabitId id;

    // Definition
    private final String name;
    private final String description;

    // Tracking configuration
    private final TrackingType trackingType;
    private final NumericConfig numericConfig;  // Only if NUMERIC

    // Visibility & sharing
    private final boolean isPublic;
    private final boolean isPrivate;

    // Provenance
    private final HabitPractitionerId creator;
    private final HabitId sourceHabit;  // If copied from another habit

    // Timestamps
    private final Instant createdAt;
    private Instant updatedAt;

    /**
     * Full constructor for creating a Habit with all fields.
     * Use factory methods for most cases.
     *
     * @param id the unique identifier
     * @param name the habit name (1-200 characters)
     * @param description optional description (max 1000 characters)
     * @param trackingType BOOLEAN or NUMERIC
     * @param numericConfig required if NUMERIC, null if BOOLEAN
     * @param isPublic can others see/copy this habit?
     * @param isPrivate only visible to creator?
     * @param creator who created this habit
     * @param sourceHabit if copied, the source habit ID
     * @param createdAt when created
     */
    public Habit(
            HabitId id,
            String name,
            String description,
            TrackingType trackingType,
            NumericConfig numericConfig,
            boolean isPublic,
            boolean isPrivate,
            HabitPractitionerId creator,
            HabitId sourceHabit,
            Instant createdAt) {

        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.name = validateName(name);
        this.description = validateDescription(description);
        this.trackingType = Objects.requireNonNull(trackingType, "TrackingType cannot be null");
        this.numericConfig = validateNumericConfig(trackingType, numericConfig);
        validateVisibility(isPublic, isPrivate);
        this.isPublic = isPublic;
        this.isPrivate = isPrivate;
        this.creator = Objects.requireNonNull(creator, "Creator cannot be null");
        this.sourceHabit = sourceHabit;
        this.createdAt = validateCreatedAt(createdAt);
        this.updatedAt = createdAt;
    }

    /**
     * Creates a new boolean habit (yes/no tracking).
     *
     * @param name the habit name
     * @param description optional description
     * @param isPublic can others see/copy this?
     * @param creator who is creating this
     * @return a new boolean Habit
     */
    public static Habit createBoolean(
            String name,
            String description,
            boolean isPublic,
            HabitPractitionerId creator) {

        return new Habit(
                HabitId.generate(),
                name,
                description,
                TrackingType.BOOLEAN,
                null,  // No numeric config for boolean
                isPublic,
                !isPublic,  // If public, not private; if not public, private
                creator,
                null,  // Not a copy
                Instant.now()
        );
    }

    /**
     * Creates a new numeric habit (quantity tracking).
     *
     * @param name the habit name
     * @param description optional description
     * @param numericConfig the numeric configuration
     * @param isPublic can others see/copy this?
     * @param creator who is creating this
     * @return a new numeric Habit
     */
    public static Habit createNumeric(
            String name,
            String description,
            NumericConfig numericConfig,
            boolean isPublic,
            HabitPractitionerId creator) {

        return new Habit(
                HabitId.generate(),
                name,
                description,
                TrackingType.NUMERIC,
                numericConfig,
                isPublic,
                !isPublic,
                creator,
                null,  // Not a copy
                Instant.now()
        );
    }

    /**
     * Creates a copy of an existing habit.
     * Copies start as private, even if the source was public.
     *
     * @param source the habit to copy
     * @param newCreator who is copying this
     * @return a new Habit that is a copy of the source
     */
    public static Habit copyFrom(Habit source, HabitPractitionerId newCreator) {
        Objects.requireNonNull(source, "Source habit cannot be null");
        Objects.requireNonNull(newCreator, "New creator cannot be null");

        if (!source.canBeCopied()) {
            throw new IllegalArgumentException("Cannot copy private habit");
        }

        return new Habit(
                HabitId.generate(),
                source.name,
                source.description,
                source.trackingType,
                source.numericConfig,
                false,  // Copies start as private
                true,
                newCreator,
                source.id,  // Reference to source
                Instant.now()
        );
    }

    // Behavior methods

    /**
     * Checks if this habit is a copy of another habit.
     *
     * @return true if this is a copy
     */
    public boolean isCopy() {
        return sourceHabit != null;
    }

    /**
     * Checks if this is a boolean habit.
     *
     * @return true if trackingType is BOOLEAN
     */
    public boolean isBoolean() {
        return trackingType == TrackingType.BOOLEAN;
    }

    /**
     * Checks if this is a numeric habit.
     *
     * @return true if trackingType is NUMERIC
     */
    public boolean isNumeric() {
        return trackingType == TrackingType.NUMERIC;
    }

    /**
     * Checks if this habit can be copied by others.
     *
     * @return true if the habit is public
     */
    public boolean canBeCopied() {
        return isPublic;
    }

    /**
     * Checks if this habit is owned by the given practitioner.
     *
     * @param practitionerId the practitioner to check
     * @return true if the practitioner created this habit
     */
    public boolean isOwnedBy(HabitPractitionerId practitionerId) {
        return creator.equals(practitionerId);
    }

    // Validation helper methods

    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        String trimmed = name.trim();
        if (trimmed.length() > 200) {
            throw new IllegalArgumentException("Name cannot exceed 200 characters, got: " + trimmed.length());
        }
        return trimmed;
    }

    private String validateDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;  // Description is optional
        }
        String trimmed = description.trim();
        if (trimmed.length() > 1000) {
            throw new IllegalArgumentException("Description cannot exceed 1000 characters, got: " + trimmed.length());
        }
        return trimmed;
    }

    private NumericConfig validateNumericConfig(TrackingType type, NumericConfig config) {
        if (type == TrackingType.NUMERIC && config == null) {
            throw new IllegalArgumentException("NumericConfig required for NUMERIC tracking");
        }
        if (type == TrackingType.BOOLEAN && config != null) {
            throw new IllegalArgumentException("NumericConfig must be null for BOOLEAN tracking");
        }
        return config;
    }

    private void validateVisibility(boolean isPublic, boolean isPrivate) {
        if (isPublic && isPrivate) {
            throw new IllegalArgumentException("Habit cannot be both public and private");
        }
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

    public HabitId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TrackingType getTrackingType() {
        return trackingType;
    }

    public NumericConfig getNumericConfig() {
        return numericConfig;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public HabitPractitionerId getCreator() {
        return creator;
    }

    public HabitId getSourceHabit() {
        return sourceHabit;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // equals, hashCode, toString based on ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Habit habit = (Habit) o;
        return Objects.equals(id, habit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Habit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + trackingType +
                ", public=" + isPublic +
                ", copy=" + isCopy() +
                '}';
    }

    /**
     * Standalone test method (ADR-004: Domain model testing with main()).
     */
    public static void main(String[] args) {
        System.out.println("Testing Habit...\n");

        // Setup
        HabitPractitionerId creator1 = HabitPractitionerId.generate();
        HabitPractitionerId creator2 = HabitPractitionerId.generate();

        // Test 1: Create boolean habit (public)
        Habit boolHabit = Habit.createBoolean("Pray Rosary", "Daily prayer", true, creator1);
        assert boolHabit != null;
        assert boolHabit.getName().equals("Pray Rosary");
        assert boolHabit.isBoolean();
        assert !boolHabit.isNumeric();
        assert boolHabit.isPublic();
        assert !boolHabit.isPrivate();
        assert !boolHabit.isCopy();
        assert boolHabit.canBeCopied();
        System.out.println("✓ Test 1: Create boolean habit: " + boolHabit);

        // Test 2: Create boolean habit (private)
        Habit privateBool = Habit.createBoolean("Personal Goal", null, false, creator1);
        assert !privateBool.isPublic();
        assert privateBool.isPrivate();
        assert !privateBool.canBeCopied();
        System.out.println("✓ Test 2: Create private boolean habit");

        // Test 3: Create numeric habit
        NumericConfig swimConfig = new NumericConfig("meters", null, null, 1500);
        Habit numericHabit = Habit.createNumeric("Swimming", "Cardio workout", swimConfig, true, creator1);
        assert numericHabit.isNumeric();
        assert !numericHabit.isBoolean();
        assert numericHabit.getNumericConfig() != null;
        assert numericHabit.getNumericConfig().unit().equals("meters");
        System.out.println("✓ Test 3: Create numeric habit: " + numericHabit);

        // Test 4: Copy a public habit
        Habit copy = Habit.copyFrom(boolHabit, creator2);
        assert copy.isCopy();
        assert copy.getSourceHabit().equals(boolHabit.getId());
        assert copy.getName().equals(boolHabit.getName());
        assert !copy.isPublic();  // Copies start as private
        assert copy.isPrivate();
        assert copy.isOwnedBy(creator2);
        assert !copy.isOwnedBy(creator1);
        System.out.println("✓ Test 4: Copy public habit: " + copy);

        // Test 5: Cannot copy private habit
        try {
            Habit.copyFrom(privateBool, creator2);
            assert false : "Should not be able to copy private habit";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 5: Cannot copy private habit: " + e.getMessage());
        }

        // Test 6: isOwnedBy
        assert boolHabit.isOwnedBy(creator1);
        assert !boolHabit.isOwnedBy(creator2);
        assert copy.isOwnedBy(creator2);
        System.out.println("✓ Test 6: isOwnedBy works");

        // Test 7: Null name validation
        try {
            Habit.createBoolean(null, null, false, creator1);
            assert false : "Should throw for null name";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 7: Null name validation works: " + e.getMessage());
        }

        // Test 8: Blank name validation
        try {
            Habit.createBoolean("   ", null, false, creator1);
            assert false : "Should throw for blank name";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 8: Blank name validation works: " + e.getMessage());
        }

        // Test 9: Name too long
        try {
            String longName = "A".repeat(201);
            Habit.createBoolean(longName, null, false, creator1);
            assert false : "Should throw for name too long";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 9: Name too long validation works: " + e.getMessage());
        }

        // Test 10: Description too long
        try {
            String longDesc = "A".repeat(1001);
            Habit.createBoolean("Test", longDesc, false, creator1);
            assert false : "Should throw for description too long";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 10: Description too long validation works: " + e.getMessage());
        }

        // Test 11: Null description is ok
        Habit noDesc = Habit.createBoolean("Test", null, false, creator1);
        assert noDesc.getDescription() == null;
        System.out.println("✓ Test 11: Null description is allowed");

        // Test 12: Blank description becomes null
        Habit blankDesc = Habit.createBoolean("Test", "   ", false, creator1);
        assert blankDesc.getDescription() == null;
        System.out.println("✓ Test 12: Blank description becomes null");

        // Test 13: Null trackingType validation
        try {
            new Habit(HabitId.generate(), "Test", null, null, null, false, true, creator1, null, Instant.now());
            assert false : "Should throw for null trackingType";
        } catch (NullPointerException e) {
            System.out.println("✓ Test 13: Null trackingType validation works: " + e.getMessage());
        }

        // Test 14: NUMERIC without NumericConfig
        try {
            new Habit(HabitId.generate(), "Test", null, TrackingType.NUMERIC, null, false, true, creator1, null, Instant.now());
            assert false : "Should throw for NUMERIC without config";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 14: NUMERIC requires NumericConfig: " + e.getMessage());
        }

        // Test 15: BOOLEAN with NumericConfig
        try {
            NumericConfig config = new NumericConfig("pages", null, null, 10);
            new Habit(HabitId.generate(), "Test", null, TrackingType.BOOLEAN, config, false, true, creator1, null, Instant.now());
            assert false : "Should throw for BOOLEAN with config";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 15: BOOLEAN cannot have NumericConfig: " + e.getMessage());
        }

        // Test 16: Both public and private
        try {
            new Habit(HabitId.generate(), "Test", null, TrackingType.BOOLEAN, null, true, true, creator1, null, Instant.now());
            assert false : "Should throw for both public and private";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 16: Cannot be both public and private: " + e.getMessage());
        }

        // Test 17: Null creator
        try {
            Habit.createBoolean("Test", null, false, null);
            assert false : "Should throw for null creator";
        } catch (NullPointerException e) {
            System.out.println("✓ Test 17: Null creator validation works: " + e.getMessage());
        }

        // Test 18: Null createdAt
        try {
            new Habit(HabitId.generate(), "Test", null, TrackingType.BOOLEAN, null, false, true, creator1, null, null);
            assert false : "Should throw for null createdAt";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 18: Null createdAt validation works: " + e.getMessage());
        }

        // Test 19: Future createdAt
        try {
            Instant future = Instant.now().plusSeconds(3600);
            new Habit(HabitId.generate(), "Test", null, TrackingType.BOOLEAN, null, false, true, creator1, null, future);
            assert false : "Should throw for future createdAt";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Test 19: Future createdAt validation works: " + e.getMessage());
        }

        // Test 20: Equality based on ID
        Habit h1 = Habit.createBoolean("A", null, false, creator1);
        Habit h2 = Habit.createBoolean("A", null, false, creator1);
        assert !h1.equals(h2) : "Different IDs should not be equal";
        Habit h3 = new Habit(h1.getId(), "B", null, TrackingType.BOOLEAN, null, true, false, creator2, null, Instant.now());
        assert h1.equals(h3) : "Same ID should be equal";
        System.out.println("✓ Test 20: Equality based on ID works");

        // Test 21: HashCode based on ID
        assert h1.hashCode() == h3.hashCode();
        System.out.println("✓ Test 21: HashCode based on ID works");

        // Test 22: toString()
        String str = boolHabit.toString();
        assert str.contains("Pray Rosary");
        assert str.contains("BOOLEAN");
        System.out.println("✓ Test 22: toString() works: " + str);

        // Test 23: Whitespace trimming in name
        Habit trimmed = Habit.createBoolean("  Test Name  ", null, false, creator1);
        assert trimmed.getName().equals("Test Name");
        System.out.println("✓ Test 23: Name whitespace trimming works");

        // Test 24: Null source in copyFrom
        try {
            Habit.copyFrom(null, creator1);
            assert false : "Should throw for null source";
        } catch (NullPointerException e) {
            System.out.println("✓ Test 24: Null source in copyFrom validation works: " + e.getMessage());
        }

        // Test 25: Null newCreator in copyFrom
        try {
            Habit.copyFrom(boolHabit, null);
            assert false : "Should throw for null newCreator";
        } catch (NullPointerException e) {
            System.out.println("✓ Test 25: Null newCreator in copyFrom validation works: " + e.getMessage());
        }

        System.out.println("\n✅ All Habit tests passed!");
    }
}
