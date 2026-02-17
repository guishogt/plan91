package com.ctoblue.plan91.adapter.in.web.controller;

import com.ctoblue.plan91.adapter.in.web.dto.CompleteEntryRequest;
import com.ctoblue.plan91.adapter.in.web.dto.HabitEntryDto;
import com.ctoblue.plan91.adapter.in.web.mapper.HabitEntryDtoMapper;
import com.ctoblue.plan91.adapter.out.persistence.entity.HabitEntryEntity;
import com.ctoblue.plan91.application.usecase.routine.CompleteEntryCommand;
import com.ctoblue.plan91.application.usecase.routine.CompleteEntryUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for habit entry management.
 *
 * <p>Endpoints:
 * <ul>
 *   <li>POST /api/entries - Complete a habit entry</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/entries")
public class HabitEntryController {

    private final CompleteEntryUseCase completeEntryUseCase;
    private final HabitEntryDtoMapper habitEntryDtoMapper;

    public HabitEntryController(
            CompleteEntryUseCase completeEntryUseCase,
            HabitEntryDtoMapper habitEntryDtoMapper) {
        this.completeEntryUseCase = completeEntryUseCase;
        this.habitEntryDtoMapper = habitEntryDtoMapper;
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
}
