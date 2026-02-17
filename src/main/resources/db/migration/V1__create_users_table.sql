-- V1: Create users table for Spring Security authentication
--
-- This table stores authentication credentials for login.
-- Each user in this table will have a corresponding habit_practitioner record.

CREATE TABLE users (
    id                  BINARY(16)      PRIMARY KEY COMMENT 'UUID as binary',
    email               VARCHAR(255)    NOT NULL UNIQUE COMMENT 'User email (login username)',
    password_hash       VARCHAR(255)    NOT NULL COMMENT 'BCrypt hashed password',
    enabled             BOOLEAN         NOT NULL DEFAULT TRUE COMMENT 'Account enabled/disabled',
    account_locked      BOOLEAN         NOT NULL DEFAULT FALSE COMMENT 'Account locked status',
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_users_email (email),
    INDEX idx_users_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='User authentication table for Spring Security';
