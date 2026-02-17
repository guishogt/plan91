-- Add target_days column to routines table
-- Allows users to configure how many days their routine should last (default 91)

ALTER TABLE routines
ADD COLUMN target_days INT NOT NULL DEFAULT 91;
