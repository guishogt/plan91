package com.ctoblue.plan91;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;

/**
 * Plan 91 - Habit Tracking Application
 *
 * Main Spring Boot application class.
 *
 * Core Features:
 * - 91-day habit cycles
 * - One-strike forgiveness rule
 * - Domain-Driven Design with Hexagonal Architecture
 *
 * @author Luis Martinez (CTOBlue)
 * @version 0.1.0-SNAPSHOT
 */
@SpringBootApplication(exclude = {
    OAuth2ClientAutoConfiguration.class,
    OAuth2ResourceServerAutoConfiguration.class
})
public class Plan91Application {

    public static void main(String[] args) {
        SpringApplication.run(Plan91Application.class, args);
    }
}
