package com.ctoblue.plan91.infrastructure.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Flyway configuration that repairs the schema history before migrating.
 * This is needed when a previous migration failed and left the history in a bad state.
 *
 * Only active in production profile to fix the Railway deployment issue.
 * Can be removed once the database is healthy.
 */
@Configuration
@Profile("production")
public class FlywayRepairConfig {

    @Bean
    public FlywayMigrationStrategy repairMigrationStrategy() {
        return flyway -> {
            // Repair the schema history to fix any failed migrations
            flyway.repair();
            // Then run migrations as normal
            flyway.migrate();
        };
    }
}
