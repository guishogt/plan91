package com.ctoblue.plan91.adapter.in.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for practitioner-related UI pages.
 *
 * <p>Epic 06: Social Features - Practitioner profiles
 */
@Controller
@RequestMapping("/practitioners")
public class PractitionerPageController {

    /**
     * Shows the practitioner profile page.
     */
    @GetMapping("/profile")
    public String profilePage() {
        return "practitioners/profile";
    }
}
