# Plan 91 - Domain Schema

**Status**: ✅ Approved - Implementation Starting
**Version**: 1.0
**Date**: 2026-01-30

> Final model: HabitTracker → Habit (definition) → Routine (tracking) → HabitEntry (completion)

---

## Domain Schema

```
schema plan91 {

    // =========================================
    // The person using Plan 91
    // =========================================
    thing HabitTracker {
        // Personal info
        firstName: string
        lastName: string
        email: string

        // Auth & tracking
        auth0Id: string              // External auth provider ID
        lastLogin: timestamp?
        lastLoginIp: string?
        originalTimezone: string     // User's timezone (e.g., "America/Los_Angeles")

        // Timestamps
        createdAt: timestamp
        updatedAt: timestamp

        // Relationships
        inverse {
            createdHabits: Habit[0..*] for creator rel "created"
            routines: Routine[0..*] for tracker rel "tracking"
        }

        display "{firstName} {lastName}"
    }


    // =========================================
    // Habit Definition (What you want to do)
    // =========================================
    thing Habit {
        // Definition
        name: string                 // e.g., "Pray Rosary", "Swimming", "Read 10 pages"
        description: string?         // Optional details

        // Tracking configuration
        trackingType: enum(BOOLEAN, NUMERIC)
        numericUnit: string?         // e.g., "meters", "pages", "minutes"
        numericMin: integer?         // Min value if numeric
        numericMax: integer?         // Max value if numeric
        numericTarget: integer?      // Target value (e.g., 10 pages)

        // Visibility & sharing
        isPublic: boolean            // Can others see/copy this habit?
        isPrivate: boolean           // Only visible to creator

        // Provenance
        creator: HabitTracker rel "created by"
        sourceHabit: Habit? rel "copied from"  // If this is a copy

        // Timestamps
        createdAt: timestamp
        updatedAt: timestamp

        // Relationships
        inverse {
            copies: Habit[0..*] for sourceHabit rel "copied as"
            routines: Routine[0..*] for habit rel "used in"
        }

        display "{name}"
    }


    // =========================================
    // Routine (91-day commitment to a Habit)
    // =========================================
    thing Routine {
        // Link to what we're tracking
        habit: Habit rel "tracking"
        tracker: HabitTracker rel "tracked by"

        // Recurrence rules (when is it expected?)
        recurrenceType: enum(DAILY, WEEKDAYS, WEEKENDS, SPECIFIC_DAYS, NTH_DAY_OF_MONTH)
        recurrenceDays: set<DayOfWeek>?     // For SPECIFIC_DAYS: [MON, WED, FRI]
        recurrenceNthDay: integer?          // For NTH_DAY_OF_MONTH: which day (1=Monday, 7=Sunday)
        recurrenceNthWeek: integer?         // For NTH_DAY_OF_MONTH: which week (1-4)

        // The 91-day cycle
        startDate: date              // When this routine started (immutable)
        expectedEndDate: date        // startDate + 91 days
        completedAt: timestamp?      // When reached 91 days (null if ongoing)
        isCompleted: boolean         // Reached 91 days?

        // Streak tracking (one-strike rule)
        currentStreak: integer       // Consecutive completions on expected days
        longestStreak: integer       // Best streak achieved
        totalCompletions: integer    // All-time completions
        hasUsedStrike: boolean       // Missed once?
        strikeDate: date?            // When the strike was used
        lastCompletionDate: date?    // Last time completed

        // Status
        status: enum(ACTIVE, PAUSED, COMPLETED, ABANDONED)

        // Timestamps
        createdAt: timestamp
        updatedAt: timestamp

        // Relationships
        inverse {
            entries: HabitEntry[0..*] for routine rel "completions"
        }

        display "{tracker.firstName}'s {habit.name} (Day {currentStreak}/91)"
    }


    // =========================================
    // HabitEntry (Individual completion)
    // =========================================
    thing HabitEntry {
        // Link to routine
        routine: Routine rel "completion of"
        tracker: HabitTracker rel "completed by"  // Denormalized for quick queries

        // When it happened
        completionDate: date         // The day (in user's timezone)
        completedAt: timestamp       // Exact moment

        // What happened (for numeric tracking)
        numericValue: integer?       // e.g., 1500 (meters), 12 (pages)

        // User notes
        note: string?                // e.g., "I swam today. 1500m, felt great!"

        // Timestamps
        createdAt: timestamp

        display "{routine.habit.name} on {completionDate}"
    }


    // =========================================
    // Category (Optional - for organization)
    // =========================================
    thing Category {
        name: string
        color: string                // Hex color, e.g., "#1F6F8B"
        icon: string?                // Emoji or icon identifier

        tracker: HabitTracker rel "owned by"

        inverse {
            habits: Habit[0..*] for category rel "categorized as"
        }

        display "{name}"
    }

    // Add category to Habit (optional)
    extend Habit {
        category: Category? rel "in category"
    }
}
```

