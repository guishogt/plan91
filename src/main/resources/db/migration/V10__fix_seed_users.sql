-- V10: Fix seed users with valid BCrypt hashes
-- This fixes V9 which had an invalid hash

-- Delete any existing seed users and their data (cascade delete in correct order)
-- 1. Delete habit_entries for routines owned by these practitioners
DELETE he FROM habit_entries he
INNER JOIN routines r ON he.routine_id = r.id
INNER JOIN habit_practitioners hp ON r.practitioner_id = hp.id
WHERE hp.email IN ('admin', 'luis@fernandezgt.com');

-- 2. Delete routines owned by these practitioners
DELETE r FROM routines r
INNER JOIN habit_practitioners hp ON r.practitioner_id = hp.id
WHERE hp.email IN ('admin', 'luis@fernandezgt.com');

-- 3. Delete the practitioners
DELETE FROM habit_practitioners WHERE email IN ('admin', 'luis@fernandezgt.com');

-- 4. Delete the users
DELETE FROM users WHERE email IN ('admin', 'luis@fernandezgt.com');

-- Insert admin user with valid hash (password: admin)
INSERT INTO users (id, email, password_hash, enabled, account_locked, created_at, updated_at)
VALUES (
    UUID_TO_BIN(UUID()),
    'admin',
    '$2a$12$Rb.9lYogSCD16OnxlwYX0.1.AoBFQOdVhA1NdSRSSxq4w.1fF9VDy',
    TRUE,
    FALSE,
    NOW(),
    NOW()
);

-- Insert luis user with valid hash (password: inteligencia)
INSERT INTO users (id, email, password_hash, enabled, account_locked, created_at, updated_at)
VALUES (
    UUID_TO_BIN(UUID()),
    'luis@fernandezgt.com',
    '$2a$12$kO.ZXtjmMowt9JWGUuY4juG/t1Ojhx7uB2M.6DQasHjGZmSJPc0Ey',
    TRUE,
    FALSE,
    NOW(),
    NOW()
);

-- Create practitioner for admin
INSERT INTO habit_practitioners (id, user_id, first_name, last_name, email, bio, original_timezone, created_at, updated_at)
SELECT
    UUID_TO_BIN(UUID()),
    id,
    'Admin',
    'User',
    'admin',
    'Plan 91 admin account',
    'America/New_York',
    NOW(),
    NOW()
FROM users WHERE email = 'admin';

-- Create practitioner for luis
INSERT INTO habit_practitioners (id, user_id, first_name, last_name, email, bio, original_timezone, created_at, updated_at)
SELECT
    UUID_TO_BIN(UUID()),
    id,
    'Luis',
    'Fernandez',
    'luis@fernandezgt.com',
    NULL,
    'America/Guatemala',
    NOW(),
    NOW()
FROM users WHERE email = 'luis@fernandezgt.com';
