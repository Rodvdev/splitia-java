package com.splitia.dto.response;

import com.splitia.model.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String priority;
    private UUID groupId;
    private String groupName;
    private UUID assignedToId;
    private String assignedToName;
    private UUID createdById;
    private String createdByName;
    private List<TaskTagResponse> tags;
    private Integer position;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Expense association
    private UUID expenseId;
    
    // Future expense fields
    private BigDecimal futureExpenseAmount;
    private String futureExpenseCurrency;
    private UUID futureExpensePaidById;
    private String futureExpensePaidByName;
    private List<FutureExpenseShareResponse> futureExpenseShares;
}

