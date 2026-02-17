/**
 * Interfaces layer - Entry points to the application.
 *
 * <p>This layer exposes the application to external clients. It includes:
 * <ul>
 *   <li>REST API controllers (for HTMX interactions)</li>
 *   <li>Web UI controllers (Thymeleaf pages)</li>
 *   <li>DTOs for API requests/responses</li>
 *   <li>Mappers between DTOs and domain objects</li>
 * </ul>
 *
 * <p>Controllers delegate to application use cases and return appropriate
 * responses (JSON for REST API, views for web UI).
 *
 * <p>This layer depends on the application and domain layers.
 *
 * @since 0.1.0
 */
package com.ctoblue.plan91.interfaces;
