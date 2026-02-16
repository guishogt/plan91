package com.ctoblue.plan91.adapter.out.persistence.entity;

import com.ctoblue.plan91.domain.routine.RecurrenceType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

/**
 * Embeddable for RecurrenceRule value object.
 *
 * <p>Embedded in RoutineEntity to store recurrence configuration.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurrenceRuleEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_type", nullable = false, length = 30)
    private RecurrenceType type;

    @Column(name = "recurrence_specific_days", length = 50)
    private String specificDays;  // Comma-separated: "MONDAY,WEDNESDAY,FRIDAY"

    @Column(name = "recurrence_nth_day", length = 10)
    private String nthDay;  // Day of week for NTH_DAY_OF_MONTH

    @Column(name = "recurrence_nth_week")
    private Integer nthWeek;  // Week number (1-4)
}
