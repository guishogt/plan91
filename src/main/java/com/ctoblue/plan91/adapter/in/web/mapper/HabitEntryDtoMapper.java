package com.ctoblue.plan91.adapter.in.web.mapper;

import com.ctoblue.plan91.adapter.in.web.dto.CompleteEntryRequest;
import com.ctoblue.plan91.adapter.in.web.dto.HabitEntryDto;
import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntryEntity;
import com.ctoblue.plan91.application.usecase.routine.CompleteEntryCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting between HabitEntryEntity and HabitEntryDto.
 */
@Mapper(componentModel = "spring")
public interface HabitEntryDtoMapper {

    /**
     * Converts HabitEntryEntity to HabitEntryDto (for responses).
     */
    @Mapping(target = "id", expression = "java(entity.getId().toString())")
    @Mapping(target = "routineId", expression = "java(entity.getRoutine().getId().toString())")
    HabitEntryDto toDto(HabitEntryEntity entity);

    /**
     * Converts CompleteEntryRequest to CompleteEntryCommand.
     */
    CompleteEntryCommand toCommand(CompleteEntryRequest request);
}
