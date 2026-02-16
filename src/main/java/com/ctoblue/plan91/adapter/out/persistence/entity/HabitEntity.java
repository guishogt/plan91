package com.ctoblue.plan91.adapter.out.persistence.entity;

import com.ctoblue.plan91.domain.habit.TrackingType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity for habits table (habit definitions).
 *
 * <p>Maps to the Habit domain aggregate.
 * Habits are definitions/templates that can be shared and copied.
 */
@Entity
@Table(name = "habits", indexes = {
        @Index(name = "idx_habits_creator", columnList = "creator_id"),
        @Index(name = "idx_habits_public", columnList = "is_public"),
        @Index(name = "idx_habits_name", columnList = "name"),
        @Index(name = "idx_habits_source", columnList = "source_habit_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private HabitPractitionerEntity creator;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "tracking_type", nullable = false, length = 20)
    private TrackingType trackingType;

    // Numeric config (embedded as columns)
    @Column(name = "numeric_unit_name", length = 50)
    private String numericUnitName;

    @Column(name = "numeric_min_value")
    private Integer numericMinValue;

    @Column(name = "numeric_max_value")
    private Integer numericMaxValue;

    @Column(name = "numeric_target")
    private Integer numericTarget;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    @Column(name = "is_private", nullable = false)
    @Builder.Default
    private Boolean isPrivate = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_habit_id")
    private HabitEntity sourceHabit;

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
        if (isPublic == null) {
            isPublic = false;
        }
        if (isPrivate == null) {
            isPrivate = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
