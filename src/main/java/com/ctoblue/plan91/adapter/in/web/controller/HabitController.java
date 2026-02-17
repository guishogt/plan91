package com.ctoblue.plan91.adapter.in.web.controller;

import com.ctoblue.plan91.adapter.in.web.dto.CreateHabitRequest;
import com.ctoblue.plan91.adapter.in.web.dto.EditHabitRequest;
import com.ctoblue.plan91.adapter.in.web.dto.HabitDto;
import com.ctoblue.plan91.adapter.in.web.mapper.HabitDtoMapper;
import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntity;
import com.ctoblue.plan91.application.usecase.habit.CopyHabitUseCase;
import com.ctoblue.plan91.application.usecase.habit.CreateHabitCommand;
import com.ctoblue.plan91.application.usecase.habit.CreateHabitUseCase;
import com.ctoblue.plan91.application.usecase.habit.DeleteHabitUseCase;
import com.ctoblue.plan91.application.usecase.habit.EditHabitCommand;
import com.ctoblue.plan91.application.usecase.habit.EditHabitUseCase;
import com.ctoblue.plan91.application.usecase.habit.QueryHabitsUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for habit management.
 *
 * <p>Endpoints:
 * <ul>
 *   <li>POST /api/habits - Create a new habit</li>
 *   <li>GET /api/habits/{id} - Get a habit by ID</li>
 *   <li>GET /api/habits - Get habits (by practitioner or search)</li>
 *   <li>GET /api/habits/public - Get all public habits</li>
 *   <li>PUT /api/habits/{id} - Edit a habit</li>
 *   <li>DELETE /api/habits/{id} - Delete a habit</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final CreateHabitUseCase createHabitUseCase;
    private final EditHabitUseCase editHabitUseCase;
    private final DeleteHabitUseCase deleteHabitUseCase;
    private final QueryHabitsUseCase queryHabitsUseCase;
    private final CopyHabitUseCase copyHabitUseCase;
    private final HabitDtoMapper habitDtoMapper;

    public HabitController(
            CreateHabitUseCase createHabitUseCase,
            EditHabitUseCase editHabitUseCase,
            DeleteHabitUseCase deleteHabitUseCase,
            QueryHabitsUseCase queryHabitsUseCase,
            CopyHabitUseCase copyHabitUseCase,
            HabitDtoMapper habitDtoMapper) {
        this.createHabitUseCase = createHabitUseCase;
        this.editHabitUseCase = editHabitUseCase;
        this.deleteHabitUseCase = deleteHabitUseCase;
        this.queryHabitsUseCase = queryHabitsUseCase;
        this.copyHabitUseCase = copyHabitUseCase;
        this.habitDtoMapper = habitDtoMapper;
    }

    /**
     * Creates a new habit.
     *
     * @param request the create request
     * @return the created habit
     */
    @PostMapping
    public ResponseEntity<HabitDto> createHabit(@Valid @RequestBody CreateHabitRequest request) {
        CreateHabitCommand command = habitDtoMapper.toCommand(request);
        HabitEntity habit = createHabitUseCase.execute(command);
        HabitDto dto = habitDtoMapper.toDto(habit);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Gets a habit by ID.
     *
     * @param id the habit's ID
     * @return the habit
     */
    @GetMapping("/{id}")
    public ResponseEntity<HabitDto> getHabit(@PathVariable String id) {
        HabitEntity habit = queryHabitsUseCase.getHabitById(id);
        HabitDto dto = habitDtoMapper.toDto(habit);
        return ResponseEntity.ok(dto);
    }

    /**
     * Gets habits for a practitioner.
     *
     * @param practitionerId the practitioner's ID
     * @return list of habits
     */
    @GetMapping
    public ResponseEntity<List<HabitDto>> getHabitsForPractitioner(
            @RequestParam String practitionerId) {
        List<HabitEntity> habits = queryHabitsUseCase.getHabitsForPractitioner(practitionerId);
        List<HabitDto> dtos = habits.stream()
                .map(habitDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Gets all public habits.
     *
     * @return list of public habits
     */
    @GetMapping("/public")
    public ResponseEntity<List<HabitDto>> getPublicHabits() {
        List<HabitEntity> habits = queryHabitsUseCase.getPublicHabits();
        List<HabitDto> dtos = habits.stream()
                .map(habitDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Searches habits by name.
     *
     * @param q the search query
     * @return list of matching habits
     */
    @GetMapping("/search")
    public ResponseEntity<List<HabitDto>> searchHabits(@RequestParam String q) {
        List<HabitEntity> habits = queryHabitsUseCase.searchHabits(q);
        List<HabitDto> dtos = habits.stream()
                .map(habitDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Edits a habit.
     *
     * @param id the habit's ID
     * @param request the edit request
     * @return the updated habit
     */
    @PutMapping("/{id}")
    public ResponseEntity<HabitDto> editHabit(
            @PathVariable String id,
            @Valid @RequestBody EditHabitRequest request) {
        EditHabitCommand command = habitDtoMapper.toCommand(id, request);
        HabitEntity habit = editHabitUseCase.execute(command);
        HabitDto dto = habitDtoMapper.toDto(habit);
        return ResponseEntity.ok(dto);
    }

    /**
     * Deletes a habit.
     *
     * @param id the habit's ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@PathVariable String id) {
        deleteHabitUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Copies a public habit to a practitioner's library.
     *
     * @param id the habit's ID to copy
     * @param practitionerId the practitioner copying the habit
     * @return the newly created habit
     */
    @PostMapping("/{id}/copy")
    public ResponseEntity<HabitDto> copyHabit(
            @PathVariable String id,
            @RequestParam String practitionerId) {
        HabitEntity habit = copyHabitUseCase.execute(id, practitionerId);
        HabitDto dto = habitDtoMapper.toDto(habit);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
