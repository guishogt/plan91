package com.ctoblue.plan91.adapter.in.web.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Demo controller to showcase HTMX interactions.
 *
 * <p>This controller provides simple endpoints for testing HTMX functionality
 * without requiring full application infrastructure. It demonstrates common
 * HTMX patterns like partial page updates, form submissions, and dynamic content.
 *
 * <p><b>Note:</b> This is for demonstration purposes only and will be removed
 * once real feature controllers are implemented.
 */
@Controller
@RequestMapping("/demo")
public class HtmxDemoController {

    /**
     * Serves the HTMX demo page.
     */
    @GetMapping("/htmx")
    public String htmxDemoPage() {
        return "test-htmx";
    }

    /**
     * Returns a simple success message.
     * Demonstrates basic hx-get usage.
     */
    @GetMapping("/message")
    @ResponseBody
    public String getMessage() {
        return """
            <div class="bg-success-50 border border-success-200 rounded-lg p-4">
                <p class="text-success-800 font-semibold">✅ HTMX is working!</p>
                <p class="text-success-600 text-sm">This content was loaded dynamically without a page refresh.</p>
            </div>
            """;
    }

    /**
     * Returns current server time.
     * Demonstrates dynamic content updates.
     */
    @GetMapping("/time")
    @ResponseBody
    public String getCurrentTime() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
        return """
            <div class="text-center">
                <p class="text-2xl font-bold text-primary-600">%s</p>
                <p class="text-sm text-gray-500">Server time (click again to refresh)</p>
            </div>
            """.formatted(time);
    }

    /**
     * Handles form submission and returns personalized greeting.
     * Demonstrates hx-post with form data.
     */
    @PostMapping("/submit")
    @ResponseBody
    public String submitForm(@RequestParam(required = false) String name) {
        if (name == null || name.isBlank()) {
            return """
                <div class="bg-danger-50 border border-danger-200 rounded-lg p-4">
                    <p class="text-danger-800 font-semibold">❌ Error</p>
                    <p class="text-danger-600 text-sm">Name cannot be empty!</p>
                </div>
                """;
        }

        return """
            <div class="bg-primary-50 border border-primary-200 rounded-lg p-4">
                <p class="text-primary-800 font-semibold">👋 Hello, %s!</p>
                <p class="text-primary-600 text-sm">Your form was submitted successfully via HTMX.</p>
            </div>
            """.formatted(name.trim());
    }

    /**
     * Simulates adding an item to a list.
     * Demonstrates appending content with hx-swap="beforeend".
     */
    @PostMapping("/add-item")
    @ResponseBody
    public String addItem(@RequestParam String item) {
        return """
            <li class="flex items-center justify-between p-3 bg-gray-50 rounded-lg mb-2">
                <span class="text-gray-700">%s</span>
                <button hx-delete="/demo/delete-item"
                        hx-target="closest li"
                        hx-swap="outerHTML swap:0.5s"
                        class="text-danger-600 hover:text-danger-800 text-sm font-semibold">
                    Delete
                </button>
            </li>
            """.formatted(item);
    }

    /**
     * Simulates deleting an item.
     * Returns empty string to remove the element.
     */
    @DeleteMapping("/delete-item")
    @ResponseBody
    public String deleteItem() {
        // Return empty string - HTMX will swap this with the target element
        return "";
    }

    /**
     * Simulates a loading delay to demonstrate loading states.
     * Waits 2 seconds before responding.
     */
    @GetMapping("/slow")
    @ResponseBody
    public String slowResponse() throws InterruptedException {
        Thread.sleep(2000); // Simulate slow network/processing
        return """
            <div class="bg-warning-50 border border-warning-200 rounded-lg p-4">
                <p class="text-warning-800 font-semibold">⏱️ Slow Response</p>
                <p class="text-warning-600 text-sm">This response took 2 seconds to demonstrate loading indicators.</p>
            </div>
            """;
    }

    /**
     * Returns a list of habit suggestions.
     * Demonstrates search/filter functionality with hx-trigger.
     */
    @GetMapping("/search")
    @ResponseBody
    public String search(@RequestParam(defaultValue = "") String query) {
        String[] allHabits = {
            "Morning Meditation",
            "Evening Walk",
            "Read 10 Pages",
            "Drink 8 Glasses of Water",
            "Exercise 30 Minutes",
            "Journal Writing",
            "Practice Gratitude"
        };

        StringBuilder results = new StringBuilder();
        for (String habit : allHabits) {
            if (query.isBlank() || habit.toLowerCase().contains(query.toLowerCase())) {
                results.append("""
                    <div class="p-2 hover:bg-primary-50 rounded cursor-pointer border-b border-gray-100">
                        <p class="text-gray-800">%s</p>
                    </div>
                    """.formatted(habit));
            }
        }

        if (results.isEmpty()) {
            return """
                <div class="p-4 text-center text-gray-500">
                    <p>No habits found matching "%s"</p>
                </div>
                """.formatted(query);
        }

        return results.toString();
    }
}
