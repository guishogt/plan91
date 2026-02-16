package com.ctoblue.plan91.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity for habit_practitioners table (domain users).
 *
 * <p>Maps to the HabitPractitioner domain aggregate.
 * Each practitioner is linked to a UserEntity for authentication.
 */
@Entity
@Table(name = "habit_practitioners", indexes = {
        @Index(name = "idx_habit_practitioners_email", columnList = "email"),
        @Index(name = "idx_habit_practitioners_user_id", columnList = "user_id"),
        @Index(name = "idx_habit_practitioners_auth0_id", columnList = "auth0_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitPractitionerEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "auth0_id", length = 255)
    private String auth0Id;

    @Column(name = "last_login")
    private Instant lastLogin;

    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;

    @Column(name = "original_timezone", nullable = false, length = 50)
    private String originalTimezone;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
