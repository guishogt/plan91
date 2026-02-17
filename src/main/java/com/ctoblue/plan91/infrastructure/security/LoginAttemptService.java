package com.ctoblue.plan91.infrastructure.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to track and limit failed login attempts.
 *
 * <p>Protects against brute force attacks by blocking IPs after too many failed attempts.
 */
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_MINUTES = 15;

    private final ConcurrentHashMap<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    /**
     * Record a failed login attempt for an IP address.
     */
    public void loginFailed(String ip) {
        AttemptInfo info = attempts.compute(ip, (key, existing) -> {
            if (existing == null || existing.isExpired()) {
                return new AttemptInfo(1, Instant.now());
            }
            return new AttemptInfo(existing.count + 1, existing.firstAttempt);
        });
    }

    /**
     * Record a successful login - clears the attempt counter.
     */
    public void loginSucceeded(String ip) {
        attempts.remove(ip);
    }

    /**
     * Check if an IP address is blocked due to too many failed attempts.
     */
    public boolean isBlocked(String ip) {
        AttemptInfo info = attempts.get(ip);
        if (info == null) {
            return false;
        }
        if (info.isExpired()) {
            attempts.remove(ip);
            return false;
        }
        return info.count >= MAX_ATTEMPTS;
    }

    /**
     * Get remaining attempts for an IP address.
     */
    public int getRemainingAttempts(String ip) {
        AttemptInfo info = attempts.get(ip);
        if (info == null || info.isExpired()) {
            return MAX_ATTEMPTS;
        }
        return Math.max(0, MAX_ATTEMPTS - info.count);
    }

    /**
     * Get minutes until block expires.
     */
    public long getBlockTimeRemaining(String ip) {
        AttemptInfo info = attempts.get(ip);
        if (info == null) {
            return 0;
        }
        long elapsed = Instant.now().getEpochSecond() - info.firstAttempt.getEpochSecond();
        long remaining = (BLOCK_DURATION_MINUTES * 60) - elapsed;
        return Math.max(0, remaining / 60);
    }

    private static class AttemptInfo {
        final int count;
        final Instant firstAttempt;

        AttemptInfo(int count, Instant firstAttempt) {
            this.count = count;
            this.firstAttempt = firstAttempt;
        }

        boolean isExpired() {
            return Instant.now().isAfter(
                firstAttempt.plusSeconds(BLOCK_DURATION_MINUTES * 60)
            );
        }
    }
}
