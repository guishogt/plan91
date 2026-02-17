# Epic 07: Routine Progress & Visualization

## Overview
Add visual progress tracking and analytics to help users understand their 91-day journey and stay motivated.

## Goals
- Visualize completion history with calendar views
- Show progress metrics and statistics
- Display streak information prominently
- Provide motivating insights and feedback

## Tickets

### Backend
1. **Get Entry History Use Case** - Retrieve all entries for a routine
2. **Calculate Statistics Use Case** - Compute completion rates, patterns
3. **Get Calendar Data Use Case** - Format entries for calendar display
4. **Progress Analytics Endpoint** - GET /api/routines/{id}/analytics

### Frontend
5. **Calendar View Component** - Monthly calendar showing completions
6. **Progress Ring/Bar Component** - Visual progress indicator (X/91 days)
7. **Streak Display Component** - Current and longest streak badges
8. **Stats Cards** - Completion rate, consistency score
9. **Enhanced Dashboard** - Integrate all visualization components
10. **Routine Detail Page** - Dedicated page for each routine

## Features

### 1. Calendar View
```
Shows monthly view with:
- Completed days (green)
- Missed days (red, if broke streak)
- Scheduled days (blue outline)
- Today (bold border)
- Can navigate between months
```

### 2. Progress Visualization
```
- Progress ring: X/91 days complete
- Percentage complete
- Days remaining
- Expected completion date
```

### 3. Streak Information
```
- Current streak: 🔥 15 days
- Longest streak: ⭐ 25 days
- Total completions: 40 days
- Strike status: Available/Used
```

### 4. Statistics
```
- Completion rate: 85%
- Weekly average: 6.2 days
- Consistency score: A+
- Best day of week: Monday
- Current pace: On track/Behind/Ahead
```

### 5. Insights & Motivation
```
- "You're on a roll! 15 days straight!"
- "Just 51 days to go!"
- "You complete 95% on Mondays"
- "Milestone: 30 days achieved! 🎉"
```

## Architecture

### Calendar Data Structure
```json
{
  "routineId": "uuid",
  "month": "2026-02",
  "entries": [
    {
      "date": "2026-02-01",
      "completed": true,
      "value": null,
      "notes": "..."
    }
  ],
  "scheduledDays": ["2026-02-01", "2026-02-02", ...],
  "stats": {
    "totalDays": 28,
    "completedDays": 20,
    "completionRate": 71.4
  }
}
```

### Analytics Endpoint Response
```json
{
  "routine": {...},
  "progress": {
    "daysCompleted": 40,
    "totalDays": 91,
    "percentage": 43.96,
    "daysRemaining": 51
  },
  "streaks": {
    "current": 15,
    "longest": 25,
    "hasUsedStrike": false
  },
  "completionRate": 85.0,
  "weeklyAverage": 6.2,
  "consistencyScore": "A+",
  "pace": "on_track"
}
```

## Implementation Plan

### Phase 1: Backend Analytics (30 min)
1. Create GetRoutineAnalyticsUseCase
2. Create GetCalendarDataUseCase
3. Add analytics endpoint to RoutineController
4. Test with existing routine data

### Phase 2: Calendar Component (45 min)
1. Create calendar.js component
2. Monthly grid with day cells
3. Color coding for completion status
4. Month navigation
5. Integrate into routine detail page

### Phase 3: Progress Components (30 min)
1. Progress ring SVG component
2. Streak badges with icons
3. Stats cards layout
4. Responsive design

### Phase 4: Routine Detail Page (30 min)
1. Create routine detail page template
2. Integrate all visualization components
3. Add notes/history section
4. Update navigation links

## Success Criteria
- ✓ Calendar displays completion history accurately
- ✓ Progress metrics match database state
- ✓ Streak calculations are correct
- ✓ Visual components are responsive
- ✓ Page loads quickly with data
- ✓ Users can navigate months easily

## Testing Strategy

### Manual Tests
- Complete entries for various dates
- Check calendar displays correctly
- Verify streak calculations
- Test month navigation
- Check responsive layout

### Edge Cases
- Routine just started (day 1)
- Routine almost complete (day 90)
- Routine with strike used
- Routine with gaps in completion
- Different recurrence patterns

## Future Enhancements (Epic 09+)
- Heatmap visualization (GitHub-style)
- Export progress as PDF/image
- Compare multiple routines
- Predictive completion likelihood
- Habit correlation analysis
- Share progress on social media
