package com.ctoblue.plan91.application.usecase.routine;

import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntryEntity;
import com.ctoblue.plan91.adapter.out.persistence.entity.RoutineEntity;
import com.ctoblue.plan91.adapter.out.persistence.mapper.RoutineMapper;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitEntryJpaRepository;
import com.ctoblue.plan91.adapter.out.persistence.repository.RoutineJpaRepository;
import com.ctoblue.plan91.domain.routine.Routine;
import com.ctoblue.plan91.domain.routine.RoutineId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Use case for completing a daily habit entry.
 *
 * <p>This use case:
 * <ul>
 *   <li>Records the completion in habit_entries table</li>
 *   <li>Updates the routine's streak tracking</li>
 *   <li>Handles the one-strike rule</li>
 * </ul>
 */
@Service
public class CompleteEntryUseCase {

    private final RoutineJpaRepository routineRepository;
    private final HabitEntryJpaRepository entryRepository;
    private final RoutineMapper routineMapper;

    public CompleteEntryUseCase(
            RoutineJpaRepository routineRepository,
            HabitEntryJpaRepository entryRepository,
            RoutineMapper routineMapper) {
        this.routineRepository = routineRepository;
        this.entryRepository = entryRepository;
        this.routineMapper = routineMapper;
    }

    /**
     * Completes a habit entry.
     *
     * @param command the completion command
     * @return the created entry entity
     * @throws IllegalArgumentException if routine not found or already completed today
     */
    @Transactional
    public HabitEntryEntity execute(CompleteEntryCommand command) {
        // 1. Validate routine exists
        UUID routineId = UUID.fromString(command.routineId());
        RoutineEntity routineEntity = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Routine not found: " + routineId));

        // 2. Check if already completed on this date
        if (entryRepository.existsByRoutineIdAndDate(routineId, command.date())) {
            throw new IllegalArgumentException("Already completed on " + command.date());
        }

        // 3. Convert to domain and record completion
        Routine routine = routineMapper.toDomain(routineEntity);
        routine.recordCompletion(command.date());

        // 4. Update routine entity with new streak
        RoutineEntity updatedRoutine = routineMapper.toEntity(routine);
        updatedRoutine.setId(routineEntity.getId());
        updatedRoutine.setHabit(routineEntity.getHabit());
        updatedRoutine.setPractitioner(routineEntity.getPractitioner());
        routineRepository.save(updatedRoutine);

        // 5. Create habit entry
        HabitEntryEntity entry = HabitEntryEntity.builder()
                .routine(routineEntity)
                .date(command.date())
                .completed(true)
                .value(command.value())
                .notes(command.notes())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        // 6. Save and return
        return entryRepository.save(entry);
    }
}
