package com.ctoblue.plan91.adapter.out.persistence.mapper;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntryEntity;
import com.ctoblue.plan91.domain.habitentry.HabitEntry;
import com.ctoblue.plan91.domain.habitentry.HabitEntryId;
import com.ctoblue.plan91.domain.routine.RoutineId;
import org.mapstruct.*;

/**
 * MapStruct mapper for HabitEntry domain entity.
 *
 * <p>Converts between:
 * <ul>
 *   <li>HabitEntry (domain) ↔ HabitEntryEntity (JPA)</li>
 * </ul>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface HabitEntryMapper {

    /**
     * Converts domain HabitEntry to JPA entity.
     *
     * @param entry the domain object
     * @return the JPA entity
     */
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "routine", ignore = true)  // Set separately
    HabitEntryEntity toEntity(HabitEntry entry);

    /**
     * Converts JPA entity to domain HabitEntry.
     *
     * @param entity the JPA entity
     * @return the domain object
     */
    default HabitEntry toDomain(HabitEntryEntity entity) {
        if (entity == null) return null;

        RoutineId routineId = new RoutineId(entity.getRoutine().getId());

        // Use factory methods based on whether it's numeric or boolean
        if (entity.getValue() != null) {
            return HabitEntry.recordNumeric(
                    routineId,
                    entity.getDate(),
                    entity.getValue(),
                    entity.getNotes()
            );
        } else {
            return HabitEntry.recordBoolean(
                    routineId,
                    entity.getDate(),
                    entity.getNotes()
            );
        }
    }
}
