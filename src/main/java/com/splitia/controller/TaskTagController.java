package com.splitia.controller;

import com.splitia.dto.request.CreateTaskTagRequest;
import com.splitia.dto.request.UpdateTaskTagRequest;
import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.TaskTagResponse;
import com.splitia.service.TaskTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/task-tags")
@RequiredArgsConstructor
@Tag(name = "Task Tags", description = "Task tag management endpoints")
public class TaskTagController {
    
    private final TaskTagService taskTagService;
    
    @GetMapping("/group/{groupId}")
    @Operation(summary = "Get tags by group")
    public ResponseEntity<ApiResponse<List<TaskTagResponse>>> getTagsByGroup(@PathVariable UUID groupId) {
        List<TaskTagResponse> tags = taskTagService.getTagsByGroup(groupId);
        return ResponseEntity.ok(ApiResponse.success(tags));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get tag by ID")
    public ResponseEntity<ApiResponse<TaskTagResponse>> getTagById(@PathVariable UUID id) {
        TaskTagResponse tag = taskTagService.getTagById(id);
        return ResponseEntity.ok(ApiResponse.success(tag));
    }
    
    @PostMapping
    @Operation(summary = "Create a new tag")
    public ResponseEntity<ApiResponse<TaskTagResponse>> createTag(@Valid @RequestBody CreateTaskTagRequest request) {
        TaskTagResponse tag = taskTagService.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(tag, "Tag created successfully"));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update tag")
    public ResponseEntity<ApiResponse<TaskTagResponse>> updateTag(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskTagRequest request) {
        TaskTagResponse tag = taskTagService.updateTag(id, request);
        return ResponseEntity.ok(ApiResponse.success(tag, "Tag updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tag (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteTag(@PathVariable UUID id) {
        taskTagService.softDeleteTag(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Tag deleted successfully"));
    }
}

