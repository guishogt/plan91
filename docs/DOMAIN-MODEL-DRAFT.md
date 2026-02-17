# Plan 91 - Domain Model (Conceptual Draft)

**Status**: 🔄 Draft for Discussion
**Version**: 0.1
**Date**: 2026-01-30

> This is a simplified conceptual model to discuss and refine before writing code.

---

## Core Entities Overview

```
┌─────────┐
│  User   │───┐
└─────────┘   │
              │ owns
              ▼
         ┌──────────┐
         │ Category │
         └──────────┘
              │
              │ categorizes
              ▼
         ┌────────┐
         │ Habit  │───┐
         └────────┘   │
              │       │ tracks
              │       ▼
              │  ┌──────────────┐
              │  │ HabitEntry   │
              └──┤ (completions)│
                 └──────────────┘
```

---

## Entity Details

### 1. User

**Purpose**: Represents a person using Plan 91

**Properties**:
```
- id: UUID (unique identifier)
- auth0Id: String (external auth provider ID)
- email: String (user's email)
- timezone: String (e.g., "America/Los_Angeles")
- createdAt: Timestamp
```

**Relationships**:
- Has many Categories (1 → N)
- Has many Habits (1 → N)
- Has many HabitEntries (1 → N)

**Business Rules**:
- Email must be unique
- Timezone required (defaults to UTC if not provided)
- Cannot delete user if they have habits (must archive/delete habits first)

---

### 2. Category

**Purpose**: Organize habits into groups (e.g., "Health", "Learning")

**Properties**:
```
- id: UUID
- userId: UUID (owner)
- name: String (e.g., "Health", "Career")
- color: String (hex color like "#1F6F8B")
- icon: String (emoji or icon identifier, optional)
- createdAt: Timestamp
```

**Relationships**:
- Belongs to User (N → 1)
- Has many Habits (1 → N)

**Business Rules**:
- Name must be unique per user
- Color must be valid hex format
- Cannot delete category if habits are using it (must reassign first)
- Default category "Uncategorized" always exists

**Questions to Discuss**:
- Should we have default categories (Health, Work, Personal, etc.)?
- Or let users create all their own?

---

### 3. Habit (CORE ENTITY)

**Purpose**: Represents a habit the user is tracking for 91 days

**Properties**:
```
- id: UUID
- userId: UUID (owner)
- categoryId: UUID
- name: String (e.g., "Read 10 pages")
- description: String (optional, user notes)
- trackingType: Enum (BOOLEAN or NUMERIC)

# For numeric tracking:
- numericUnit: String (e.g., "pages", "minutes", "glasses")
- numericMin: Integer (optional, e.g., 1)
- numericMax: Integer (optional, e.g., 100)
- numericTarget: Integer (optional, e.g., 10)

# Recurrence (when is it expected):
- recurrenceType: Enum (DAILY, WEEKDAYS, WEEKENDS, SPECIFIC_DAYS, NTH_DAY_OF_MONTH)
- recurrenceDays: Set<DayOfWeek> (for SPECIFIC_DAYS, e.g., [MON, WED, FRI])
- recurrenceNthDay: Integer (for NTH_DAY_OF_MONTH, e.g., 1 = first Monday)
- recurrenceNthWeek: Integer (for NTH_DAY_OF_MONTH, which week)

# Streak tracking:
- currentStreak: Integer (consecutive completions)
- longestStreak: Integer (historical best)
- totalCompletions: Integer (all time)
- hasUsedStrike: Boolean (missed once?)
- strikeDate: Date (when strike was used, optional)

# Dates:
- startDate: Date (when habit started, immutable)
- expectedEndDate: Date (startDate + 91 days)
- lastCompletionDate: Date (last time completed, optional)
- completedAt: Timestamp (when reached 91 days, optional)
- isCompleted: Boolean (reached 91 days?)

- createdAt: Timestamp
- updatedAt: Timestamp
```

**Relationships**:
- Belongs to User (N → 1)
- Belongs to Category (N → 1)
- Has many HabitEntries (1 → N)

**Business Rules**:
- Name required (max 100 chars)
- Description optional (max 500 chars)
- Start date cannot be changed after creation
- Can only have one completion per calendar day
- Streak resets if miss 2 consecutive expected days
- After 91 days: user chooses to continue or restart

**Questions to Discuss**:
- Should we store streak calculation in the Habit entity?
- Or calculate it on-demand from HabitEntries?
- **My suggestion**: Store it (currentStreak, hasUsedStrike) for performance, but recalculate if data seems wrong

---

### 4. HabitEntry

**Purpose**: Records a single completion of a habit

**Properties**:
```
- id: UUID
- habitId: UUID (which habit)
- userId: UUID (owner, for quick queries)
- completedAt: Timestamp (exact moment of completion)
- completionDate: Date (the day it was completed, user's timezone)
- numericValue: Integer (for numeric habits, optional)
- note: String (optional user note, max 200 chars)
```

**Relationships**:
- Belongs to Habit (N → 1)
- Belongs to User (N → 1)

**Business Rules**:
- Cannot create entry for future dates
- Cannot have multiple entries for same habit + date (UNIQUE constraint)
- CompletionDate must be an "expected day" according to recurrence rule
- Numeric value must be within min/max if configured
- Cannot delete entries (maybe allow within 5 minutes? TBD)

