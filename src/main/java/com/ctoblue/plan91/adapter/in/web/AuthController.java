package com.ctoblue.plan91.adapter.in.web;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitPractitionerEntity;
import com.ctoblue.plan91.adapter.out.persistence.entity.UserEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitPractitionerJpaRepository;
import com.ctoblue.plan91.adapter.out.persistence.repository.UserJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;

import java.time.Instant;

/**
 * Controller for authentication pages (login, register, profile).
 *
 * <p>Epic 04: Now connected to database with BCrypt password encryption.
 */
@Controller
public class AuthController {

    private final UserJpaRepository userRepository;
    private final HabitPractitionerJpaRepository practitionerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserJpaRepository userRepository,
            HabitPractitionerJpaRepository practitionerRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.practitionerRepository = practitionerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Display the login page.
     */
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("oauth2Enabled", false); // OAuth2 temporarily disabled
        return "pages/login";
    }

    /**
     * Display the registration page.
     */
    @GetMapping("/register")
    public String registerPage() {
        return "pages/register";
    }

    /**
     * Handle user registration - Epic 04: Now saves to database!
     */
    @PostMapping("/register")
    @Transactional
    public String registerUser(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String timezone,
            Model model) {

        // 1. Validate input
        if (firstName == null || firstName.isBlank() ||
            lastName == null || lastName.isBlank() ||
            email == null || email.isBlank() ||
            password == null || password.isBlank() ||
            timezone == null || timezone.isBlank()) {
            model.addAttribute("error", "All fields are required");
            return "pages/register";
        }

        // 2. Check if email already exists
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email already registered");
            model.addAttribute("firstName", firstName);
            model.addAttribute("lastName", lastName);
            model.addAttribute("email", email);
            model.addAttribute("timezone", timezone);
            return "pages/register";
        }

        try {
            // 3. Hash password with BCrypt
            String hashedPassword = passwordEncoder.encode(password);

            // 4. Create User entity (authentication)
            UserEntity userEntity = UserEntity.builder()
                    .email(email)
                    .passwordHash(hashedPassword)
                    .enabled(true)
                    .accountLocked(false)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            // 5. Save user
            userEntity = userRepository.save(userEntity);

            // 6. Create HabitPractitioner entity (domain user)
            HabitPractitionerEntity practitionerEntity = HabitPractitionerEntity.builder()
                    .user(userEntity)
                    .firstName(firstName.trim())
                    .lastName(lastName.trim())
                    .email(email)
                    .auth0Id(null)  // Local auth, no Auth0
                    .originalTimezone(timezone)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            // 7. Save practitioner
            practitionerRepository.save(practitionerEntity);

            // Success! Redirect to login
            model.addAttribute("success", "Account created successfully! Please log in.");
            return "pages/login";

        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            model.addAttribute("firstName", firstName);
            model.addAttribute("lastName", lastName);
            model.addAttribute("email", email);
            model.addAttribute("timezone", timezone);
            return "pages/register";
        }
    }

    /**
     * API endpoint to get current user's info (practitioner ID, etc.)
     */
    @GetMapping("/api/me")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCurrentUser(java.security.Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        String email = principal.getName();

        return practitionerRepository.findByEmail(email)
                .map(practitioner -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("practitionerId", practitioner.getId().toString());
                    response.put("email", practitioner.getEmail());
                    response.put("firstName", practitioner.getFirstName());
                    response.put("lastName", practitioner.getLastName());
                    response.put("timezone", practitioner.getOriginalTimezone());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Display the user profile page - Epic 04: Now loads from database!
     */
    @GetMapping("/profile")
    public String profilePage(Model model, java.security.Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();

        // Load practitioner from database
        HabitPractitionerEntity practitioner = practitionerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Practitioner not found for email: " + email));

        // Add attributes to model
        model.addAttribute("firstName", practitioner.getFirstName());
        model.addAttribute("lastName", practitioner.getLastName());
        model.addAttribute("email", practitioner.getEmail());
        model.addAttribute("timezone", practitioner.getOriginalTimezone());

        return "pages/profile";
    }
}
