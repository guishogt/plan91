package com.ctoblue.plan91.adapter.out.persistence.mapper;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitStreakEmbeddable;
import com.ctoblue.plan91.adapter.out.persistence.entity.RecurrenceRuleEmbeddable;
import com.ctoblue.plan91.adapter.out.persistence.entity.RoutineEntity;
import com.ctoblue.plan91.domain.habit.HabitId;
import com.ctoblue.plan91.domain.habitpractitioner.HabitPractitionerId;
import com.ctoblue.plan91.domain.routine.*;
import org.mapstruct.*;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for Routine domain aggregate.
 *
 * <p>Converts between:
 * <ul>
 *   <li>Routine (domain) ↔ RoutineEntity (JPA)</li>
 *   <li>RecurrenceRule (value object) ↔ RecurrenceRuleEmbeddable</li>
 *   <li>HabitStreak (value object) ↔ HabitStreakEmbeddable</li>
 * </ul>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RoutineMapper {

    /**
     * Converts domain Routine to JPA entity.
     *
     * @param routine the domain object
     * @return the JPA entity
     */
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "habit", ignore = true)  // Set separately
    @Mapping(target = "practitioner", ignore = true)  // Set separately
    @Mapping(target = "recurrenceRule", source = "recurrenceRule")
    @Mapping(target = "streak", source = "streak")
    @Mapping(target = "targetDays", source = "targetDays")
    RoutineEntity toEntity(Routine routine);

    /**
     * Converts JPA entity to domain Routine.
     *
     * @param entity the JPA entity
     * @return the domain object
     */
    @Mapping(target = "id", expression = "java(new RoutineId(entity.getId()))")
    @Mapping(target = "habitId", expression = "java(new HabitId(entity.getHabit().getId()))")
    @Mapping(target = "practitionerId", expression = "java(new HabitPractitionerId(entity.getPractitioner().getId()))")
    @Mapping(target = "recurrenceRule", source = "recurrenceRule")
    @Mapping(target = "streak", source = "streak")
    @Mapping(target = "targetDays", source = "targetDays")
    Routine toDomain(RoutineEntity entity);

    // ========================================
    // RecurrenceRule Conversions
    // ========================================

    /**
     * Converts RecurrenceRule domain value object to embeddable.
     */
    default RecurrenceRuleEmbeddable toRecurrenceRuleEmbeddable(RecurrenceRule rule) {
        if (rule == null) return null;

        RecurrenceRuleEmbeddable embeddable = new RecurrenceRuleEmbeddable();
        embeddable.setType(rule.type());

        if (rule.type() == RecurrenceType.SPECIFIC_DAYS && rule.specificDays() != null) {
            String daysString = rule.specificDays().stream()
                    .map(Enum::name)
                    .sorted()
                    .collect(Collectors.joining(","));
            embeddable.setSpecificDays(daysString);
        }

        if (rule.type() == RecurrenceType.NTH_DAY_OF_MONTH) {
            embeddable.setNthDay(rule.nthDay() != null ? rule.nthDay().name() : null);
            embeddable.setNthWeek(rule.nthWeek());
        }

        return embeddable;
    }

    /**
     * Converts RecurrenceRuleEmbeddable to domain value object.
     */
    default RecurrenceRule toRecurrenceRule(RecurrenceRuleEmbeddable embeddable) {
        if (embeddable == null) return null;

        RecurrenceType type = embeddable.getType();

        Set<DayOfWeek> specificDays = null;
        if (type == RecurrenceType.SPECIFIC_DAYS && embeddable.getSpecificDays() != null) {
            specificDays = Arrays.stream(embeddable.getSpecificDays().split(","))
                    .map(String::trim)
                    .map(DayOfWeek::valueOf)
                    .collect(Collectors.toSet());
        }

        DayOfWeek nthDay = null;
        if (type == RecurrenceType.NTH_DAY_OF_MONTH && embeddable.getNthDay() != null) {
            nthDay = DayOfWeek.valueOf(embeddable.getNthDay());
        }

        return new RecurrenceRule(
                type,
                specificDays,
                nthDay,
                embeddable.getNthWeek()
        );
    }

    // ========================================
    // HabitStreak Conversions
    // ========================================

    /**
     * Converts HabitStreak domain value object to embeddable.
     */
    default HabitStreakEmbeddable toStreakEmbeddable(HabitStreak streak) {
        if (streak == null) {
            return new HabitStreakEmbeddable(0, 0, 0, false, null, null);
        }

        return HabitStreakEmbeddable.builder()
                .currentStreak(streak.currentStreak())
                .longestStreak(streak.longestStreak())
                .totalCompletions(streak.totalCompletions())
                .hasUsedStrike(streak.hasUsedStrike())
                .strikeDate(streak.strikeDate())
                .lastCompletionDate(streak.lastCompletionDate())
                .build();
    }

    /**
     * Converts HabitStreakEmbeddable to domain value object.
     */
    default HabitStreak toStreak(HabitStreakEmbeddable embeddable) {
        if (embeddable == null) {
            return HabitStreak.initial();
        }

        return new HabitStreak(
                embeddable.getCurrentStreak(),
                embeddable.getLongestStreak(),
                embeddable.getTotalCompletions(),
                embeddable.getHasUsedStrike(),
                embeddable.getStrikeDate(),
                embeddable.getLastCompletionDate()
        );
    }
}
