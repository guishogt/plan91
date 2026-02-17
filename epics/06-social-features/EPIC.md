# Epic 06: Social Features

## Overview
Add social features that allow practitioners to discover, copy, and share habits with the community.

## Goals
- Enable copying of public habits
- Create practitioner profile pages
- Add social discovery features
- Build community engagement

## Tickets

### Backend
1. **Copy Habit Use Case** - Copy a public habit to practitioner's library
2. **Practitioner Profile Query** - Get practitioner public info and stats
3. **Public Routines Query** - View completed public routines
4. **Habit Stats Aggregation** - Track how many times a habit has been copied

### API Layer
5. **Profile API Endpoint** - GET /api/practitioners/{id}
6. **Copy Habit Endpoint** - POST /api/habits/{id}/copy
7. **Public Stats Endpoint** - GET /api/habits/{id}/stats

### Frontend
8. **Practitioner Profile Page** - Show public habits and stats
9. **Improved Browse Page** - Add filtering and sorting
10. **Copy Habit Flow** - One-click copy from browse page
11. **Habit Detail Modal** - Show habit details before copying

## Architecture

### Copy Habit Flow
```
User clicks "Copy" on public habit
  ↓
POST /api/habits/{id}/copy
  ↓
CopyHabitUseCase validates:
  - Habit is public
  - User doesn't already have this habit
  ↓
Creates new habit with source_habit_id reference
  ↓
Returns new habit
  ↓
Redirects to "Start Routine" for new habit
```

### Practitioner Profile
```
Profile shows:
- Name, bio, timezone
- Public habits created
- Active public routines (optional)
- Stats: total habits created, total routines completed
```

## Implementation Plan

### Phase 1: Copy Habit Feature
1. Create CopyHabitUseCase
2. Add API endpoint
3. Update browse page UI
4. Test complete copy flow

### Phase 2: Practitioner Profiles
1. Add bio field to HabitPractitioner
2. Create profile query use case
3. Build profile page UI
4. Link from habits to creator profiles

### Phase 3: Discovery & Stats
1. Add habit popularity stats
2. Improve browse page with filters
3. Add "Trending" section
4. Show "X people are doing this habit"

## Testing Strategy

### Manual Tests
- Copy a public habit and verify it appears in "My Habits"
- Start routine from copied habit
- View practitioner profile
- Check stats are accurate

### Integration Tests
- Copy habit preserves all configuration
- Can't copy private habits
- Can't copy habit you already own
- Stats increment correctly

## Success Criteria
- ✓ Users can copy public habits
- ✓ Practitioner profiles display correctly
- ✓ Habit stats show copy count
- ✓ Browse page has filters
- ✓ No duplicate habits when copying

## Future Enhancements (Epic 10+)
- Following system
- Activity feed
- Comments on habits
- Habit collections/categories
- Recommended habits based on completion patterns
