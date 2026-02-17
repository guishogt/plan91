package com.ctoblue.plan91.adapter.in.web.controller;

import com.ctoblue.plan91.adapter.in.web.dto.RoutineDto;
import com.ctoblue.plan91.adapter.in.web.dto.StartRoutineRequest;
import com.ctoblue.plan91.adapter.in.web.dto.UpdateRoutineRequest;
import com.ctoblue.plan91.adapter.in.web.mapper.RoutineDtoMapper;
import com.ctoblue.plan91.adapter.out.persistence.entity.RecurrenceRuleEmbeddable;
import com.ctoblue.plan91.adapter.out.persistence.entity.RoutineEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.RoutineJpaRepository;
import com.ctoblue.plan91.application.usecase.routine.GetCalendarDataUseCase;
import com.ctoblue.plan91.application.usecase.routine.GetRoutineAnalyticsUseCase;
import com.ctoblue.plan91.application.usecase.routine.QueryRoutinesUseCase;
import com.ctoblue.plan91.application.usecase.routine.StartRoutineCommand;
import com.ctoblue.plan91.application.usecase.routine.StartRoutineUseCase;
import com.ctoblue.plan91.domain.routine.RoutineStatus;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for routine management.
 *
 * <p>Endpoints:
 * <ul>
 *   <li>POST /api/routines - Start a new 91-day routine</li>
 *   <li>PUT /api/routines/{id} - Update a routine</li>
 *   <li>POST /api/routines/{id}/archive - Archive a routine</li>
 *   <li>GET /api/routines/{id} - Get a routine by ID</li>
 *   <li>GET /api/routines - Get routines for a practitioner</li>
 *   <li>GET /api/routines/active - Get active routines</li>
 *   <li>GET /api/routines/date/{date} - Get routines scheduled for a date</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/routines")
public class RoutineController {

    private final StartRoutineUseCase startRoutineUseCase;
    private final QueryRoutinesUseCase queryRoutinesUseCase;
    private final GetCalendarDataUseCase getCalendarDataUseCase;
    private final GetRoutineAnalyticsUseCase getRoutineAnalyticsUseCase;
    private final RoutineDtoMapper routineDtoMapper;
    private final RoutineJpaRepository routineRepository;

    public RoutineController(
            StartRoutineUseCase startRoutineUseCase,
            QueryRoutinesUseCase queryRoutinesUseCase,
            GetCalendarDataUseCase getCalendarDataUseCase,
            GetRoutineAnalyticsUseCase getRoutineAnalyticsUseCase,
            RoutineDtoMapper routineDtoMapper,
            RoutineJpaRepository routineRepository) {
        this.startRoutineUseCase = startRoutineUseCase;
        this.queryRoutinesUseCase = queryRoutinesUseCase;
        this.getCalendarDataUseCase = getCalendarDataUseCase;
        this.getRoutineAnalyticsUseCase = getRoutineAnalyticsUseCase;
        this.routineDtoMapper = routineDtoMapper;
        this.routineRepository = routineRepository;
    }

