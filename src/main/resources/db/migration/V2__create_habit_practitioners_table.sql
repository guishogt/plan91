-- V2: Create habit_practitioners table (Domain users)
--
-- This table represents the HabitPractitioner aggregate root.
-- Each practitioner is linked to a users table entry for authentication.

CREATE TABLE habit_practitioners (
    id                  BINARY(16)      PRIMARY KEY COMMENT 'UUID as binary (HabitPractitionerId)',
    user_id             BINARY(16)      NOT NULL UNIQUE COMMENT 'FK to users table',

    -- Personal information
    first_name          VARCHAR(100)    NOT NULL COMMENT 'First name (1-100 characters)',
    last_name           VARCHAR(100)    NOT NULL COMMENT 'Last name (1-100 characters)',
    email               VARCHAR(255)    NOT NULL UNIQUE COMMENT 'Email address (must match users.email)',

    -- Authentication tracking
    auth0_id            VARCHAR(255)    NULL COMMENT 'Auth0 provider ID (null for local auth)',
    last_login          TIMESTAMP       NULL COMMENT 'Last successful login timestamp',
    last_login_ip       VARCHAR(45)     NULL COMMENT 'Last login IP address (IPv4 or IPv6)',

    -- Timezone
    original_timezone   VARCHAR(50)     NOT NULL COMMENT 'User timezone (e.g., America/Los_Angeles)',

    -- Audit timestamps
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_habit_practitioners_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,

    -- Indexes
    INDEX idx_habit_practitioners_email (email),
    INDEX idx_habit_practitioners_user_id (user_id),
    INDEX idx_habit_practitioners_auth0_id (auth0_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Habit Practitioner aggregate root - domain users who practice habits';
