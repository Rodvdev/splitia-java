package com.splitia.controller;

import com.splitia.dto.request.CreateSettlementRequest;
import com.splitia.dto.request.UpdateSettlementRequest;
import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.SettlementResponse;
import com.splitia.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
@Tag(name = "Settlements", description = "Settlement management endpoints")
public class SettlementController {
    
    private final SettlementService settlementService;
    
    @GetMapping
    @Operation(summary = "Get settlements for a group (paginated)")
    public ResponseEntity<ApiResponse<Page<SettlementResponse>>> getSettlements(
            @RequestParam UUID groupId,
            Pageable pageable) {
        Page<SettlementResponse> settlements = settlementService.getSettlements(groupId, pageable);
        return ResponseEntity.ok(ApiResponse.success(settlements));
    }
    
    @PostMapping
    @Operation(summary = "Create a new settlement")
    public ResponseEntity<ApiResponse<SettlementResponse>> createSettlement(@Valid @RequestBody CreateSettlementRequest request) {
        SettlementResponse settlement = settlementService.createSettlement(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(settlement, "Settlement created successfully"));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get settlement by ID")
    public ResponseEntity<ApiResponse<SettlementResponse>> getSettlementById(@PathVariable UUID id) {
        SettlementResponse settlement = settlementService.getSettlementById(id);
        return ResponseEntity.ok(ApiResponse.success(settlement));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update settlement (Admin only)")
    public ResponseEntity<ApiResponse<SettlementResponse>> updateSettlement(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSettlementRequest request) {
        SettlementResponse settlement = settlementService.updateSettlement(id, request);
        return ResponseEntity.ok(ApiResponse.success(settlement, "Settlement updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete settlement (soft delete, Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteSettlement(@PathVariable UUID id) {
        settlementService.softDeleteSettlement(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Settlement deleted successfully"));
    }
}

