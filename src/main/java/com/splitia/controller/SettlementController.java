package com.splitia.controller;

import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.SettlementResponse;
import com.splitia.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
@Tag(name = "Settlements", description = "Settlement management endpoints")
public class SettlementController {
    
    private final SettlementService settlementService;
    
    @GetMapping("/{id}")
    @Operation(summary = "Get settlement by ID")
    public ResponseEntity<ApiResponse<SettlementResponse>> getSettlementById(@PathVariable UUID id) {
        SettlementResponse settlement = settlementService.getSettlementById(id);
        return ResponseEntity.ok(ApiResponse.success(settlement));
    }
}