    /**
     * Starts a new 91-day routine.
     *
     * @param request the start request
     * @return the created routine
     */
    @PostMapping
    public ResponseEntity<RoutineDto> startRoutine(@Valid @RequestBody StartRoutineRequest request) {
        StartRoutineCommand command = routineDtoMapper.toCommand(request);
        RoutineEntity routine = startRoutineUseCase.execute(command);
        RoutineDto dto = routineDtoMapper.toDto(routine);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Updates an existing routine.
     *
     * @param id the routine's ID
     * @param request the update request
     * @return the updated routine
     */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<RoutineDto> updateRoutine(
            @PathVariable String id,
            @Valid @RequestBody UpdateRoutineRequest request) {

        UUID routineId = UUID.fromString(id);
        RoutineEntity routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Routine not found: " + id));

        // Update fields if provided
        if (request.startDate() != null) {
            routine.setStartDate(request.startDate());
        }
        if (request.recurrenceType() != null) {
            RecurrenceRuleEmbeddable rule = new RecurrenceRuleEmbeddable();
            rule.setType(request.recurrenceType());
            // Convert Set to comma-separated string
            if (request.specificDays() != null && !request.specificDays().isEmpty()) {
                rule.setSpecificDays(String.join(",", request.specificDays()));
            }
            rule.setNthDay(request.nthDay());
            rule.setNthWeek(request.nthWeek());
            routine.setRecurrenceRule(rule);
        }
        if (request.targetDays() != null) {
            routine.setTargetDays(request.targetDays());
        }

        routine.setUpdatedAt(Instant.now());
        RoutineEntity saved = routineRepository.save(routine);
        RoutineDto dto = routineDtoMapper.toDto(saved);
        return ResponseEntity.ok(dto);
    }

    /**
     * Archives a routine.
     *
     * @param id the routine's ID
     * @return 204 No Content on success
     */
    @PostMapping("/{id}/archive")
    @Transactional
    public ResponseEntity<Void> archiveRoutine(@PathVariable String id) {
        UUID routineId = UUID.fromString(id);
        RoutineEntity routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Routine not found: " + id));

        routine.setStatus(RoutineStatus.ARCHIVED);
        routine.setUpdatedAt(Instant.now());
        routineRepository.save(routine);

        return ResponseEntity.noContent().build();
    }

    /**
     * Gets a routine by ID.
     *
     * @param id the routine's ID
     * @return the routine
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoutineDto> getRoutine(@PathVariable String id) {
        RoutineEntity routine = queryRoutinesUseCase.getRoutineById(id);
        RoutineDto dto = routineDtoMapper.toDto(routine);
        return ResponseEntity.ok(dto);
    }

    /**
     * Gets all routines for a practitioner.
     *
     * @param practitionerId the practitioner's ID
     * @return list of routines
     */
    @GetMapping
    public ResponseEntity<List<RoutineDto>> getAllRoutines(@RequestParam String practitionerId) {
        List<RoutineEntity> routines = queryRoutinesUseCase.getAllRoutines(practitionerId);
        List<RoutineDto> dtos = routines.stream()
                .map(routineDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Gets active routines for a practitioner.
     *
     * @param practitionerId the practitioner's ID
     * @return list of active routines
     */
    @GetMapping("/active")
    public ResponseEntity<List<RoutineDto>> getActiveRoutines(@RequestParam String practitionerId) {
        List<RoutineEntity> routines = queryRoutinesUseCase.getActiveRoutines(practitionerId);
        List<RoutineDto> dtos = routines.stream()
                .map(routineDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Gets routines by status.
     *
     * @param practitionerId the practitioner's ID
     * @param status the routine status
     * @return list of routines
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RoutineDto>> getRoutinesByStatus(
            @RequestParam String practitionerId,
            @PathVariable RoutineStatus status) {
        List<RoutineEntity> routines = queryRoutinesUseCase.getRoutinesByStatus(practitionerId, status);
        List<RoutineDto> dtos = routines.stream()
                .map(routineDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Gets routines scheduled for a specific date.
     *
     * @param practitionerId the practitioner's ID
     * @param date the date
     * @return list of routines
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<RoutineDto>> getRoutinesForDate(
            @RequestParam String practitionerId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<RoutineEntity> routines = queryRoutinesUseCase.getRoutinesForDate(practitionerId, date);
        List<RoutineDto> dtos = routines.stream()
                .map(routineDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Gets analytics data for a routine.
     *
     * @param id the routine's ID
     * @return analytics data
     */
    @GetMapping("/{id}/analytics")
    public ResponseEntity<GetRoutineAnalyticsUseCase.RoutineAnalytics> getAnalytics(@PathVariable String id) {
        GetRoutineAnalyticsUseCase.RoutineAnalytics analytics = getRoutineAnalyticsUseCase.execute(id);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Gets calendar data for a routine for a specific month.
     *
     * @param id the routine's ID
     * @param yearMonth the year-month (e.g., "2026-02")
     * @return calendar data
     */
    @GetMapping("/{id}/calendar")
    public ResponseEntity<GetCalendarDataUseCase.CalendarData> getCalendarData(
            @PathVariable String id,
            @RequestParam String yearMonth) {
        GetCalendarDataUseCase.CalendarData data = getCalendarDataUseCase.execute(id, yearMonth);
        return ResponseEntity.ok(data);
    }
}
