package com.ctoblue.plan91.adapter.out.persistence.mapper;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitPractitionerEntity;
import com.ctoblue.plan91.domain.habitpractitioner.Email;
import com.ctoblue.plan91.domain.habitpractitioner.HabitPractitioner;
import com.ctoblue.plan91.domain.habitpractitioner.HabitPractitionerId;
import org.mapstruct.*;

/**
 * MapStruct mapper for HabitPractitioner domain aggregate.
 *
 * <p>Converts between:
 * <ul>
 *   <li>HabitPractitioner (domain) ↔ HabitPractitionerEntity (JPA)</li>
 * </ul>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface HabitPractitionerMapper {

    /**
     * Converts domain HabitPractitioner to JPA entity.
     *
     * @param practitioner the domain object
     * @return the JPA entity
     */
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "email", source = "email.value")
    @Mapping(target = "user", ignore = true)  // Set separately when persisting
    HabitPractitionerEntity toEntity(HabitPractitioner practitioner);

    /**
     * Converts JPA entity to domain HabitPractitioner.
     *
     * @param entity the JPA entity
     * @return the domain object
     */
    @Mapping(target = "id", expression = "java(new HabitPractitionerId(entity.getId()))")
    @Mapping(target = "email", expression = "java(new Email(entity.getEmail()))")
    HabitPractitioner toDomain(HabitPractitionerEntity entity);
}
