package com.ctoblue.plan91;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic smoke test to verify Spring context loads.
 */
@SpringBootTest
@ActiveProfiles("test")
class Plan91ApplicationTests {

    @Test
    void contextLoads() {
        // This test will fail if the Spring context cannot be loaded
        // It's a basic smoke test to ensure the application starts
    }
}
