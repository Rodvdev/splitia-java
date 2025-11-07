package com.splitia.controller;

import com.splitia.dto.request.CreateBudgetRequest;
import com.splitia.dto.request.UpdateBudgetRequest;
import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.BudgetResponse;
import com.splitia.service.BudgetService;
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
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "Budgets", description = "Budget management endpoints")
public class BudgetController {
    
    private final BudgetService budgetService;
    
    @GetMapping
    @Operation(summary = "Get user budgets (paginated)")
    public ResponseEntity<ApiResponse<Page<BudgetResponse>>> getBudgets(Pageable pageable) {
        Page<BudgetResponse> budgets = budgetService.getBudgets(pageable);
        return ResponseEntity.ok(ApiResponse.success(budgets));
    }
    
    @PostMapping
    @Operation(summary = "Create a new budget")
    public ResponseEntity<ApiResponse<BudgetResponse>> createBudget(@Valid @RequestBody CreateBudgetRequest request) {
        BudgetResponse budget = budgetService.createBudget(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(budget, "Budget created successfully"));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get budget by ID")
    public ResponseEntity<ApiResponse<BudgetResponse>> getBudgetById(@PathVariable UUID id) {
        BudgetResponse budget = budgetService.getBudgetById(id);
        return ResponseEntity.ok(ApiResponse.success(budget));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update budget")
    public ResponseEntity<ApiResponse<BudgetResponse>> updateBudget(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBudgetRequest request) {
        BudgetResponse budget = budgetService.updateBudget(id, request);
        return ResponseEntity.ok(ApiResponse.success(budget, "Budget updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete budget (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(@PathVariable UUID id) {
        budgetService.softDeleteBudget(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Budget deleted successfully"));
    }
}

