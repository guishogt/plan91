package com.ctoblue.plan91.domain;

import com.ctoblue.plan91.domain.habit.Habit;
import com.ctoblue.plan91.domain.habit.HabitId;
import com.ctoblue.plan91.domain.habitentry.HabitEntry;
import com.ctoblue.plan91.domain.habitpractitioner.Email;
import com.ctoblue.plan91.domain.habitpractitioner.HabitPractitioner;
import com.ctoblue.plan91.domain.habitpractitioner.HabitPractitionerId;
import com.ctoblue.plan91.domain.routine.*;
import com.ctoblue.plan91.domain.routine.service.RecurrenceCalculatorService;
import com.ctoblue.plan91.domain.routine.service.RoutineProgressService;

import java.time.LocalDate;
import java.util.*;

/**
 * Interactive CLI demo for the Plan 91 domain model.
 *
 * <p>Allows you to:
 * <ul>
 *   <li>Create a habit practitioner (you!)</li>
 *   <li>Create habits (boolean and numeric)</li>
 *   <li>Start routines with different recurrence patterns</li>
 *   <li>Record daily completions</li>
 *   <li>Track progress and streaks</li>
 *   <li>See the one-strike rule in action</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 * mvn clean compile
 * java -cp target/classes com.ctoblue.plan91.domain.DomainModelDemo
 * </pre>
 */
public class DomainModelDemo {

    private static final Scanner scanner = new Scanner(System.in);
    private static HabitPractitioner practitioner;
    private static final Map<String, Habit> habits = new HashMap<>();
    private static final Map<String, Routine> routines = new HashMap<>();
    private static final Map<String, List<HabitEntry>> entries = new HashMap<>();
    private static final RoutineProgressService progressService = new RoutineProgressService();
    private static final RecurrenceCalculatorService recurrenceService = new RecurrenceCalculatorService();
    private static LocalDate currentDate = LocalDate.now();

    public static void main(String[] args) {
        printWelcome();
        createPractitioner();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> createHabit();
                case "2" -> listHabits();
                case "3" -> startRoutine();
                case "4" -> listRoutines();
                case "5" -> recordCompletion();
                case "6" -> viewProgress();
                case "7" -> recordMiss();
                case "8" -> advanceDate();
                case "9" -> quickDemo();
                case "0" -> running = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }

