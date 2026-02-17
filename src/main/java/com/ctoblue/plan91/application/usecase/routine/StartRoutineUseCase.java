package com.ctoblue.plan91.application.usecase.routine;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntity;
import com.ctoblue.plan91.adapter.out.persistence.entity.HabitPractitionerEntity;
import com.ctoblue.plan91.adapter.out.persistence.entity.RoutineEntity;
import com.ctoblue.plan91.adapter.out.persistence.mapper.RoutineMapper;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitJpaRepository;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitPractitionerJpaRepository;
import com.ctoblue.plan91.adapter.out.persistence.repository.RoutineJpaRepository;
import com.ctoblue.plan91.domain.habit.HabitId;
import com.ctoblue.plan91.domain.habitpractitioner.HabitPractitionerId;
import com.ctoblue.plan91.domain.routine.DayOfWeek;
import com.ctoblue.plan91.domain.routine.RecurrenceRule;
import com.ctoblue.plan91.domain.routine.RecurrenceType;
import com.ctoblue.plan91.domain.routine.Routine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Use case for starting a new 91-day routine.
 *
 * <p>A routine represents a practitioner's commitment to practice a habit
 * for 91 days with specific recurrence rules.
 */
@Service
public class StartRoutineUseCase {

    private final RoutineJpaRepository routineRepository;
    private final HabitJpaRepository habitRepository;
    private final HabitPractitionerJpaRepository practitionerRepository;
    private final RoutineMapper routineMapper;

    public StartRoutineUseCase(
            RoutineJpaRepository routineRepository,
            HabitJpaRepository habitRepository,
            HabitPractitionerJpaRepository practitionerRepository,
            RoutineMapper routineMapper) {
        this.routineRepository = routineRepository;
        this.habitRepository = habitRepository;
        this.practitionerRepository = practitionerRepository;
        this.routineMapper = routineMapper;
    }

    /**
     * Starts a new routine.
     *
     * @param command the start command
     * @return the created routine entity
     * @throws IllegalArgumentException if habit or practitioner not found
     */
    @Transactional
    public RoutineEntity execute(StartRoutineCommand command) {
        // 1. Validate practitioner exists
        UUID practitionerId = UUID.fromString(command.practitionerId());
        HabitPractitionerEntity practitioner = practitionerRepository.findById(practitionerId)
                .orElseThrow(() -> new IllegalArgumentException("Practitioner not found: " + practitionerId));

        // 2. Validate habit exists
        UUID habitId = UUID.fromString(command.habitId());
        HabitEntity habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Habit not found: " + habitId));

        // 3. Build recurrence rule from command
        RecurrenceRule recurrenceRule = buildRecurrenceRule(command);

        // 4. Create domain Routine using factory method
        Routine routine = Routine.start(
                new HabitId(habitId),
                new HabitPractitionerId(practitionerId),
                recurrenceRule,
                command.startDate(),
                command.targetDays()
        );

        // 5. Convert to entity using mapper
        RoutineEntity routineEntity = routineMapper.toEntity(routine);

        // 6. Set relationships (mapper ignores these)
        routineEntity.setHabit(habit);
        routineEntity.setPractitioner(practitioner);

        // 7. Save and return
        return routineRepository.save(routineEntity);
    }

    /**
     * Builds a RecurrenceRule from the command.
     */
    private RecurrenceRule buildRecurrenceRule(StartRoutineCommand command) {
        RecurrenceType type = command.recurrenceType();

        return switch (type) {
            case DAILY -> RecurrenceRule.daily();
            case WEEKDAYS -> RecurrenceRule.weekdays();
            case WEEKENDS -> RecurrenceRule.weekends();
            case SPECIFIC_DAYS -> {
                if (command.specificDays() == null || command.specificDays().isEmpty()) {
                    throw new IllegalArgumentException("Specific days required for SPECIFIC_DAYS recurrence");
                }
                Set<DayOfWeek> days = command.specificDays().stream()
                        .map(DayOfWeek::valueOf)
                        .collect(Collectors.toSet());
                yield RecurrenceRule.specificDays(days);
            }
            case NTH_DAY_OF_MONTH -> {
                if (command.nthDay() == null || command.nthWeek() == null) {
                    throw new IllegalArgumentException("nthDay and nthWeek required for NTH_DAY_OF_MONTH recurrence");
                }
                DayOfWeek day = DayOfWeek.valueOf(command.nthDay());
                yield RecurrenceRule.nthDayOfMonth(day, command.nthWeek());
            }
            case TIMES_PER_WEEK_1 -> RecurrenceRule.timesPerWeek(1);
            case TIMES_PER_WEEK_3 -> RecurrenceRule.timesPerWeek(3);
            case TIMES_PER_WEEK_4 -> RecurrenceRule.timesPerWeek(4);
            case TIMES_PER_WEEK_5 -> RecurrenceRule.timesPerWeek(5);
            case TIMES_PER_WEEK_6 -> RecurrenceRule.timesPerWeek(6);
        };
    }
}
