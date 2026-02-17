package com.ctoblue.plan91.adapter.in.web.mapper;

import com.ctoblue.plan91.adapter.in.web.dto.RoutineDto;
import com.ctoblue.plan91.adapter.in.web.dto.StartRoutineRequest;
import com.ctoblue.plan91.adapter.out.persistence.entity.RoutineEntity;
import com.ctoblue.plan91.application.usecase.routine.StartRoutineCommand;
import com.ctoblue.plan91.domain.routine.DayOfWeek;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for converting between RoutineEntity and RoutineDto.
 */
@Mapper(componentModel = "spring")
public interface RoutineDtoMapper {

    /**
     * Converts RoutineEntity to RoutineDto (for responses).
     */
    @Mapping(target = "id", expression = "java(entity.getId().toString())")
    @Mapping(target = "habitId", expression = "java(entity.getHabit().getId().toString())")
    @Mapping(target = "habitName", source = "habit.name")
    @Mapping(target = "practitionerId", expression = "java(entity.getPractitioner().getId().toString())")
    @Mapping(target = "recurrenceType", source = "recurrenceRule.type")
    @Mapping(target = "specificDays", expression = "java(parseSpecificDays(entity.getRecurrenceRule().getSpecificDays()))")
    @Mapping(target = "nthDay", source = "recurrenceRule.nthDay")
    @Mapping(target = "nthWeek", source = "recurrenceRule.nthWeek")
    @Mapping(target = "currentStreak", source = "streak.currentStreak")
    @Mapping(target = "longestStreak", source = "streak.longestStreak")
    @Mapping(target = "totalCompletions", source = "streak.totalCompletions")
    @Mapping(target = "hasUsedStrike", source = "streak.hasUsedStrike")
    @Mapping(target = "lastCompletionDate", source = "streak.lastCompletionDate")
    RoutineDto toDto(RoutineEntity entity);

    /**
     * Converts StartRoutineRequest to StartRoutineCommand.
     */
    StartRoutineCommand toCommand(StartRoutineRequest request);

    /**
     * Parses comma-separated days string to Set of day names.
     */
    default Set<String> parseSpecificDays(String specificDays) {
        if (specificDays == null || specificDays.isBlank()) {
            return null;
        }
        return Arrays.stream(specificDays.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
}
