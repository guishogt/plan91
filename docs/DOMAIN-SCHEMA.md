# Plan 91 - Domain Schema

**Status**: ✅ Approved - Implementation Started
**Version**: 1.1
**Date**: 2026-01-30

> Final model: HabitPractitioner → Habit (definition) → Routine (tracking) → HabitEntry (completion)

---

## Domain Schema

```
schema plan91 {

    // =========================================
    // The person using Plan 91
    // =========================================
    thing HabitPractitioner {
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
            routines: Routine[0..*] for practitioner rel "practiced by"
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
        creator: HabitPractitioner rel "created by"
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
        practitioner: HabitPractitioner rel "practiced by"

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

        display "{practitioner.firstName}'s {habit.name} (Day {currentStreak}/91)"
    }


    // =========================================
    // HabitEntry (Individual completion)
    // =========================================
    thing HabitEntry {
        // Link to routine
        routine: Routine rel "completion of"
        practitioner: HabitPractitioner rel "completed by"  // Denormalized for quick queries

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

        practitioner: HabitPractitioner rel "owned by"

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
┌──────────────────────┐
│  HabitPractitioner   │ (The Person)
│  - firstName         │
│  - lastName          │
│  - email             │
│  - timezone          │
└────────┬─────────────┘
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
   - Only ONE active routine per habit at a time (focused commitment)
   - Example: Try "Swimming" Jan-Mar 2026, complete it, then start new routine in June 2026

3. **HabitEntry** = Each time you did it
   - "I swam today, 1500m, at 9am, felt great!"
   - Belongs to a Routine
   - Timestamped proof of completion
   - Can be deleted/edited anytime

### Public Habits

```
HabitPractitioner: John
    creates →
        Habit: "Pray Rosary"
            isPublic: true
            ↓
HabitPractitioner: Maria
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

### HabitPractitioner
```yaml
id: practitioner-001
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
creator: practitioner-001
sourceHabit: null
createdAt: 2026-01-01T10:30:00Z
```

### Routine (91-day commitment)
```yaml
id: routine-001
habit: habit-001
practitioner: practitioner-001
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
practitioner: practitioner-001
completionDate: 2026-01-20
completedAt: 2026-01-20T18:30:00Z
numericValue: 1500
note: "I swam today. 1500m, at 6:30pm. Felt great!"
createdAt: 2026-01-20T18:30:15Z
```

---

## Database Constraints

### Unique Constraints
1. `HabitPractitioner.email` - UNIQUE
2. `HabitEntry.routineId + completionDate` - UNIQUE (one entry per day per routine)
3. `Routine.habitId + practitionerId` - UNIQUE (only one active routine per habit per practitioner)

### Foreign Keys
1. `Habit.creator` → `HabitPractitioner.id`
2. `Habit.sourceHabit` → `Habit.id` (self-reference for copies)
3. `Routine.habit` → `Habit.id`
4. `Routine.practitioner` → `HabitPractitioner.id`
5. `HabitEntry.routine` → `Routine.id`
6. `HabitEntry.practitioner` → `HabitPractitioner.id` (denormalized)

### Indexes
1. `Habit.isPublic` (find public habits)
2. `Habit.creator` (find practitioner's habits)
3. `Routine.practitioner` (find practitioner's routines)
4. `Routine.status` (active/completed filter)
5. `Routine.habitId + practitionerId` (enforce one active routine per habit)
6. `HabitEntry.routineId` (get routine's entries)
7. `HabitEntry.completionDate` (date queries)
8. `HabitEntry.practitioner + completionDate` (today's completions)

---

## Design Decisions Made

### 1. Categories ✅
**Decision**: Option A - Yes, optional (habits can be categorized)
- Categories are included in the schema as optional
- Habits can be organized but don't require a category
- Practitioners can create custom categories with colors and icons

### 2. Multiple Routines per Habit ✅
**Decision**: Option B - No, only one active routine per habit at a time
- A practitioner can only have ONE active routine for a given habit
- Database constraint: `UNIQUE(Routine.habitId + practitionerId)` where status = ACTIVE
- This prevents confusion and keeps commitment focused
- Can start a new routine after completing/abandoning the current one

### 3. Copying Habits ✅
**Decision**: Option A - Create a full copy (you can modify it)
- When copying a public habit, create a new Habit instance
- The copy has `sourceHabit` pointing to the original
- The copy belongs to the new practitioner (`creator` field)
- Changes to the copy don't affect the original
- Allows customization (e.g., different tracking units, targets)

### 4. Routine Lifecycle ✅
**Decision**: Option B - Allow user to "extend" past 91 days, but ask user first
- When routine reaches 91 days, prompt the practitioner
- Options: Mark as COMPLETED, or EXTEND for another cycle
- This gives flexibility while maintaining the 91-day milestone
- **Implementation note**: Will ask user about extension behavior during routine completion flow

### 5. Deleting Entries ✅
**Decision**: Option B - Yes, always
- Practitioners can delete HabitEntry records at any time
- Supports correcting mistakes and maintaining data accuracy
- Deletion will recalculate streaks (via StreakCalculationService)
- Audit trail may be added later if needed

---

## What's Different from V0.1?

**Old Model**:
- User → Category → Habit → HabitEntry
- Habit contained everything (tracking + definition)

**New Model** (Better!):
- HabitPractitioner → Habit (definition) → Routine (tracking) → HabitEntry
- **Separation**: Definition vs Commitment vs Completion
- **Sharing**: Habits can be public/copied
- **Multiple attempts**: Can try the same habit multiple times
- **One active routine**: Only one active routine per habit at a time (focused commitment)

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
