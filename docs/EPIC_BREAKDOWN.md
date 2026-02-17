# Plan 91 - Epic Breakdown & Tickets

**Version**: 1.0
**Last Updated**: 2026-01-29
**Total Tickets**: 139 (PLAN91-001 through PLAN91-139)

---

## Quick Reference

| Epic | Status | Tickets | Duration | Priority |
|------|--------|---------|----------|----------|
| [Epic 00](#epic-00-setup--documentation) | 🟢 In Progress (57%) | 001-007 | 2-3 days | Critical |
| [Epic 01](#epic-01-domain-model-with-standalone-testing) | ⏳ Pending | 008-019 | 3-4 days | Critical |
| [Epic 02](#epic-02-frontend-foundation) | ⏳ Pending | 020-028 | 2-3 days | High |
| [Epic 03](#epic-03-authentication) | ⏳ Pending | 029-040 | 2-3 days | Critical |
| [Epic 04](#epic-04-infrastructure--persistence) | ⏳ Pending | 041-053 | 2-3 days | High |
| [Epic 05](#epic-05-habit-management) | ⏳ Pending | 054-067 | 4-5 days | Critical |
| [Epic 06](#epic-06-dashboard-today-view) | ⏳ Pending | 068-078 | 3 days | Critical |
| [Epic 07](#epic-07-categories) | ⏳ Pending | 079-087 | 1-2 days | Medium |
| [Epic 08](#epic-08-analytics--statistics) | ⏳ Pending | 088-102 | 3-4 days | Medium |
| [Epic 09](#epic-09-milestones--achievements) | ⏳ Pending | 103-115 | 2 days | Medium |
| [Epic 10](#epic-10-pwa--polish) | ⏳ Pending | 116-129 | 3 days | Medium |
| [Epic 11](#epic-11-deployment--devops) | ⏳ Pending | 130-139 | 2 days | High |

**Total Duration**: ~30-40 days (6-8 weeks)

---

## Development Flow

```
Epic 0 (Setup) → Epic 1 (Model) → Epic 2 (Frontend) → Epic 3 (Auth) →
Epic 4 (Infrastructure) → Epics 5-9 (Features) → Epic 10 (Polish) → Epic 11 (Deploy)
```

**Key Changes from Original Plan**:
- Frontend moved earlier (Epic 2, was Epic 9)
- Authentication moved earlier (Epic 3)
- Domain model testing isolated (Epic 1, uses `main()` methods before DB)
- All tickets numbered sequentially (001-139)

See `docs/DECISIONS.md` for rationale (ADR-001).

---

## Epic 00: Setup & Documentation

**Status**: 🟢 In Progress (4/7 complete, 57%)
**Duration**: 2-3 days
**Priority**: Critical
**Goal**: Establish project foundation with documentation, diagrams, and structure

**Rationale**: Documentation-first approach ensures agents have clear context (ADR-003).

### Tickets

| # | Ticket | Status | Effort |
|---|--------|--------|--------|
| 001 | Create project documentation structure | ✅ Complete | Small |
| 002 | Add UML use case diagrams to docs | ✅ Complete | Small |
| 003 | Initialize Git repository with .gitignore | ✅ Complete | Small |
| 004 | Create Maven project structure | ✅ Complete | Medium |
| 005 | Set up hexagonal architecture package structure | 🔄 Next | Small |
| 006 | Create README.md with project overview | ✅ Partial | Small |
| 007 | Set up basic logging configuration | ⏳ Pending | Small |

**Deliverables**:
- ✅ Complete documentation (DOMAIN-MODEL.md, USE-CASES.md, DECISIONS.md)
- ✅ Project skeleton with Maven
- ✅ Git repository initialized
- ⏳ Hexagonal architecture packages

**Dependencies**: None (foundation epic)

**Files Created**:
- `docs/DOMAIN-MODEL.md`
- `docs/USE-CASES.md`
- `docs/DECISIONS.md`
- `docs/CURRENT_WORK.md`
- `pom.xml`
- `src/main/java/com/ctoblue/plan91/Plan91Application.java`

---

## Epic 01: Domain Model with Standalone Testing

**Status**: ⏳ Pending
**Duration**: 3-4 days
**Priority**: Critical
**Goal**: Create domain model classes with `main()` method testing (no database yet)

**Rationale**: Test business logic in isolation before infrastructure complexity (ADR-001, ADR-004).

### Tickets

| # | Ticket | Effort | Description |
|---|--------|--------|-------------|
| 008 | Create Value Objects (UserId, Email, HabitId, CategoryId) | Small | Base value objects with validation |
| 009 | Create RecurrenceRule value object with main() tests | Medium | Complex recurrence logic |
| 010 | Create NumericConfig value object | Small | Numeric tracking configuration |
| 011 | Create HabitStreak value object with main() tests | Medium | Streak calculation logic |
| 012 | Create User aggregate with main() tests | Small | User aggregate root |
| 013 | Create Category aggregate with main() tests | Small | Category aggregate |
| 014 | Create Habit aggregate (core) with main() tests | Large | Core habit aggregate |
| 015 | Create HabitEntry entity with validation tests | Small | Entry entity |
| 016 | Implement RecurrenceEvaluationService with main() tests | Large | Recurrence evaluation service |
| 017 | Implement StreakCalculationService with main() tests | Large | Streak calculation service |
| 018 | Test all domain business rules in standalone mode | Medium | Integration testing |
| 019 | Document domain model with additional Mermaid diagrams | Small | Update docs |

**Deliverables**:
- All domain classes working standalone (no Spring, no database)
- Testable via `java ClassName.java`
- Each class has `public static void main()` with sample data and assertions

**Dependencies**: Epic 00

**Key Feature**: Standalone testing with main() methods before adding database complexity

**Package Structure**:
```
com.ctoblue.plan91.domain/
├── user/
│   ├── User.java
│   ├── UserId.java
│   ├── Email.java
│   └── UserRepository.java (interface)
├── category/
│   ├── Category.java
│   ├── CategoryId.java
│   └── CategoryRepository.java
├── habit/
│   ├── Habit.java
│   ├── HabitEntry.java
│   ├── HabitId.java
│   ├── HabitStreak.java
│   ├── RecurrenceRule.java
│   ├── NumericConfig.java
│   ├── HabitRepository.java
│   ├── StreakCalculationService.java
│   └── RecurrenceEvaluationService.java
└── shared/
    ├── DomainEvent.java
    └── ValueObject.java
```

---

## Epic 02: Frontend Foundation

**Status**: ⏳ Pending
**Duration**: 2-3 days
**Priority**: High
**Goal**: Set up HTMX + Tailwind + base layouts for early UI testing

**Rationale**: Frontend available early enables visual testing and iteration (ADR-001).

### Tickets

| # | Ticket | Effort | Description |
|---|--------|--------|-------------|
| 020 | Configure Tailwind CSS with Plan 91 theme | Medium | Theme setup with blue palette |
| 021 | Integrate HTMX | Small | HTMX dependency and config |
| 022 | Create base Thymeleaf layout template | Medium | Base layout with nav |
| 023 | Create navigation component | Small | Top nav component |
| 024 | Create form components and utilities | Medium | Reusable form elements |
| 025 | Create loading states and skeleton loaders | Small | Loading UI patterns |
| 026 | Create toast/notification component | Small | Toast notifications |
| 027 | Create modal component | Medium | Modal dialog component |
| 028 | Create sample static dashboard page (mockup) | Medium | Visual mockup |

**Deliverables**:
- Reusable frontend components
- Sample pages to visualize design
- Static pages (will connect to backend in later epics)

**Dependencies**: Epic 00

**Note**: MOVED UP from original Epic 09 to enable early UI testing

---

## Epic 03: Authentication

**Status**: ⏳ Pending
**Duration**: 2-3 days
**Priority**: Critical
**Goal**: Auth0 integration, user registration, and authentication UI

**Rationale**: Authentication needed before building user-specific features (ADR-001).

### Tickets

| # | Ticket | Effort | Description |
|---|--------|--------|-------------|
| 029 | Auth0 tenant setup and configuration | Small | Create Auth0 account |
| 030 | Spring Security + Auth0 integration | Large | Auth0 SDK integration |
| 031 | JWT token handling and validation | Medium | JWT processing |
| 032 | User registration endpoint | Medium | Registration API |
| 033 | Login page UI | Small | Login form |
| 034 | Registration page UI | Small | Registration form |
| 035 | Password reset flow | Medium | Password reset |
| 036 | Profile/settings page UI | Medium | User profile page |
| 037 | Logout functionality | Small | Logout endpoint |
| 038 | Protected route handling | Medium | Route guards |
| 039 | Current user resolution from JWT | Medium | User from token |
| 040 | Timezone handling utilities | Small | Timezone support |

**Deliverables**:
- Working authentication system (UI + backend)
- Protected endpoints
- User profile management

**Dependencies**: Epic 00, Epic 02 (for UI components)

**Note**: MOVED EARLIER to establish user foundation before feature development

---

## Epic 04: Infrastructure & Persistence

**Status**: ⏳ Pending
**Duration**: 2-3 days
**Priority**: High
**Goal**: Spring Boot, JPA, MySQL, Flyway - connect domain to database

### Tickets

| # | Ticket | Effort | Description |
|---|--------|--------|-------------|
| 041 | Spring Boot project configuration | Small | Spring config |
| 042 | MySQL database setup with Docker Compose | Medium | Docker setup |
| 043 | Flyway migration for users table | Small | User schema |
| 044 | Flyway migration for categories table | Small | Category schema |
| 045 | Flyway migration for habits table | Medium | Habit schema |
| 046 | Flyway migration for habit_entries table | Medium | Entry schema |
| 047 | Flyway migration for milestones table | Small | Milestone schema |
| 048 | Create JPA entities (UserEntity, CategoryEntity, etc.) | Large | JPA entities |
| 049 | Create MapStruct mappers (domain ↔ entity) | Large | Mapping layer |
| 050 | Implement JPA repositories | Medium | Repository layer |
| 051 | Convert domain main() tests to JUnit + Testcontainers | Large | Convert tests |
| 052 | Application properties configuration | Small | Config files |
| 053 | Logging and error handling setup | Medium | Logging setup |

**Deliverables**:
- Spring Boot app connected to MySQL
- Domain objects persisted
- Proper JUnit tests replacing main() methods

**Dependencies**: Epic 01 (domain model), Epic 03 (auth setup)

**Note**: Remove `main()` methods from domain classes, replace with proper JUnit tests

---

## Epic 05: Habit Management

**Status**: ⏳ Pending
**Duration**: 4-5 days
**Priority**: Critical
**Goal**: Complete habit CRUD functionality (backend + frontend)

### Tickets

| # | Ticket | Effort | Description |
|---|--------|--------|-------------|
| 054 | Create habit use case | Medium | Create habit logic |
| 055 | Edit habit use case | Medium | Update habit |
| 056 | Delete habit use case | Small | Delete habit |
| 057 | Complete habit entry use case | Large | Mark complete |
| 058 | Query habit details use case | Small | Get habit |
| 059 | Habit REST API endpoints | Large | REST controllers |
| 060 | DTOs and request/response mappers | Medium | API DTOs |
| 061 | API error handling | Medium | Error responses |
| 062 | Create habit form UI (multi-step) | Large | Create form |
| 063 | Recurrence rule UI component | Large | Recurrence UI |
| 064 | Numeric tracking configuration UI | Medium | Numeric config |
| 065 | Edit habit form UI | Medium | Edit form |
| 066 | Delete habit confirmation UI | Small | Delete confirm |
| 067 | Habit detail view page | Medium | Detail page |

**Deliverables**:
- Full habit management (create, edit, delete, view)
- UI and API complete
- Core business logic working

**Dependencies**: Epic 04

---

## Epic 06: Dashboard (Today View)

**Status**: ⏳ Pending
**Duration**: 3 days
**Priority**: Critical
**Goal**: Main user interface for daily habit tracking

### Tickets

| # | Ticket | Effort | Description |
|---|--------|--------|-------------|
| 068 | Dashboard backend - today's habits endpoint | Medium | Today's API |
| 069 | Dashboard layout and structure | Medium | Dashboard layout |
| 070 | Habit card component | Medium | Card component |
| 071 | Complete button interaction (HTMX) | Medium | Complete button |
| 072 | Add note modal | Small | Note entry |
| 073 | Add numeric value input | Small | Value input |
| 074 | Strike warning banner | Small | Strike warning |
| 075 | Streak display component | Small | Streak display |
| 076 | Progress bar (X/91 days) | Small | Progress bar |
| 077 | Category filtering | Medium | Filter habits |
| 078 | Empty states | Small | Empty UI |

**Deliverables**:
- Fully functional dashboard for daily tracking
- HTMX interactions
- Strike system visible

**Dependencies**: Epic 05

---

## Epic 07: Categories

**Status**: ⏳ Pending
**Duration**: 1-2 days
**Priority**: Medium
**Goal**: User-defined category management

### Tickets

| # | Ticket | Effort | Description |
|---|--------|--------|-------------|
| 079 | Category use cases (create, update, delete, list) | Medium | Category logic |
| 080 | Category REST API endpoints | Small | Category API |
| 081 | Category validation rules | Small | Validation |
| 082 | Category list view UI | Small | List page |
| 083 | Create category modal | Small | Create UI |
| 084 | Edit category modal | Small | Edit UI |
| 085 | Delete category confirmation | Small | Delete UI |
| 086 | Color picker component | Medium | Color picker |
| 087 | Emoji/icon selector | Medium | Icon selector |

**Deliverables**:
- Full category CRUD (backend + UI)
- Color and icon selection

**Dependencies**: Epic 04, Epic 05

---

## Epic 08: Analytics & Statistics

**Status**: ⏳ Pending
**Duration**: 3-4 days
**Priority**: Medium
**Goal**: Habit statistics, charts, and insights

### Tickets

| # | Ticket | Effort | Description |
|---|--------|--------|-------------|
| 088 | Habit statistics calculation service | Large | Stats service |
| 089 | Category-based analytics aggregation | Medium | Category stats |
| 090 | Chart data formatting (for Chart.js) | Medium | Chart data |
| 091 | Calendar heatmap data generation | Medium | Heatmap data |
| 092 | Analytics REST endpoints | Medium | Analytics API |
| 093 | Dashboard summary endpoint | Small | Summary API |
| 094 | Stats dashboard layout UI | Medium | Stats layout |
| 095 | Habit selector dropdown | Small | Habit selector |
| 096 | Key metrics cards | Small | Metrics cards |
| 097 | Chart.js integration | Medium | Chart setup |
| 098 | Line chart for numeric habits | Medium | Line chart |
| 099 | Calendar heatmap component | Large | Heatmap UI |
| 100 | Category breakdown visualization | Medium | Category viz |
| 101 | Date range selector | Small | Date picker |
| 102 | Export data feature (optional) | Medium | Data export |

**Deliverables**:
- Complete analytics dashboard
- Charts and statistics
- Data export

**Dependencies**: Epic 05, Epic 07

---

## Epic 09: Milestones & Achievements

**Status**: ⏳ Pending
**Duration**: 2 days
**Priority**: Medium
**Goal**: 91-day milestone tracking and celebration

### Tickets

| # | Ticket | Effort | Description |
|---|--------|--------|-------------|
| 103 | Milestone domain model | Small | Milestone entity |
| 104 | Milestone repository | Small | Milestone repo |
| 105 | Milestone achievement detection service | Medium | Detection logic |
| 106 | Weekly milestone triggers (7, 14, 21...) | Small | Weekly triggers |
| 107 | Monthly milestone triggers (30, 60) | Small | Monthly triggers |
| 108 | Day 91 completion milestone | Medium | 91-day logic |
| 109 | Milestone REST endpoints | Small | Milestone API |
| 110 | Milestone notification system UI | Medium | Notification UI |
| 111 | Achievement modal/card | Medium | Achievement UI |
| 112 | Milestone history view | Small | History page |
| 113 | 91-day completion flow UI | Medium | Completion flow |
| 114 | Continue vs. restart decision UI | Small | Decision UI |
| 115 | Shareable achievement card (optional) | Medium | Share card |

**Deliverables**:
- Milestone system with celebration UI
- Achievement notifications

**Dependencies**: Epic 05

---

## Epic 10: PWA & Polish

**Status**: ⏳ Pending
**Duration**: 3 days
**Priority**: Medium
**Goal**: Make app installable, accessible, and polished

### Tickets

| # | Ticket | Effort | Description |
|---|--------|--------|-------------|
| 116 | PWA manifest.json configuration | Small | PWA manifest |
| 117 | Service worker implementation | Large | Service worker |
| 118 | Offline page/fallback | Small | Offline page |
| 119 | Install prompt UI | Small | Install prompt |
| 120 | Caching strategy for assets | Medium | Cache strategy |
| 121 | Background sync for completion | Medium | Background sync |
| 122 | App icon assets (all sizes) | Small | App icons |
| 123 | Loading states across all interactions | Medium | Loading states |
| 124 | Error handling and user feedback | Medium | Error handling |
| 125 | Responsive design (mobile, tablet, desktop) | Large | Responsive |
| 126 | Accessibility (ARIA labels, keyboard nav) | Medium | A11y |
| 127 | Performance optimization | Medium | Performance |
| 128 | Animation and transitions | Medium | Animations |
| 129 | Dark mode (optional) | Medium | Dark theme |

**Deliverables**:
- Polished, installable PWA
- Accessible and responsive
- Offline support

**Dependencies**: All feature epics (05-09)

---

## Epic 11: Deployment & DevOps

**Status**: ⏳ Pending
**Duration**: 2 days
**Priority**: High
**Goal**: Deploy to Railway with CI/CD

### Tickets

| # | Ticket | Effort | Description |
|---|--------|--------|-------------|
| 130 | Production Docker configuration | Medium | Docker setup |
| 131 | Railway project setup | Small | Railway config |
| 132 | MySQL on Railway | Small | DB setup |
| 133 | Environment variables management | Small | Env vars |
| 134 | Auth0 production configuration | Small | Auth0 prod |
| 135 | GitHub Actions CI/CD pipeline | Large | CI/CD |
| 136 | Database backup strategy | Medium | Backups |
| 137 | Monitoring and logging | Medium | Monitoring |
| 138 | Domain and SSL setup | Small | Domain setup |
| 139 | Production smoke tests | Medium | Smoke tests |

**Deliverables**:
- Live production application on Railway
- CI/CD pipeline
- Monitoring and backups

**Dependencies**: All epics complete

---

## Progress Tracking

**Completed Tickets**: 4/139 (3%)
- ✅ PLAN91-001: Documentation structure
- ✅ PLAN91-002: UML diagrams
- ✅ PLAN91-003: Git setup
- ✅ PLAN91-004: Maven project

**Current Ticket**: PLAN91-005 (Hexagonal architecture packages)

**Next Epic**: Epic 01 (Domain Model) - starts with PLAN91-008

---

## References

- **Full Specification**: `PLAN91_SPECIFICATION.md`
- **Current Work**: `docs/CURRENT_WORK.md`
- **Domain Model**: `docs/DOMAIN-MODEL.md`
- **Use Cases**: `docs/USE-CASES.md`
- **Decisions**: `docs/DECISIONS.md`

---

**Last Updated**: 2026-01-29
**Next Review**: After Epic 00 completion
