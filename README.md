# Plan 91 - Habit Tracking Application

A minimalist habit tracking application built on the principle of sustainable behavior change through the "one-strike" rule and 91-day commitment cycles.

## Overview

Plan 91 helps you build lasting habits with:
- **91-day cycles**: Based on proven habit formation methodology
- **One-strike rule**: Forgiving but accountable - miss once, but not twice
- **Minimalist design**: Clean, distraction-free interface
- **Data-driven insights**: Charts and analytics to visualize progress
- **PWA support**: Install on any device, work offline

## Tech Stack

### Backend
- **Language**: Java 21 (LTS)
- **Framework**: Spring Boot 4
- **Architecture**: Domain-Driven Design (DDD) with Hexagonal Architecture
- **Security**: Spring Security + Auth0
- **Database**: MySQL 8.0
- **Migrations**: Flyway
- **Mapping**: MapStruct
- **Testing**: JUnit 5, Testcontainers

### Frontend
- **Templates**: Thymeleaf (server-side rendering)
- **Interactivity**: HTMX
- **Styling**: Tailwind CSS
- **JavaScript**: Vanilla JS (minimal)
- **Charts**: Chart.js

### Infrastructure
- **Containerization**: Docker & Docker Compose
- **Hosting**: Railway
- **CI/CD**: GitHub Actions
- **Auth Provider**: Auth0

## Project Structure

```
plan91/
├── docs/                    # Documentation
│   ├── PLAN91_SPECIFICATION.md  # Master specification
│   ├── DOMAIN-MODEL.md      # Domain model with diagrams
│   ├── USE-CASES.md         # UML use case diagrams
│   ├── DECISIONS.md         # Architecture Decision Records
│   └── CURRENT_WORK.md      # Agent coordination (what to work on now)
│
├── epics/                   # Work breakdown
│   ├── 00-setup/           # Documentation and project setup
│   ├── 01-domain-model/    # Core business logic
│   ├── 02-frontend/        # UI components
│   └── ...                 # (11 epics total)
│
├── src/
│   ├── main/
│   │   ├── java/com/ctoblue/plan91/
│   │   │   ├── domain/         # Pure business logic (DDD)
│   │   │   ├── application/    # Use cases
│   │   │   ├── infrastructure/ # External adapters (DB, Auth0)
│   │   │   └── interfaces/     # Entry points (REST, Web)
│   │   └── resources/
│   │       ├── templates/      # Thymeleaf templates
│   │       ├── static/         # CSS, JS, images
│   │       └── db/migration/   # Flyway SQL scripts
│   └── test/
│
├── docker/
│   └── docker-compose.yml
│
├── pom.xml                  # Maven configuration
└── README.md               # This file
```

## Quick Start

> **Note**: Full setup instructions will be added as the project progresses.

### Prerequisites
- Java 21
- Maven 3.9+
- Docker & Docker Compose
- Auth0 account (for authentication)

### Local Development

```bash
# Clone the repository
git clone https://github.com/yourusername/plan91.git
cd plan91

# Start MySQL via Docker Compose
docker-compose up -d

# Run the application
./mvnw spring-boot:run

# Access the app
open http://localhost:8080
```

### Run Tests

```bash
# Unit tests
./mvnw test

# Integration tests (with Testcontainers)
./mvnw verify
```

## Development Workflow

This project uses **agent-driven development** with AI assistants (Claude Code, Cursor, etc.).

### For AI Agents

1. Read `/docs/CURRENT_WORK.md` to find the current ticket
2. Read the ticket file (e.g., `/epics/00-setup/tickets/PLAN91-001.md`)
3. Read referenced documentation (SPECIFICATION, DOMAIN-MODEL)
4. Implement according to acceptance criteria
5. Update CURRENT_WORK.md when complete

### For Human Developers

- All tickets are self-contained with full context
- Epic breakdown in `/docs/PLAN91_SPECIFICATION.md`
- Sequential ticket numbering: PLAN91-001, PLAN91-002, etc.
- See `/docs/DECISIONS.md` for architectural decisions

## Epic Overview

| Epic | Focus | Status |
|------|-------|--------|
| 00 | Setup & Documentation | 🟢 In Progress |
| 01 | Domain Model (standalone testing) | ⏳ Pending |
| 02 | Frontend Foundation | ⏳ Pending |
| 03 | Authentication | ⏳ Pending |
| 04 | Infrastructure & Persistence | ⏳ Pending |
| 05 | Habit Management | ⏳ Pending |
| 06 | Dashboard | ⏳ Pending |
| 07 | Categories | ⏳ Pending |
| 08 | Analytics | ⏳ Pending |
| 09 | Milestones | ⏳ Pending |
| 10 | PWA & Polish | ⏳ Pending |
| 11 | Deployment | ⏳ Pending |

## Core Concepts

### The One-Strike Rule

> "Humans are imperfect. One mistake shouldn't destroy progress."

- **Normal state**: Tracking as expected
- **Strike active**: Missed one day - you get a warning
- **Streak broken**: Missed two consecutive expected days

Completing a habit after using a strike clears the strike.

### 91-Day Cycles

Based on the "Plan 91" methodology - 13 weeks of sustained habit formation.

**Milestones**:
- Weekly: Days 7, 14, 21, 28, 35, 42, 49, 56, 63, 70, 77, 84
- Monthly: Days 30, 60
- Final: Day 91

Upon completion, choose to:
1. **Continue**: Keep tracking past 91 days
2. **Restart**: Archive and start a fresh cycle

### Recurrence Rules

Flexible scheduling:
- Daily (all days)
- Weekdays only (Mon-Fri)
- Weekends only (Sat-Sun)
- Specific days (any combination)
- Nth day of month (e.g., "First Monday")

### Tracking Types

- **Boolean**: Simple done/not done
- **Numeric**: Track a value (pages, minutes, glasses of water, etc.)

## Documentation

- **[Full Specification](docs/PLAN91_SPECIFICATION.md)** - Complete product spec and epic breakdown
- **[Domain Model](docs/DOMAIN-MODEL.md)** - DDD model with Mermaid diagrams
- **[Use Cases](docs/USE-CASES.md)** - UML diagrams and user journeys
- **[Decisions Log](docs/DECISIONS.md)** - Why we made certain choices (ADRs)
- **[Current Work](docs/CURRENT_WORK.md)** - What agents should work on now

## Design Principles

- **Minimalist UI**: Clean typography, generous whitespace
- **Mobile-first**: Touch-optimized, responsive design
- **Progressive enhancement**: Works without JavaScript
- **Accessibility**: WCAG AA compliance
- **Performance**: Server-side rendering, optimized assets

## Contributing

> Contribution guidelines will be added as the project matures.

For now:
1. Follow the ticket-based workflow
2. Reference ticket IDs in commits (e.g., `feat: add habit model [PLAN91-014]`)
3. Ensure all tests pass before committing
4. Update documentation when making architectural changes

## License

TBD

---

**Version**: 0.1.0 (Pre-Alpha)
**Last Updated**: 2026-01-29
**Maintainer**: Luis Martinez (CTOBlue)
