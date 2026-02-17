/**
 * Category domain - Category aggregate for organizing habits.
 *
 * <p>This package contains:
 * <ul>
 *   <li>{@code Category} - Aggregate root for user-defined categories</li>
 *   <li>{@code CategoryId} - Value object for category identity</li>
 *   <li>{@code CategoryRepository} - Port (interface) for category persistence</li>
 * </ul>
 *
 * <p>Categories are user-defined with a name, color, and optional icon/emoji.
 * Each habit belongs to exactly one category.
 *
 * @since 0.1.0
 */
package com.ctoblue.plan91.domain.category;
