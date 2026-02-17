package com.ctoblue.plan91.adapter.in.web.mapper;

import com.ctoblue.plan91.adapter.in.web.dto.CreateHabitRequest;
import com.ctoblue.plan91.adapter.in.web.dto.EditHabitRequest;
import com.ctoblue.plan91.adapter.in.web.dto.HabitDto;
import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntity;
import com.ctoblue.plan91.application.usecase.habit.CreateHabitCommand;
import com.ctoblue.plan91.application.usecase.habit.EditHabitCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting between HabitEntity and HabitDto.
 */
@Mapper(componentModel = "spring")
public interface HabitDtoMapper {

    /**
     * Converts HabitEntity to HabitDto (for responses).
     */
    @Mapping(target = "id", expression = "java(entity.getId().toString())")
    @Mapping(target = "creatorId", expression = "java(entity.getCreator().getId().toString())")
    @Mapping(target = "numericUnit", source = "numericUnitName")
    @Mapping(target = "numericMin", source = "numericMinValue")
    @Mapping(target = "numericMax", source = "numericMaxValue")
    @Mapping(target = "sourceHabitId", expression = "java(entity.getSourceHabit() != null ? entity.getSourceHabit().getId().toString() : null)")
    HabitDto toDto(HabitEntity entity);

    /**
     * Converts CreateHabitRequest to CreateHabitCommand.
     */
    @Mapping(target = "numericUnit", source = "numericUnit")
    @Mapping(target = "numericMin", source = "numericMin")
    @Mapping(target = "numericMax", source = "numericMax")
    @Mapping(target = "numericTarget", source = "numericTarget")
    CreateHabitCommand toCommand(CreateHabitRequest request);

    /**
     * Converts EditHabitRequest to EditHabitCommand.
     */
    @Mapping(target = "habitId", source = "habitId")
    @Mapping(target = "numericUnit", source = "request.numericUnit")
    @Mapping(target = "numericMin", source = "request.numericMin")
    @Mapping(target = "numericMax", source = "request.numericMax")
    @Mapping(target = "numericTarget", source = "request.numericTarget")
    EditHabitCommand toCommand(String habitId, EditHabitRequest request);
}
