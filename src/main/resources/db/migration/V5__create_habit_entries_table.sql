-- V5: Create habit_entries table (Daily completions)
--
-- This table represents HabitEntry entities (not an aggregate root).
-- Entries record daily completions for routines.
-- Each entry belongs to a routine and represents one day's completion.

CREATE TABLE habit_entries (
    id                  BINARY(16)      PRIMARY KEY COMMENT 'UUID as binary (HabitEntryId)',
    routine_id          BINARY(16)      NOT NULL COMMENT 'FK to routines',

    -- Completion info
    date                DATE            NOT NULL COMMENT 'The date of completion',
    completed           BOOLEAN         NOT NULL DEFAULT TRUE COMMENT 'Always true (marking completion)',

    -- Value (for numeric habits only)
    value               INT             NULL COMMENT 'Numeric value (e.g., "ran 5 miles", "read 30 pages")',

    -- Optional notes
    notes               VARCHAR(1000)   NULL COMMENT 'User notes about this completion',

    -- Audit timestamps
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_habit_entries_routine
        FOREIGN KEY (routine_id) REFERENCES routines(id)
        ON DELETE CASCADE,

    -- Business rule: One entry per date per routine
    CONSTRAINT uq_habit_entries_routine_date
        UNIQUE (routine_id, date),

    -- Indexes
    INDEX idx_habit_entries_routine (routine_id),
    INDEX idx_habit_entries_date (date),
    INDEX idx_habit_entries_routine_date (routine_id, date)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Habit entries - daily completion records';
