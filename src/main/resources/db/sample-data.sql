-- Sample Data Script for Plan 91
-- Creates a test user with multiple routines and historical entries

-- Clean up existing sample data (if any)
DELETE FROM habit_entries WHERE routine_id IN (
    SELECT id FROM routines WHERE practitioner_id = (
        SELECT id FROM habit_practitioners WHERE email = 'luis@luis.com'
    )
);
DELETE FROM routines WHERE practitioner_id = (
    SELECT id FROM habit_practitioners WHERE email = 'luis@luis.com'
);
DELETE FROM habits WHERE creator_id = (
    SELECT id FROM habit_practitioners WHERE email = 'luis@luis.com'
);
DELETE FROM habit_practitioners WHERE email = 'luis@luis.com';
DELETE FROM users WHERE email = 'luis@luis.com';

-- Create sample user
-- Username: luis@luis.com
-- Password: demo123
INSERT INTO users (id, email, password_hash, enabled, account_locked, created_at, updated_at)
VALUES (
    UUID_TO_BIN(UUID()),
    'luis@luis.com',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYzpLHJ3uj6',  -- BCrypt hash for "demo123"
    TRUE,
    FALSE,
    NOW(),
    NOW()
);

-- Get the user ID for the practitioner
SET @user_id = (SELECT id FROM users WHERE email = 'luis@luis.com');

-- Create sample practitioner
INSERT INTO habit_practitioners (id, user_id, first_name, last_name, email, bio, auth0_id, original_timezone, created_at, updated_at)
VALUES (
    UUID_TO_BIN(UUID()),
    @user_id,
    'Luis',
    'Fernandez',
    'luis@luis.com',
    'Building better habits through consistent daily practice and tracking my 91-day journey.',
    'auth0|sample123',
    'America/New_York',
    NOW(),
    NOW()
);

-- Get the practitioner ID for subsequent inserts
SET @practitioner_id = (SELECT id FROM habit_practitioners WHERE email = 'luis@luis.com');

-- Create Habit 1: Morning Meditation (Daily, started 45 days ago)
SET @habit1_id = UUID_TO_BIN(UUID());
INSERT INTO habits (id, creator_id, name, description, tracking_type, is_public, is_private, created_at, updated_at)
VALUES (
    @habit1_id,
    @practitioner_id,
    'Morning Meditation',
    'Start each day with 15 minutes of mindfulness meditation',
    'BOOLEAN',
    TRUE,
    FALSE,
    DATE_SUB(NOW(), INTERVAL 50 DAY),
    NOW()
);

-- Create Routine 1 for Morning Meditation (started 45 days ago)
SET @routine1_id = UUID_TO_BIN(UUID());
INSERT INTO routines (
    id, practitioner_id, habit_id, start_date, expected_end_date,
    recurrence_type, status,
    current_streak, longest_streak, total_completions, has_used_strike, strike_date, last_completion_date,
    created_at, updated_at
)
VALUES (
    @routine1_id,
    @practitioner_id,
    @habit1_id,
    DATE_SUB(CURDATE(), INTERVAL 45 DAY),
    DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 45 DAY), INTERVAL 90 DAY),
    'DAILY',
    'ACTIVE',
    7, 15, 38, FALSE, NULL, DATE_SUB(CURDATE(), INTERVAL 1 DAY),
    DATE_SUB(NOW(), INTERVAL 45 DAY),
    NOW()
);

-- Create Habit 2: Exercise (Weekdays, started 30 days ago)
SET @habit2_id = UUID_TO_BIN(UUID());
INSERT INTO habits (id, creator_id, name, description, tracking_type, numeric_unit_name, numeric_min_value, numeric_max_value, numeric_target, is_public, is_private, created_at, updated_at)
VALUES (
    @habit2_id,
    @practitioner_id,
    'Daily Exercise',
    'Get moving with at least 30 minutes of physical activity',
    'NUMERIC',
    'minutes',
    15,
    120,
    30,
    TRUE,
    FALSE,
    DATE_SUB(NOW(), INTERVAL 35 DAY),
    NOW()
);

