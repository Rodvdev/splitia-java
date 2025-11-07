package com.splitia.controller;

import com.splitia.dto.response.ApiResponse;
import com.splitia.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management endpoints")
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/users")
    @Operation(summary = "Get all users (Admin only)")
    public ResponseEntity<ApiResponse<Void>> getAllUsers() {
        adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

