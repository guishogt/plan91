# Plan 91 - Domain Model

**Version**: 1.0
**Last Updated**: 2026-01-29

This document provides a detailed view of the Plan 91 domain model using Domain-Driven Design (DDD) principles.

---

## Table of Contents

1. [Domain Overview](#domain-overview)
2. [Bounded Contexts](#bounded-contexts)
3. [Aggregates](#aggregates)
4. [Domain Services](#domain-services)
5. [Domain Events](#domain-events)
6. [Business Rules](#business-rules)
7. [Diagrams](#diagrams)

---

## Domain Overview

Plan 91 is built around the core concept of **habit tracking with a forgiving accountability system**.

### Core Concepts
- **91-Day Cycle**: The fundamental commitment period for habit formation
- **One-Strike Rule**: Users can miss once without penalty, but not twice consecutively
- **Streak Tracking**: Counts consecutive completions on expected days
- **Recurrence Rules**: Flexible scheduling (daily, weekdays, custom)
- **Numeric vs Boolean Tracking**: Track completion or measure quantity

---

## Bounded Contexts

```mermaid
graph TB
    subgraph "Plan 91 System"
        IC[Identity Context]
        HC[Habits Context]
        AC[Analytics Context]
        NC[Notifications Context]

        IC -.User ID.-> HC
        IC -.User ID.-> AC
        HC -.Events.-> NC
        HC -.Data.-> AC
    end

    style IC fill:#E6D5B8
    style HC fill:#1F6F8B,color:#fff
    style AC fill:#99A8B2
    style NC fill:#1C2B2D,color:#fff
```

### Identity Context
**Responsibility**: User management and authentication
**Core Concept**: User identity, timezone management
**External Dependency**: Auth0

### Habits Context (CORE)
**Responsibility**: Habit lifecycle, tracking, streak calculation
**Core Concept**: Habits, Entries, Categories, Streaks
**This is the heart of the application**

### Analytics Context
**Responsibility**: Statistics, visualizations, insights
**Core Concept**: Aggregated data, charts, reports

### Notifications Context
**Responsibility**: Milestones, achievements, reminders
**Core Concept**: Events, notifications, kudos

---

## Aggregates

### Aggregate: User (Identity Context)

```mermaid
classDiagram
    class User {
        <<Aggregate Root>>
        -UserId id
        -String auth0Id
        -Email email
        -ZoneId timezone
        -Password hashedPassword
        -Timestamp createdAt
        -Timestamp updatedAt
        +updateTimezone(ZoneId)
        +updateEmail(Email)
    }

    class UserId {
        <<Value Object>>
        -UUID value
        +of(UUID)
        +generate()
    }

    class Email {
        <<Value Object>>
        -String value
        +of(String)
        +isValid()
    }

    User --> UserId
    User --> Email
```

**Business Rules**:
- Email must be unique across all users
- Timezone is required (defaults to UTC)
- Auth0 ID links to external identity provider
- Password must be securely hashed (BCrypt)

---

### Aggregate: Category (Habits Context)

```mermaid
classDiagram
    class Category {
        <<Aggregate Root>>
        -CategoryId id
        -UserId userId
        -String name
        -String color
        -String icon
        -Timestamp createdAt
        +rename(String)
        +changeColor(String)
        +changeIcon(String)
    }

    class CategoryId {
        <<Value Object>>
        -UUID value
    }

    Category --> CategoryId
    Category --> UserId
```

**Business Rules**:
- Category names must be unique per user
- Color must be valid hex color (#RRGGBB)
- Cannot delete category with associated habits (or must reassign first)
- Icon is optional (emoji or identifier)

---

### Aggregate: Habit (Habits Context - CORE)

```mermaid
classDiagram
    class Habit {
        <<Aggregate Root>>
        -HabitId id
        -UserId userId
        -CategoryId categoryId
        -String name
        -String description
        -TrackingType trackingType
        -NumericConfig numericConfig
        -RecurrenceRule recurrenceRule
        -HabitStreak streak
        -LocalDate startDate
        -boolean isCompleted
        -List~HabitEntry~ entries
        +complete(LocalDate, String, Integer)
        +edit(String, String, CategoryId)
        +recalculateStreak(ZoneId)
        +isExpectedDay(LocalDate)
        +getDaysTo91()
    }

    class HabitEntry {
        <<Entity>>
        -HabitEntryId id
        -HabitId habitId
        -UserId userId
        -Timestamp completedAt
        -LocalDate completionDate
        -Integer numericValue
        -String note
    }

    class HabitStreak {
        <<Value Object>>
        -int currentStreak
        -int longestStreak
        -int totalCompletions
        -boolean hasUsedStrike
        -LocalDate strikeDate
        -LocalDate startDate
        -LocalDate expectedEndDate
        -LocalDate lastCompletionDate
        +getDaysTo91()
        +getCompletionRate()
        +useStrike(LocalDate)
        +clearStrike()
        +breakStreak()
    }

    class NumericConfig {
        <<Value Object>>
        -String unitName
        -Integer minValue
        -Integer maxValue
        -Integer targetValue
        +isValueValid(Integer)
    }

    class RecurrenceRule {
        <<Value Object>>
        -RecurrenceType type
        -Set~DayOfWeek~ daysOfWeek
        -Integer dayOfMonth
        -Integer weekOfMonth
        +isExpectedDay(LocalDate)
        +getNextExpectedDay(LocalDate)
    }

    class TrackingType {
        <<Enumeration>>
        BOOLEAN
        NUMERIC
    }

    class RecurrenceType {
        <<Enumeration>>
        DAILY
        WEEKDAYS
        WEEKENDS
        SPECIFIC_DAYS
        NTH_DAY_OF_MONTH
    }

    Habit *-- HabitEntry
    Habit *-- HabitStreak
    Habit *-- NumericConfig
    Habit *-- RecurrenceRule
    Habit --> TrackingType
    RecurrenceRule --> RecurrenceType
```

**Business Rules**:
- Name required (max 100 chars)
- Description optional (max 500 chars)
- Start date cannot be changed after creation
- One completion per calendar day maximum
- Cannot backdate completions
- Streak calculation considers user timezone
- Numeric habits require NumericConfig
- Boolean habits ignore numeric values

---

## Domain Services

Domain Services contain business logic that doesn't naturally belong to a single entity.

### StreakCalculationService

```mermaid
sequenceDiagram
    participant Client
    participant StreakService
    participant RecurrenceService
    participant Habit

    Client->>StreakService: calculateStreak(habit, entries, timezone)
    StreakService->>RecurrenceService: getExpectedDays(startDate, today, rule)
    RecurrenceService-->>StreakService: List<LocalDate>
    StreakService->>StreakService: mapCompletedDays(entries)
    StreakService->>StreakService: walkBackwardsAndCountStreak()
    StreakService->>StreakService: detectStrikeUsage()
    StreakService->>StreakService: detectStreakBreak()
    StreakService-->>Client: HabitStreak
```

**Responsibilities**:
- Calculate current streak
- Detect strike usage
- Detect streak breaks
- Calculate longest streak
- Count total completions
- All calculations timezone-aware

**Key Algorithm**:
```
For each expected day (from today backwards):
    If completed:
        Increment streak
        Clear last miss tracking
    Else (missed):
        If already missed once before:
            Streak broken - STOP
        Else if strike not yet used:
            Use strike
            Mark strike date
            Continue streak
        Else:
            Streak broken - STOP
```

---

### RecurrenceEvaluationService

```mermaid
flowchart TD
    Start[isExpectedDay?] --> CheckType{Recurrence Type?}
    CheckType -->|DAILY| ReturnTrue[Return true]
    CheckType -->|WEEKDAYS| CheckWeekday{Mon-Fri?}
    CheckWeekday -->|Yes| ReturnTrue
    CheckWeekday -->|No| ReturnFalse[Return false]
    CheckType -->|WEEKENDS| CheckWeekend{Sat-Sun?}
    CheckWeekend -->|Yes| ReturnTrue
    CheckWeekend -->|No| ReturnFalse
    CheckType -->|SPECIFIC_DAYS| CheckInSet{Day in set?}
    CheckInSet -->|Yes| ReturnTrue
    CheckInSet -->|No| ReturnFalse
    CheckType -->|NTH_DAY_OF_MONTH| CheckNth{Matches Nth day?}
    CheckNth -->|Yes| ReturnTrue
    CheckNth -->|No| ReturnFalse
```

**Responsibilities**:
- Determine if a date is an "expected day" for a habit
- Generate list of expected days in a date range
- Handle all recurrence types
- Support complex patterns (e.g., "First Monday of each month")

---

## Domain Events

Events represent significant occurrences in the domain.

```mermaid
graph LR
    HC[Habit Created] --> NA[Notification Service]
    HCO[Habit Completed] --> SC[Streak Calculation]
    HCO --> MS[Milestone Check]
    SU[Strike Used] --> NA
    SB[Streak Broken] --> NA
    MA[Milestone Achieved] --> NA
    H91[91 Days Complete] --> NA

    style HC fill:#E6D5B8
    style HCO fill:#E6D5B8
    style SU fill:#f9a825
    style SB fill:#e53935
    style MA fill:#43a047
    style H91 fill:#1F6F8B,color:#fff
```

### Event Types

**HabitCreated**
```java
{
    habitId: UUID,
    userId: UUID,
    name: String,
    startDate: LocalDate,
    occurredAt: Timestamp
}
```

**HabitCompleted**
```java
{
    habitId: UUID,
    userId: UUID,
    completionDate: LocalDate,
    numericValue: Integer?, // optional
    occurredAt: Timestamp
}
```

**StrikeUsed**
```java
{
    habitId: UUID,
    userId: UUID,
    strikeDate: LocalDate,
    occurredAt: Timestamp
}
```

**StrikeCleared**
```java
{
    habitId: UUID,
    userId: UUID,
    occurredAt: Timestamp
}
```

**StreakBroken**
```java
{
    habitId: UUID,
    userId: UUID,
    finalStreak: int,
    occurredAt: Timestamp
}
```

**MilestoneAchieved**
```java
{
    habitId: UUID,
    userId: UUID,
    milestoneDay: int, // 7, 14, 21, 30, etc.
    habitName: String,
    occurredAt: Timestamp
}
```

**Habit91DaysComplete**
```java
{
    habitId: UUID,
    userId: UUID,
    habitName: String,
    finalStreak: int,
    occurredAt: Timestamp
}
```

---

## Business Rules

### Streak Calculation Rules

**Rule 1: Consecutive Expected Days**
> A streak is the count of consecutive completions on days where the habit was expected, according to its recurrence rule.

**Example**: A weekday-only habit completed Mon-Fri has a 5-day streak, even though Saturday/Sunday passed.

---

**Rule 2: One-Strike Forgiveness**
> Missing one expected day activates the strike. The streak continues. Missing a second expected day before completing another breaks the streak.

**States**:
1. **Normal**: No strikes used, all expected days completed
2. **Strike Active**: Missed one day, can't miss another
3. **Streak Broken**: Missed twice, streak resets to 0

**Transitions**:
```
Normal --[miss]--> Strike Active
Strike Active --[complete]--> Normal (strike cleared)
Strike Active --[miss]--> Streak Broken
Streak Broken --[complete]--> Normal (new streak starts at 1)
```

---

**Rule 3: Timezone-Aware Dates**
> All date comparisons must use the user's configured timezone. A completion at 11:30 PM PST should count for that day in PST, not UTC.

---

**Rule 4: No Backdating**
> Users cannot record completions for past dates. This prevents cheating. Completions are always for "today" in the user's timezone.

---

**Rule 5: One Completion Per Day**
> Maximum one HabitEntry per habit per calendar day. The database enforces this with a unique constraint.

---

### Milestone Rules

**Weekly Milestones**: Days 7, 14, 21, 28, 35, 42, 49, 56, 63, 70, 77, 84
**Monthly Milestones**: Days 30, 60
**Final Milestone**: Day 91

**Trigger**: When currentStreak reaches milestone number, fire MilestoneAchieved event

---

### 91-Day Completion Rules

**When currentStreak == 91**:
1. Fire Habit91DaysComplete event
2. Set `isCompleted = true`
3. Set `completedAt = now()`
4. User chooses:
   - **Continue**: Keep tracking, streak continues past 91
   - **Restart**: Archive this habit, create new 91-day cycle

---

## Diagrams

### Domain Model Overview

```mermaid
erDiagram
    USER ||--o{ HABIT : creates
    USER ||--o{ CATEGORY : creates
    USER ||--o{ HABIT_ENTRY : records

    CATEGORY ||--o{ HABIT : categorizes
    HABIT ||--|{ HABIT_ENTRY : contains

    USER {
        uuid id PK
        string auth0_id UK
        string email UK
        string timezone
        timestamp created_at
    }

    CATEGORY {
        uuid id PK
        uuid user_id FK
        string name
        string color
        string icon
    }

    HABIT {
        uuid id PK
        uuid user_id FK
        uuid category_id FK
        string name
        string description
        enum tracking_type
        json numeric_config
        json recurrence_rule
        int current_streak
        int longest_streak
        int total_completions
        boolean has_used_strike
        date strike_date
        date start_date
        boolean is_completed
    }

    HABIT_ENTRY {
        uuid id PK
        uuid habit_id FK
        uuid user_id FK
        timestamp completed_at
        date completion_date
        int numeric_value
        string note
    }
```

### Streak Calculation State Machine

```mermaid
stateDiagram-v2
    [*] --> Normal: Habit Created

    Normal --> StrikeActive: Miss Expected Day
    Normal --> Normal: Complete on Expected Day

    StrikeActive --> Normal: Complete (Strike Cleared)
    StrikeActive --> StreakBroken: Miss Second Expected Day

    StreakBroken --> Normal: Complete (New Streak)
    StreakBroken --> StreakBroken: Miss

    Normal --> Milestone: Streak Reaches 7, 14, 21...
    Milestone --> Normal: Continue

    Normal --> Complete91: Streak Reaches 91
    Complete91 --> [*]: Archive or Continue
```

### Use Case Diagram

```mermaid
graph TB
    subgraph "User Actions"
        U[User]
    end

    subgraph "Habit Management"
        UC1[Create Habit]
        UC2[Edit Habit]
        UC3[Delete Habit]
        UC4[View Habit Details]
    end

    subgraph "Daily Tracking"
        UC5[Complete Habit Today]
        UC6[Add Note to Entry]
        UC7[View Today's Habits]
    end

    subgraph "Analytics"
        UC8[View Habit Statistics]
        UC9[View Progress Charts]
        UC10[View Calendar Heatmap]
        UC11[View Category Breakdown]
    end

    subgraph "Milestones"
        UC12[View Milestones]
        UC13[Celebrate Achievement]
        UC14[Complete 91 Days]
    end

    subgraph "Categories"
        UC15[Create Category]
        UC16[Edit Category]
        UC17[Delete Category]
    end

    U --> UC1
    U --> UC2
    U --> UC3
    U --> UC4
    U --> UC5
    U --> UC6
    U --> UC7
    U --> UC8
    U --> UC9
    U --> UC10
    U --> UC11
    U --> UC12
    U --> UC13
    U --> UC14
    U --> UC15
    U --> UC16
    U --> UC17

    UC5 -.triggers.-> UC12
    UC5 -.triggers.-> UC14
```

---

## Testing the Model

### Standalone Testing Approach

Before implementing persistence, each domain class should have a `public static void main()` method for testing:

**Example: HabitStreak.java**
```java
public class HabitStreak {
    private int currentStreak;
    private int longestStreak;
    private int totalCompletions;
    private boolean hasUsedStrike;
    private LocalDate strikeDate;
    private LocalDate startDate;
    private LocalDate expectedEndDate;

    // ... constructors, getters, business methods

    public int getDaysTo91() {
        return 91 - currentStreak;
    }

    // Standalone test
    public static void main(String[] args) {
        System.out.println("=== Testing HabitStreak ===\n");

        // Test 1: New streak
        HabitStreak newStreak = new HabitStreak(
            0, 0, 0, false, null,
            LocalDate.now(), LocalDate.now().plusDays(91), null
        );
        assert newStreak.getDaysTo91() == 91;
        System.out.println("✓ New streak: " + newStreak.getDaysTo91() + " days to 91");

        // Test 2: Active streak
        HabitStreak activeStreak = new HabitStreak(
            15, 15, 15, false, null,
            LocalDate.now().minusDays(15), LocalDate.now().plusDays(76),
            LocalDate.now().minusDays(1)
        );
        assert activeStreak.getDaysTo91() == 76;
        System.out.println("✓ Active streak: " + activeStreak.getDaysTo91() + " days to 91");

        // Test 3: Strike used
        HabitStreak strikeStreak = new HabitStreak(
            10, 10, 10, true, LocalDate.now().minusDays(2),
            LocalDate.now().minusDays(10), LocalDate.now().plusDays(81),
            LocalDate.now().minusDays(1)
        );
        assert strikeStreak.hasUsedStrike();
        System.out.println("✓ Strike active: streak continues at " + strikeStreak.getCurrentStreak());

        System.out.println("\n=== All HabitStreak tests passed! ===");
    }
}
```

Run with: `java HabitStreak.java` (Java 21 single-file execution)

---

**Last Updated**: 2026-01-29
**Next Review**: After Epic 1 completion