-- Create Routine 2 for Exercise (started 30 days ago)
SET @routine2_id = UUID_TO_BIN(UUID());
INSERT INTO routines (
    id, practitioner_id, habit_id, start_date, expected_end_date,
    recurrence_type, recurrence_specific_days, status,
    current_streak, longest_streak, total_completions, has_used_strike, strike_date, last_completion_date,
    created_at, updated_at
)
VALUES (
    @routine2_id,
    @practitioner_id,
    @habit2_id,
    DATE_SUB(CURDATE(), INTERVAL 30 DAY),
    DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 30 DAY), INTERVAL 90 DAY),
    'SPECIFIC_DAYS',
    'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY',
    'ACTIVE',
    5, 12, 24, TRUE, DATE_SUB(CURDATE(), INTERVAL 15 DAY), DATE_SUB(CURDATE(), INTERVAL 1 DAY),
    DATE_SUB(NOW(), INTERVAL 30 DAY),
    NOW()
);

-- Create Habit 3: Reading (Daily, started 60 days ago)
SET @habit3_id = UUID_TO_BIN(UUID());
INSERT INTO habits (id, creator_id, name, description, tracking_type, numeric_unit_name, numeric_min_value, numeric_max_value, numeric_target, is_public, is_private, created_at, updated_at)
VALUES (
    @habit3_id,
    @practitioner_id,
    'Read Books',
    'Read at least 20 pages per day to develop a reading habit',
    'NUMERIC',
    'pages',
    10,
    100,
    20,
    TRUE,
    FALSE,
    DATE_SUB(NOW(), INTERVAL 65 DAY),
    NOW()
);

-- Create Routine 3 for Reading (started 60 days ago)
SET @routine3_id = UUID_TO_BIN(UUID());
INSERT INTO routines (
    id, practitioner_id, habit_id, start_date, expected_end_date,
    recurrence_type, status,
    current_streak, longest_streak, total_completions, has_used_strike, strike_date, last_completion_date,
    created_at, updated_at
)
VALUES (
    @routine3_id,
    @practitioner_id,
    @habit3_id,
    DATE_SUB(CURDATE(), INTERVAL 60 DAY),
    DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 60 DAY), INTERVAL 90 DAY),
    'DAILY',
    'ACTIVE',
    12, 22, 52, FALSE, NULL, DATE_SUB(CURDATE(), INTERVAL 1 DAY),
    DATE_SUB(NOW(), INTERVAL 60 DAY),
    NOW()
);

-- Create Habit 4: Journaling (Daily, started 20 days ago)
SET @habit4_id = UUID_TO_BIN(UUID());
INSERT INTO habits (id, creator_id, name, description, tracking_type, is_public, is_private, created_at, updated_at)
VALUES (
    @habit4_id,
    @practitioner_id,
    'Evening Journaling',
    'Reflect on the day with 10 minutes of journaling before bed',
    'BOOLEAN',
    FALSE,
    TRUE,
    DATE_SUB(NOW(), INTERVAL 25 DAY),
    NOW()
);

-- Create Routine 4 for Journaling (started 20 days ago)
SET @routine4_id = UUID_TO_BIN(UUID());
INSERT INTO routines (
    id, practitioner_id, habit_id, start_date, expected_end_date,
    recurrence_type, status,
    current_streak, longest_streak, total_completions, has_used_strike, strike_date, last_completion_date,
    created_at, updated_at
)
VALUES (
    @routine4_id,
    @practitioner_id,
    @habit4_id,
    DATE_SUB(CURDATE(), INTERVAL 20 DAY),
    DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 20 DAY), INTERVAL 90 DAY),
    'DAILY',
    'ACTIVE',
    3, 8, 16, FALSE, NULL, DATE_SUB(CURDATE(), INTERVAL 1 DAY),
    DATE_SUB(NOW(), INTERVAL 20 DAY),
    NOW()
);

-- ============================================
-- Historical Entries for Morning Meditation
-- ============================================

-- Days 1-10: Consistent start (10/10)
INSERT INTO habit_entries (id, routine_id, date, completed, notes, created_at)
SELECT UUID_TO_BIN(UUID()), @routine1_id, DATE_SUB(CURDATE(), INTERVAL d DAY), TRUE,
       CASE
           WHEN d % 3 = 0 THEN 'Great meditation session today!'
           WHEN d % 5 = 0 THEN 'Feeling more centered and focused'
           ELSE NULL
       END,
       NOW()
