# Architecture Decision Records (ADR)

This document logs important architectural and design decisions made during Plan 91 development.

## Format
Each decision follows this structure:
- **Date**: When the decision was made
- **Status**: Proposed | Accepted | Superseded
- **Context**: Why we needed to make this decision
- **Decision**: What we decided
- **Consequences**: What this means for the project

---

## ADR-001: Restructure Development Flow for Early Testing

**Date**: 2026-01-29
**Status**: Accepted
**Context**:
- Original plan had frontend very late (Epics 9-15)
- Model testing was dependent on full infrastructure setup
- No way to validate domain logic without database
- Authentication was delayed until Epic 3

**Decision**:
- Create Epic 0 for setup (documentation, diagrams, structure)
- Move domain model to Epic 1 with standalone testing (public static void main)
- Move frontend foundation to Epic 2 (immediately after model)
- Move authentication to Epic 3 (early, before complex features)
- All model classes should be testable without database initially

**Consequences**:
- ✅ Can test domain logic immediately in isolation
- ✅ Frontend available early for visual testing
- ✅ Authentication established before building features
- ✅ Better agent workflow with testable increments
- ⚠️ May need to refactor model classes when adding persistence
- ⚠️ Frontend might need updates as backend evolves

**Rationale**:
This allows us to validate the core business logic (streak calculation, recurrence rules)
in simple Java main methods before bringing in Spring, JPA, and database complexity.

---

## ADR-002: Sequential Ticket Numbering

**Date**: 2026-01-29
**Status**: Accepted
**Context**:
- Original plan had tickets numbered per epic (e.g., Epic 1: 001-005, Epic 2: 001-003)
- This caused confusion when referencing tickets across epics
- Hard to track overall progress

**Decision**:
- Use sequential numbering across ALL epics: 001, 002, 003...
- Format: PLAN91-{number} (e.g., PLAN91-001, PLAN91-002)
- Each ticket increments regardless of epic

**Consequences**:
- ✅ Unique identifier for each ticket
- ✅ Easy to reference in commits and discussions
- ✅ Clear sense of overall progress
- ⚠️ Can't tell which epic a ticket belongs to from number alone
  (but that's what the ticket file structure is for)

---

## ADR-003: Documentation-First Approach

**Date**: 2026-01-29
**Status**: Accepted
**Context**:
- Agents need clear context to work autonomously
- Domain knowledge needs to be captured before coding
- Architectural decisions need to be visible

**Decision**:
- Create comprehensive documentation in Epic 0
- Generate Mermaid diagrams for domain model
- Create UML use case diagrams
- Document all decisions in DECISIONS.md
- Keep CURRENT_WORK.md updated for agent coordination

**Consequences**:
- ✅ Agents have clear context for implementation
- ✅ Design validated before coding begins
- ✅ Future developers can understand why choices were made
- ⚠️ Upfront time investment before coding starts
- ⚠️ Documentation must be kept in sync with code

---

## ADR-004: Domain Model Testing with main() Methods

**Date**: 2026-01-29
**Status**: Accepted
**Context**:
- Want to validate domain logic before infrastructure exists
- JUnit tests require test framework setup
- Need simplest possible validation approach

**Decision**:
- Each domain class gets a public static void main() method initially
- Use mock/sample data in main() to test business rules
- Later, convert to proper JUnit tests when infrastructure is ready
- This is temporary scaffolding, not production code

**Consequences**:
- ✅ Can test domain logic immediately
- ✅ No framework dependencies needed
- ✅ Easy to demonstrate business rules
- ⚠️ main() methods must be removed/commented out later
- ⚠️ Not a substitute for proper unit tests

**Example**:
```java
public class HabitStreak {
    // ... value object implementation

    public static void main(String[] args) {
        // Test streak calculation
        HabitStreak streak = new HabitStreak(5, 10, 15, false, null,
            LocalDate.now().minusDays(5), LocalDate.now().plusDays(86));
        System.out.println("Current streak: " + streak.getCurrentStreak());
        System.out.println("Days to 91: " + streak.getDaysTo91());
        assert streak.getDaysTo91() == 86 : "Days to 91 calculation wrong!";
    }
}
```

---

## ADR-005: Frontend Technologies

**Date**: 2026-01-29
**Status**: Accepted (from original spec)
**Context**:
- Need mobile-first PWA
- Want minimal JavaScript complexity
- Server-side rendering for performance

**Decision**:
- HTMX for dynamic interactions
- Tailwind CSS for styling
- Thymeleaf for server-side templates
- Vanilla JS where needed (minimal)
- Chart.js for analytics visualizations

**Consequences**:
- ✅ Simple, maintainable frontend
- ✅ Works without JavaScript (progressive enhancement)
- ✅ Fast page loads
- ⚠️ Limited rich interactions compared to React/Vue
- ⚠️ Learning curve for HTMX patterns

---

## ADR-006: Git Branch Strategy

**Date**: 2026-01-29
**Status**: Accepted
**Context**:
- Need clear branching model for agent-driven development
- Must support parallel work on different epics
- Should enable safe integration and deployment

**Decision**:

**Main Branches**:
- `master` - Production-ready code (deployed to Railway)
- `develop` - Integration branch for completed features

**Supporting Branches**:
- `epic/{epic-id}` - One branch per epic (e.g., `epic/00-setup`, `epic/01-domain-model`)
- `ticket/{ticket-id}` - Optional, for complex tickets (e.g., `ticket/PLAN91-025`)

**Workflow**:
1. Create epic branch from `develop`: `git checkout -b epic/01-domain-model develop`
2. Work on tickets in epic branch (or create ticket sub-branches)
3. Commit with ticket references: `git commit -m "feat: add HabitStreak [PLAN91-011]"`
4. When epic complete, merge to `develop`: `git checkout develop && git merge epic/01-domain-model`
5. Test on `develop`
6. Merge `develop` to `master` for production releases

**Commit Message Format**:
```
<type>(<scope>): <subject>

[optional body]

Epic: <epic-id>
Tickets: <ticket-ids>
```

Types: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`

**Consequences**:
- ✅ Clear separation of work by epic
- ✅ Easy to see what's in progress
- ✅ Safe integration point (develop)
- ✅ Production branch (master) always deployable
- ⚠️ Need discipline to keep branches up to date

**Current State**:
- Repository initialized on `master` branch
- Git configured with user: Luis Fernandez <luishernan@gmail.com>
- Initial commit includes Epic 0 setup

**Next Steps**:
- Can create `develop` branch when needed
- Start working in `epic/00-setup` or continue on `master` for now

---

## Template for Future ADRs

```markdown
## ADR-XXX: [Title]

**Date**: YYYY-MM-DD
**Status**: Proposed | Accepted | Superseded
**Context**:
[Why this decision is needed]

**Decision**:
[What we decided to do]

**Consequences**:
- ✅ Positive outcome 1
- ✅ Positive outcome 2
- ⚠️ Trade-off 1
- ❌ Negative outcome (if any)

**Alternatives Considered**:
- Option A: [Why rejected]
- Option B: [Why rejected]
```

---

**Last Updated**: 2026-01-29
