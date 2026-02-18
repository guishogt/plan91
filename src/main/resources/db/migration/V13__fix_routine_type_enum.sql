-- V13: Fix routine_type column to be ENUM type
-- Hibernate expects ENUM type, not VARCHAR

ALTER TABLE routines MODIFY COLUMN routine_type ENUM('ROUTINE', 'TRACKER') NOT NULL DEFAULT 'ROUTINE';
