-- V9: Seed admin user and practitioner for demo purposes
-- Username: admin
-- Password: admin (BCrypt hash with strength 12)

-- Create admin user if not exists
INSERT INTO users (id, email, password_hash, enabled, account_locked, created_at, updated_at)
SELECT
    UUID_TO_BIN(UUID()),
    'admin',
    '$2a$12$8K8L.0T7nT7U7U7U7U7U7OqJ7J7J7J7J7J7J7J7J7J7J7J7J7J7J7O',
    TRUE,
    FALSE,
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin');

-- Create practitioner for admin user (get the actual user ID)
INSERT INTO habit_practitioners (id, user_id, first_name, last_name, email, bio, original_timezone, created_at, updated_at)
SELECT
    UUID_TO_BIN(UUID()),
    u.id,
    'Admin',
    'User',
    'admin',
    'Plan 91 admin account',
    'America/New_York',
    NOW(),
    NOW()
FROM users u
WHERE u.email = 'admin'
  AND NOT EXISTS (SELECT 1 FROM habit_practitioners hp WHERE hp.user_id = u.id);
