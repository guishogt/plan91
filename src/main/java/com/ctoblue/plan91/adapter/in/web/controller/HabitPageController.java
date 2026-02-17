package com.ctoblue.plan91.adapter.in.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for habit-related UI pages.
 *
 * <p>Serves Thymeleaf templates for:
 * <ul>
 *   <li>Habit creation form</li>
 *   <li>Habit list/browse</li>
 *   <li>Routine dashboard</li>
 *   <li>Routine detail page</li>
 * </ul>
 */
@Controller
public class HabitPageController {

    @GetMapping("/routines/detail")
    public String routineDetailPage() {
        return "routines/detail";
    }

    /**
     * Shows the habit creation form.
     */
    @GetMapping("/habits/create")
    public String createHabitPage() {
        return "habits/create";
    }

    /**
     * Shows the list of habits for the current user.
     */
    @GetMapping("/habits")
    public String listHabitsPage() {
        return "habits/list";
    }

    /**
     * Shows the public habit browse page.
     */
    @GetMapping("/habits/browse")
    public String browseHabitsPage() {
        return "habits/browse";
    }

    /**
     * Shows the analytics and statistics dashboard.
     */
    @GetMapping("/habits/stats")
    public String statsPage() {
        return "habits/stats";
    }
}
