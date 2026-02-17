# Current Work Context for AI Agents

This file tells agents what to work on and provides necessary context.

## Current Epic
**Epic 00: Setup & Documentation**

## Current Ticket
**PLAN91-003** - Initialize Git Repository with .gitignore
(Path: `/epics/00-setup/tickets/PLAN91-003.md`)

## Instructions for AI Agents

When starting work:
1. Read this file first to understand current focus
2. Read `/docs/SPECIFICATION.md` for overall system understanding
3. Read `/docs/DOMAIN-MODEL.md` for domain knowledge
4. Read `/epics/{current-epic}/EPIC.md` for epic context
5. Read the specific ticket file referenced above
6. Implement according to ticket acceptance criteria
7. Update this file when you complete a ticket

## Context Files Priority (Read in Order)
1. `CURRENT_WORK.md` (this file) - What to do now
2. `docs/PLAN91_SPECIFICATION.md` - Master specification
3. `docs/DOMAIN-MODEL.md` - Domain model and business rules
4. `docs/DECISIONS.md` - Why we made certain choices
5. `epics/{epic-id}/EPIC.md` - Current epic overview
6. `epics/{epic-id}/tickets/{ticket-id}.md` - Specific ticket

## Completed Tickets
- ✅ PLAN91-001: Create project documentation structure (2026-01-29)
- ✅ PLAN91-002: Add UML use case diagrams to docs (2026-01-29)

## Notes
- All ticket numbers are sequential (001, 002, 003...) across all epics
- Test everything in isolation before integration
- Document decisions in DECISIONS.md
- For model work: Create public static void main() test methods first

## Quick Reference
- **Project**: Plan 91 - Habit Tracking Application
- **Stack**: Java 21, Spring Boot 4, HTMX, Tailwind CSS, MySQL
- **Architecture**: DDD with Hexagonal Architecture
- **Goal**: Build MVP in 6 weeks with agent-driven development
