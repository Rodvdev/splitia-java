package com.splitia.controller;

import com.splitia.dto.request.CreateProjectRequest;
import com.splitia.dto.request.CreateTimeEntryRequest;
import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.ProjectResponse;
import com.splitia.dto.response.TimeEntryResponse;
import com.splitia.model.enums.ProjectStatus;
import com.splitia.service.ProjectService;
import com.splitia.service.TimeEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project management endpoints")
public class ProjectController {
    
    private final ProjectService projectService;
    private final TimeEntryService timeEntryService;
    
    @GetMapping
    @Operation(summary = "Get all projects with filters")
    public ResponseEntity<ApiResponse<Page<ProjectResponse>>> getAllProjects(
            Pageable pageable,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) UUID managerId) {
        Page<ProjectResponse> projects = projectService.getAllProjects(pageable, status, managerId);
        return ResponseEntity.ok(ApiResponse.success(projects));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(@PathVariable UUID id) {
        ProjectResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.success(project));
    }
    
    @PostMapping
    @Operation(summary = "Create a new project")
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(@Valid @RequestBody CreateProjectRequest request) {
        UUID createdById = getCurrentUserId();
        ProjectResponse project = projectService.createProject(request, createdById);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(project, "Project created successfully"));
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update project status")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProjectStatus(
            @PathVariable UUID id,
            @RequestParam ProjectStatus status) {
        ProjectResponse project = projectService.updateProjectStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(project, "Project status updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete project. Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        projectService.deleteProject(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Project permanently deleted" : "Project soft deleted"));
    }
    
    // Time Entries
    @GetMapping("/{id}/time-entries")
    @Operation(summary = "Get time entries for a project")
    public ResponseEntity<ApiResponse<Page<TimeEntryResponse>>> getProjectTimeEntries(
            @PathVariable UUID id,
            Pageable pageable) {
        Page<TimeEntryResponse> timeEntries = timeEntryService.getAllTimeEntries(pageable, id, null, null, null);
        return ResponseEntity.ok(ApiResponse.success(timeEntries));
    }
    
    @GetMapping("/time-entries")
    @Operation(summary = "Get all time entries with filters")
    public ResponseEntity<ApiResponse<Page<TimeEntryResponse>>> getAllTimeEntries(
            Pageable pageable,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Page<TimeEntryResponse> timeEntries = timeEntryService.getAllTimeEntries(pageable, projectId, userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(timeEntries));
    }
    
    @GetMapping("/time-entries/{id}")
    @Operation(summary = "Get time entry by ID")
    public ResponseEntity<ApiResponse<TimeEntryResponse>> getTimeEntryById(@PathVariable UUID id) {
        TimeEntryResponse timeEntry = timeEntryService.getTimeEntryById(id);
        return ResponseEntity.ok(ApiResponse.success(timeEntry));
    }
    
    @PostMapping("/time-entries")
    @Operation(summary = "Create a new time entry")
    public ResponseEntity<ApiResponse<TimeEntryResponse>> createTimeEntry(@Valid @RequestBody CreateTimeEntryRequest request) {
        UUID userId = getCurrentUserId();
        TimeEntryResponse timeEntry = timeEntryService.createTimeEntry(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(timeEntry, "Time entry created successfully"));
    }
    
    @DeleteMapping("/time-entries/{id}")
    @Operation(summary = "Delete time entry. Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteTimeEntry(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        timeEntryService.deleteTimeEntry(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Time entry permanently deleted" : "Time entry soft deleted"));
    }
    
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof com.splitia.security.CustomUserDetails) {
            com.splitia.security.CustomUserDetails userDetails = (com.splitia.security.CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUserId();
        }
        return null;
    }
}

