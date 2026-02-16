package com.ctoblue.plan91.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDate;

/**
 * Embeddable for HabitStreak value object.
 *
 * <p>Embedded in RoutineEntity to store streak tracking data.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitStreakEmbeddable {

    @Column(name = "current_streak", nullable = false)
    @Builder.Default
    private Integer currentStreak = 0;

    @Column(name = "longest_streak", nullable = false)
    @Builder.Default
    private Integer longestStreak = 0;

    @Column(name = "total_completions", nullable = false)
    @Builder.Default
    private Integer totalCompletions = 0;

    @Column(name = "has_used_strike", nullable = false)
    @Builder.Default
    private Boolean hasUsedStrike = false;

    @Column(name = "strike_date")
    private LocalDate strikeDate;

    @Column(name = "last_completion_date")
    private LocalDate lastCompletionDate;
}
