-- V3: Create habits table (Habit definitions)
--
-- This table represents the Habit aggregate root.
-- Habits are definitions/templates that can be shared and copied.
-- They are separate from Routines (which represent 91-day commitments).

CREATE TABLE habits (
    id                  BINARY(16)      PRIMARY KEY COMMENT 'UUID as binary (HabitId)',
    creator_id          BINARY(16)      NOT NULL COMMENT 'FK to habit_practitioners (who created this habit)',

    -- Definition
    name                VARCHAR(200)    NOT NULL COMMENT 'Habit name (1-200 characters)',
    description         VARCHAR(1000)   NULL COMMENT 'Optional description (max 1000 characters)',

    -- Tracking configuration
    tracking_type       VARCHAR(20)     NOT NULL COMMENT 'BOOLEAN or NUMERIC',

    -- Numeric config (only if NUMERIC)
    numeric_unit_name   VARCHAR(50)     NULL COMMENT 'Unit name (e.g., "pages", "minutes")',
    numeric_min_value   INT             NULL COMMENT 'Minimum value',
    numeric_max_value   INT             NULL COMMENT 'Maximum value',
    numeric_target      INT             NULL COMMENT 'Target value',

    -- Visibility & sharing
    is_public           BOOLEAN         NOT NULL DEFAULT FALSE COMMENT 'Can others see/copy?',
    is_private          BOOLEAN         NOT NULL DEFAULT TRUE COMMENT 'Only visible to creator?',

    -- Provenance (copying/forking)
    source_habit_id     BINARY(16)      NULL COMMENT 'FK to habits (if copied from another habit)',

    -- Audit timestamps
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_habits_creator
        FOREIGN KEY (creator_id) REFERENCES habit_practitioners(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_habits_source
        FOREIGN KEY (source_habit_id) REFERENCES habits(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_tracking_type
        CHECK (tracking_type IN ('BOOLEAN', 'NUMERIC')),

    CONSTRAINT chk_numeric_config
        CHECK (
            (tracking_type = 'NUMERIC' AND numeric_unit_name IS NOT NULL) OR
            (tracking_type = 'BOOLEAN' AND numeric_unit_name IS NULL)
        ),

    CONSTRAINT chk_visibility
        CHECK (NOT (is_public = TRUE AND is_private = TRUE)),

    -- Indexes
    INDEX idx_habits_creator (creator_id),
    INDEX idx_habits_public (is_public),
    INDEX idx_habits_name (name),
    INDEX idx_habits_source (source_habit_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Habit aggregate root - habit definitions/templates';
