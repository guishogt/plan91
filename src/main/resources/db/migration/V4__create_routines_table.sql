-- V4: Create routines table (91-day commitments)
--
-- This table represents the Routine aggregate root.
-- A Routine is a practitioner's 91-day commitment to practice a habit.
-- Embeds RecurrenceRule and HabitStreak as columns.

CREATE TABLE routines (
    id                      BINARY(16)      PRIMARY KEY COMMENT 'UUID as binary (RoutineId)',
    habit_id                BINARY(16)      NOT NULL COMMENT 'FK to habits',
    practitioner_id         BINARY(16)      NOT NULL COMMENT 'FK to habit_practitioners',

    -- Recurrence (when is it expected?)
    recurrence_type         VARCHAR(30)     NOT NULL COMMENT 'DAILY, WEEKDAYS, WEEKENDS, SPECIFIC_DAYS, NTH_DAY_OF_MONTH',
    recurrence_specific_days VARCHAR(50)    NULL COMMENT 'Comma-separated days for SPECIFIC_DAYS (e.g., "MONDAY,WEDNESDAY,FRIDAY")',
    recurrence_nth_day      VARCHAR(10)     NULL COMMENT 'Day of week for NTH_DAY_OF_MONTH',
    recurrence_nth_week     INT             NULL COMMENT 'Week number (1-4) for NTH_DAY_OF_MONTH',

    -- The 91-day cycle
    start_date              DATE            NOT NULL COMMENT 'When the routine started',
    expected_end_date       DATE            NOT NULL COMMENT 'Start date + 90 days (91 days total)',
    completed_at            DATE            NULL COMMENT 'When 91 days were completed (null if ongoing)',

    -- Streak tracking (embedded HabitStreak value object)
    current_streak          INT             NOT NULL DEFAULT 0 COMMENT 'Current consecutive completions',
    longest_streak          INT             NOT NULL DEFAULT 0 COMMENT 'Longest streak achieved',
    total_completions       INT             NOT NULL DEFAULT 0 COMMENT 'Total completions all-time',
    has_used_strike         BOOLEAN         NOT NULL DEFAULT FALSE COMMENT 'Has used one-strike rule?',
    strike_date             DATE            NULL COMMENT 'Date when strike was used',
    last_completion_date    DATE            NULL COMMENT 'Last completion date',

    -- Status
    status                  VARCHAR(20)     NOT NULL COMMENT 'ACTIVE, COMPLETED, ABANDONED',

    -- Audit timestamps
    created_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_routines_habit
        FOREIGN KEY (habit_id) REFERENCES habits(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_routines_practitioner
        FOREIGN KEY (practitioner_id) REFERENCES habit_practitioners(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_recurrence_type
        CHECK (recurrence_type IN ('DAILY', 'WEEKDAYS', 'WEEKENDS', 'SPECIFIC_DAYS', 'NTH_DAY_OF_MONTH')),

    CONSTRAINT chk_status
        CHECK (status IN ('ACTIVE', 'COMPLETED', 'ABANDONED')),

    CONSTRAINT chk_91_day_cycle
        CHECK (DATEDIFF(expected_end_date, start_date) = 90),

    CONSTRAINT chk_streak_values
        CHECK (current_streak >= 0 AND longest_streak >= 0 AND total_completions >= 0),

    CONSTRAINT chk_current_longest
        CHECK (current_streak <= longest_streak),

    -- Indexes
    INDEX idx_routines_habit (habit_id),
    INDEX idx_routines_practitioner (practitioner_id),
    INDEX idx_routines_status (status),
    INDEX idx_routines_start_date (start_date),
    INDEX idx_routines_practitioner_status (practitioner_id, status),
    INDEX idx_routines_practitioner_active (practitioner_id, status, start_date)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Routine aggregate root - 91-day commitments to habits';
