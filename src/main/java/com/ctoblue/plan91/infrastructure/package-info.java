/**
 * Infrastructure layer - External adapters and technical concerns.
 *
 * <p>This layer contains implementations of domain ports and integrations
 * with external systems. It includes:
 * <ul>
 *   <li>JPA repositories (implementing domain repository interfaces)</li>
 *   <li>Database entities and mappers</li>
 *   <li>Security configuration (Auth0, Spring Security)</li>
 *   <li>External service integrations</li>
 *   <li>Technical configuration (database, caching, etc.)</li>
 * </ul>
 *
 * <p><b>Key Principle:</b> This layer depends on the domain layer by
 * implementing its interfaces (ports), but the domain never depends on
 * infrastructure.
 *
 * <p>Uses Spring Framework, JPA/Hibernate, and other technical libraries.
 *
 * @since 0.1.0
 */
package com.ctoblue.plan91.infrastructure;
