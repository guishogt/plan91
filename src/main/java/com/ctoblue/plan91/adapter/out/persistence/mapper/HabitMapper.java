package com.ctoblue.plan91.adapter.out.persistence.mapper;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntity;
import com.ctoblue.plan91.domain.habit.Habit;
import com.ctoblue.plan91.domain.habit.HabitId;
import com.ctoblue.plan91.domain.habit.NumericConfig;
import com.ctoblue.plan91.domain.habit.TrackingType;
import com.ctoblue.plan91.domain.habitpractitioner.HabitPractitionerId;
import org.mapstruct.*;

/**
 * MapStruct mapper for Habit domain aggregate.
 *
 * <p>Converts between:
 * <ul>
 *   <li>Habit (domain) ↔ HabitEntity (JPA)</li>
 * </ul>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface HabitMapper {

    /**
     * Converts domain Habit to JPA entity.
     *
     * @param habit the domain object
     * @return the JPA entity
     */
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "creator", ignore = true)  // Set separately
    @Mapping(target = "sourceHabit", ignore = true)  // Set separately
    @Mapping(target = "isPublic", source = "public")
    @Mapping(target = "isPrivate", source = "private")
    @Mapping(target = "numericUnitName", source = "numericConfig.unit")
    @Mapping(target = "numericMinValue", source = "numericConfig.min")
    @Mapping(target = "numericMaxValue", source = "numericConfig.max")
    @Mapping(target = "numericTarget", source = "numericConfig.target")
    HabitEntity toEntity(Habit habit);

    /**
     * Converts JPA entity to domain Habit.
     *
     * @param entity the JPA entity
     * @return the domain object
     */
    @Mapping(target = "id", expression = "java(new HabitId(entity.getId()))")
    @Mapping(target = "creator", expression = "java(new HabitPractitionerId(entity.getCreator().getId()))")
    @Mapping(target = "sourceHabit", expression = "java(entity.getSourceHabit() != null ? new HabitId(entity.getSourceHabit().getId()) : null)")
    @Mapping(target = "numericConfig", expression = "java(toNumericConfig(entity))")
    Habit toDomain(HabitEntity entity);

    /**
     * Converts entity numeric fields to NumericConfig value object.
     *
     * @param entity the JPA entity
     * @return NumericConfig or null if boolean habit
     */
    default NumericConfig toNumericConfig(HabitEntity entity) {
        if (entity.getTrackingType() == TrackingType.BOOLEAN) {
            return null;
        }
        return new NumericConfig(
                entity.getNumericUnitName(),
                entity.getNumericMinValue(),
                entity.getNumericMaxValue(),
                entity.getNumericTarget()
        );
    }
}
