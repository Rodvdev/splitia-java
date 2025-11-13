package com.splitia.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExpenseRequest {
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Description is required")
    @Size(min = 1, max = 255, message = "Description must be between 1 and 255 characters")
    private String description;
    
    @NotNull(message = "Date is required")
    private LocalDateTime date;
    
    private String currency = "USD";
    
    private String location;
    
    private String notes;
    
    private UUID groupId;
    
    private UUID categoryId;
    
    @NotNull(message = "Paid by user ID is required")
    private UUID paidById;
    
    private List<ExpenseShareRequest> shares;
}

