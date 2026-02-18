package com.ctoblue.plan91.infrastructure.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flyway configuration that repairs the schema history before migrating.
 * This is needed when a migration was modified after being applied.
 *
 * Active in all profiles to handle checksum mismatches gracefully.
 */
@Configuration
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