**Questions to Discuss**:
- Should we allow editing/deleting entries?
- Or lock them once created (for integrity)?
- **My suggestion**: Allow delete within 5 minutes, then lock

---

## Value Objects (Embedded Properties)

These are NOT separate entities, but complex properties:

### RecurrenceRule
```
- type: DAILY | WEEKDAYS | WEEKENDS | SPECIFIC_DAYS | NTH_DAY_OF_MONTH
- daysOfWeek: Set<DayOfWeek> (if SPECIFIC_DAYS)
- nthDay: Integer (if NTH_DAY_OF_MONTH, e.g., 1 = first Monday)
- nthWeek: Integer (which week, e.g., 1, 2, 3, 4)
```

**Examples**:
- "Every day": `{ type: DAILY }`
- "Weekdays only": `{ type: WEEKDAYS }`
- "Mon, Wed, Fri": `{ type: SPECIFIC_DAYS, daysOfWeek: [MON, WED, FRI] }`
- "First Monday of month": `{ type: NTH_DAY_OF_MONTH, nthDay: MON, nthWeek: 1 }`

### NumericConfig
```
- unit: String (e.g., "pages", "minutes")
- min: Integer (optional)
- max: Integer (optional)
- target: Integer (what you're aiming for)
```

### HabitStreak
```
- currentStreak: Integer
- longestStreak: Integer
- totalCompletions: Integer
- hasUsedStrike: Boolean
- strikeDate: Date (optional)
- lastCompletionDate: Date (optional)
```

---

## Key Questions for Discussion

### 1. Streak Calculation
**Option A**: Store streak in Habit entity (faster queries, but needs recalculation logic)
**Option B**: Calculate on-demand from HabitEntries (always accurate, but slower)

**My recommendation**: Option A (store it), with ability to recalculate if needed

### 2. Entry Modification
**Option A**: Entries are immutable once created (prevents cheating)
**Option B**: Allow delete within X minutes (undo mistakes)
**Option C**: Full edit/delete allowed (most flexible, but can game the system)

**My recommendation**: Option B (5-minute undo window)

### 3. Category Management
**Option A**: Provide default categories (Health, Work, Personal, Learning, etc.)
**Option B**: Users create all categories from scratch
**Option C**: Suggest defaults but allow customization

**My recommendation**: Option C (suggest but don't force)

### 4. Recurrence Complexity
**Current design supports**:
- Daily
- Weekdays (Mon-Fri)
- Weekends (Sat-Sun)
- Specific days (any combination)
- Nth day of month (e.g., "first Monday")

**Question**: Is this enough? Or should we support:
- Every N days (e.g., "every 3 days")?
- Multiple times per day?

**My recommendation**: Start with what we have, add more if users request

### 5. Numeric Tracking Validation
For numeric habits:
- Should `min` and `max` be required or optional?
- If user enters value outside range, reject or allow with warning?

**My recommendation**: Optional min/max, reject if provided and violated

---

## Database Constraints

### Unique Constraints
1. `User.email` - UNIQUE
2. `Category.userId + Category.name` - UNIQUE (name unique per user)
3. `HabitEntry.habitId + HabitEntry.completionDate` - UNIQUE (one entry per day)

### Foreign Keys
1. `Category.userId` → `User.id`
2. `Habit.userId` → `User.id`
3. `Habit.categoryId` → `Category.id`
4. `HabitEntry.habitId` → `Habit.id`
5. `HabitEntry.userId` → `User.id`

### Indexes (for performance)
1. `Habit.userId` (list user's habits)
2. `Habit.categoryId` (filter by category)
3. `HabitEntry.habitId` (get habit's entries)
4. `HabitEntry.completionDate` (date-based queries)
5. `HabitEntry.userId + completionDate` (dashboard: today's completions)

---

## Sample Data Example

```yaml
User:
  id: user-001
  email: john@example.com
  timezone: "America/Los_Angeles"

Category:
  id: cat-001
  userId: user-001
  name: "Health"
  color: "#43a047"
  icon: "💪"

Habit:
  id: habit-001
  userId: user-001
  categoryId: cat-001
  name: "Read 10 pages"
  trackingType: NUMERIC
  numericUnit: "pages"
  numericTarget: 10
  recurrenceType: DAILY
  startDate: 2026-01-01
  currentStreak: 15
  hasUsedStrike: false

HabitEntry:
  id: entry-001
  habitId: habit-001
  userId: user-001
  completionDate: 2026-01-15
  completedAt: 2026-01-15T22:30:00Z
  numericValue: 12
  note: "Great chapter!"
```

---

## What's Missing? (For Later Epics)

These are out of scope for Epic 01, will add later:

- **Milestones**: Tracking 7-day, 14-day, 30-day achievements (Epic 09)
- **Notifications**: Reminders and achievement celebrations (Epic 09)
- **Sharing**: Social features, sharing achievements (Epic 10, optional)
- **Analytics**: Aggregated statistics, charts (Epic 08)
- **History**: Archived habits after 91 days (Epic 05)

---

## Next Steps

1. **Discuss this design**: What feels right? What feels wrong?
2. **Simplify or add complexity**: Too much? Too little?
3. **Answer the questions** above
4. **Finalize the model**
5. **Then write code** (Value Objects → Aggregates → Services)

---

**Let's discuss!** What do you think? Any concerns or changes you'd like to make?
