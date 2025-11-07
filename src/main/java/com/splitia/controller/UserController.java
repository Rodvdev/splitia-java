package com.splitia.controller;

import com.splitia.dto.request.ChangePasswordRequest;
import com.splitia.dto.request.UpdatePreferencesRequest;
import com.splitia.dto.request.UpdateProfileRequest;
import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.UserResponse;
import com.splitia.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        UserResponse user = userService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PutMapping("/me")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserResponse user = userService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success(user, "Profile updated successfully"));
    }
    
    @PutMapping("/me/password")
    @Operation(summary = "Change user password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }
    
    @PutMapping("/me/preferences")
    @Operation(summary = "Update user preferences")
    public ResponseEntity<ApiResponse<UserResponse>> updatePreferences(@Valid @RequestBody UpdatePreferencesRequest request) {
        UserResponse user = userService.updatePreferences(request);
        return ResponseEntity.ok(ApiResponse.success(user, "Preferences updated successfully"));
    }
}

