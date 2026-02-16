package com.ctoblue.plan91.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA entity for habit_entries table (daily completions).
 *
 * <p>Maps to the HabitEntry domain entity.
 * Entries record daily completions for routines.
 */
@Entity
@Table(name = "habit_entries",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_habit_entries_routine_date", columnNames = {"routine_id", "date"})
        },
        indexes = {
                @Index(name = "idx_habit_entries_routine", columnList = "routine_id"),
                @Index(name = "idx_habit_entries_date", columnList = "date"),
                @Index(name = "idx_habit_entries_routine_date", columnList = "routine_id,date")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitEntryEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id", nullable = false)
    private RoutineEntity routine;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "completed", nullable = false)
    @Builder.Default
    private Boolean completed = true;

    @Column(name = "value")
    private Integer value;

    @Column(name = "notes", length = 1000)
    private String notes;

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
        if (completed == null) {
            completed = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
