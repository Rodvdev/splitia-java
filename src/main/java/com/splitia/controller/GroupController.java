package com.splitia.controller;

import com.splitia.dto.request.CreateGroupRequest;
import com.splitia.dto.request.UpdateGroupRequest;
import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.GroupResponse;
import com.splitia.service.GroupService;
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
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "Groups", description = "Group management endpoints")
public class GroupController {
    
    private final GroupService groupService;
    
    @GetMapping
    @Operation(summary = "Get user's groups")
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getUserGroups() {
        List<GroupResponse> groups = groupService.getUserGroups();
        return ResponseEntity.ok(ApiResponse.success(groups));
    }
    
    @PostMapping
    @Operation(summary = "Create a new group")
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        GroupResponse group = groupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(group, "Group created successfully"));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get group by ID")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroupById(@PathVariable UUID id) {
        GroupResponse group = groupService.getGroupById(id);
        return ResponseEntity.ok(ApiResponse.success(group));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update group")
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGroupRequest request) {
        GroupResponse group = groupService.updateGroup(id, request);
        return ResponseEntity.ok(ApiResponse.success(group, "Group updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete group")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(@PathVariable UUID id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Group deleted successfully"));
    }
    
    @PostMapping("/{id}/members")
    @Operation(summary = "Add member to group")
    public ResponseEntity<ApiResponse<Void>> addMember(
            @PathVariable UUID id,
            @RequestParam UUID userId) {
        groupService.addMember(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Member added successfully"));
    }
    
    @DeleteMapping("/{id}/members/{userId}")
    @Operation(summary = "Remove member from group")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable UUID id,
            @PathVariable UUID userId) {
        groupService.removeMember(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Member removed successfully"));
    }
}

