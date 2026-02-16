package com.ctoblue.plan91.adapter.out.persistence.entity;

import com.ctoblue.plan91.domain.routine.RoutineStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA entity for routines table (91-day commitments).
 *
 * <p>Maps to the Routine domain aggregate.
 * A Routine is a practitioner's 91-day commitment to practice a habit.
 */
@Entity
@Table(name = "routines", indexes = {
        @Index(name = "idx_routines_habit", columnList = "habit_id"),
        @Index(name = "idx_routines_practitioner", columnList = "practitioner_id"),
        @Index(name = "idx_routines_status", columnList = "status"),
        @Index(name = "idx_routines_start_date", columnList = "start_date"),
        @Index(name = "idx_routines_practitioner_status", columnList = "practitioner_id,status"),
        @Index(name = "idx_routines_practitioner_active", columnList = "practitioner_id,status,start_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private HabitEntity habit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practitioner_id", nullable = false)
    private HabitPractitionerEntity practitioner;

    // Recurrence rule (embedded)
    @Embedded
    private RecurrenceRuleEmbeddable recurrenceRule;

    // Target days (how many completions needed, default 91)
    @Column(name = "target_days", nullable = false)
    @Builder.Default
    private int targetDays = 91;

    // The N-day cycle
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "expected_end_date", nullable = false)
    private LocalDate expectedEndDate;

    @Column(name = "completed_at")
    private LocalDate completedAt;

    // Streak tracking (embedded)
    @Embedded
    @Builder.Default
    private HabitStreakEmbeddable streak = new HabitStreakEmbeddable();

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RoutineStatus status;

    // Audit timestamps
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
        if (streak == null) {
            streak = new HabitStreakEmbeddable();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
