package com.splitia.controller;

import com.splitia.dto.response.ApiResponse;
import com.splitia.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI", description = "AI assistant endpoints")
public class AIController {
    
    private final AIService aiService;
    
    @PostMapping("/process-message")
    @Operation(summary = "Process message with AI")
    public ResponseEntity<ApiResponse<Void>> processMessage(@RequestBody String message) {
        aiService.processMessage(message);
        return ResponseEntity.ok(ApiResponse.success(null, "Message processed"));
    }
}

