package com.splitia.controller;

import com.splitia.dto.request.CreateExpenseRequest;
import com.splitia.dto.request.UpdateExpenseRequest;
import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.ExpenseResponse;
import com.splitia.service.ExpenseService;
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
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Expense management endpoints")
public class ExpenseController {
    
    private final ExpenseService expenseService;
    
    @GetMapping
    @Operation(summary = "Get expenses")
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> getExpenses(
            @RequestParam(required = false) UUID groupId,
            Pageable pageable) {
        Page<ExpenseResponse> expenses = expenseService.getExpenses(groupId, pageable);
        return ResponseEntity.ok(ApiResponse.success(expenses));
    }
    
    @PostMapping
    @Operation(summary = "Create a new expense")
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(@Valid @RequestBody CreateExpenseRequest request) {
        ExpenseResponse expense = expenseService.createExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(expense, "Expense created successfully"));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get expense by ID")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(@PathVariable UUID id) {
        ExpenseResponse expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(ApiResponse.success(expense));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update expense")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateExpenseRequest request) {
        ExpenseResponse expense = expenseService.updateExpense(id, request);
        return ResponseEntity.ok(ApiResponse.success(expense, "Expense updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete expense (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable UUID id) {
        expenseService.softDeleteExpense(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Expense deleted successfully"));
    }
}

