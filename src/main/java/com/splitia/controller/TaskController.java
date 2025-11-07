package com.splitia.controller;

import com.splitia.dto.request.CreateTaskRequest;
import com.splitia.dto.request.UpdateTaskRequest;
import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.TaskResponse;
import com.splitia.model.enums.TaskStatus;
import com.splitia.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management endpoints (Kanban)")
public class TaskController {
    
    private final TaskService taskService;
    
    @GetMapping("/group/{groupId}")
    @Operation(summary = "Get tasks by group (paginated)")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getTasksByGroup(
            @PathVariable UUID groupId,
            Pageable pageable) {
        Page<TaskResponse> tasks = taskService.getTasksByGroup(groupId, pageable);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @GetMapping("/group/{groupId}/status/{status}")
    @Operation(summary = "Get tasks by group and status (for Kanban columns)")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByGroupAndStatus(
            @PathVariable UUID groupId,
            @PathVariable TaskStatus status) {
        List<TaskResponse> tasks = taskService.getTasksByGroupAndStatus(groupId, status);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(@PathVariable UUID id) {
        TaskResponse task = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(task));
    }
    
    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@Valid @RequestBody CreateTaskRequest request) {
        TaskResponse task = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(task, "Task created successfully"));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update task")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request) {
        TaskResponse task = taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success(task, "Task updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable UUID id) {
        taskService.softDeleteTask(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Task deleted successfully"));
    }
}

