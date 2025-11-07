package com.splitia.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    private String description;
    
    @NotNull(message = "Group ID is required")
    private UUID groupId;
    
    private UUID assignedToId;
    
    private LocalDate startDate;
    
    private LocalDate dueDate;
    
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, URGENT
    
    private List<UUID> tagIds;
    
    // Expense association (optional - for referencing existing expense)
    private UUID expenseId;
    
    // Flag to create expense automatically
    private Boolean createFutureExpense = false;
    
    // Future expense fields (for storing future expense info without creating Expense)
    @DecimalMin(value = "0.01", message = "Future expense amount must be greater than 0")
    private BigDecimal futureExpenseAmount;
    
    private String futureExpenseCurrency = "USD";
    
    private UUID futureExpensePaidById;
    
    private List<ExpenseShareRequest> futureExpenseShares;
}

