# Epic 04: Infrastructure & Persistence

**Status**: 🚀 **IN PROGRESS**
**Priority**: Critical
**Estimated Duration**: 2-3 days
**Goal**: Connect domain model to MySQL database with Spring Data JPA, Flyway migrations, and convert tests to JUnit

---

## Overview

This epic connects our beautiful domain model (from Epic 01) and authentication UI (from Epic 03) to a real MySQL database. We'll set up the infrastructure layer, create database schemas, implement repositories, and convert all our `main()` test methods to proper JUnit tests with Testcontainers.

**Why This Epic Exists**:
- Domain model needs persistence to store data
- Authentication needs database to store users
- Standalone `main()` tests need to become proper JUnit tests
- Application needs production-ready infrastructure

---

## Objectives

1. Set up MySQL database with Docker Compose for local development
2. Create Flyway migrations for all database tables
3. Create JPA entities for all domain models
4. Implement MapStruct mappers between domain and JPA entities
5. Create Spring Data JPA repositories
6. Connect authentication to database with BCrypt password encryption
7. Convert all domain `main()` tests to JUnit with Testcontainers
8. Configure application properties for different environments
9. Set up proper logging and error handling

---

## Success Criteria

- [ ] MySQL database running in Docker container
- [ ] All tables created via Flyway migrations
- [ ] JPA entities for: User, HabitPractitioner, Habit, Routine, HabitEntry
- [ ] MapStruct mappers converting domain ↔ entities
- [ ] Spring Data JPA repositories implemented
- [ ] Registration creates users in database with encrypted passwords
- [ ] Login validates against database
- [ ] All 215+ domain tests converted to JUnit and passing
- [ ] Testcontainers used for integration tests
- [ ] Application properties configured for dev/test/prod

---

## Tickets

| Ticket | Title | Status | Effort |
|--------|-------|--------|--------|
| PLAN91-041 | Spring Boot configuration (already done) | ✅ Complete | Small |
| PLAN91-042 | MySQL database setup with Docker Compose | 🔄 In Progress | Medium |
| PLAN91-043 | Flyway migration for users table | ⏳ Pending | Small |
| PLAN91-044 | Flyway migration for habit_practitioners table | ⏳ Pending | Small |
| PLAN91-045 | Flyway migration for habits table | ⏳ Pending | Medium |
| PLAN91-046 | Flyway migration for routines table | ⏳ Pending | Medium |
| PLAN91-047 | Flyway migration for habit_entries table | ⏳ Pending | Medium |
| PLAN91-048 | Create JPA entities for all domain models | ⏳ Pending | Large |
| PLAN91-049 | Create MapStruct mappers (domain ↔ entity) | ⏳ Pending | Large |
| PLAN91-050 | Implement Spring Data JPA repositories | ⏳ Pending | Medium |
| PLAN91-051 | Convert domain main() tests to JUnit + Testcontainers | ⏳ Pending | Large |
| PLAN91-052 | Application properties configuration | ⏳ Pending | Small |
| PLAN91-053 | Logging and error handling setup | ⏳ Pending | Medium |

---

## Dependencies

**Depends on**:
- Epic 00 (Setup) ✅ Complete
- Epic 01 (Domain Model) ✅ Complete
- Epic 03 (Authentication UI) ✅ Complete

**Blocks**:
- Epic 05 (Habit Management) - needs repositories
- Epic 06+ (All feature work) - needs persistence

---

## Deliverables

1. **Database Infrastructure**
   - Docker Compose with MySQL 8
   - Flyway migrations for all tables
   - Database initialized and ready

2. **JPA Layer**
   - JPA entities for all domain models
   - Proper annotations (@Entity, @Table, @Column)
   - Relationships configured (@OneToMany, @ManyToOne)

3. **Mapping Layer**
   - MapStruct mappers
   - Bidirectional conversion (domain ↔ entity)
   - Proper null handling

4. **Repository Layer**
   - Spring Data JPA repositories
   - Custom query methods
   - Repository interfaces for each aggregate

5. **Authentication Connected**
   - User registration saves to database
   - Passwords encrypted with BCrypt
   - Login validates against database
   - UserDetailsService implementation

6. **Testing Infrastructure**
   - All domain tests converted to JUnit 5
   - Testcontainers for integration tests
   - 215+ tests still passing
   - Fast test execution

7. **Configuration**
   - application-dev.yml for local development
   - application-test.yml for testing
   - application-prod.yml for production
   - Proper logging configuration

---

## Architecture

### Hexagonal Architecture Layers

```
┌─────────────────────────────────────────────────────────────┐
│                        Application                          │
│                    (Use Cases / Services)                   │
└─────────────────────────────────────────────────────────────┘
                              ▲
                              │
┌─────────────────────────────┴───────────────────────────────┐
│                        Domain Model                          │
│          (Aggregates, Entities, Value Objects)              │
│                    - Business Logic -                        │
└─────────────────────────────────────────────────────────────┘
                              ▲
                              │
                    ┌─────────┴─────────┐
                    │   MapStruct       │
                    │   Mappers         │
                    └─────────┬─────────┘
                              │
┌─────────────────────────────┴───────────────────────────────┐
│                   Infrastructure Layer                       │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ JPA Entities │  │ Repositories │  │   Flyway     │     │
│  └──────────────┘  └──────────────┘  │  Migrations  │     │
│                                       └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │  MySQL Database │
                    └─────────────────┘
```

