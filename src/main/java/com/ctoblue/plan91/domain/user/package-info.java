/**
 * User domain - User aggregate and related value objects.
 *
 * <p>This package contains:
 * <ul>
 *   <li>{@code User} - Aggregate root representing a user account</li>
 *   <li>{@code UserId} - Value object for user identity</li>
 *   <li>{@code Email} - Value object with email validation</li>
 *   <li>{@code UserRepository} - Port (interface) for user persistence</li>
 * </ul>
 *
 * <p>The User aggregate handles authentication linkage (Auth0),
 * timezone management, and user profile information.
 *
 * @since 0.1.0
 */
package com.ctoblue.plan91.domain.user;
