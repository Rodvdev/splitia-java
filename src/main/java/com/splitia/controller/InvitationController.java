package com.splitia.controller;

import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.GroupInvitationResponse;
import com.splitia.model.enums.InvitationStatus;
import com.splitia.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
@Tag(name = "Invitations", description = "Group invitation endpoints")
public class InvitationController {

    private final GroupService groupService;

    @GetMapping("/me")
    @Operation(summary = "Get my invitations (pending by default)")
    public ResponseEntity<ApiResponse<List<GroupInvitationResponse>>> getMyInvitations(
            @RequestParam(name = "status", required = false) InvitationStatus status
    ) {
        List<GroupInvitationResponse> invitations = groupService.getMyInvitations(status);
        return ResponseEntity.ok(ApiResponse.success(invitations));
    }
}