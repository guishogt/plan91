package com.ctoblue.plan91.adapter.in.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for the main dashboard page.
 *
 * <p>Displays an overview of the user's active routines, progress,
 * and statistics. This is the main landing page after login.
 */
@Controller
public class DashboardController {

    /**
     * Displays the main dashboard.
     * Loads real user data from the backend.
     */
    @GetMapping("/")
    public String dashboard(Model model) {
        return "pages/dashboard";
    }

    /**
     * Alternative route for dashboard (explicit /dashboard path).
     */
    @GetMapping("/dashboard")
    public String dashboardExplicit(Model model) {
        return dashboard(model);
    }
}