---

## Visual Relationships

```
┌──────────────────┐
│  HabitTracker    │ (The Person)
│  - firstName     │
│  - lastName      │
│  - email         │
│  - timezone      │
└────────┬─────────┘
         │
         │ creates
         ▼
    ┌─────────┐
    │  Habit  │ (The Definition/Template)
    │ - name  │ "Pray Rosary", "Swimming"
    │ - type  │ BOOLEAN or NUMERIC
    │ - public│ Can others copy?
    └────┬────┘
         │ ╲
         │  ╲ copies (sourceHabit)
         │   ╲
         │    ▼
         │  ┌─────────┐
         │  │  Habit  │ (A copy)
         │  └─────────┘
         │
         │ used in
         ▼
    ┌──────────┐
    │ Routine  │ (The 91-day Commitment)
    │ - streak │
    │ - strike │
    │ - dates  │
    │ - status │
    └────┬─────┘
         │
         │ has
         ▼
    ┌──────────────┐
    │ HabitEntry   │ (Individual Completion)
    │ - date       │
    │ - value      │ "1500m"
    │ - note       │ "Felt great!"
    └──────────────┘
```

---

## Key Concepts

### Separation of Concerns

1. **Habit** = WHAT you want to do
   - Definition/template
   - Can be shared publicly
   - Can be copied by others
   - Defines tracking type (boolean/numeric)

2. **Routine** = YOUR 91-day commitment
   - When you'll do it (recurrence)
   - Streak tracking
   - One-strike rule enforcement
   - You can have multiple Routines for the same Habit
   - Example: Try "Swimming" in Jan 2026, abandon it, try again in June 2026

3. **HabitEntry** = Each time you did it
   - "I swam today, 1500m, at 9am, felt great!"
   - Belongs to a Routine
   - Timestamped proof of completion

### Public Habits

```
HabitTracker: John
    creates →
        Habit: "Pray Rosary"
            isPublic: true
            ↓
HabitTracker: Maria
    sees John's habit →
        copies it →
            Habit: "Pray Rosary" (Maria's copy)
                sourceHabit: John's "Pray Rosary"
                creator: Maria
                ↓
            Routine: Maria's commitment
                habit: Maria's copy of "Pray Rosary"
```

---

## Example Data

### HabitTracker
```yaml
id: tracker-001
firstName: "Luis"
lastName: "Martinez"
email: "luis@example.com"
auth0Id: "auth0|123456"
originalTimezone: "America/Los_Angeles"
createdAt: 2026-01-01T10:00:00Z
```

### Habit (Definition)
```yaml
id: habit-001
name: "Swimming"
description: "Cardio workout"
trackingType: NUMERIC
numericUnit: "meters"
numericTarget: 1500
isPublic: true
isPrivate: false
creator: tracker-001
sourceHabit: null
createdAt: 2026-01-01T10:30:00Z
```

