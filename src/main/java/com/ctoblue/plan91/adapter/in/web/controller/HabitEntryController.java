package com.ctoblue.plan91.adapter.in.web.controller;

import com.ctoblue.plan91.adapter.in.web.dto.CompleteEntryRequest;
import com.ctoblue.plan91.adapter.in.web.dto.HabitEntryDto;
import com.ctoblue.plan91.adapter.in.web.mapper.HabitEntryDtoMapper;
import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntryEntity;
import com.ctoblue.plan91.application.usecase.routine.CompleteEntryCommand;
import com.ctoblue.plan91.application.usecase.routine.CompleteEntryUseCase;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitEntryJpaRepository;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for habit entry management.
 *
 * <p>Endpoints:
 * <ul>
 *   <li>POST /api/entries - Complete a habit entry</li>
 *   <li>GET /api/entries/completed-routines - Get routine IDs completed on a date</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/entries")
public class HabitEntryController {

    private final CompleteEntryUseCase completeEntryUseCase;
    private final HabitEntryDtoMapper habitEntryDtoMapper;
    private final HabitEntryJpaRepository entryRepository;

    public HabitEntryController(
            CompleteEntryUseCase completeEntryUseCase,
            HabitEntryDtoMapper habitEntryDtoMapper,
            HabitEntryJpaRepository entryRepository) {
        this.completeEntryUseCase = completeEntryUseCase;
        this.habitEntryDtoMapper = habitEntryDtoMapper;
        this.entryRepository = entryRepository;
    }

    /**
     * Completes a habit entry for a specific date.
     *
     * @param request the completion request
     * @return the created entry
     */
    @PostMapping
    public ResponseEntity<HabitEntryDto> completeEntry(@Valid @RequestBody CompleteEntryRequest request) {
        CompleteEntryCommand command = habitEntryDtoMapper.toCommand(request);
        HabitEntryEntity entry = completeEntryUseCase.execute(command);
        HabitEntryDto dto = habitEntryDtoMapper.toDto(entry);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Gets routine IDs that have been completed on a specific date.
     *
     * @param routineIds comma-separated list of routine IDs to check
     * @param date the date to check
     * @return set of routine IDs that have entries on the given date
     */
    @GetMapping("/completed-routines")
    public ResponseEntity<Set<String>> getCompletedRoutinesForDate(
            @RequestParam List<String> routineIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<UUID> uuids = routineIds.stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());

        Set<String> completedIds = entryRepository.findByRoutineIdInAndDate(uuids, date)
                .stream()
                .map(entry -> entry.getRoutine().getId().toString())
                .collect(Collectors.toSet());

        return ResponseEntity.ok(completedIds);
    }
}
