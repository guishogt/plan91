-- V11: Seed public habits library
-- 50 curated habits across different categories

-- Get admin practitioner ID
SET @admin_id = (SELECT id FROM habit_practitioners WHERE email = 'admin');

-- Health & Fitness
INSERT INTO habits (id, creator_id, name, description, tracking_type, is_public, is_private, created_at, updated_at)
VALUES
    (UUID_TO_BIN(UUID()), @admin_id, 'Drink 8 glasses of water', 'Stay hydrated throughout the day with at least 8 glasses of water', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Exercise for 30 minutes', 'Get at least 30 minutes of physical activity', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Take 10,000 steps', 'Walk at least 10,000 steps throughout the day', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Do morning stretches', 'Start the day with 5-10 minutes of stretching', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Get 7-8 hours of sleep', 'Maintain consistent sleep schedule for optimal rest', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'No alcohol', 'Abstain from alcoholic beverages', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'No smoking', 'Stay smoke-free for the day', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Take vitamins', 'Take daily vitamins or supplements', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Eat vegetables', 'Include vegetables in at least one meal', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'No junk food', 'Avoid processed snacks and fast food', 'BOOLEAN', TRUE, FALSE, NOW(), NOW());

-- Mindfulness & Mental Health
INSERT INTO habits (id, creator_id, name, description, tracking_type, is_public, is_private, created_at, updated_at)
VALUES
    (UUID_TO_BIN(UUID()), @admin_id, 'Meditate for 10 minutes', 'Practice mindfulness meditation', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Journal', 'Write in a personal journal', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Practice gratitude', 'Write down 3 things you are grateful for', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'No social media', 'Take a break from social media platforms', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Deep breathing exercises', 'Practice deep breathing for stress relief', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Screen-free hour before bed', 'Avoid screens 1 hour before sleeping', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Practice positive affirmations', 'Repeat positive affirmations to yourself', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Spend time in nature', 'Get outside and enjoy natural surroundings', 'BOOLEAN', TRUE, FALSE, NOW(), NOW());

-- Learning & Personal Development
INSERT INTO habits (id, creator_id, name, description, tracking_type, is_public, is_private, created_at, updated_at)
VALUES
    (UUID_TO_BIN(UUID()), @admin_id, 'Read for 30 minutes', 'Read books or educational content', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Learn a new word', 'Expand vocabulary with a new word daily', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Practice a language', 'Study or practice a foreign language', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Listen to a podcast', 'Listen to an educational or inspiring podcast', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Take an online course lesson', 'Complete a lesson from an online course', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Watch a TED talk', 'Watch an inspiring or educational TED talk', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Practice a musical instrument', 'Practice playing an instrument for at least 15 minutes', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Write code', 'Practice programming or work on a coding project', 'BOOLEAN', TRUE, FALSE, NOW(), NOW());

-- Productivity & Organization
INSERT INTO habits (id, creator_id, name, description, tracking_type, is_public, is_private, created_at, updated_at)
VALUES
    (UUID_TO_BIN(UUID()), @admin_id, 'Make the bed', 'Start the day by making your bed', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Plan tomorrow', 'Write down tasks and priorities for the next day', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Review goals', 'Review and reflect on personal goals', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Inbox zero', 'Process and organize all emails', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Declutter for 10 minutes', 'Spend 10 minutes organizing or cleaning', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Complete top 3 priorities', 'Finish the 3 most important tasks of the day', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'No procrastination', 'Start tasks immediately without delay', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Wake up early', 'Wake up before 7 AM', 'BOOLEAN', TRUE, FALSE, NOW(), NOW());

-- Social & Relationships
INSERT INTO habits (id, creator_id, name, description, tracking_type, is_public, is_private, created_at, updated_at)
VALUES
    (UUID_TO_BIN(UUID()), @admin_id, 'Call a friend or family member', 'Reach out to someone you care about', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Perform a random act of kindness', 'Do something kind for someone without expecting anything in return', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Give a compliment', 'Genuinely compliment someone', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Practice active listening', 'Focus fully when someone is speaking to you', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Spend quality time with family', 'Dedicate uninterrupted time to family', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'No complaining', 'Avoid complaining for the entire day', 'BOOLEAN', TRUE, FALSE, NOW(), NOW());

-- Financial
INSERT INTO habits (id, creator_id, name, description, tracking_type, is_public, is_private, created_at, updated_at)
VALUES
    (UUID_TO_BIN(UUID()), @admin_id, 'Track expenses', 'Record all spending for the day', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'No unnecessary purchases', 'Avoid impulse buying and stick to necessities', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Save money', 'Transfer money to savings account', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Review financial goals', 'Check progress toward financial objectives', 'BOOLEAN', TRUE, FALSE, NOW(), NOW());

-- Creative & Hobbies
INSERT INTO habits (id, creator_id, name, description, tracking_type, is_public, is_private, created_at, updated_at)
VALUES
    (UUID_TO_BIN(UUID()), @admin_id, 'Draw or sketch', 'Create a drawing or sketch', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Write creatively', 'Write fiction, poetry, or creative content', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Take a photo', 'Capture a meaningful or artistic photo', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Cook a healthy meal', 'Prepare a nutritious home-cooked meal', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Try something new', 'Experience something outside your comfort zone', 'BOOLEAN', TRUE, FALSE, NOW(), NOW()),
    (UUID_TO_BIN(UUID()), @admin_id, 'Work on a side project', 'Dedicate time to a personal project', 'BOOLEAN', TRUE, FALSE, NOW(), NOW());
