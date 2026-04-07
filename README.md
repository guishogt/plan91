# Plan 91 - Habit Tracking Application

A minimalist habit tracking application built on the principle of sustainable behavior change through the "one-strike" rule and 91-day commitment cycles.

**Live URL**: https://plan91-app-production.up.railway.app

---

## For AI Agents / Bots

> **Start here if you're an AI assistant working on this codebase.**

### Essential Reading Order

1. **[CLAUDE.md](./CLAUDE.md)** - Critical deployment rules, database backup commands, common issues
2. **[PLAN91_SPECIFICATION.md](./PLAN91_SPECIFICATION.md)** - Full product specification and business rules
3. **[docs/DOMAIN-MODEL.md](./docs/DOMAIN-MODEL.md)** - Domain entities and relationships
4. **[docs/USE-CASES.md](./docs/USE-CASES.md)** - User flows and use case diagrams

### Project Architecture

```
src/main/java/com/ctoblue/plan91/
├── adapter/
│   ├── in/web/           # Controllers, DTOs, REST endpoints
│   │   ├── controller/   # @RestController classes
│   │   ├── dto/          # Request/Response DTOs
│   │   └── mapper/       # MapStruct mappers
│   └── out/persistence/  # Database layer
│       ├── entity/       # JPA entities (@Entity)
│       ├── repository/   # Spring Data repositories
│       └── mapper/       # Entity <-> Domain mappers
├── application/
│   └── usecase/          # Business use cases
├── domain/               # Pure domain logic (no Spring dependencies)
│   └── routine/          # Routine aggregate (streaks, one-strike rule)
└── infrastructure/
    └── security/         # Spring Security config, filters
```

### Key Files to Know

| File | Purpose |
|------|---------|
| `HabitEntryController.java` | Entry CRUD (mark complete, skip, uncomplete) |
| `RoutineController.java` | Routine CRUD, start/edit/delete |
| `dashboard-loader.js` | Main dashboard UI logic (routine cards, modals) |
| `auth.js` | Authentication helpers, CSRF handling |
| `SecurityConfig.java` | Spring Security rules, session config |
| `application.yml` | App config, datasource, session settings |
| `db/migration/V*.sql` | Flyway migrations (currently at V13) |

### Database Schema (Key Tables)

- `users` - User accounts (email, password hash)
- `habit_practitioners` - User profiles linked to habits
- `habits` - Habit definitions (name, tracking type)
- `routines` - Active habit routines (start date, streaks, one-strike status)
- `habit_entries` - Daily completions (`completed` boolean, `notes`, `value`)

### Current Features

- User registration/login with session auth + Remember Me (30 days)
- Create routines (91-day) or trackers (no end date)
- Mark habits complete with optional notes
- Skip habits with reason ("Didn't do it" feature)
- Date navigation (view/edit up to 5 days back)
- Streak tracking with one-strike forgiveness rule
- Expected end date display on routine cards

### Deployment

- **Platform**: Railway (Dockerfile-based)
- **Database**: MySQL 9.4 on Railway
- **Deploy command**: `railway up --detach`
- **CRITICAL**: Always backup database before deploying (see CLAUDE.md)

### API Endpoints (Main)

```
POST   /api/entries              # Mark habit complete
POST   /api/entries/skip         # Record skip with note
DELETE /api/entries              # Uncomplete an entry
GET    /api/entries/status       # Get completed/skipped status for date
GET    /api/routines/active      # Get user's active routines
POST   /api/routines             # Create new routine
```

---

## Overview

Plan 91 helps you build lasting habits with:
- **91-day cycles**: Based on proven habit formation methodology
- **One-strike rule**: Forgiving but accountable - miss once, but not twice
- **Trackers**: Simple counters without the 91-day commitment
- **Minimalist design**: Clean, distraction-free interface

## Tech Stack

### Backend
- **Language**: Java 17
- **Framework**: Spring Boot 3.2.2, Spring Security 6
- **Database**: MySQL 9.4
- **Migrations**: Flyway
- **Mapping**: MapStruct

### Frontend
- **Templates**: Thymeleaf (server-side rendering)
- **Interactivity**: HTMX
- **Styling**: Tailwind CSS
- **JavaScript**: Vanilla JS

### Infrastructure
- **Hosting**: Railway
- **Build**: Docker (multi-stage)

## Local Development

### Prerequisites
- Java 17
- Maven 3.9+
- Docker & Docker Compose (for MySQL)

### Setup

```bash
# Start MySQL
docker-compose up -d

# Run the app (dev profile)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Access at http://localhost:8080
```

### Build CSS (Tailwind)

```bash
npm install
npm run build:css
```

## Core Concepts

### The One-Strike Rule

> "Humans are imperfect. One mistake shouldn't destroy progress."

- **Normal state**: Tracking as expected
- **Strike active**: Missed one day - you get a warning
- **Streak broken**: Missed two consecutive expected days

### Routine Types

- **ROUTINE**: 91-day commitment with progress tracking and end date
- **TRACKER**: Simple counter, no end date, no one-strike rule

### Tracking Types

- **BOOLEAN**: Simple done/not done
- **NUMERIC**: Track a value (pages read, minutes exercised, etc.)

## Documentation

- **[CLAUDE.md](./CLAUDE.md)** - AI agent instructions & deployment rules
- **[PLAN91_SPECIFICATION.md](./PLAN91_SPECIFICATION.md)** - Full product spec
- **[docs/DOMAIN-MODEL.md](./docs/DOMAIN-MODEL.md)** - DDD model diagrams
- **[docs/USE-CASES.md](./docs/USE-CASES.md)** - User journeys
- **[docs/EPIC_BREAKDOWN.md](./docs/EPIC_BREAKDOWN.md)** - Development epics

---

**Version**: 1.0.0
**Last Updated**: 2026-02-20
**Maintainer**: Luis Martinez (CTOBlue)
