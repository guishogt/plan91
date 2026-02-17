package com.ctoblue.plan91.domain.habitentry;

import com.ctoblue.plan91.domain.routine.RoutineId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entity representing a single completion record for a specific date within a Routine.
 *
 * <p>HabitEntry records daily progress:
 * <ul>
 *   <li>Boolean habits: Just marks completion (value is null)</li>
 *   <li>Numeric habits: Stores actual value (e.g., "ran 5 miles", "read 30 pages")</li>
 * </ul>
 *
 * <p>Business rules:
 * <ul>
 *   <li>One entry per date per routine (enforced at repository)</li>
 *   <li>Entries are always deletable</li>
 *   <li>Date and routine are immutable once created</li>
 *   <li>Notes are optional and can be updated</li>
 *   <li>Value can be updated for numeric habits</li>
 * </ul>
 */
public class HabitEntry {

    private final HabitEntryId id;
    private final RoutineId routineId;
    private final LocalDate date;
    private final boolean completed;
    private Integer value;  // Nullable, only for numeric habits
    private String notes;   // Optional user notes
    private final Instant createdAt;
    private Instant updatedAt;

    /**
     * Private constructor (use factory methods).
     */
    private HabitEntry(
            HabitEntryId id,
            RoutineId routineId,
            LocalDate date,
            boolean completed,
            Integer value,
            String notes,
            Instant createdAt
    ) {
        Objects.requireNonNull(id, "HabitEntryId cannot be null");
        Objects.requireNonNull(routineId, "RoutineId cannot be null");
        Objects.requireNonNull(date, "Date cannot be null");
        Objects.requireNonNull(createdAt, "CreatedAt cannot be null");

        // Trim notes
        if (notes != null && notes.isBlank()) {
            notes = null;
        } else if (notes != null) {
            notes = notes.trim();
        }

        this.id = id;
        this.routineId = routineId;
        this.date = date;
        this.completed = completed;
        this.value = value;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    /**
     * Records a boolean habit completion.
     *
     * @param routineId the routine this entry belongs to
     * @param date the date of completion
     * @param notes optional notes (can be null)
     * @return a new HabitEntry for a boolean habit
     */
    public static HabitEntry recordBoolean(
            RoutineId routineId,
            LocalDate date,
            String notes
    ) {
        return new HabitEntry(
                HabitEntryId.generate(),
                routineId,
                date,
                true,  // Boolean habits are always "completed"
                null,  // No value for boolean
                notes,
                Instant.now()
        );
    }

    /**
     * Records a numeric habit completion.
     *
     * @param routineId the routine this entry belongs to
     * @param date the date of completion
     * @param value the numeric value recorded
     * @param notes optional notes (can be null)
     * @return a new HabitEntry for a numeric habit
     */
    public static HabitEntry recordNumeric(
            RoutineId routineId,
            LocalDate date,
            int value,
            String notes
    ) {
        return new HabitEntry(
                HabitEntryId.generate(),
                routineId,
                date,
                true,  // Mark as completed
                value,
                notes,
                Instant.now()
        );
    }

    /**
     * Updates the value (numeric habits only).
     *
     * @param newValue the new value
     * @throws IllegalStateException if this is a boolean habit
     */
    public void updateValue(int newValue) {
        if (value == null) {
            throw new IllegalStateException("Cannot update value for boolean habit entry");
        }
        this.value = newValue;
        this.updatedAt = Instant.now();
    }

    /**
     * Updates the notes.
     *
     * @param newNotes the new notes (null or blank clears notes)
     */
    public void updateNotes(String newNotes) {
        if (newNotes == null || newNotes.isBlank()) {
            this.notes = null;
        } else {
            this.notes = newNotes.trim();
        }
        this.updatedAt = Instant.now();
    }

    /**
     * Checks if this entry belongs to the given routine.
     *
     * @param routineId the routine ID to check
     * @return true if this entry belongs to the routine
     */
    public boolean isForRoutine(RoutineId routineId) {
        return this.routineId.equals(routineId);
    }

    /**
     * Checks if this entry is for the given date.
     *
     * @param date the date to check
     * @return true if this entry is for the date
     */
    public boolean isOnDate(LocalDate date) {
        return this.date.equals(date);
    }

    /**
     * Checks if this is a numeric habit entry.
     *
     * @return true if this entry has a numeric value
     */
    public boolean isNumeric() {
        return value != null;
    }

    /**
     * Checks if this is a boolean habit entry.
     *
     * @return true if this entry has no numeric value
     */
    public boolean isBoolean() {
        return value == null;
    }

    // Getters

    public HabitEntryId getId() {
        return id;
    }

    public RoutineId getRoutineId() {
        return routineId;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Integer getValue() {
        return value;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HabitEntry that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "HabitEntry{" +
                "id=" + id +
                ", routineId=" + routineId +
                ", date=" + date +
                ", completed=" + completed +
                ", value=" + value +
                ", notes='" + notes + '\'' +
                '}';
    }

    /**
     * Standalone test method (ADR-004: Domain model testing with main()).
     */
    public static void main(String[] args) {
        System.out.println("Testing HabitEntry...\n");

        RoutineId routine1 = RoutineId.generate();
        RoutineId routine2 = RoutineId.generate();
        LocalDate today = LocalDate.of(2026, 1, 31);
        LocalDate yesterday = LocalDate.of(2026, 1, 30);

        // Test 1: Create boolean entry
        HabitEntry boolEntry = HabitEntry.recordBoolean(routine1, today, "Felt great!");
        assert boolEntry.getId() != null;
        assert boolEntry.getRoutineId().equals(routine1);
        assert boolEntry.getDate().equals(today);
        assert boolEntry.isCompleted();
        assert boolEntry.getValue() == null;
        assert boolEntry.getNotes().equals("Felt great!");
        assert boolEntry.isBoolean();
        assert !boolEntry.isNumeric();
        System.out.println("✓ Test 1: Boolean entry created: " + boolEntry);

        // Test 2: Create numeric entry
        HabitEntry numEntry = HabitEntry.recordNumeric(routine1, today, 5, "Ran 5 miles");
        assert numEntry.getId() != null;
        assert numEntry.getRoutineId().equals(routine1);
        assert numEntry.getDate().equals(today);
        assert numEntry.isCompleted();
        assert numEntry.getValue() == 5;
        assert numEntry.getNotes().equals("Ran 5 miles");
        assert numEntry.isNumeric();
        assert !numEntry.isBoolean();
        System.out.println("✓ Test 2: Numeric entry created: " + numEntry);

        // Test 3: Create entry with null notes
        HabitEntry noNotes = HabitEntry.recordBoolean(routine1, today, null);
        assert noNotes.getNotes() == null;
        System.out.println("✓ Test 3: Entry with null notes works");

        // Test 4: Create entry with blank notes (should be null)
        HabitEntry blankNotes = HabitEntry.recordBoolean(routine1, today, "   ");
        assert blankNotes.getNotes() == null;
        System.out.println("✓ Test 4: Blank notes converted to null");

        // Test 5: Update value (numeric entry)
        numEntry.updateValue(7);
        assert numEntry.getValue() == 7;
        System.out.println("✓ Test 5: Value updated from 5 to 7");

        // Test 6: Cannot update value for boolean entry
        try {
            boolEntry.updateValue(10);
            assert false : "Should throw for boolean entry";
        } catch (IllegalStateException e) {
            System.out.println("✓ Test 6: Cannot update value for boolean entry: " + e.getMessage());
        }

        // Test 7: Update notes
        boolEntry.updateNotes("Updated notes");
        assert boolEntry.getNotes().equals("Updated notes");
        System.out.println("✓ Test 7: Notes updated");

        // Test 8: Clear notes with null
        boolEntry.updateNotes(null);
        assert boolEntry.getNotes() == null;
        System.out.println("✓ Test 8: Notes cleared with null");

        // Test 9: Clear notes with blank string
        numEntry.updateNotes("   ");
        assert numEntry.getNotes() == null;
        System.out.println("✓ Test 9: Notes cleared with blank string");

        // Test 10: isForRoutine()
        assert boolEntry.isForRoutine(routine1);
        assert !boolEntry.isForRoutine(routine2);
        System.out.println("✓ Test 10: isForRoutine() works");

        // Test 11: isOnDate()
        assert boolEntry.isOnDate(today);
        assert !boolEntry.isOnDate(yesterday);
        System.out.println("✓ Test 11: isOnDate() works");

        // Test 12: Null id validation
        try {
            new HabitEntry(null, routine1, today, true, null, null, Instant.now());
            assert false : "Should throw for null id";
        } catch (NullPointerException e) {
            System.out.println("✓ Test 12: Null id validation works: " + e.getMessage());
        }

        // Test 13: Null routineId validation
        try {
            HabitEntry.recordBoolean(null, today, null);
            assert false : "Should throw for null routineId";
        } catch (NullPointerException e) {
            System.out.println("✓ Test 13: Null routineId validation works: " + e.getMessage());
        }

        // Test 14: Null date validation
        try {
            HabitEntry.recordBoolean(routine1, null, null);
            assert false : "Should throw for null date";
        } catch (NullPointerException e) {
            System.out.println("✓ Test 14: Null date validation works: " + e.getMessage());
        }

        // Test 15: Equality based on ID
        HabitEntry entry1 = HabitEntry.recordBoolean(routine1, today, null);
        HabitEntry entry2 = HabitEntry.recordBoolean(routine1, today, null);
        assert !entry1.equals(entry2) : "Different IDs should not be equal";
        assert entry1.equals(entry1) : "Same object should be equal";
        System.out.println("✓ Test 15: Equality based on ID works");

        // Test 16: Notes trimming
        HabitEntry withSpaces = HabitEntry.recordBoolean(routine1, today, "  trimmed  ");
        assert withSpaces.getNotes().equals("trimmed");
        System.out.println("✓ Test 16: Notes trimmed correctly");

        // Test 17: updatedAt changes
        HabitEntry entry = HabitEntry.recordNumeric(routine1, today, 10, "test");
        Instant original = entry.getUpdatedAt();
        try {
            Thread.sleep(10);  // Small delay to ensure timestamp changes
        } catch (InterruptedException e) {
            // Ignore
        }
        entry.updateValue(20);
        assert entry.getUpdatedAt().isAfter(original);
        System.out.println("✓ Test 17: updatedAt changes on update");

        // Test 18: toString() works
        String str = boolEntry.toString();
        assert str.contains("HabitEntry");
        assert str.contains(routine1.toString());
        System.out.println("✓ Test 18: toString() works: " + str);

        System.out.println("\n✅ All HabitEntry tests passed!");
    }
}
