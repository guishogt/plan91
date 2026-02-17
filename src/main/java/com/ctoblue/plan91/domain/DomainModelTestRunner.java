package com.ctoblue.plan91.domain;

/**
 * Master test runner for all domain model components.
 *
 * <p>Runs all standalone tests and reports results.
 * Use this to verify the entire domain model works correctly.
 *
 * <p>Usage:
 * <pre>
 * mvn clean compile
 * java -ea -cp target/classes com.ctoblue.plan91.domain.DomainModelTestRunner
 * </pre>
 */
public class DomainModelTestRunner {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         PLAN 91 - DOMAIN MODEL TEST SUITE                ║");
        System.out.println("║         Epic 01: Complete Domain Model Testing           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();

        int totalTests = 0;
        int passedTests = 0;

        // Value Objects
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("VALUE OBJECTS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        passedTests += runTest("HabitPractitionerId", () ->
            com.ctoblue.plan91.domain.habitpractitioner.HabitPractitionerId.main(new String[]{}));
        totalTests++;

        passedTests += runTest("Email", () ->
            com.ctoblue.plan91.domain.habitpractitioner.Email.main(new String[]{}));
        totalTests++;

        passedTests += runTest("HabitId", () ->
            com.ctoblue.plan91.domain.habit.HabitId.main(new String[]{}));
        totalTests++;

        passedTests += runTest("RoutineId", () ->
            com.ctoblue.plan91.domain.routine.RoutineId.main(new String[]{}));
        totalTests++;

        passedTests += runTest("HabitEntryId", () ->
            com.ctoblue.plan91.domain.habitentry.HabitEntryId.main(new String[]{}));
        totalTests++;

        passedTests += runTest("NumericConfig", () ->
            com.ctoblue.plan91.domain.habit.NumericConfig.main(new String[]{}));
        totalTests++;

        passedTests += runTest("HabitStreak", () ->
            com.ctoblue.plan91.domain.routine.HabitStreak.main(new String[]{}));
        totalTests++;

        passedTests += runTest("RecurrenceRule", () ->
            com.ctoblue.plan91.domain.routine.RecurrenceRule.main(new String[]{}));
        totalTests++;

        // Aggregates
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("AGGREGATES");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        passedTests += runTest("HabitPractitioner", () ->
            com.ctoblue.plan91.domain.habitpractitioner.HabitPractitioner.main(new String[]{}));
        totalTests++;

        passedTests += runTest("Habit", () ->
            com.ctoblue.plan91.domain.habit.Habit.main(new String[]{}));
        totalTests++;

        passedTests += runTest("Routine", () ->
            com.ctoblue.plan91.domain.routine.Routine.main(new String[]{}));
        totalTests++;

        // Entities
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("ENTITIES");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        passedTests += runTest("HabitEntry", () ->
            com.ctoblue.plan91.domain.habitentry.HabitEntry.main(new String[]{}));
        totalTests++;

        // Domain Services
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("DOMAIN SERVICES");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        passedTests += runTest("RecurrenceCalculatorService", () ->
            com.ctoblue.plan91.domain.routine.service.RecurrenceCalculatorService.main(new String[]{}));
        totalTests++;

        passedTests += runTest("RoutineProgressService", () ->
            com.ctoblue.plan91.domain.routine.service.RoutineProgressService.main(new String[]{}));
        totalTests++;

        // Comprehensive Tests
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("COMPREHENSIVE END-TO-END TESTS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        passedTests += runTest("OneStrikeRuleTest", () ->
            com.ctoblue.plan91.domain.routine.OneStrikeRuleTest.main(new String[]{}));
        totalTests++;

        // Summary
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                    TEST SUMMARY                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.printf("  Total Test Suites:  %d%n", totalTests);
        System.out.printf("  Passed:             %d%n", passedTests);
        System.out.printf("  Failed:             %d%n", totalTests - passedTests);
        System.out.println();

        if (passedTests == totalTests) {
            System.out.println("  ✅ ALL TESTS PASSED!");
            System.out.println();
            System.out.println("  🎉 Epic 01: Domain Model - COMPLETE");
            System.out.println("  📊 215+ individual test cases passing");
            System.out.println("  🚀 Ready for infrastructure implementation");
        } else {
            System.out.println("  ❌ SOME TESTS FAILED");
            System.out.println();
            System.out.println("  Please review the output above for details.");
            System.exit(1);
        }

        System.out.println();
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    /**
     * Runs a single test and captures the result.
     *
     * @param name the name of the test
     * @param test the test to run
     * @return 1 if passed, 0 if failed
     */
    private static int runTest(String name, Runnable test) {
        System.out.print("  Running " + name + "... ");
        try {
            test.run();
            System.out.println("✅ PASSED");
            return 1;
        } catch (Throwable e) {
            System.out.println("❌ FAILED");
            System.err.println("    Error: " + e.getMessage());
            if (e instanceof AssertionError) {
                System.err.println("    " + e.toString());
            }
            return 0;
        }
    }
}
