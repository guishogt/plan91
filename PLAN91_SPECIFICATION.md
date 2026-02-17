# Plan 91 - Habit Tracking Application
## Complete Specification & Agent-Driven Development Plan

**Version:** 1.0  
**Last Updated:** January 29, 2026  
**Author:** Luis Martinez (CTOBlue)

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [Product Vision](#product-vision)
3. [Functional Requirements](#functional-requirements)
4. [Non-Functional Requirements](#non-functional-requirements)
5. [Domain Model (DDD)](#domain-model-ddd)
6. [Technical Architecture](#technical-architecture)
7. [Database Schema](#database-schema)
8. [API Design](#api-design)
9. [UI/UX Design](#uiux-design)
10. [Agent-Driven Development Structure](#agent-driven-development-structure)
11. [Epic Breakdown](#epic-breakdown)
12. [Ticket Template](#ticket-template)
13. [Example Ticket](#example-ticket)
14. [Development Workflow](#development-workflow)

---

## Executive Summary

Plan 91 is a minimalist habit tracking application built on the principle of sustainable behavior change through the "one-strike" rule and 91-day commitment cycles. The application emphasizes simplicity, accountability, and data-driven progress visualization.

**Core Value Proposition:**
- Track habits with a forgiving but strict accountability system (one-strike rule)
- 91-day commitment cycles (based on "Plan 91" book concept)
- Clean, minimalist interface focused on daily completion
- Data-driven insights and progress visualization

**Target Platform:** Progressive Web App (PWA) with potential native iOS conversion

---

## Product Vision

### Why 91 Days?

The 91-day cycle is based on the "Plan 91" book methodology. This represents roughly 13 weeks of sustained habit formation, longer than the commonly cited 21 or 66 days, providing a more robust foundation for lasting behavioral change.

### The One-Strike Rule

**Philosophy:** Humans are imperfect. One mistake shouldn't destroy progress, but consistent failure should reset the journey.

**Mechanics:**
- Users can miss a habit once without breaking their streak
- Missing a second time before completing another entry breaks the streak
- Visual indicators warn users when they're on their "strike"
- Completion after using a strike clears the strike status

### Post-91 Day Journey

Upon completing 91 days, users choose:
1. **Continue:** Keep tracking indefinitely with the same streak
2. **Restart:** Mark the habit as complete and start a fresh 91-day cycle

---

## Functional Requirements

### 1. User Management

#### Authentication
- **Login Methods:**
  - Email/password registration and login
  - OAuth via Auth0 (Google, Apple)
- **Security:**
  - JWT token-based authentication
  - Secure password reset flow
  - Session management
- **User Profile:**
  - Email (unique identifier)
  - Timezone (critical for habit tracking)
  - Basic settings

#### No Role-Based Access
- All users have equal permissions
- No admin/moderator roles in MVP
- Pure personal tracking (no shared access)

### 2. Habit Management

#### Habit Creation
Users can create habits with:
- **Name:** Short descriptive title (max 100 chars)
- **Description:** Optional longer explanation (max 500 chars)
- **Category:** Single user-defined category assignment
- **Tracking Type:** 
  - **Boolean:** Simple complete/incomplete
  - **Numeric:** Track a number (pages read, minutes exercised, etc.)
- **Recurrence Rules:**
  - Daily (all days)
  - Weekdays only (Monday-Friday)
  - Weekends only (Saturday-Sunday)
  - Specific days (select any combination of days)
  - Nth day of month (e.g., first Monday of each month)

#### Numeric Tracking Configuration
For numeric habits:
- **Unit Name:** What's being counted (pages, minutes, reps)
- **Min Value:** Optional minimum threshold
- **Max Value:** Optional maximum threshold
- **Target Value:** Optional daily goal

#### Habit Editing
- Modify habit details anytime
- Cannot change start date (preserves streak integrity)
- Changing recurrence rules recalculates streak appropriately

#### Habit Deletion
- Soft delete with confirmation
- Option to export data before deletion

### 3. Daily Tracking

#### Completion Interface
**Dashboard (Today View):**
- List of today's active habits based on recurrence rules
- Large, touch-friendly completion button/area
- Quick completion with single tap/click
- Optional note entry after completion
- Optional numeric value entry (for numeric habits)

**Completion Recording:**
- Records date and timestamp in user's timezone
- One completion per calendar day
- Cannot backdate completions (prevents cheating)
- Instant visual feedback on completion

#### Strike System
**Visual Indicators:**
- **Normal State:** Default appearance
- **Strike Active:** Warning color/border indicating one miss
- **Streak Broken:** Clear indication that streak reset

**Business Rules:**
1. Missing a scheduled day activates strike
2. Strike persists until next completion
3. Completion after strike clears strike status
4. Missing a second day while strike is active breaks streak
5. Streak count resets to 0 on break

### 4. Categories/Tags

**User-Defined Categories:**
- Create custom categories (Health, Finance, Learning, etc.)
- Assign color to each category
- Optional emoji/icon
- Each habit assigned to one category (not multiple)

**Category Management:**
- Create, edit, delete categories
- Reassign habits when category deleted
- Filter dashboard by category

### 5. Analytics & Stats

#### Habit-Level Statistics
- **Current Streak:** Days in current streak
- **Longest Streak:** Best streak ever achieved
- **Total Completions:** All-time completion count
- **Completion Rate:** Percentage of expected days completed
- **Days to 91:** Progress indicator (X/91)

#### Visualizations
**For Numeric Habits:**
- Line chart showing value over time
- Trend analysis
- Average, min, max values

**For All Habits:**
- Calendar heatmap (GitHub-style contribution graph)
- Weekly pattern analysis (which days most/least consistent)
- Monthly overview

**Category Analytics:**
- Breakdown by category
- Compare completion rates across categories
- Category-specific insights

### 6. Milestone System

**Weekly Kudos:** Days 7, 14, 21, 28, 35, 42, 49, 56, 63, 70, 77, 84
**Monthly Kudos:** Days 30, 60
**Final Milestone:** Day 91

**Milestone Features:**
- Achievement notifications
- Celebratory UI elements
- Shareable achievement cards (optional)
- Milestone history view

### 7. No Template/Social Features in MVP

**Explicitly NOT included:**
- Public habit templates
- Browsing others' habits
- Social sharing
- Leaderboards
- Community features

**Rationale:** Focus on personal accountability and simplicity for MVP. Social features can be post-MVP if validated.

---

## Non-Functional Requirements

### Technology Stack

#### Backend
- **Language:** Java 21 (LTS)
- **Framework:** Spring Boot 3.2+
- **Architecture:** Domain-Driven Design (DDD) with Hexagonal Architecture
- **Security:** Spring Security + Auth0
- **Database:** MySQL 8.0
- **Migrations:** Flyway
- **Mapping:** MapStruct
- **Testing:** JUnit 5, Testcontainers

#### Frontend
- **Approach:** Server-side rendering with progressive enhancement
- **Interactivity:** HTMX
- **Styling:** Tailwind CSS
- **JavaScript:** Vanilla JS (minimal, only when necessary)
- **Optional:** Alpine.js for tiny reactive components
- **Charts:** Chart.js for analytics

#### Infrastructure
- **Containerization:** Docker & Docker Compose
- **Hosting:** Railway
- **Database Hosting:** MySQL on Railway
- **Authentication:** Auth0 (managed service)
- **CI/CD:** GitHub Actions

### Design Principles

#### Minimalist UI
- Clean typography (Inter or SF Pro Display)
- Generous whitespace
- Limited color palette (primary + grayscale + semantic colors)
- No unnecessary embellishments
- Touch-friendly targets (minimum 44x44px)

#### Performance
- Page load < 2 seconds
- Interaction feedback < 100ms
- Optimized database queries
- Lazy loading for analytics data
- Efficient HTMX partial updates

#### Accessibility
- WCAG 2.1 AA compliance
- Keyboard navigation
- Screen reader support
- Proper ARIA labels
- Sufficient color contrast

#### Mobile-First
- Responsive design (mobile, tablet, desktop)
- Touch-optimized interactions
- PWA for installability
- Offline support (basic)

### Progressive Web App (PWA)

**Requirements:**
- Install prompt on supported browsers
- App manifest with icons
- Service worker for offline support
- Cached assets for offline viewing
- Background sync for pending completions (nice-to-have)

**iOS Conversion Path:**
- API-first design enables future native app
- PWA provides immediate mobile experience
- Native iOS app can be developed later if needed

---

## Domain Model (DDD)

### Bounded Contexts

```
┌─────────────────────────────────────────────────────────┐
│                     Plan 91 System                       │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Identity   │  │    Habits    │  │  Analytics   │  │
│  │   Context    │  │   Context    │  │   Context    │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
│         │                  │                  │          │
│         └──────────────────┴──────────────────┘          │
│                            │                              │
│                  ┌──────────────────┐                    │
│                  │  Notifications   │                    │
│                  │    Context       │                    │
│                  └──────────────────┘                    │
└─────────────────────────────────────────────────────────┘
```

### Aggregates & Entities

#### 1. User Aggregate (Identity Context)

**User (Aggregate Root)**
```java
public class User {
    private UserId id;              // Value Object
    private String auth0Id;         // External identity
    private Email email;            // Value Object
    private ZoneId timezone;        // Critical for date calculations
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
```

**Value Objects:**
- `UserId`: UUID wrapper with validation
- `Email`: Email with format validation

#### 2. Category Aggregate (Habits Context)

**Category (Aggregate Root)**
```java
public class Category {
    private CategoryId id;          // Value Object
    private UserId userId;          // Reference to owner
    private String name;            // Max 100 chars
    private String color;           // Hex color code
    private String icon;            // Optional emoji
    private Timestamp createdAt;
}
```

**Business Rules:**
- Category names must be unique per user
- Cannot delete category with associated habits (or reassign first)

#### 3. Habit Aggregate (Habits Context - CORE)

**Habit (Aggregate Root)**
```java
public class Habit {
    // Identity
    private HabitId id;
    private UserId userId;
    private CategoryId categoryId;  // Single category
    
    // Description
    private String name;
    private String description;
    
    // Tracking Configuration
    private TrackingType trackingType;  // BOOLEAN | NUMERIC
    private NumericConfig numericConfig;  // Value Object (optional)
    private RecurrenceRule recurrenceRule;  // Value Object
    
    // Streak State
    private HabitStreak streak;  // Value Object
    
    // Lifecycle
    private LocalDate startDate;
    private boolean isCompleted;  // After 91 days
    private Timestamp completedAt;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Aggregate children
    private List<HabitEntry> entries;
    
    // Domain Methods
    public void complete(LocalDate date, String note, Integer value);
    public void edit(String name, String description, CategoryId categoryId);
    public void delete();
    public HabitStreak recalculateStreak(ZoneId timezone);
}
```

**HabitEntry (Entity within Habit Aggregate)**
```java
public class HabitEntry {
    private HabitEntryId id;
    private HabitId habitId;        // Parent reference
    private UserId userId;
    private Timestamp completedAt;  // Full timestamp
    private LocalDate completionDate;  // Normalized for querying
    private Integer numericValue;   // Optional
    private String note;            // Optional, max 500 chars
    private Timestamp createdAt;
}
```

**Value Objects:**

```java
// Numeric tracking configuration
public class NumericConfig {
    private String unitName;        // "pages", "minutes", etc.
    private Integer minValue;       // Optional
    private Integer maxValue;       // Optional
    private Integer targetValue;    // Optional daily goal
}

// Recurrence rules
public class RecurrenceRule {
    private RecurrenceType type;    // DAILY, WEEKDAYS, etc.
    private Set<DayOfWeek> daysOfWeek;  // For SPECIFIC_DAYS
    private Integer dayOfMonth;     // For NTH_DAY_OF_MONTH
    private Integer weekOfMonth;    // 1-5
}

public enum RecurrenceType {
    DAILY,
    WEEKDAYS,
    WEEKENDS,
    SPECIFIC_DAYS,
    NTH_DAY_OF_MONTH
}

// Streak tracking
public class HabitStreak {
    private int currentStreak;
    private int longestStreak;
    private int totalCompletions;
    private boolean hasUsedStrike;
    private LocalDate strikeDate;   // When strike was used
    private LocalDate startDate;
    private LocalDate expectedEndDate;  // Start + 91 days
    private LocalDate lastCompletionDate;
}
```

### Domain Services

#### StreakCalculationService
```java
public interface StreakCalculationService {
    HabitStreak calculateStreak(
        Habit habit, 
        List<HabitEntry> entries, 
        ZoneId userTimezone
    );
    
    boolean isStreakBroken(
        LocalDate lastCompletion,
        boolean hasUsedStrike,
        LocalDate strikeDate,
        LocalDate today,
        RecurrenceRule rule
    );
}
```

**Responsibilities:**
- Calculate current streak based on recurrence rules
- Detect strike usage
- Detect streak breaks
- Calculate longest streak
- Count total completions

#### RecurrenceEvaluationService
```java
public interface RecurrenceEvaluationService {
    List<LocalDate> getExpectedDaysBetween(
        LocalDate start,
        LocalDate end,
        RecurrenceRule rule
    );
    
    boolean isExpectedDay(LocalDate date, RecurrenceRule rule);
}
```

**Responsibilities:**
- Evaluate which days a habit is expected
- Handle complex recurrence patterns
- Support timezone-aware calculations

### Domain Events

```java
public interface DomainEvent {
    UUID eventId();
    Timestamp occurredAt();
}

public class HabitCreated implements DomainEvent { ... }
public class HabitCompleted implements DomainEvent { ... }
public class StrikeUsed implements DomainEvent { ... }
public class StrikeCleared implements DomainEvent { ... }
public class StreakBroken implements DomainEvent { ... }
public class MilestoneAchieved implements DomainEvent { ... }
public class HabitCompleted91Days implements DomainEvent { ... }
```

---

## Technical Architecture

### Hexagonal Architecture (Ports & Adapters)

```
┌─────────────────────────────────────────────────────────┐
│                    Interfaces Layer                      │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐        │
│  │ REST API   │  │  Web UI    │  │ CLI (Test) │        │
│  │ (HTMX)     │  │ (Thymeleaf)│  │            │        │
│  └────────────┘  └────────────┘  └────────────┘        │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│                  Application Layer                       │
│  ┌────────────────────────────────────────────┐         │
│  │  Use Cases (Application Services)          │         │
│  │  - CreateHabitUseCase                      │         │
│  │  - CompleteHabitUseCase                    │         │
│  │  - CalculateAnalyticsUseCase               │         │
│  └────────────────────────────────────────────┘         │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│                    Domain Layer                          │
│  ┌─────────────┐  ┌──────────────┐  ┌────────────┐     │
│  │ Aggregates  │  │   Services   │  │   Events   │     │
│  │ - Habit     │  │ - Streak     │  │ - Created  │     │
│  │ - User      │  │ - Recurrence │  │ - Broken   │     │
│  │ - Category  │  │              │  │            │     │
│  └─────────────┘  └──────────────┘  └────────────┘     │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│                Infrastructure Layer                      │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐        │
│  │ JPA Repos  │  │   Auth0    │  │  Railway   │        │
│  │  (MySQL)   │  │            │  │  Config    │        │
│  └────────────┘  └────────────┘  └────────────┘        │
└─────────────────────────────────────────────────────────┘
```

### Project Structure

```
plan91/
├── src/
│   ├── main/
│   │   ├── java/com/ctoblue/plan91/
│   │   │   ├── domain/                    # Pure business logic
│   │   │   │   ├── habit/
│   │   │   │   │   ├── Habit.java
│   │   │   │   │   ├── HabitEntry.java
│   │   │   │   │   ├── HabitId.java
│   │   │   │   │   ├── HabitStreak.java
│   │   │   │   │   ├── RecurrenceRule.java
│   │   │   │   │   ├── NumericConfig.java
│   │   │   │   │   ├── HabitRepository.java  # Interface
│   │   │   │   │   ├── StreakCalculationService.java
│   │   │   │   │   └── RecurrenceEvaluationService.java
│   │   │   │   ├── user/
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── UserId.java
│   │   │   │   │   ├── Email.java
│   │   │   │   │   └── UserRepository.java
│   │   │   │   ├── category/
│   │   │   │   │   ├── Category.java
│   │   │   │   │   ├── CategoryId.java
│   │   │   │   │   └── CategoryRepository.java
│   │   │   │   └── shared/               # Shared kernel
│   │   │   │       ├── DomainEvent.java
│   │   │   │       └── ValueObject.java
│   │   │   │
│   │   │   ├── application/               # Use cases
│   │   │   │   ├── habit/
│   │   │   │   │   ├── CreateHabitUseCase.java
│   │   │   │   │   ├── EditHabitUseCase.java
│   │   │   │   │   ├── DeleteHabitUseCase.java
│   │   │   │   │   ├── CompleteHabitUseCase.java
│   │   │   │   │   ├── AddHabitEntryUseCase.java
│   │   │   │   │   └── GetHabitDetailsUseCase.java
│   │   │   │   ├── analytics/
│   │   │   │   │   ├── CalculateHabitStatsUseCase.java
│   │   │   │   │   ├── GenerateChartDataUseCase.java
│   │   │   │   │   └── GetDashboardSummaryUseCase.java
│   │   │   │   ├── category/
│   │   │   │   │   ├── CreateCategoryUseCase.java
│   │   │   │   │   ├── UpdateCategoryUseCase.java
│   │   │   │   │   └── DeleteCategoryUseCase.java
│   │   │   │   └── user/
│   │   │   │       ├── RegisterUserUseCase.java
│   │   │   │       └── GetUserProfileUseCase.java
│   │   │   │
│   │   │   ├── infrastructure/            # External adapters
│   │   │   │   ├── persistence/
│   │   │   │   │   ├── jpa/
│   │   │   │   │   │   ├── JpaHabitRepository.java
│   │   │   │   │   │   ├── JpaUserRepository.java
│   │   │   │   │   │   ├── JpaCategoryRepository.java
│   │   │   │   │   │   └── entity/
│   │   │   │   │   │       ├── HabitEntity.java
│   │   │   │   │   │       ├── HabitEntryEntity.java
│   │   │   │   │   │       ├── UserEntity.java
│   │   │   │   │   │       └── CategoryEntity.java
│   │   │   │   │   └── mapper/
│   │   │   │   │       ├── HabitMapper.java
│   │   │   │   │       └── UserMapper.java
│   │   │   │   ├── auth0/
│   │   │   │   │   ├── Auth0Configuration.java
│   │   │   │   │   └── Auth0UserService.java
│   │   │   │   └── config/
│   │   │   │       ├── SecurityConfig.java
│   │   │   │       ├── DatabaseConfig.java
│   │   │   │       └── AppConfig.java
│   │   │   │
│   │   │   └── interfaces/                # Entry points
│   │   │       ├── rest/
│   │   │       │   ├── HabitController.java
│   │   │       │   ├── CategoryController.java
│   │   │       │   ├── AnalyticsController.java
│   │   │       │   ├── UserController.java
│   │   │       │   ├── dto/
│   │   │       │   │   ├── CreateHabitRequest.java
│   │   │       │   │   ├── HabitResponse.java
│   │   │       │   │   └── ...
│   │   │       │   └── mapper/
│   │   │       │       └── HabitDtoMapper.java
│   │   │       └── web/
│   │   │           ├── DashboardController.java
│   │   │           ├── StatsController.java
│   │   │           └── AuthController.java
│   │   │
│   │   └── resources/
│   │       ├── templates/                 # Thymeleaf + HTMX
│   │       │   ├── layout/
│   │       │   │   ├── base.html
│   │       │   │   └── components/
│   │       │   ├── dashboard/
│   │       │   │   └── today.html
│   │       │   ├── stats/
│   │       │   │   └── overview.html
│   │       │   ├── habit/
│   │       │   │   ├── create.html
│   │       │   │   ├── edit.html
│   │       │   │   └── details.html
│   │       │   └── auth/
│   │       │       ├── login.html
│   │       │       └── register.html
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   └── tailwind.css
│   │       │   ├── js/
│   │       │   │   ├── app.js
│   │       │   │   └── charts.js
│   │       │   └── images/
│   │       │       └── icons/
│   │       ├── db/migration/              # Flyway
│   │       │   ├── V1__create_users_table.sql
│   │       │   ├── V2__create_categories_table.sql
│   │       │   ├── V3__create_habits_table.sql
│   │       │   └── V4__create_habit_entries_table.sql
│   │       └── application.yml
│   │
│   └── test/
│       ├── java/com/ctoblue/plan91/
│       │   ├── domain/
│       │   │   └── habit/
│       │   │       ├── HabitTest.java
│       │   │       └── StreakCalculationServiceTest.java
│       │   ├── application/
│       │   │   └── habit/
│       │   │       └── CompleteHabitUseCaseTest.java
│       │   └── infrastructure/
│       │       └── persistence/
│       │           └── JpaHabitRepositoryTest.java
│       └── resources/
│           └── application-test.yml
│
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
│
├── docs/                                  # Agent context files
│   ├── SPECIFICATION.md                   # This file
│   ├── DOMAIN-MODEL.md
│   ├── API-DESIGN.md
│   ├── ARCHITECTURE.md
│   └── DECISIONS.md
│
├── epics/                                 # Work breakdown
│   ├── 01-domain-modeling/
│   ├── 02-infrastructure-setup/
│   ├── 03-authentication/
│   └── ...
│
├── scripts/
│   ├── generate-tickets.sh
│   └── validate-context.sh
│
├── pom.xml
├── README.md
└── .gitignore
```

---

## Database Schema

```sql
-- ============================================================
-- Users (Auth0 managed, light reference table)
-- ============================================================
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    auth0_id VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    timezone VARCHAR(50) NOT NULL DEFAULT 'UTC',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_auth0_id (auth0_id),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Categories (User-defined)
-- ============================================================
CREATE TABLE categories (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(7) NOT NULL,  -- Hex color
    icon VARCHAR(50),           -- Emoji or icon identifier
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_category (user_id, name),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Habits (Aggregate Root)
-- ============================================================
CREATE TABLE habits (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    category_id VARCHAR(36),    -- Nullable, single category
    
    -- Description
    name VARCHAR(100) NOT NULL,
    description TEXT,
    
    -- Tracking Configuration
    tracking_type ENUM('BOOLEAN', 'NUMERIC') DEFAULT 'BOOLEAN' NOT NULL,
    numeric_config JSON,        -- {unit, min, max, target}
    
    -- Recurrence Rules
    recurrence_rule JSON NOT NULL,  -- {type, daysOfWeek, dayOfMonth, weekOfMonth}
    
    -- Streak State
    current_streak INT DEFAULT 0 NOT NULL,
    longest_streak INT DEFAULT 0 NOT NULL,
    total_completions INT DEFAULT 0 NOT NULL,
    has_used_strike BOOLEAN DEFAULT FALSE NOT NULL,
    strike_date DATE,
    last_completion_date DATE,
    
    -- Lifecycle
    start_date DATE NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE NOT NULL,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_category_id (category_id),
    INDEX idx_start_date (start_date),
    INDEX idx_is_completed (is_completed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Habit Entries (Entity within Habit Aggregate)
-- ============================================================
CREATE TABLE habit_entries (
    id VARCHAR(36) PRIMARY KEY,
    habit_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    
    -- Completion Details
    completed_at TIMESTAMP NOT NULL,
    completion_date DATE NOT NULL,  -- Normalized for querying
    numeric_value INT,              -- Optional, for numeric habits
    note TEXT,                      -- Optional
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (habit_id) REFERENCES habits(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Prevent duplicate completions on same day
    UNIQUE KEY unique_habit_date (habit_id, completion_date),
    
    INDEX idx_habit_id (habit_id),
    INDEX idx_user_id (user_id),
    INDEX idx_completion_date (completion_date),
    INDEX idx_completed_at (completed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Milestones (Achievement tracking)
-- ============================================================
CREATE TABLE milestones (
    id VARCHAR(36) PRIMARY KEY,
    habit_id VARCHAR(36) NOT NULL,
    
    day_number INT NOT NULL,    -- 7, 14, 21, 30, etc.
    title VARCHAR(100) NOT NULL,
    message TEXT,
    
    achieved BOOLEAN DEFAULT FALSE NOT NULL,
    achieved_at TIMESTAMP,
    
    FOREIGN KEY (habit_id) REFERENCES habits(id) ON DELETE CASCADE,
    UNIQUE KEY unique_habit_milestone (habit_id, day_number),
    INDEX idx_habit_id (habit_id),
    INDEX idx_achieved (achieved)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## API Design

### RESTful Endpoints

#### Authentication
```
POST   /api/auth/register          # Register with email/password
POST   /api/auth/login             # Login with email/password
POST   /api/auth/logout            # Logout current session
POST   /api/auth/refresh           # Refresh JWT token
GET    /api/auth/me                # Get current user
PUT    /api/auth/profile           # Update profile (timezone, etc.)
```

#### Categories
```
GET    /api/categories             # List user's categories
POST   /api/categories             # Create new category
GET    /api/categories/{id}        # Get category details
PUT    /api/categories/{id}        # Update category
DELETE /api/categories/{id}        # Delete category
```

#### Habits
```
GET    /api/habits                 # List user's habits
                                    # Query params: ?active=true&category={id}
POST   /api/habits                 # Create new habit
GET    /api/habits/{id}            # Get habit details
PUT    /api/habits/{id}            # Update habit
DELETE /api/habits/{id}            # Delete habit

POST   /api/habits/{id}/complete   # Mark as complete today (boolean)
POST   /api/habits/{id}/entries    # Add entry with note/value
GET    /api/habits/{id}/entries    # Get habit history
                                    # Query params: ?from={date}&to={date}
DELETE /api/habits/{id}/entries/{entryId}  # Delete entry

GET    /api/habits/{id}/streak     # Get streak information
POST   /api/habits/{id}/complete91 # Mark 91-day completion
```

#### Analytics
```
GET    /api/habits/{id}/stats      # Habit statistics
GET    /api/habits/{id}/chart      # Chart data for numeric habits
                                    # Query params: ?from={date}&to={date}
GET    /api/dashboard/summary      # Dashboard overview
GET    /api/analytics/categories   # Category breakdown
```

### Request/Response Examples

#### Create Habit
```json
POST /api/habits
{
  "name": "Read 10 pages daily",
  "description": "Build a consistent reading habit",
  "categoryId": "cat-uuid-123",
  "trackingType": "NUMERIC",
  "numericConfig": {
    "unitName": "pages",
    "minValue": 1,
    "targetValue": 10
  },
  "recurrenceRule": {
    "type": "DAILY"
  }
}

Response: 201 Created
{
  "id": "habit-uuid-456",
  "name": "Read 10 pages daily",
  "description": "Build a consistent reading habit",
  "categoryId": "cat-uuid-123",
  "trackingType": "NUMERIC",
  "numericConfig": {
    "unitName": "pages",
    "minValue": 1,
    "targetValue": 10
  },
  "recurrenceRule": {
    "type": "DAILY"
  },
  "streak": {
    "currentStreak": 0,
    "longestStreak": 0,
    "totalCompletions": 0,
    "hasUsedStrike": false,
    "daysTo91": 91
  },
  "startDate": "2026-01-29",
  "isCompleted": false,
  "createdAt": "2026-01-29T10:30:00Z"
}
```

#### Complete Habit
```json
POST /api/habits/{id}/complete
{
  "note": "Finished chapter 3",
  "numericValue": 12
}

Response: 200 OK
{
  "habitId": "habit-uuid-456",
  "entryId": "entry-uuid-789",
  "completionDate": "2026-01-29",
  "completedAt": "2026-01-29T22:15:00Z",
  "note": "Finished chapter 3",
  "numericValue": 12,
  "streak": {
    "currentStreak": 1,
    "longestStreak": 1,
    "totalCompletions": 1,
    "hasUsedStrike": false,
    "daysTo91": 90
  },
  "milestoneAchieved": null
}
```

#### Get Habit Stats
```json
GET /api/habits/{id}/stats

Response: 200 OK
{
  "habitId": "habit-uuid-456",
  "habitName": "Read 10 pages daily",
  "currentStreak": 23,
  "longestStreak": 23,
  "totalCompletions": 23,
  "completionRate": 100.0,
  "daysTo91": 68,
  "hasUsedStrike": false,
  "averageNumericValue": 11.5,
  "weeklyPattern": {
    "MONDAY": 4,
    "TUESDAY": 3,
    "WEDNESDAY": 3,
    "THURSDAY": 3,
    "FRIDAY": 4,
    "SATURDAY": 3,
    "SUNDAY": 3
  },
  "recentEntries": [
    {
      "date": "2026-01-29",
      "numericValue": 12,
      "note": "Finished chapter 3"
    }
    // ... more entries
  ]
}
```

---

## UI/UX Design

### Design System

#### Color Palette
```css
/* Primary */
--primary-50: #f0f9ff;
--primary-500: #3b82f6;   /* Main brand color */
--primary-700: #1d4ed8;

/* Grayscale */
--gray-50: #f9fafb;
--gray-100: #f3f4f6;
--gray-500: #6b7280;
--gray-900: #111827;

/* Semantic */
--success: #10b981;       /* Green for completion */
--warning: #f59e0b;       /* Amber for strike warning */
--error: #ef4444;         /* Red for broken streak */
```

#### Typography
```css
/* Font Stack */
font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;

/* Scale */
--text-xs: 0.75rem;     /* 12px */
--text-sm: 0.875rem;    /* 14px */
--text-base: 1rem;      /* 16px */
--text-lg: 1.125rem;    /* 18px */
--text-xl: 1.25rem;     /* 20px */
--text-2xl: 1.5rem;     /* 24px */
--text-3xl: 1.875rem;   /* 30px */
```

#### Spacing
```css
/* 8px base scale */
--space-1: 0.25rem;  /* 4px */
--space-2: 0.5rem;   /* 8px */
--space-3: 0.75rem;  /* 12px */
--space-4: 1rem;     /* 16px */
--space-6: 1.5rem;   /* 24px */
--space-8: 2rem;     /* 32px */
```

### Key Screens

#### 1. Dashboard (Today View)

**Layout:**
```
┌─────────────────────────────────────────────┐
│  Plan 91                        [Profile ▼] │  <- Header
├─────────────────────────────────────────────┤
│  Today • January 29              🔥 23 days │  <- Date & Streak Summary
├─────────────────────────────────────────────┤
│                                              │
│  ⚠️ Reading habit needs attention!          │  <- Strike Warning (conditional)
│  You've used your one-time skip             │
│                                              │
├─────────────────────────────────────────────┤
│  [Health ▼]  [All]                          │  <- Category Filter
├─────────────────────────────────────────────┤
│                                              │
│  ┌─────────────────────────────────────┐   │
│  │ 📚 Read 10 pages                    │   │  <- Habit Card
│  │ Health • 23-day streak              │   │
│  │                                      │   │
│  │ [✓ Complete]         [Add Note]     │   │
│  │                                      │   │
│  │ ━━━━━━━━━━━━━━━━━━━━━━ 23/91       │   │
│  └─────────────────────────────────────┘   │
│                                              │
│  ┌─────────────────────────────────────┐   │
│  │ ⚠️ Daily supplements                │   │  <- Strike State
│  │ Health • 15-day streak (1 skip used)│   │
│  │                                      │   │
│  │ [✓ Complete Today!]                 │   │
│  │                                      │   │
│  │ ━━━━━━━━━━━━━━━━━━━━━━ 15/91       │   │
│  └─────────────────────────────────────┘   │
│                                              │
│  ┌─────────────────────────────────────┐   │
│  │ ✅ Morning meditation               │   │  <- Completed
│  │ Mindfulness • 8-day streak          │   │
│  │                                      │   │
│  │ ✓ Completed at 6:30 AM              │   │
│  │                                      │   │
│  │ ━━━━━━━━━━━━━━━━━━━━━━━━ 8/91      │   │
│  └─────────────────────────────────────┘   │
│                                              │
├─────────────────────────────────────────────┤
│ [Dashboard] [Stats] [Habits] [Profile]      │  <- Bottom Nav
└─────────────────────────────────────────────┘
```

**Interactions (HTMX):**
- Complete button → POST to API → Update card with checkmark
- Add Note → Modal with textarea → POST with note
- Category filter → Update displayed habits (no page reload)

#### 2. Stats Dashboard

**Layout:**
```
┌─────────────────────────────────────────────┐
│  ← Back                         Statistics  │
├─────────────────────────────────────────────┤
│                                              │
│  [Read 10 pages ▼]         [Last 30 days ▼] │  <- Habit & Date Range
│                                              │
├─────────────────────────────────────────────┤
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ Current  │  │ Longest  │  │  Total   │  │  <- Key Metrics
│  │  Streak  │  │  Streak  │  │Completed │  │
│  │    23    │  │    23    │  │    23    │  │
│  └──────────┘  └──────────┘  └──────────┘  │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │      Pages Read Over Time            │   │  <- Line Chart
│  │  15 ┤            ╭─╮                 │   │
│  │  10 ┤      ╭─╮  │ │  ╭─╮            │   │
│  │   5 ┤   ╭─╮│ │╭─╯ │╭─╯ │            │   │
│  │   0 └───┴─┴┴─┴───┴┴───┴────────     │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │      Calendar Heatmap                │   │  <- GitHub-style
│  │  Mon  ░ ▓ ▓ ▓ ░                      │   │
│  │  Wed  ▓ ▓ ░ ▓ ▓                      │   │
│  │  Fri  ▓ ▓ ▓ ▓ ▓                      │   │
│  │  Sun  ▓ ░ ▓ ▓ ▓                      │   │
│  └──────────────────────────────────────┘   │
│                                              │
├─────────────────────────────────────────────┤
│ [Dashboard] [Stats] [Habits] [Profile]      │
└─────────────────────────────────────────────┘
```

#### 3. Create/Edit Habit Form

**Layout:**
```
┌─────────────────────────────────────────────┐
│  ← Back                      Create Habit   │
├─────────────────────────────────────────────┤
│                                              │
│  Habit Name *                                │
│  ┌─────────────────────────────────────┐   │
│  │ Read 10 pages daily                 │   │
│  └─────────────────────────────────────┘   │
│                                              │
│  Description                                 │
│  ┌─────────────────────────────────────┐   │
│  │ Build a consistent reading habit    │   │
│  │                                      │   │
│  └─────────────────────────────────────┘   │
│                                              │
│  Category *                                  │
│  [Health ▼]                                  │
│                                              │
│  Tracking Type *                             │
│  ( ) Simple (Done/Not Done)                  │
│  (•) Track a Number                          │
│                                              │
│  ┌─────────────────────────────────────┐   │
│  │ What are you tracking?              │   │
│  │ ┌─────────────────────┐             │   │
│  │ │ pages               │             │   │
│  │ └─────────────────────┘             │   │
│  │                                      │   │
│  │ Daily Target (optional)              │   │
│  │ ┌───┐                                │   │
│  │ │10 │ pages                          │   │
│  │ └───┘                                │   │
│  └─────────────────────────────────────┘   │
│                                              │
│  Frequency *                                 │
│  (•) Every day                               │
│  ( ) Weekdays only                           │
│  ( ) Specific days                           │
│  ( ) Custom schedule                         │
│                                              │
│  [ Create Habit ]                            │
│                                              │
└─────────────────────────────────────────────┘
```

### Component Library

#### Habit Card Component
```html
<div class="habit-card" 
     data-habit-id="{id}"
     data-state="{normal|strike|completed|broken}">
  
  <div class="habit-header">
    <span class="habit-icon">{emoji}</span>
    <h3 class="habit-name">{name}</h3>
    <span class="category-badge">{category}</span>
  </div>
  
  <div class="habit-stats">
    <span class="streak-indicator">🔥 {currentStreak}-day streak</span>
  </div>
  
  <!-- State-dependent UI -->
  <div class="habit-actions">
    <!-- Normal state -->
    <button class="btn-complete" 
            hx-post="/api/habits/{id}/complete"
            hx-swap="outerHTML"
            hx-target="closest .habit-card">
      ✓ Complete
    </button>
    
    <!-- Strike state -->
    <button class="btn-complete btn-warning">
      ✓ Complete Today! (Skip used)
    </button>
  </div>
  
  <div class="progress-bar">
    <div class="progress-fill" style="width: {percentage}%"></div>
    <span class="progress-text">{current}/91</span>
  </div>
</div>
```

---

## Agent-Driven Development Structure

### Philosophy

This project is designed to be built by AI agents (Claude Code, Cursor, etc.) working on discrete, self-contained units of work. Each "ticket" contains all the context an agent needs to implement a feature without human intervention.

### Hierarchy

```
Specification (Master Doc)
    ↓
Epics (High-level features)
    ↓
Tickets (Implementable units)
    ↓
Code (Generated by agents)
```

### Directory Structure

```
plan91/
├── docs/                           # Agent context
│   ├── SPECIFICATION.md            # This file (master spec)
│   ├── DOMAIN-MODEL.md             # DDD model reference
│   ├── API-DESIGN.md               # API contracts
│   ├── ARCHITECTURE.md             # System architecture
│   └── DECISIONS.md                # Architecture Decision Records
│
└── epics/                          # Work breakdown
    ├── 01-domain-modeling/
    │   ├── EPIC.md
    │   └── tickets/
    │       ├── 001-habit-aggregate.md
    │       ├── 002-streak-calculation-service.md
    │       └── ...
    ├── 02-infrastructure-setup/
    │   ├── EPIC.md
    │   └── tickets/
    ├── 03-authentication/
    └── ... (up to Epic 18)
```

### Context Sharing

**Critical Files Agents Must Read:**
1. `/docs/SPECIFICATION.md` - Master specification (this file)
2. `/docs/DOMAIN-MODEL.md` - Domain model details
3. `/epics/{epic-name}/EPIC.md` - Current epic context
4. `/epics/{epic-name}/tickets/{ticket-id}.md` - Current ticket

**Cross-Agent Communication:**
- All context in markdown files
- No external tools required (Linear, Jira, etc.)
- Git commits serve as progress tracking
- Completed tickets marked with ✅ in file

---

## Epic Breakdown

### Epic 01: Domain Modeling & Architecture
**Duration:** 2-3 days  
**Goal:** Define core domain model and architectural patterns

**Tickets:**
1. Define bounded contexts and context map
2. Model Habit aggregate with invariants
3. Model User and Category aggregates
4. Define StreakCalculationService
5. Define RecurrenceEvaluationService
6. Document domain events
7. Create value objects (HabitId, UserId, Email, etc.)

**Deliverable:** Complete domain model in Java interfaces/classes

---

### Epic 02: Infrastructure Setup
**Duration:** 1-2 days  
**Goal:** Scaffold project with all infrastructure

**Tickets:**
1. Spring Boot project initialization
2. Docker Compose setup (app + MySQL)
3. Database schema design (Flyway migrations)
4. Project structure per hexagonal architecture
5. Logging and error handling foundation
6. Application properties configuration

**Deliverable:** Runnable Spring Boot app with database

---

### Epic 03: Authentication & User Management
**Duration:** 2-3 days  
**Goal:** Auth0 integration and user registration

**Tickets:**
1. Auth0 tenant setup and configuration
2. Spring Security + Auth0 integration
3. JWT token handling and validation
4. User registration endpoint
5. User repository and persistence
6. Current user resolution from JWT
7. Timezone handling utilities

**Deliverable:** Working authentication with protected endpoints

---

### Epic 04: Category Management
**Duration:** 1 day  
**Goal:** User-defined categories

**Tickets:**
1. Category aggregate and repository
2. Create category use case
3. List categories use case
4. Update category use case
5. Delete category use case
6. Category REST endpoints
7. Category validation rules

**Deliverable:** Full CRUD for categories via API

---

### Epic 05: Habit Core Domain
**Duration:** 3-4 days  
**Goal:** Core habit business logic

**Tickets:**
1. Habit aggregate implementation
2. Habit repository (JPA)
3. Recurrence rule evaluation service
4. Streak calculation service
5. Create habit use case
6. Complete habit entry use case
7. Edit habit use case
8. Delete habit use case
9. Query habit details use case

**Deliverable:** All habit business logic working with tests

---

### Epic 06: Habit REST API
**Duration:** 2 days  
**Goal:** Expose habit functionality via REST

**Tickets:**
1. Habit controller with create endpoint
2. Complete habit endpoint
3. Add entry with note/numeric value endpoint
4. List user's habits endpoint
5. Get habit details endpoint
6. Update habit endpoint
7. Delete habit endpoint
8. Get habit entries (history) endpoint
9. DTOs and mappers
10. API error handling

**Deliverable:** Full REST API for habits

---

### Epic 07: Analytics & Stats Backend
**Duration:** 2-3 days  
**Goal:** Calculate and serve analytics data

**Tickets:**
1. Habit statistics calculation service
2. Category-based analytics aggregation
3. Chart data formatting (for Chart.js)
4. Calendar heatmap data generation
5. Analytics REST endpoints
6. Dashboard summary endpoint

**Deliverable:** Analytics API ready for frontend

---

### Epic 08: Milestone System
**Duration:** 1-2 days  
**Goal:** 91-day milestone tracking

**Tickets:**
1. Milestone domain model
2. Milestone repository
3. Milestone achievement detection service
4. Weekly milestone triggers
5. Monthly milestone triggers
6. Day 91 completion milestone
7. Milestone REST endpoints

**Deliverable:** Milestone system with API

---

### Epic 09: Frontend Foundation
**Duration:** 2 days  
**Goal:** Set up HTMX + Tailwind + base layouts

**Tickets:**
1. Tailwind CSS configuration
2. HTMX integration and configuration
3. Base HTML template (layout)
4. Navigation component
5. Form utilities
6. Loading states and skeletons
7. Toast/notification component
8. Modal component

**Deliverable:** Reusable frontend components

---

### Epic 10: Authentication UI
**Duration:** 1-2 days  
**Goal:** Login, registration, profile pages

**Tickets:**
1. Login page
2. Registration page
3. Password reset flow
4. Profile/settings page
5. Logout functionality
6. Protected route handling

**Deliverable:** Complete auth user flow

---

### Epic 11: Category Management UI
**Duration:** 1 day  
**Goal:** UI for managing categories

**Tickets:**
1. Category list view
2. Create category modal
3. Edit category modal
4. Delete category confirmation
5. Color picker component
6. Emoji/icon selector

**Deliverable:** Full category CRUD UI

---

### Epic 12: Habit Management UI
**Duration:** 2-3 days  
**Goal:** Create, edit, delete habits

**Tickets:**
1. Create habit form (multi-step)
2. Recurrence rule UI
3. Numeric tracking configuration UI
4. Category assignment in form
5. Edit habit form
6. Delete habit confirmation
7. Habit detail view page

**Deliverable:** Full habit management UI

---

### Epic 13: Dashboard (Today View)
**Duration:** 3 days  
**Goal:** Main user interface for daily tracking

**Tickets:**
1. Dashboard layout and structure
2. Habit card component
3. Complete button interaction (HTMX)
4. Add note modal
5. Add numeric value input
6. Strike warning banner
7. Streak display
8. Progress bar (X/91 days)
9. Category filtering
10. Empty states

**Deliverable:** Fully functional dashboard

---

### Epic 14: Analytics & Stats UI
**Duration:** 2-3 days  
**Goal:** Visualize habit progress

**Tickets:**
1. Stats dashboard layout
2. Habit selector dropdown
3. Key metrics cards
4. Chart.js integration
5. Line chart for numeric habits
6. Calendar heatmap component
7. Category breakdown visualization
8. Date range selector
9. Export data feature (bonus)

**Deliverable:** Complete analytics dashboard

---

### Epic 15: Milestone & Kudos UI
**Duration:** 1-2 days  
**Goal:** Celebrate achievements

**Tickets:**
1. Milestone notification system
2. Achievement modal/card
3. Milestone history view
4. 91-day completion flow
5. Continue vs. restart decision UI
6. Shareable achievement card (optional)

**Deliverable:** Engaging milestone experience

---

### Epic 16: PWA Features
**Duration:** 2 days  
**Goal:** Make it installable and work offline

**Tickets:**
1. manifest.json configuration
2. Service worker implementation
3. Offline page/fallback
4. Install prompt UI
5. Caching strategy for assets
6. Background sync for completion
7. App icon assets (all sizes)

**Deliverable:** Fully functional PWA

---

### Epic 17: Polish & UX Refinement
**Duration:** 2-3 days  
**Goal:** Improve user experience

**Tickets:**
1. Loading states across all interactions
2. Error handling and user feedback
3. Responsive design (mobile, tablet, desktop)
4. Accessibility (ARIA labels, keyboard nav)
5. Performance optimization
6. Animation and transitions
7. Dark mode (optional)

**Deliverable:** Polished user experience

---

### Epic 18: Deployment & DevOps
**Duration:** 2 days  
**Goal:** Deploy to Railway with CI/CD

**Tickets:**
1. Production Docker configuration
2. Railway project setup
3. MySQL on Railway
4. Environment variables management
5. Auth0 production configuration
6. GitHub Actions CI/CD pipeline
7. Database backup strategy
8. Monitoring and logging
9. Domain and SSL setup

**Deliverable:** Live production application

---

## Ticket Template

Every ticket follows this structure to ensure agents have complete context:

```markdown
# [TICKET-ID] - [Title]

**Epic**: [Epic Name]
**Priority**: High | Medium | Low
**Estimated Effort**: Small (1-2h) | Medium (3-5h) | Large (1-2d)
**Dependencies**: [List of ticket IDs that must be completed first]

## Context

[Brief explanation of WHY this ticket exists and how it fits into the larger system]

## Domain Knowledge Required

[Key domain concepts the agent needs to understand]
- Reference to DOMAIN-MODEL.md sections
- Specific business rules
- Examples of domain behavior

## Acceptance Criteria

- [ ] Criterion 1 (specific, testable)
- [ ] Criterion 2
- [ ] Criterion 3

## Technical Requirements

### Implementation Details
[Specific technical guidance]
- Which patterns to use (e.g., DDD aggregate, value object)
- Which libraries/frameworks
- Code structure expectations

### Files to Create/Modify
```
src/main/java/com/ctoblue/plan91/
  domain/habit/
    Habit.java          [CREATE]
    HabitRepository.java [CREATE]
```

### API Contract (if applicable)
```json
POST /api/habits
Request: { ... }
Response: { ... }
```

## Test Requirements

### Unit Tests
- Test case 1
- Test case 2

### Integration Tests (if applicable)
- Integration scenario 1

## Example Code/Pseudocode

[Provide examples or pseudocode to guide implementation]

```java
// Example structure
public class Habit {
    private HabitId id;
    private UserId userId;
    // ...
}
```

## Definition of Done

- [ ] Code implemented per acceptance criteria
- [ ] Unit tests written and passing
- [ ] Integration tests (if applicable) passing
- [ ] Code follows project conventions
- [ ] No compiler warnings
- [ ] Documentation updated (if public API)

## References

- DOMAIN-MODEL.md § Habit Aggregate
- ARCHITECTURE.md § Hexagonal Architecture
- [External reference if needed]

## Notes for Agent

[Any specific guidance, gotchas, or considerations]
```

---

## Example Ticket

Here's a complete example ticket from Epic 05:

```markdown
# PLAN91-050104 - Implement Streak Calculation Service

**Epic**: 05-Habit-Core-Domain
**Priority**: High
**Estimated Effort**: Medium (4-6h)
**Dependencies**: PLAN91-050101 (Habit Aggregate), PLAN91-050103 (Recurrence Rule Service)

## Context

The streak calculation is the heart of Plan 91's habit tracking. It must correctly:
1. Count only days where the habit is expected (per recurrence rules)
2. Track the "one-strike" rule (can miss once, but not twice)
3. Calculate current streak, longest streak, and total completions
4. Determine if a streak is broken

This service is a **Domain Service** because streak calculation logic spans multiple entities/value objects and embodies critical business rules.

## Domain Knowledge Required

### Core Business Rules:
1. **Streak Definition**: Consecutive days completing a habit on days it's expected
2. **One-Strike Rule**: 
   - First miss sets `hasUsedStrike = true` and records `strikeDate`
   - Streak continues
   - Completing an entry clears the strike
   - Second consecutive miss breaks the streak
3. **Expected Days**: Only count days where recurrence rule says habit should be done
4. **Timezone**: All date calculations use user's timezone

### Reference:
- See DOMAIN-MODEL.md § Streak Calculation
- See SPECIFICATION.md § Business Rules

## Acceptance Criteria

- [ ] Service correctly calculates current streak based on recurrence rules
- [ ] Service identifies when one-strike is used
- [ ] Service detects when streak is broken (two misses)
- [ ] Service clears strike when habit is completed after a miss
- [ ] Service handles edge cases (habit just created, timezone changes)
- [ ] Service calculates longest streak correctly
- [ ] Service counts total completions accurately
- [ ] All business rules are unit tested with examples

## Technical Requirements

### Implementation Details

Create a **Domain Service** in the domain layer:

```
src/main/java/com/ctoblue/plan91/domain/habit/
  StreakCalculationService.java
```

**Pattern**: Stateless service with pure functions
**Dependencies**: 
- RecurrenceEvaluationService (to determine expected days)
- Clock (for testability - inject via Spring)

### Method Signatures

```java
public class StreakCalculationService {
    
    private final RecurrenceEvaluationService recurrenceService;
    private final Clock clock;
    
    /**
     * Calculate streak status for a habit
     * @return Updated HabitStreak value object
     */
    public HabitStreak calculateStreak(
        Habit habit,
        List<HabitEntry> entries,
        ZoneId userTimezone
    );
    
    /**
     * Determine if adding an entry would clear a strike
     */
    public boolean wouldClearStrike(
        LocalDate entryDate,
        LocalDate strikeDate,
        RecurrenceRule rule
    );
    
    /**
     * Check if streak should be broken
     */
    public boolean isStreakBroken(
        LocalDate lastCompletion,
        boolean hasUsedStrike,
        LocalDate strikeDate,
        LocalDate today,
        RecurrenceRule rule
    );
}
```

### Algorithm Pseudocode

```
function calculateStreak(habit, entries, timezone):
    expectedDays = recurrenceService.getExpectedDays(
        habit.startDate, 
        today, 
        habit.recurrenceRule
    )
    
    completedDays = entries.map(e => e.completionDate).toSet()
    
    currentStreak = 0
    hasUsedStrike = false
    strikeDate = null
    lastMissedDate = null
    
    // Walk backwards from today
    for day in expectedDays.reverse():
        if day in completedDays:
            currentStreak++
            lastMissedDate = null  // Reset miss tracking
        else:
            if lastMissedDate is not null:
                // Two misses in a row - streak broken
                break
            else if not hasUsedStrike:
                // First miss - use strike
                hasUsedStrike = true
                strikeDate = day
                lastMissedDate = day
                currentStreak++  // Strike doesn't break streak
            else:
                // Already used strike, another miss breaks it
                break
    
    return new HabitStreak(
        currentStreak,
        calculateLongestStreak(entries, expectedDays),
        completedDays.size(),
        hasUsedStrike,
        strikeDate
    )
```

## Test Requirements

### Unit Tests

Create comprehensive tests covering:

```java
@Test
void shouldCalculateStreakForDailyHabit() {
    // Given: Daily habit, completed last 5 days
    // When: Calculate streak
    // Then: Current streak = 5
}

@Test
void shouldUseStrikeOnFirstMiss() {
    // Given: Daily habit, completed 5 days, missed yesterday
    // When: Calculate streak
    // Then: hasUsedStrike = true, currentStreak = 5 (continues)
}

@Test
void shouldBreakStreakOnSecondMiss() {
    // Given: Daily habit, completed 5 days, missed 2 days ago and yesterday
    // When: Calculate streak
    // Then: Streak broken, currentStreak = 0
}

@Test
void shouldClearStrikeWhenCompletedAfterMiss() {
    // Given: Strike used yesterday, completed today
    // When: Calculate streak
    // Then: hasUsedStrike = false, strikeDate = null
}

@Test
void shouldOnlyCountExpectedDays() {
    // Given: Weekday-only habit, weekend passed
    // When: Calculate streak
    // Then: Weekend doesn't count as miss
}

@Test
void shouldHandleTimezoneCorrectly() {
    // Given: User in PST, habit completed at 11pm PST
    // When: Calculate streak (server in UTC)
    // Then: Counts as correct day in user timezone
}

@Test
void shouldCalculateLongestStreakCorrectly() {
    // Given: Current streak = 5, but had 10-day streak before
    // When: Calculate
    // Then: longestStreak = 10
}
```

## Example Code

```java
package com.ctoblue.plan91.domain.habit;

import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StreakCalculationService {
    
    private final RecurrenceEvaluationService recurrenceService;
    private final Clock clock;
    
    public StreakCalculationService(
        RecurrenceEvaluationService recurrenceService,
        Clock clock
    ) {
        this.recurrenceService = recurrenceService;
        this.clock = clock;
    }
    
    public HabitStreak calculateStreak(
        Habit habit,
        List<HabitEntry> entries,
        ZoneId userTimezone
    ) {
        LocalDate today = LocalDate.now(clock.withZone(userTimezone));
        
        List<LocalDate> expectedDays = recurrenceService.getExpectedDaysBetween(
            habit.getStartDate(),
            today,
            habit.getRecurrenceRule()
        );
        
        Set<LocalDate> completedDays = entries.stream()
            .map(HabitEntry::getCompletionDate)
            .collect(Collectors.toSet());
        
        // Calculate current streak
        int currentStreak = 0;
        boolean hasUsedStrike = false;
        LocalDate strikeDate = null;
        LocalDate lastMissedDate = null;
        
        // Reverse iteration from today backwards
        List<LocalDate> reversedExpected = new ArrayList<>(expectedDays);
        Collections.reverse(reversedExpected);
        
        for (LocalDate day : reversedExpected) {
            if (completedDays.contains(day)) {
                currentStreak++;
                lastMissedDate = null;
            } else {
                if (lastMissedDate != null) {
                    // Second miss - break
                    break;
                } else if (!hasUsedStrike) {
                    // First miss - use strike
                    hasUsedStrike = true;
                    strikeDate = day;
                    lastMissedDate = day;
                    currentStreak++; // Continues
                } else {
                    // Strike used, break on next miss
                    break;
                }
            }
        }
        
        int longestStreak = calculateLongestStreak(expectedDays, completedDays);
        int totalCompletions = completedDays.size();
        
        return new HabitStreak(
            currentStreak,
            longestStreak,
            totalCompletions,
            hasUsedStrike,
            strikeDate,
            habit.getStartDate(),
            today
        );
    }
    
    private int calculateLongestStreak(
        List<LocalDate> expectedDays,
        Set<LocalDate> completedDays
    ) {
        // Implementation for longest streak calculation
        // Similar logic but track maximum
        // ...
    }
    
    public boolean isStreakBroken(
        LocalDate lastCompletion,
        boolean hasUsedStrike,
        LocalDate strikeDate,
        LocalDate today,
        RecurrenceRule rule
    ) {
        // Check if there are two consecutive misses
        // ...
    }
}
```

## Definition of Done

- [ ] StreakCalculationService class created with all methods
- [ ] Unit tests written covering all scenarios (min 90% coverage)
- [ ] Edge cases tested (new habit, timezone edge cases)
- [ ] JavaDoc comments on public methods
- [ ] Code reviewed (self-review checklist)
- [ ] No SonarLint warnings
- [ ] Integration with Habit aggregate verified

## References

- DOMAIN-MODEL.md § Habit Aggregate § HabitStreak
- DOMAIN-MODEL.md § Domain Services
- SPECIFICATION.md § Business Rules § Streak Calculation
- [DDD Pattern: Domain Service](https://martinfowler.com/bliki/EvansClassification.html)

## Notes for Agent

**Critical Considerations:**
1. **Timezone handling is crucial** - all date comparisons must be in user's timezone
2. **The strike logic is the most complex part** - pay special attention to edge cases
3. **Performance**: For long-running habits (365+ days), this calculation could be expensive
   - Consider caching strategy for future optimization
   - For MVP, direct calculation is fine
4. **Testing strategy**: Use fixed Clock in tests for deterministic results

**Common Pitfalls to Avoid:**
- Don't use system timezone - always use user timezone
- Don't assume consecutive dates - recurrence might skip days
- Don't forget to clear strike when habit is completed
- Don't count non-expected days as misses

**Dependencies:**
Ensure RecurrenceEvaluationService is complete before starting this ticket.
```

---

## Development Workflow

### For Project Manager (You)

1. **Review and refine epics**: Adjust scope based on priorities
2. **Generate ticket files**: Create all markdown files in `/epics/` structure
3. **Prioritize tickets**: Mark critical path and dependencies
4. **Define epic order**: Some epics can run in parallel

### For AI Agents (Claude Code, Cursor, etc.)

**Session Start Prompt Template:**

```
I'm working on Plan 91, a habit tracking application.

Context files to read:
- /docs/SPECIFICATION.md (master spec)
- /docs/DOMAIN-MODEL.md (domain model reference)
- /epics/[current-epic]/EPIC.md (current epic overview)

Current ticket: /epics/[epic]/tickets/[ticket-id].md

Instructions:
1. Read the ticket file completely
2. Review all referenced documentation
3. Implement according to acceptance criteria
4. Write tests as specified
5. Run tests and ensure they pass
6. Mark ticket complete when all DoD items checked

Do not deviate from the domain model without discussing first.
All code must follow DDD principles and hexagonal architecture.
```

### Using Claude Code

```bash
# 1. Start work on a ticket
$ cd plan91
$ codex chat --file epics/05-habit-core-domain/tickets/004-streak-calculation-service.md

# 2. Agent reads context and implements
# (Agent creates files, writes tests, etc.)

# 3. Run tests
$ codex run "./mvnw test"

# 4. Mark ticket complete
$ echo "✅ COMPLETED $(date)" >> epics/05-habit-core-domain/tickets/004-streak-calculation-service.md
$ git add .
$ git commit -m "feat(domain): implement streak calculation service [PLAN91-050104]"

# 5. Move to next ticket
```

### Tracking Progress

**In each ticket file:**
```markdown
## Status

- Started: 2026-01-29 10:00
- Completed: 2026-01-29 14:30
- Agent: Claude Code
- Commits: abc123, def456

## Notes

- Modified algorithm slightly to handle timezone edge case
- Added extra test for DST transition
```

### Quality Assurance

**Self-Review Checklist** (in each ticket):
- [ ] Code compiles without warnings
- [ ] All tests pass
- [ ] Test coverage > 80%
- [ ] JavaDoc on public methods
- [ ] No hardcoded values
- [ ] Follows naming conventions
- [ ] No TODOs left in code
- [ ] Logging added where appropriate

---

## Next Steps

### Immediate Actions

1. **Create reference documents:**
   - `/docs/DOMAIN-MODEL.md` - Detailed domain model
   - `/docs/API-DESIGN.md` - Complete API specs
   - `/docs/ARCHITECTURE.md` - Architecture diagrams

2. **Generate Epic 01 tickets:**
   - Create all ticket files for domain modeling
   - Populate with full context

3. **Set up repository:**
   - Initialize Git repo
   - Create branch strategy (main, develop, feature/*)
   - Add `.gitignore`

4. **Start with Epic 02 (Infrastructure):**
   - This is the most concrete starting point
   - Gets the project scaffold in place
   - Agents can start building immediately

### Recommended Order

```
Phase 1: Foundation (Week 1)
├─ Epic 02: Infrastructure Setup
├─ Epic 01: Domain Modeling (in parallel)
└─ Epic 03: Authentication

Phase 2: Core Backend (Week 2-3)
├─ Epic 04: Categories
├─ Epic 05: Habit Core Domain
├─ Epic 06: Habit REST API
├─ Epic 07: Analytics Backend
└─ Epic 08: Milestones

Phase 3: Frontend (Week 4-5)
├─ Epic 09: Frontend Foundation
├─ Epic 10: Auth UI
├─ Epic 11: Category UI
├─ Epic 12: Habit Management UI
├─ Epic 13: Dashboard
├─ Epic 14: Analytics UI
└─ Epic 15: Milestones UI

Phase 4: Polish & Deploy (Week 6)
├─ Epic 16: PWA Features
├─ Epic 17: Polish & UX
└─ Epic 18: Deployment
```

### Success Metrics

**MVP Complete When:**
- [ ] Users can register and log in
- [ ] Users can create habits with all tracking types
- [ ] Users can complete habits daily
- [ ] Streak calculation works correctly
- [ ] One-strike rule functions as designed
- [ ] Dashboard shows today's habits
- [ ] Stats page shows analytics
- [ ] App is deployed on Railway
- [ ] PWA is installable on mobile

---

## Appendix

### Glossary

- **Habit**: A behavior to track over 91 days
- **Streak**: Consecutive days completing a habit
- **Strike**: The one-time forgiveness for missing a day
- **Recurrence Rule**: Which days a habit applies to
- **Completion**: Recording that a habit was done on a specific day
- **Milestone**: Achievement checkpoints (weekly, monthly, 91-day)

### Technology References

- **Spring Boot**: https://spring.io/projects/spring-boot
- **Auth0**: https://auth0.com/docs
- **HTMX**: https://htmx.org/docs/
- **Tailwind CSS**: https://tailwindcss.com/docs
- **Chart.js**: https://www.chartjs.org/docs/
- **Railway**: https://docs.railway.app/

### DDD Resources

- [Domain-Driven Design Reference](https://www.domainlanguage.com/ddd/reference/)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Aggregates in DDD](https://martinfowler.com/bliki/DDD_Aggregate.html)

---

**Document Version:** 1.0  
**Last Updated:** January 29, 2026  
**Next Review:** After Epic 01 completion
