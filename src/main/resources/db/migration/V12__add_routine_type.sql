-- V12: Add routine_type column to distinguish ROUTINE vs TRACKER
-- ROUTINE: Goal-oriented with one-strike rule (default)
-- TRACKER: Streak tracking without penalties, runs indefinitely

ALTER TABLE routines ADD COLUMN routine_type VARCHAR(20) NOT NULL DEFAULT 'ROUTINE';

-- Make target_days nullable (TRACKER doesn't have a target)
ALTER TABLE routines MODIFY COLUMN target_days INT NULL;

-- Make expected_end_date nullable (TRACKER runs indefinitely)
ALTER TABLE routines MODIFY COLUMN expected_end_date DATE NULL;
