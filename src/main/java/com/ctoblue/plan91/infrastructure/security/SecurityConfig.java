package com.ctoblue.plan91.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;

/**
 * Spring Security configuration for Plan 91.
 *
 * <p>Epic 04: Database authentication with BCrypt password encryption.
 * Users authenticate against MySQL database.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final RateLimitFilter rateLimitFilter;
    private final CsrfCookieFilter csrfCookieFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Value("${GOOGLE_CLIENT_ID:}")
    private String googleClientId;

    public SecurityConfig(UserDetailsService userDetailsService, RateLimitFilter rateLimitFilter,
                          CsrfCookieFilter csrfCookieFilter, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.rateLimitFilter = rateLimitFilter;
        this.csrfCookieFilter = csrfCookieFilter;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    private boolean isOAuth2Enabled() {
        // Check for valid Google OAuth client ID (must contain 'googleusercontent.com')
        return googleClientId != null && googleClientId.contains("googleusercontent.com");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(csrfCookieFilter, CsrfFilter.class)
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(authorize -> authorize
                // Allow static resources without authentication
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                // Allow demo pages without authentication (for Epic 02)
                .requestMatchers("/demo/**", "/test-**").permitAll()
                // Allow authentication pages (login, register)
                .requestMatchers("/login", "/register").permitAll()
                // API endpoints require authentication (session-based)
                .requestMatchers("/api/**").authenticated()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")  // Custom login page
                .defaultSuccessUrl("/dashboard", true)  // Redirect to dashboard after login
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")  // Redirect to login with logout message
                .permitAll()
            )
            // CSRF enabled - tokens provided via cookie for JavaScript fetch
            .csrf(csrf -> csrf
                .csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler())
            );

        // Only enable OAuth2 login if Google credentials are configured
        if (isOAuth2Enabled()) {
            http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login")  // Same custom login page
                .successHandler(oAuth2LoginSuccessHandler)  // Handle user creation
            );
        }

        return http.build();
    }

    /**
     * Password encoder bean using BCrypt with strength 12.
     *
     * @return BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Authentication provider that uses database authentication.
     *
     * @return DaoAuthenticationProvider configured with UserDetailsService and PasswordEncoder
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Authentication manager bean.
     *
     * @param authConfig authentication configuration
     * @return AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
