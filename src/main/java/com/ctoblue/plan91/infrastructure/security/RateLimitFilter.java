package com.ctoblue.plan91.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that blocks login attempts from IPs that have exceeded the rate limit.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final LoginAttemptService loginAttemptService;

    public RateLimitFilter(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Only check rate limit for login POST requests
        if (isLoginRequest(request)) {
            String ip = getClientIP(request);
            if (loginAttemptService.isBlocked(ip)) {
                long minutesRemaining = loginAttemptService.getBlockTimeRemaining(ip);
                response.sendRedirect("/login?blocked=true&minutes=" + minutesRemaining);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && "/login".equals(request.getRequestURI());
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
