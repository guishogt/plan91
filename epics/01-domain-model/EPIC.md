# Epic 01: Domain Model with Standalone Testing

**Status**: ✅ **COMPLETE** (7/7 tickets complete, 100%)
**Priority**: Critical
**Estimated Duration**: 3-4 days
**Goal**: Implement complete domain model with standalone `main()` testing before database

---

## Overview

This epic implements the core domain model based on the approved DOMAIN-SCHEMA.md (v1.0). All domain classes will be pure Java with no framework dependencies, testable via `public static void main()` methods.

**Why This Epic Exists** (per ADR-004):
- Domain logic must be validated before infrastructure
- `main()` methods allow immediate testing without Spring/DB
- Clear separation between domain and infrastructure concerns
- Supports test-driven development approach

---

## Domain Model Overview

```
HabitPractitioner (The Person)
    ↓ creates
Habit (The Definition)
    ↓ used in
Routine (The 91-day Commitment)
    ↓ has
HabitEntry (Individual Completion)
```

---

## Objectives

1. Create Value Objects with validation
2. Implement Habit aggregate with business rules
3. Implement Routine aggregate with streak tracking
4. Implement HabitEntry entity
5. Create Domain Services for complex logic
6. Test everything via standalone `main()` methods
7. Validate one-strike rule implementation
8. Ensure 91-day cycle logic is correct

---

## Success Criteria

- [ ] All value objects created with validation
- [ ] All aggregates implemented with business rules
- [ ] All domain services created
- [ ] Each class has working `main()` test method
- [ ] One-strike rule logic tested and verified
- [ ] Streak calculation tested with edge cases
- [ ] Recurrence rule evaluation tested
- [ ] No framework dependencies in domain layer
- [ ] Code compiles with `mvn clean compile`

---

## Tickets

| Ticket | Title | Status | Effort |
|--------|-------|--------|--------|
| PLAN91-008 | Create Value Objects (HabitPractitionerId, Email, HabitId, RoutineId, etc.) | ✅ Complete | Medium |
| PLAN91-009 | Create HabitPractitioner aggregate | ✅ Complete | Medium |
| PLAN91-010 | Create Habit aggregate with public/private logic | ✅ Complete | Medium |
| PLAN91-011 | Create Routine aggregate with streak tracking | ✅ Complete | Large |
| PLAN91-012 | Create HabitEntry entity | ✅ Complete | Small |
| PLAN91-014 | Create Domain Services (RoutineProgress, RecurrenceCalculator) | ✅ Complete | Large |
| PLAN91-015 | Test one-strike rule with main() scenarios | ✅ Complete | Medium |

---

## Dependencies

**Depends on**: Epic 00 (Setup) ✅ Complete

---

## Deliverables

1. **Value Objects** (Immutable, validated)
   - HabitPractitionerId
   - Email
   - HabitId
   - RoutineId
   - HabitEntryId
   - CategoryId
   - HabitStreak
   - RecurrenceRule
   - NumericConfig

2. **Aggregates**
   - HabitPractitioner (root)
   - Habit (root)
   - Routine (root)

3. **Entities**
   - HabitEntry

4. **Domain Services**
   - StreakCalculationService
   - RecurrenceEvaluationService

5. **Repository Interfaces** (Ports)
   - HabitPractitionerRepository
   - HabitRepository
   - RoutineRepository
   - HabitEntryRepository

---

## Package Structure

```
src/main/java/com/ctoblue/plan91/domain/
├── habitpractitioner/
│   ├── HabitPractitioner.java
│   ├── HabitPractitionerId.java
│   ├── Email.java
│   └── HabitPractitionerRepository.java
├── habit/
│   ├── Habit.java
│   ├── HabitId.java
│   ├── NumericConfig.java
│   └── HabitRepository.java
├── routine/
│   ├── Routine.java
│   ├── RoutineId.java
│   ├── HabitStreak.java
│   ├── RecurrenceRule.java
│   ├── RoutineRepository.java
│   └── service/
│       ├── StreakCalculationService.java
│       └── RecurrenceEvaluationService.java
├── habitentry/
│   ├── HabitEntry.java
│   ├── HabitEntryId.java
│   └── HabitEntryRepository.java
└── shared/
    ├── DomainException.java
    └── ValidationException.java
```

---

## Testing Approach (ADR-004)

Each domain class will have a `public static void main(String[] args)` method:

```java
public class HabitStreak {
    // ... fields and methods

    public static void main(String[] args) {
        System.out.println("Testing HabitStreak...");

        // Test 1: New streak
        HabitStreak newStreak = new HabitStreak(0, 0, 0, false, null);
        assert newStreak.getCurrentStreak() == 0 : "New streak should be 0";
        System.out.println("✓ New streak test passed");

        // Test 2: Increment streak
        HabitStreak incremented = newStreak.incrementStreak();
        assert incremented.getCurrentStreak() == 1 : "Incremented streak should be 1";
        System.out.println("✓ Increment test passed");

        // Test 3: Use strike
        HabitStreak withStrike = incremented.useStrike(LocalDate.now());
        assert withStrike.hasUsedStrike() : "Should have used strike";
        System.out.println("✓ Strike test passed");

        System.out.println("✅ All HabitStreak tests passed!");
    }
}
```

---

## Key Business Rules to Implement

### One-Strike Rule
- First miss: Strike used, streak preserved
- Second miss: Routine fails, streak resets to 0
- Strike is per-routine (not per habit)

### Streak Calculation
- Only counts completions on "expected days" per recurrence
- Consecutive completions increment streak
- Missing an expected day triggers strike or reset
- Completing on non-expected days doesn't count for streak

### 91-Day Cycle
- `startDate` is immutable once set
- `expectedEndDate = startDate + 91 days`
- After 91 days, routine auto-completes
- User can create new routine for same habit

### Habit Copying
- Public habits can be copied by others
- Copy creates new Habit with `sourceHabit` reference
- Copy belongs to new creator
- Changes to copy don't affect original

---

## Notes for Developers

- **No Spring annotations** in domain layer
- **No JPA annotations** in domain layer
- **Immutable value objects** (use records where appropriate)
- **Aggregates control invariants** (validate in constructor)
- **Test everything** via `main()` before moving to infrastructure
- **Use Java 21 features**: records, pattern matching, switch expressions

---

## References

- docs/DOMAIN-SCHEMA.md (v1.0)
- ADR-004: Domain Model Testing with main() Methods
- Hexagonal Architecture: Domain layer has no outward dependencies

---

**Created**: 2026-01-30
**Last Updated**: 2026-01-31
