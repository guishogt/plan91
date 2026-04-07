package com.ctoblue.plan91.adapter.in.web.controller;

import com.ctoblue.plan91.adapter.in.web.dto.CompleteEntryRequest;
import com.ctoblue.plan91.adapter.in.web.dto.HabitEntryDto;
import com.ctoblue.plan91.adapter.in.web.mapper.HabitEntryDtoMapper;
import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntryEntity;
import com.ctoblue.plan91.application.usecase.routine.CompleteEntryCommand;
import com.ctoblue.plan91.application.usecase.routine.CompleteEntryUseCase;
import com.ctoblue.plan91.adapter.out.persistence.repository.HabitEntryJpaRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for habit entry management.
 *
 * <p>Endpoints:
 * <ul>
 *   <li>POST /api/entries - Complete a habit entry</li>
 *   <li>DELETE /api/entries - Remove a habit entry (uncomplete)</li>
 *   <li>GET /api/entries/completed-routines - Get routine IDs completed on a date</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/entries")
public class HabitEntryController {

    private final CompleteEntryUseCase completeEntryUseCase;
    private final HabitEntryDtoMapper habitEntryDtoMapper;
    private final HabitEntryJpaRepository entryRepository;
    private final com.ctoblue.plan91.adapter.out.persistence.repository.RoutineJpaRepository routineRepository;

    public HabitEntryController(
            CompleteEntryUseCase completeEntryUseCase,
            HabitEntryDtoMapper habitEntryDtoMapper,
            HabitEntryJpaRepository entryRepository,
            com.ctoblue.plan91.adapter.out.persistence.repository.RoutineJpaRepository routineRepository) {
        this.completeEntryUseCase = completeEntryUseCase;
        this.habitEntryDtoMapper = habitEntryDtoMapper;
        this.entryRepository = entryRepository;
        this.routineRepository = routineRepository;
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
     * Removes a habit entry (uncompletes it) for a specific date.
     *
     * @param routineId the routine ID
     * @param date the date to uncomplete
     * @return 204 No Content on success
     */
    @DeleteMapping
    public ResponseEntity<Void> uncompleteEntry(
            @RequestParam String routineId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        UUID routineUuid = UUID.fromString(routineId);
        entryRepository.findByRoutineIdAndDate(routineUuid, date)
                .ifPresent(entryRepository::delete);

        return ResponseEntity.noContent().build();
    }

    /**
     * Gets routine IDs that have been completed on a specific date.
     * Only returns IDs where the entry has completed=true.
     *
     * @param routineIds comma-separated list of routine IDs to check
     * @param date the date to check
     * @return set of routine IDs that have been completed on the given date
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
                .filter(HabitEntryEntity::getCompleted) // Only completed entries
                .map(entry -> entry.getRoutine().getId().toString())
                .collect(Collectors.toSet());

        return ResponseEntity.ok(completedIds);
    }

    /**
     * Gets the status of routines for a specific date.
     * Returns a map of routine ID to status (completed, skipped, or none).
     *
     * @param routineIds comma-separated list of routine IDs to check
     * @param date the date to check
     * @return map of routine ID to entry status info
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, EntryStatus>> getRoutineStatusForDate(
            @RequestParam List<String> routineIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<UUID> uuids = routineIds.stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());

        Map<String, EntryStatus> statusMap = new HashMap<>();

        for (HabitEntryEntity entry : entryRepository.findByRoutineIdInAndDate(uuids, date)) {
            String routineId = entry.getRoutine().getId().toString();
            statusMap.put(routineId, new EntryStatus(
                    entry.getCompleted(),
                    entry.getNotes()
            ));
        }

        return ResponseEntity.ok(statusMap);
    }

    /**
     * Records that a habit was skipped with an optional note.
     * Creates an entry with completed=false.
     *
     * @param request the skip request (routineId, date, notes)
     * @return the created entry
     */
    @PostMapping("/skip")
    public ResponseEntity<HabitEntryDto> skipEntry(@Valid @RequestBody SkipEntryRequest request) {
        UUID routineId = UUID.fromString(request.routineId());
        LocalDate date = request.date() != null ? request.date() : LocalDate.now();

        // Check if entry already exists
        var existingEntry = entryRepository.findByRoutineIdAndDate(routineId, date);

        HabitEntryEntity entry;
        if (existingEntry.isPresent()) {
            // Update existing entry to skipped
            entry = existingEntry.get();
            entry.setCompleted(false);
            entry.setNotes(request.notes());
        } else {
            // Create new skipped entry
            var routineEntity = routineRepository.findById(routineId)
                    .orElseThrow(() -> new IllegalArgumentException("Routine not found: " + routineId));

            entry = HabitEntryEntity.builder()
                    .routine(routineEntity)
                    .date(date)
                    .completed(false)
                    .notes(request.notes())
                    .build();
        }

        entry = entryRepository.save(entry);
        HabitEntryDto dto = habitEntryDtoMapper.toDto(entry);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Simple record for entry status response.
     */
    public record EntryStatus(boolean completed, String notes) {}

    /**
     * Request DTO for skipping an entry.
     */
    public record SkipEntryRequest(
            @NotBlank(message = "Routine ID is required")
            String routineId,
            LocalDate date,
            String notes
    ) {}
}