FROM (
    SELECT 45 as d UNION SELECT 44 UNION SELECT 43 UNION SELECT 42 UNION SELECT 41
    UNION SELECT 40 UNION SELECT 39 UNION SELECT 38 UNION SELECT 37 UNION SELECT 36
) days;

-- Days 11-20: Missed 2 days (8/10)
INSERT INTO habit_entries (id, routine_id, date, completed, notes, created_at)
SELECT UUID_TO_BIN(UUID()), @routine1_id, DATE_SUB(CURDATE(), INTERVAL d DAY), TRUE, NULL, NOW()
FROM (
    SELECT 35 as d UNION SELECT 34 UNION SELECT 33 UNION SELECT 32 UNION SELECT 30
    UNION SELECT 29 UNION SELECT 27 UNION SELECT 26
) days;

-- Days 21-30: Getting back on track (9/10)
INSERT INTO habit_entries (id, routine_id, date, completed, notes, created_at)
SELECT UUID_TO_BIN(UUID()), @routine1_id, DATE_SUB(CURDATE(), INTERVAL d DAY), TRUE,
       CASE WHEN d = 20 THEN 'Back to my routine!' ELSE NULL END,
       NOW()
FROM (
    SELECT 25 as d UNION SELECT 24 UNION SELECT 23 UNION SELECT 22 UNION SELECT 21
    UNION SELECT 20 UNION SELECT 18 UNION SELECT 17 UNION SELECT 16
) days;

-- Days 31-45: Recent streak (11/15) - current 7-day streak
INSERT INTO habit_entries (id, routine_id, date, completed, notes, created_at)
SELECT UUID_TO_BIN(UUID()), @routine1_id, DATE_SUB(CURDATE(), INTERVAL d DAY), TRUE,
       CASE WHEN d = 1 THEN 'One week streak!' ELSE NULL END,
       NOW()
FROM (
    SELECT 15 as d UNION SELECT 14 UNION SELECT 13 UNION SELECT 11
    UNION SELECT 9 UNION SELECT 7 UNION SELECT 6 UNION SELECT 5 UNION SELECT 4
    UNION SELECT 3 UNION SELECT 2 UNION SELECT 1
) days;

-- ============================================
-- Historical Entries for Exercise (Weekdays only)
-- ============================================

-- Create entries for weekdays over the past 30 days
INSERT INTO habit_entries (id, routine_id, date, completed, value, notes, created_at)
SELECT
    UUID_TO_BIN(UUID()),
    @routine2_id,
    d.entry_date,
    CASE
        -- Miss some random weekdays to make it realistic
        WHEN d.entry_date = DATE_SUB(CURDATE(), INTERVAL 25 DAY) THEN FALSE
        WHEN d.entry_date = DATE_SUB(CURDATE(), INTERVAL 18 DAY) THEN FALSE
        WHEN d.entry_date = DATE_SUB(CURDATE(), INTERVAL 12 DAY) THEN FALSE
        WHEN d.entry_date = DATE_SUB(CURDATE(), INTERVAL 3 DAY) THEN FALSE
        ELSE TRUE
    END,
    CASE
        WHEN d.entry_date = DATE_SUB(CURDATE(), INTERVAL 25 DAY) THEN NULL
        WHEN d.entry_date = DATE_SUB(CURDATE(), INTERVAL 18 DAY) THEN NULL
        WHEN d.entry_date = DATE_SUB(CURDATE(), INTERVAL 12 DAY) THEN NULL
        WHEN d.entry_date = DATE_SUB(CURDATE(), INTERVAL 3 DAY) THEN NULL
        ELSE FLOOR(25 + RAND() * 35)  -- Random between 25-60 minutes
    END,
    CASE
        WHEN d.entry_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY) THEN 'Great run this morning!'
        WHEN d.entry_date = DATE_SUB(CURDATE(), INTERVAL 5 DAY) THEN 'Yoga session - feeling flexible'
        WHEN d.entry_date = DATE_SUB(CURDATE(), INTERVAL 10 DAY) THEN 'Pushed myself harder today'
        ELSE NULL
    END,
    NOW()
FROM (
    SELECT DATE_SUB(CURDATE(), INTERVAL n DAY) as entry_date
    FROM (
        SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
        UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
        UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15
        UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
        UNION SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25
        UNION SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29 UNION SELECT 30
    ) nums
) d
WHERE DAYOFWEEK(d.entry_date) BETWEEN 2 AND 6;  -- Monday to Friday