### Package Structure

```
src/main/java/com/ctoblue/plan91/
├── domain/                          # Epic 01 (already done)
│   ├── habitpractitioner/
│   ├── habit/
│   ├── routine/
│   └── shared/
├── adapter/
│   ├── in/web/                      # Epic 03 (already done)
│   └── out/persistence/             # NEW - Epic 04
│       ├── entity/                  # JPA entities
│       │   ├── UserEntity.java
│       │   ├── HabitPractitionerEntity.java
│       │   ├── HabitEntity.java
│       │   ├── RoutineEntity.java
│       │   └── HabitEntryEntity.java
│       ├── mapper/                  # MapStruct mappers
│       │   ├── HabitPractitionerMapper.java
│       │   ├── HabitMapper.java
│       │   └── RoutineMapper.java
│       └── repository/              # Spring Data JPA repos
│           ├── HabitPractitionerJpaRepository.java
│           ├── HabitJpaRepository.java
│           └── RoutineJpaRepository.java
└── infrastructure/
    └── security/                    # Epic 03 (already done)
```

---

## Database Schema

### Tables Overview

1. **users** - Authentication users (Spring Security)
2. **habit_practitioners** - Domain users (linked to auth users)
3. **habits** - Habit definitions (public library habits)
4. **routines** - User's active habit routines
5. **habit_entries** - Daily completions and check-ins

### Key Relationships

```
users (1) ──→ (1) habit_practitioners
habit_practitioners (1) ──→ (0..n) routines
habits (1) ──→ (0..n) routines
routines (1) ──→ (0..n) habit_entries
```

---

## Technology Stack

- **Database**: MySQL 8.0
- **Migration**: Flyway 9.x
- **ORM**: Spring Data JPA / Hibernate
- **Mapping**: MapStruct 1.5.x
- **Testing**: JUnit 5, Testcontainers
- **Password**: BCrypt (Spring Security)

---

## Testing Strategy

### Unit Tests (Domain Layer)
- Pure domain logic tests
- No database required
- Fast execution
- Already have 215+ tests from Epic 01

### Integration Tests (Infrastructure Layer)
- Use Testcontainers (MySQL container)
- Test repository operations
- Test mappers with real data
- Verify database constraints

### Test Organization
```
src/test/java/com/ctoblue/plan91/
├── domain/                          # Unit tests (no DB)
│   ├── habitpractitioner/           # 24 tests
│   ├── habit/                       # 44 tests
│   ├── routine/                     # 50 tests
│   ├── habitentry/                  # 18 tests
│   └── service/                     # 33 tests
└── adapter/out/persistence/         # Integration tests (Testcontainers)
    ├── repository/
    └── mapper/
```

---

## Migration from main() to JUnit

**Before (Epic 01)**:
```java
public class HabitPractitioner {
    public static void main(String[] args) {
        // Test code here
        assert condition : "Test failed";
    }
}
```

**After (Epic 04)**:
```java
@Test
void shouldCreateHabitPractitioner() {
    // Arrange
    var email = new Email("user@example.com");

    // Act
    var practitioner = HabitPractitioner.create(id, email, "John", "Doe", timezone);

    // Assert
    assertThat(practitioner.getEmail()).isEqualTo(email);
}
```

---

## Security Considerations

1. **Password Storage**:
   - Never store plain-text passwords
   - Use BCrypt with cost factor 12
   - Validate password strength

2. **Database Access**:
   - Use connection pooling
   - Prepared statements (JPA handles this)
   - Proper transaction management

3. **Sensitive Data**:
   - Store database credentials in environment variables
   - Never commit passwords to Git
   - Use Spring profiles for different environments

---

## Performance Considerations

1. **Database Indexes**:
   - Index foreign keys
   - Index frequently queried columns
   - Composite indexes for common queries

2. **N+1 Query Prevention**:
   - Use `@EntityGraph` for fetch optimization
   - Careful with lazy loading
   - Monitor query count in tests

3. **Connection Pooling**:
   - HikariCP (default in Spring Boot)
   - Configure appropriate pool size
   - Monitor connection usage

---

## Notes for Developers

- **Docker Required**: Install Docker Desktop for local development
- **Port 3306**: MySQL will run on default port 3306
- **Testcontainers**: Requires Docker daemon running for tests
- **Migration Files**: Never modify existing Flyway migrations
- **Mappers**: MapStruct generates code at compile time
- **Entities vs Domain**: Keep domain models clean, entities are for persistence only

---

## References

- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [MapStruct Documentation](https://mapstruct.org/)
- [Testcontainers Documentation](https://www.testcontainers.org/)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)

---

**Created**: 2026-02-01
**Last Updated**: 2026-02-01
