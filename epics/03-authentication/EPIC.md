# Epic 03: Authentication (Local)

**Status**: ✅ **COMPLETE** (UI pages ready, backend in Epic 04)
**Priority**: Critical
**Estimated Duration**: 1-2 days
**Goal**: Simple local authentication with registration, login, and profile management

---

## Overview

This epic implements a simple local authentication system using Spring Security with username/password stored in our database. Users can register, log in, manage their profile, and access protected routes. This provides a solid foundation that can be upgraded to Auth0 later if needed.

**Why This Epic Exists**:
- Secure authentication is critical before building user-specific features
- Local auth is simpler for development and doesn't require external services
- Provides same user experience without Auth0 complexity
- Can be upgraded to Auth0/OAuth later without changing UI

---

## Objectives

1. Create beautiful login and registration pages (styled with Tailwind)
2. Implement user registration with password encryption
3. Set up Spring Security for session-based authentication
4. Create user profile/settings pages
5. Protect authenticated routes
6. Handle timezone preferences

---

## Success Criteria

- [ ] Users can register new accounts with email/password
- [ ] Passwords are encrypted with BCrypt
- [ ] Users can log in with credentials
- [ ] Protected routes redirect to login when unauthenticated
- [ ] User profile page shows account info and allows editing
- [ ] Logout clears session and redirects to login
- [ ] Timezone is captured during registration
- [ ] Beautiful UI matches dashboard styling

---

## Tickets (Simplified)

| Ticket | Title | Status | Effort |
|--------|-------|--------|--------|
| PLAN91-029 | Login page UI (styled with Tailwind) | ✅ Complete | Small |
| PLAN91-030 | Registration page UI (styled with Tailwind) | ✅ Complete | Small |
| PLAN91-031 | Profile/settings page UI | ✅ Complete | Medium |
| PLAN91-032 | Update SecurityConfig for custom login | ✅ Complete | Small |
| PLAN91-033 | User menu with logout dropdown | ✅ Complete | Small |
| PLAN91-034 | Backend connection (deferred to Epic 04) | ⏸️ Deferred | Medium |

---

## Dependencies

**Depends on**:
- Epic 00 (Setup) ✅ Complete
- Epic 02 (Frontend Foundation) ✅ Complete

**Blocks**:
- Epic 05 (Use Cases) - needs authenticated user context
- Epic 06+ (All feature work) - needs user authentication

---

## Deliverables

1. **Auth0 Configuration**
   - Auth0 tenant created
   - Application configured
   - Callback URLs set up
   - API credentials secured

2. **Authentication UI**
   - Login page (styled with Tailwind)
   - Registration page
   - Password reset pages
   - Error handling

3. **Backend Integration**
   - Spring Security configuration
   - Auth0 SDK integration
   - JWT token processing
   - User entity creation on first login

4. **User Management**
   - Profile page
   - Settings page
   - Timezone management
   - Logout functionality

5. **Security**
   - Protected routes
   - JWT validation
   - CSRF protection
   - Secure password handling (via Auth0)

---

## Architecture

### Authentication Flow

```
User → Login Page → Auth0 → JWT Token → Spring Security → Application
```

1. User visits protected page
2. Redirected to login page
3. Login page redirects to Auth0 Universal Login
4. User enters credentials
5. Auth0 validates and returns JWT token
6. Spring Security validates JWT
7. User is authenticated in application

### Components

```
src/main/java/com/ctoblue/plan91/
├── infrastructure/
│   └── security/
│       ├── SecurityConfig.java (updated)
│       ├── Auth0Config.java
│       ├── JwtAuthenticationFilter.java
│       └── UserDetailsServiceImpl.java
├── adapter/in/web/
│   ├── AuthController.java
│   ├── ProfileController.java
│   └── auth/
│       ├── LoginController.java
│       └── RegisterController.java
└── application/
    └── auth/
        ├── AuthService.java
        └── UserService.java
```

---

## Auth0 Configuration Details

### Tenant Settings
- **Domain**: `plan91.us.auth0.com` (example)
- **Region**: US
- **Environment**: Development

### Application Settings
- **Type**: Regular Web Application
- **Allowed Callback URLs**: `http://localhost:8080/callback`
- **Allowed Logout URLs**: `http://localhost:8080/login`
- **Token Settings**: RS256 signing algorithm

### User Fields (Custom)
- `originalTimezone`: User's timezone preference

---

## Security Considerations

1. **JWT Validation**:
   - Verify signature with Auth0 public key
   - Check expiration time
   - Validate issuer and audience

2. **Token Storage**:
   - Store in HTTP-only cookie (not localStorage)
   - Set appropriate expiration
   - Refresh tokens for long sessions

3. **CSRF Protection**:
   - Enable CSRF tokens for state-changing operations
   - Validate CSRF on all POST/PUT/DELETE requests

4. **Password Security**:
   - Handled by Auth0 (not stored in our database)
   - Password complexity enforced by Auth0 rules
   - Secure password reset via Auth0

---

## Testing Approach

1. **Manual Testing**:
   - Register new user
   - Log in with existing user
   - Access protected routes
   - Test logout
   - Test password reset

2. **Integration Tests**:
   - Mock Auth0 responses
   - Test JWT validation
   - Test protected endpoint access
   - Test user creation flow

---

## Notes for Developers

- **Auth0 Free Tier**: Up to 7,000 active users for free
- **Local Development**: Use Auth0 development tenant
- **Secrets**: Store Auth0 credentials in `application-dev.yml` (not committed)
- **User Creation**: Create HabitPractitioner on first login from Auth0 profile
- **Email Verification**: Optional for MVP, can enable later

---

## References

- [Auth0 Java Spring Security Documentation](https://auth0.com/docs/quickstart/webapp/java-spring-security5)
- [Spring Security OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
- [JWT.io](https://jwt.io/) - JWT debugger

---

**Created**: 2026-01-31
**Last Updated**: 2026-01-31
