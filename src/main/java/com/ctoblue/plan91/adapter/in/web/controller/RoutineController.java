package com.ctoblue.plan91.adapter.in.web.controller;

import com.ctoblue.plan91.adapter.in.web.dto.RoutineDto;
import com.ctoblue.plan91.adapter.in.web.dto.StartRoutineRequest;
import com.ctoblue.plan91.adapter.in.web.mapper.RoutineDtoMapper;
import com.ctoblue.plan91.adapter.out.persistence.entity.RoutineEntity;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for routine management.
 *
 * <p>Endpoints:
 * <ul>
 *   <li>POST /api/routines - Start a new 91-day routine</li>
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

    public RoutineController(
            StartRoutineUseCase startRoutineUseCase,
            QueryRoutinesUseCase queryRoutinesUseCase,
            GetCalendarDataUseCase getCalendarDataUseCase,
            GetRoutineAnalyticsUseCase getRoutineAnalyticsUseCase,
            RoutineDtoMapper routineDtoMapper) {
        this.startRoutineUseCase = startRoutineUseCase;
        this.queryRoutinesUseCase = queryRoutinesUseCase;
        this.getCalendarDataUseCase = getCalendarDataUseCase;
        this.getRoutineAnalyticsUseCase = getRoutineAnalyticsUseCase;
        this.routineDtoMapper = routineDtoMapper;
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
