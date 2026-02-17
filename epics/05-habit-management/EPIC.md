# Epic 05: Habit Management (Use Cases)

**Status**: 🚀 **IN PROGRESS**
**Priority**: Critical
**Estimated Duration**: 3-4 days
**Goal**: Complete habit and routine CRUD functionality (backend + frontend)

---

## Overview

This epic implements the core functionality of Plan 91: creating habits, starting routines, and completing daily entries. Users can manage their habit library and begin their 91-day commitments.

**Why This Epic Exists**:
- Habits are the foundation of Plan 91
- Users need to create and manage their own habits
- Routines represent 91-day commitments
- Daily entries track progress

---

## Objectives

1. Implement use cases (application layer)
   - Create habit
   - Start routine (91-day commitment)
   - Complete daily entry
   - Edit/delete habits
   - Query habits and routines

2. Create REST API endpoints
   - `/api/habits` - CRUD operations
   - `/api/routines` - Start/manage routines
   - `/api/entries` - Complete daily entries

3. Build frontend UI
   - Create habit form (multi-step)
   - Habit list page
   - Habit detail page
   - Start routine flow

---

## Success Criteria

- [ ] Users can create boolean habits (yes/no tracking)
- [ ] Users can create numeric habits (quantity tracking)
- [ ] Users can configure recurrence (daily, weekdays, custom)
- [ ] Users can start a 91-day routine for a habit
- [ ] Users can complete daily entries
- [ ] Streak tracking works correctly (one-strike rule)
- [ ] Users can view their habits and routines
- [ ] Users can edit/delete habits
- [ ] All operations persist to database

---

## Architecture

### Hexagonal Architecture Layers

```
┌─────────────────────────────────────────────────────────────┐
│                   Web Layer (Controllers)                    │
│  HabitController, RoutineController, EntryController        │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                Application Layer (Use Cases)                 │
│  - CreateHabitUseCase                                       │
│  - StartRoutineUseCase                                      │
│  - CompleteEntryUseCase                                     │
│  - QueryHabitsUseCase                                       │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                    Domain Layer                              │
│  Habit, Routine, HabitEntry (already complete!)            │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│              Persistence Layer (Repositories)                │
│  HabitJpaRepository, RoutineJpaRepository (Epic 04)         │
└─────────────────────────────────────────────────────────────┘
```

### Package Structure

```
src/main/java/com/ctoblue/plan91/
├── application/
│   └── usecase/
│       ├── habit/
│       │   ├── CreateHabitUseCase.java
│       │   ├── EditHabitUseCase.java
│       │   ├── DeleteHabitUseCase.java
│       │   └── QueryHabitsUseCase.java
│       ├── routine/
│       │   ├── StartRoutineUseCase.java
│       │   ├── CompleteEntryUseCase.java
│       │   └── QueryRoutinesUseCase.java
│       └── dto/
│           ├── HabitDto.java
│           ├── RoutineDto.java
│           └── EntryDto.java
├── adapter/in/web/
│   └── api/
│       ├── HabitController.java
│       ├── RoutineController.java
│       └── EntryController.java
└── adapter/out/persistence/
    └── adapter/
        ├── HabitPersistenceAdapter.java
        └── RoutinePersistenceAdapter.java
```

---

## Tickets

| Ticket | Title | Status | Effort |
|--------|-------|--------|--------|
| PLAN91-054 | Create Habit Use Case | 🔄 In Progress | Medium |
| PLAN91-055 | Start Routine Use Case | ⏳ Pending | Large |
| PLAN91-056 | Complete Entry Use Case | ⏳ Pending | Large |
| PLAN91-057 | Query Habits Use Case | ⏳ Pending | Small |
| PLAN91-058 | Edit/Delete Habit Use Cases | ⏳ Pending | Small |
| PLAN91-059 | Habit REST API Controller | ⏳ Pending | Medium |
| PLAN91-060 | Routine REST API Controller | ⏳ Pending | Medium |
| PLAN91-061 | DTOs and Mappers | ⏳ Pending | Medium |
| PLAN91-062 | Create Habit Form UI | ⏳ Pending | Large |
| PLAN91-063 | Habit List Page UI | ⏳ Pending | Medium |
| PLAN91-064 | Start Routine Flow UI | ⏳ Pending | Medium |

---

## Implementation Plan

### Phase 1: Backend (Use Cases + API)
1. ✅ Create use case interfaces (ports)
2. 🔄 Implement CreateHabitUseCase
3. ⏳ Implement StartRoutineUseCase
4. ⏳ Implement CompleteEntryUseCase
5. ⏳ Create REST controllers
6. ⏳ Test with Postman/curl

### Phase 2: Frontend (UI)
7. ⏳ Create habit form page
8. ⏳ Create habit list page
9. ⏳ Create start routine flow
10. ⏳ Integrate with backend API

---

## Dependencies

**Depends on**:
- Epic 01 (Domain Model) ✅ Complete
- Epic 04 (Infrastructure) ✅ Complete

**Blocks**:
- Epic 06 (Dashboard/Today View) - needs habits and routines to display

---

## Testing Strategy

### Backend Testing
- Unit tests for use cases (mock repositories)
- Integration tests with real database (Testcontainers)
- API endpoint tests (MockMvc)

### Frontend Testing
- Manual testing with browser
- HTMX interactions
- Form validation

---

## Notes

- Start with **backend first** (easier to test with curl/Postman)
- Use **ports and adapters** pattern (hexagonal architecture)
- **Domain model is already complete** from Epic 01
- **Repositories are ready** from Epic 04
- Focus on **application layer** (use cases) and **web layer** (controllers)

---

**Created**: 2026-02-01
**Last Updated**: 2026-02-01