### Routine (91-day commitment)
```yaml
id: routine-001
habit: habit-001
tracker: tracker-001
recurrenceType: WEEKDAYS
recurrenceDays: [MON, TUE, WED, THU, FRI]
startDate: 2026-01-06  # First Monday
expectedEndDate: 2026-04-07  # +91 days
currentStreak: 15
longestStreak: 15
totalCompletions: 15
hasUsedStrike: false
status: ACTIVE
createdAt: 2026-01-06T08:00:00Z
```

### HabitEntry (Completion)
```yaml
id: entry-001
routine: routine-001
tracker: tracker-001
completionDate: 2026-01-20
completedAt: 2026-01-20T18:30:00Z
numericValue: 1500
note: "I swam today. 1500m, at 6:30pm. Felt great!"
createdAt: 2026-01-20T18:30:15Z
```

---

## Database Constraints

### Unique Constraints
1. `HabitTracker.email` - UNIQUE
2. `HabitEntry.routineId + completionDate` - UNIQUE (one entry per day per routine)

### Foreign Keys
1. `Habit.creator` → `HabitTracker.id`
2. `Habit.sourceHabit` → `Habit.id` (self-reference for copies)
3. `Routine.habit` → `Habit.id`
4. `Routine.tracker` → `HabitTracker.id`
5. `HabitEntry.routine` → `Routine.id`
6. `HabitEntry.tracker` → `HabitTracker.id` (denormalized)

### Indexes
1. `Habit.isPublic` (find public habits)
2. `Habit.creator` (find user's habits)
3. `Routine.tracker` (find user's routines)
4. `Routine.status` (active/completed filter)
5. `HabitEntry.routineId` (get routine's entries)
6. `HabitEntry.completionDate` (date queries)
7. `HabitEntry.tracker + completionDate` (today's completions)

---

## Questions for Discussion

### 1. Categories
Should we include Category for organizing habits?
- **Option A**: Yes, optional (habits can be categorized)
- **Option B**: No, keep it simple
- **Option C**: Use tags instead of categories

### 2. Multiple Routines per Habit
Can you have multiple ACTIVE routines for the same habit?
- **Option A**: Yes (e.g., "Swimming" MWF + "Swimming" Weekends = 2 routines)
- **Option B**: No, only one active routine per habit at a time

**My suggestion**: Option A (allow multiple)

### 3. Copying Habits
When you copy a public habit:
- **Option A**: Create a full copy (you can modify it)
- **Option B**: Just reference the original (shared definition)

**My suggestion**: Option A (full copy, so you can customize)

### 4. Routine Lifecycle
When a routine reaches 91 days:
- **Option A**: Auto-mark as COMPLETED, ask user to create new routine
- **Option B**: Allow user to "extend" past 91 days
- **Option C**: Create new routine automatically (cycle)

**My suggestion**: Option A (explicit choice)

### 5. Deleting Entries
Can users delete HabitEntries?
- **Option A**: Yes, within 5 minutes
- **Option B**: Yes, always
- **Option C**: No, immutable

**My suggestion**: Option A (5-minute undo)

---

## What's Different from V0.1?

**Old Model**:
- User → Category → Habit → HabitEntry
- Habit contained everything (tracking + definition)

**New Model** (Better!):
- HabitTracker → Habit (definition) → Routine (tracking) → HabitEntry
- **Separation**: Definition vs Commitment vs Completion
- **Sharing**: Habits can be public/copied
- **Multiple attempts**: Can try the same habit multiple times

**Why it's better**:
- ✅ Clearer separation of concerns
- ✅ Supports sharing/copying habits
- ✅ Allows multiple 91-day attempts of the same habit
- ✅ "I swam today, 1500m" makes perfect sense as an entry

---

## Next Steps

1. Confirm this structure feels right
2. Answer the 5 questions above
3. Finalize any missing pieces
4. Then start coding!

**What do you think of this model?** 🤔
