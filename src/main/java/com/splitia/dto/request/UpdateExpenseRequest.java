package com.splitia.dto.request;

import jakarta.validation.constraints.DecimalMin;
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
public class UpdateExpenseRequest {
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @Size(min = 1, max = 255, message = "Description must be between 1 and 255 characters")
    private String description;
    
    private LocalDateTime date;
    
    private String currency;
    
    private String location;
    
    private String notes;
    
    private UUID categoryId;
    
    private List<ExpenseShareRequest> shares;
}