        System.out.println("\nThanks for trying Plan 91! 🎉");
    }

    private static void printWelcome() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                   PLAN 91 - DEMO                          ║");
        System.out.println("║              Interactive Domain Model Demo                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void createPractitioner() {
        System.out.println("Let's set up your profile!\n");

        System.out.print("First name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Last name: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Email: ");
        String emailStr = scanner.nextLine().trim();

        try {
            Email email = new Email(emailStr);
            practitioner = HabitPractitioner.create(
                    firstName,
                    lastName,
                    email,
                    "demo-auth0-id",
                    "America/Chicago"
            );

            System.out.println("\n✅ Welcome, " + practitioner.getFullName() + "!");
            System.out.println("Your ID: " + practitioner.getId());
            System.out.println();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
            createPractitioner();
        }
    }

    private static void printMenu() {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("MAIN MENU");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("1. Create a Habit");
        System.out.println("2. List Habits");
        System.out.println("3. Start a Routine");
        System.out.println("4. List Routines");
        System.out.println("5. Record Completion");
        System.out.println("6. View Progress");
        System.out.println("7. Record Miss");
        System.out.println("8. Advance Date (time travel!)");
        System.out.println("9. Quick Demo (see it in action!)");
        System.out.println("0. Exit");
        System.out.println();
        System.out.println("Current date: " + currentDate);
        System.out.println();
        System.out.print("Choose an option: ");
    }

    private static void createHabit() {
        System.out.println("\n━━━ CREATE HABIT ━━━");

        System.out.print("Habit name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Type (1=Boolean/Yes-No, 2=Numeric): ");
        String type = scanner.nextLine().trim();

        Habit habit;
        if (type.equals("2")) {
            System.out.print("Unit (e.g., pages, miles, minutes): ");
            String unit = scanner.nextLine().trim();

            System.out.print("Target (optional, press Enter to skip): ");
            String targetStr = scanner.nextLine().trim();
            Integer target = targetStr.isEmpty() ? null : Integer.parseInt(targetStr);

            com.ctoblue.plan91.domain.habit.NumericConfig config =
                    new com.ctoblue.plan91.domain.habit.NumericConfig(unit, null, null, target);

            habit = Habit.createNumeric(name, description, config, false, practitioner.getId());
        } else {
            habit = Habit.createBoolean(name, description, false, practitioner.getId());
        }

        String key = "habit-" + (habits.size() + 1);
        habits.put(key, habit);

        System.out.println("\n✅ Habit created: " + name);
        System.out.println("Key: " + key);
    }

    private static void listHabits() {
        System.out.println("\n━━━ YOUR HABITS ━━━");
        if (habits.isEmpty()) {
            System.out.println("No habits yet. Create one with option 1!");
            return;
        }

        for (Map.Entry<String, Habit> entry : habits.entrySet()) {
            Habit h = entry.getValue();
            System.out.printf("[%s] %s - %s\n", entry.getKey(), h.getName(),
                    h.getTrackingType() == com.ctoblue.plan91.domain.habit.TrackingType.BOOLEAN
                            ? "Yes/No" : "Numeric (" + h.getNumericConfig().unit() + ")");
        }
    }

    private static void startRoutine() {
        System.out.println("\n━━━ START ROUTINE ━━━");

        if (habits.isEmpty()) {
            System.out.println("Create a habit first!");
            return;
        }

        listHabits();
        System.out.print("\nSelect habit key: ");
        String habitKey = scanner.nextLine().trim();

        Habit habit = habits.get(habitKey);
        if (habit == null) {
            System.out.println("Invalid habit key!");
            return;
        }

        System.out.println("\nRecurrence pattern:");
        System.out.println("1. Daily (every day)");
        System.out.println("2. Weekdays (Mon-Fri)");
        System.out.println("3. Weekends (Sat-Sun)");
        System.out.println("4. Specific days (custom)");
        System.out.print("Choose: ");
        String recChoice = scanner.nextLine().trim();

        RecurrenceRule rule;
        switch (recChoice) {
            case "1" -> rule = RecurrenceRule.daily();
            case "2" -> rule = RecurrenceRule.weekdays();
            case "3" -> rule = RecurrenceRule.weekends();
            case "4" -> {
                System.out.println("Select days (e.g., 1,3,5 for Mon,Wed,Fri):");
                System.out.println("1=Mon, 2=Tue, 3=Wed, 4=Thu, 5=Fri, 6=Sat, 7=Sun");
                System.out.print("Days: ");
                String daysStr = scanner.nextLine().trim();
                Set<DayOfWeek> days = new HashSet<>();
                for (String d : daysStr.split(",")) {
                    days.add(switch (d.trim()) {
                        case "1" -> DayOfWeek.MONDAY;
                        case "2" -> DayOfWeek.TUESDAY;
                        case "3" -> DayOfWeek.WEDNESDAY;
                        case "4" -> DayOfWeek.THURSDAY;
                        case "5" -> DayOfWeek.FRIDAY;
                        case "6" -> DayOfWeek.SATURDAY;
                        case "7" -> DayOfWeek.SUNDAY;
                        default -> throw new IllegalArgumentException("Invalid day: " + d);
                    });
                }
                rule = RecurrenceRule.specificDays(days);
            }
            default -> {
                System.out.println("Invalid choice, using daily");
                rule = RecurrenceRule.daily();
            }
        }

        Routine routine = Routine.start(habit.getId(), practitioner.getId(), rule, currentDate);
        String key = "routine-" + (routines.size() + 1);
        routines.put(key, routine);
        entries.put(key, new ArrayList<>());

        System.out.println("\n✅ Routine started for: " + habit.getName());
        System.out.println("Key: " + key);
        System.out.println("Recurrence: " + rule.type());
        System.out.println("Start date: " + currentDate);
        System.out.println("Expected end: " + routine.getExpectedEndDate());
        System.out.println("\nYou have 91 completions to finish!");
        System.out.println("Remember: One-strike rule applies - first miss uses strike, second miss abandons!");
    }

    private static void listRoutines() {
        System.out.println("\n━━━ YOUR ROUTINES ━━━");
        if (routines.isEmpty()) {
            System.out.println("No routines yet. Start one with option 3!");
            return;
        }

        for (Map.Entry<String, Routine> entry : routines.entrySet()) {
            Routine r = entry.getValue();
            HabitStreak streak = r.getStreak();

            System.out.printf("\n[%s] Status: %s\n", entry.getKey(), r.getStatus());
            System.out.printf("  Current Streak: %d days\n", streak.currentStreak());
            System.out.printf("  Longest Streak: %d days\n", streak.longestStreak());
            System.out.printf("  Total Completions: %d / 91\n", streak.totalCompletions());
            System.out.printf("  Strike Used: %s\n", streak.hasUsedStrike() ? "Yes ⚠️" : "No");

            if (r.isActive()) {
                List<HabitEntry> routineEntries = entries.get(entry.getKey());
                double compliance = progressService.calculateComplianceRate(r, routineEntries, currentDate);
                double progress = progressService.calculateOverallProgress(r, routineEntries);
                boolean onTrack = progressService.isOnTrack(r, routineEntries, currentDate);

                System.out.printf("  Compliance Rate: %.1f%%\n", compliance);
                System.out.printf("  Overall Progress: %.1f%%\n", progress);
                System.out.printf("  On Track: %s\n", onTrack ? "Yes ✅" : "No ❌");
            }
        }
    }

    private static void recordCompletion() {
        System.out.println("\n━━━ RECORD COMPLETION ━━━");

        if (routines.isEmpty()) {
            System.out.println("No routines to record completions for!");
            return;
        }

        listRoutines();
        System.out.print("\nSelect routine key: ");
        String routineKey = scanner.nextLine().trim();

        Routine routine = routines.get(routineKey);
        if (routine == null) {
            System.out.println("Invalid routine key!");
            return;
        }

        if (!routine.isActive()) {
            System.out.println("This routine is not active (status: " + routine.getStatus() + ")");
            return;
        }

        try {
            routine.recordCompletion(currentDate);
            HabitEntry entry = HabitEntry.recordBoolean(routine.getId(), currentDate, "Completed on " + currentDate);
            entries.get(routineKey).add(entry);

            System.out.println("\n✅ Completion recorded for " + currentDate + "!");
            System.out.println("Current streak: " + routine.getStreak().currentStreak() + " days");
            System.out.println("Total completions: " + routine.getStreak().totalCompletions() + " / 91");

            if (routine.isCompleted()) {
                System.out.println("\n🎉 CONGRATULATIONS! You completed your 91-day routine!");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("❌ Cannot record completion: " + e.getMessage());
        }
    }

    private static void viewProgress() {
        System.out.println("\n━━━ PROGRESS REPORT ━━━");

        if (routines.isEmpty()) {
            System.out.println("No routines to view progress for!");
            return;
        }

        listRoutines();
        System.out.print("\nSelect routine key (or press Enter for all): ");
        String routineKey = scanner.nextLine().trim();

        if (routineKey.isEmpty()) {
            listRoutines();
        } else {
            Routine routine = routines.get(routineKey);
            if (routine == null) {
                System.out.println("Invalid routine key!");
                return;
            }

            List<HabitEntry> routineEntries = entries.get(routineKey);
            System.out.println("\nDetailed Progress:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            // Find missed days
            List<LocalDate> missed = progressService.findMissedDays(routine, routineEntries, currentDate);
            if (!missed.isEmpty()) {
                System.out.println("\nMissed days:");
                for (LocalDate date : missed) {
                    System.out.println("  - " + date + " (" + date.getDayOfWeek() + ")");
                }
            } else {
                System.out.println("\n✅ No missed days yet!");
            }

            // Next expected date
            Optional<LocalDate> nextExpected = recurrenceService.findNextExpectedDate(
                    routine.getRecurrenceRule(), currentDate);
            if (nextExpected.isPresent()) {
                System.out.println("\nNext expected date: " + nextExpected.get() +
                        " (" + nextExpected.get().getDayOfWeek() + ")");
            }
        }
    }

    private static void recordMiss() {
        System.out.println("\n━━━ RECORD MISS ━━━");

        if (routines.isEmpty()) {
            System.out.println("No routines to record misses for!");
            return;
        }

        listRoutines();
        System.out.print("\nSelect routine key: ");
        String routineKey = scanner.nextLine().trim();

        Routine routine = routines.get(routineKey);
        if (routine == null) {
            System.out.println("Invalid routine key!");
            return;
        }

        if (!routine.isActive()) {
            System.out.println("This routine is not active (status: " + routine.getStatus() + ")");
            return;
        }

        routine.recordMiss(currentDate);

        if (routine.getStreak().hasUsedStrike() && routine.isActive()) {
            System.out.println("\n⚠️  STRIKE USED!");
            System.out.println("Your streak was preserved at: " + routine.getStreak().currentStreak() + " days");
            System.out.println("Next miss will abandon the routine!");
        } else if (!routine.isActive()) {
            System.out.println("\n❌ ROUTINE ABANDONED!");
            System.out.println("You used your strike already. Second miss = routine abandoned.");
            System.out.println("You can start a new routine if you want to try again.");
        } else {
            System.out.println("\nMiss recorded (not an expected day, no penalty)");
        }
    }

    private static void advanceDate() {
        System.out.print("\nAdvance by how many days? ");
        String daysStr = scanner.nextLine().trim();
        try {
            int days = Integer.parseInt(daysStr);
            currentDate = currentDate.plusDays(days);
            System.out.println("✅ Time traveled to: " + currentDate);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number!");
        }
    }

    private static void quickDemo() {
        System.out.println("\n━━━ QUICK DEMO - ONE-STRIKE RULE ━━━");
        System.out.println("Let me show you the one-strike rule in action!\n");

        // Create demo habit
        Habit demoHabit = Habit.createBoolean("Read", "Read for 30 minutes", false, practitioner.getId());
        String habitKey = "demo-habit";
        habits.put(habitKey, demoHabit);
        System.out.println("✅ Created demo habit: Read");

        // Start routine
        Routine demoRoutine = Routine.start(demoHabit.getId(), practitioner.getId(),
                RecurrenceRule.daily(), LocalDate.of(2026, 1, 1));
        String routineKey = "demo-routine";
        routines.put(routineKey, demoRoutine);
        entries.put(routineKey, new ArrayList<>());
        System.out.println("✅ Started daily routine on 2026-01-01\n");

        // Complete days 1-5
        System.out.println("Days 1-5: Completing every day...");
        for (int i = 0; i < 5; i++) {
            LocalDate date = LocalDate.of(2026, 1, 1).plusDays(i);
            demoRoutine.recordCompletion(date);
            entries.get(routineKey).add(HabitEntry.recordBoolean(demoRoutine.getId(), date, null));
        }
        System.out.println("  Current streak: " + demoRoutine.getStreak().currentStreak() + " days ✅\n");

        // Miss day 6 (first miss)
        System.out.println("Day 6: MISS (first time)");
        demoRoutine.recordMiss(LocalDate.of(2026, 1, 6));
        System.out.println("  ⚠️  Strike used! Streak preserved at: " + demoRoutine.getStreak().currentStreak() + " days");
        System.out.println("  Still ACTIVE: " + demoRoutine.isActive() + "\n");

        // Complete days 7-8
        System.out.println("Days 7-8: Completing again...");
        for (int i = 6; i < 8; i++) {
            LocalDate date = LocalDate.of(2026, 1, 1).plusDays(i);
            demoRoutine.recordCompletion(date);
            entries.get(routineKey).add(HabitEntry.recordBoolean(demoRoutine.getId(), date, null));
        }
        System.out.println("  Current streak: " + demoRoutine.getStreak().currentStreak() + " days ✅\n");

        // Miss day 9 (second miss)
        System.out.println("Day 9: MISS (second time)");
        demoRoutine.recordMiss(LocalDate.of(2026, 1, 9));
        System.out.println("  ❌ ROUTINE ABANDONED!");
        System.out.println("  Status: " + demoRoutine.getStatus());
        System.out.println("  Current streak reset to: " + demoRoutine.getStreak().currentStreak());
        System.out.println("  Longest streak preserved: " + demoRoutine.getStreak().longestStreak() + " days\n");

        System.out.println("That's the one-strike rule in action!");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
}
