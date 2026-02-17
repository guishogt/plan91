-- Add bio field to habit_practitioners table
-- Epic 06: Social Features - Practitioner profiles

ALTER TABLE habit_practitioners
    ADD COLUMN bio TEXT NULL COMMENT 'Practitioner bio/description for public profile';

-- Add index for searching practitioners by name
CREATE INDEX idx_practitioners_name ON habit_practitioners(first_name, last_name);
