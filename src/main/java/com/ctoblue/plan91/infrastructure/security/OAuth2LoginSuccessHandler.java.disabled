package com.ctoblue.plan91.infrastructure.security;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitPractitionerEntity;
import com.ctoblue.plan91.adapter.out.persistence.entity.UserEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitPractitionerJpaRepository;
import com.ctoblue.plan91.adapter.out.persistence.repository.UserJpaRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * Handles successful OAuth2 login by creating or linking user accounts.
 */
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserJpaRepository userRepository;
    private final HabitPractitionerJpaRepository practitionerRepository;
    private final PasswordEncoder passwordEncoder;

    public OAuth2LoginSuccessHandler(
            UserJpaRepository userRepository,
            HabitPractitionerJpaRepository practitionerRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.practitionerRepository = practitionerRepository;
        this.passwordEncoder = passwordEncoder;
        setDefaultTargetUrl("/dashboard");
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2User oauth2User = oauthToken.getPrincipal();
            String provider = oauthToken.getAuthorizedClientRegistrationId(); // "google"

            String email = oauth2User.getAttribute("email");
            String googleId = oauth2User.getAttribute("sub"); // Google's unique user ID
            String firstName = oauth2User.getAttribute("given_name");
            String lastName = oauth2User.getAttribute("family_name");
            String fullName = oauth2User.getAttribute("name");

            // Use full name parts if given_name/family_name not available
            if (firstName == null && fullName != null) {
                String[] parts = fullName.split(" ", 2);
                firstName = parts[0];
                lastName = parts.length > 1 ? parts[1] : "";
            }
            if (firstName == null) firstName = "User";
            if (lastName == null) lastName = "";

            // Check if user already exists by email
            var existingUser = userRepository.findByEmail(email);

            if (existingUser.isEmpty()) {
                // Create new user and practitioner
                createOAuthUser(email, googleId, firstName, lastName, provider);
            } else {
                // Optionally update the OAuth ID for existing user
                var practitioner = practitionerRepository.findByEmail(email);
                if (practitioner.isPresent() && practitioner.get().getAuth0Id() == null) {
                    HabitPractitionerEntity p = practitioner.get();
                    p.setAuth0Id(provider + "|" + googleId);
                    p.setLastLogin(Instant.now());
                    practitionerRepository.save(p);
                }
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private void createOAuthUser(String email, String oauthId, String firstName, String lastName, String provider) {
        // Generate a random password (OAuth users won't use it)
        String randomPassword = UUID.randomUUID().toString();
        String hashedPassword = passwordEncoder.encode(randomPassword);

        // Create User entity
        UserEntity user = UserEntity.builder()
                .email(email)
                .passwordHash(hashedPassword)
                .enabled(true)
                .accountLocked(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        user = userRepository.save(user);

        // Create Practitioner entity
        HabitPractitionerEntity practitioner = HabitPractitionerEntity.builder()
                .user(user)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .auth0Id(provider + "|" + oauthId)
                .originalTimezone("America/New_York") // Default timezone
                .lastLogin(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        practitionerRepository.save(practitioner);
    }
}