-- ============================================
-- Historical Entries for Reading
-- ============================================

-- Consistent reading over 60 days with some gaps
INSERT INTO habit_entries (id, routine_id, date, completed, value, notes, created_at)
SELECT
    UUID_TO_BIN(UUID()),
    @routine3_id,
    DATE_SUB(CURDATE(), INTERVAL d DAY),
    CASE
        -- Create realistic pattern with some missed days
        WHEN d % 7 = 6 THEN FALSE  -- Miss some Saturdays
        WHEN d IN (48, 42, 35, 28, 21, 14, 7) THEN FALSE  -- Miss some specific days
        ELSE TRUE
    END,
    CASE
        WHEN d % 7 = 6 THEN NULL
        WHEN d IN (48, 42, 35, 28, 21, 14, 7) THEN NULL
        ELSE FLOOR(15 + RAND() * 40)  -- Random between 15-55 pages
    END,
    CASE
        WHEN d = 1 THEN 'Finished chapter 12 - plot is getting intense!'
        WHEN d = 5 THEN 'Started a new book on productivity'
        WHEN d = 15 THEN 'Read during lunch break'
        WHEN d = 30 THEN 'Half marathon mark!'
        ELSE NULL
    END,
    NOW()
FROM (
    SELECT 1 d UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
    UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15
    UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
    UNION SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25
    UNION SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29 UNION SELECT 30
    UNION SELECT 31 UNION SELECT 32 UNION SELECT 33 UNION SELECT 34 UNION SELECT 35
    UNION SELECT 36 UNION SELECT 37 UNION SELECT 38 UNION SELECT 39 UNION SELECT 40
    UNION SELECT 41 UNION SELECT 42 UNION SELECT 43 UNION SELECT 44 UNION SELECT 45
    UNION SELECT 46 UNION SELECT 47 UNION SELECT 48 UNION SELECT 49 UNION SELECT 50
    UNION SELECT 51 UNION SELECT 52 UNION SELECT 53 UNION SELECT 54 UNION SELECT 55
    UNION SELECT 56 UNION SELECT 57 UNION SELECT 58 UNION SELECT 59 UNION SELECT 60
) days;

-- ============================================
-- Historical Entries for Journaling
-- ============================================

-- Recent habit with building consistency
INSERT INTO habit_entries (id, routine_id, date, completed, notes, created_at)
SELECT
    UUID_TO_BIN(UUID()),
    @routine4_id,
    DATE_SUB(CURDATE(), INTERVAL d DAY),
    CASE
        WHEN d IN (19, 17, 14, 10) THEN FALSE  -- Missed 4 days
        ELSE TRUE
    END,
    CASE
        WHEN d = 1 THEN 'Reflected on my progress this week'
        WHEN d = 5 THEN 'Grateful for my supportive friends'
        WHEN d = 12 THEN 'Set some new goals for next month'
        WHEN d IN (19, 17, 14, 10) THEN NULL
        ELSE 'Daily reflection complete'
    END,
    NOW()
FROM (
    SELECT 1 d UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
    UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
    UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15
    UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
) days;

-- Display summary
SELECT
    CONCAT(hp.first_name, ' ', hp.last_name) as 'Practitioner',
    hp.email as 'Email',
    COUNT(DISTINCT h.id) as 'Total Habits',
    COUNT(DISTINCT r.id) as 'Active Routines',
    COUNT(he.id) as 'Total Entries'
FROM habit_practitioners hp
LEFT JOIN habits h ON h.creator_id = hp.id
LEFT JOIN routines r ON r.practitioner_id = hp.id
LEFT JOIN habit_entries he ON he.routine_id = r.id
WHERE hp.email = 'luis@luis.com'
GROUP BY hp.id;

SELECT
    h.name as 'Habit',
    r.recurrence_type as 'Frequency',
    r.current_streak as 'Current Streak',
    r.longest_streak as 'Longest Streak',
    r.total_completions as 'Total Days',
    DATEDIFF(CURDATE(), r.start_date) as 'Days Since Start',
    ROUND((r.total_completions / DATEDIFF(CURDATE(), r.start_date)) * 100, 1) as 'Completion %'
FROM routines r
JOIN habits h ON h.id = r.habit_id
WHERE r.practitioner_id = @practitioner_id
ORDER BY r.start_date;
