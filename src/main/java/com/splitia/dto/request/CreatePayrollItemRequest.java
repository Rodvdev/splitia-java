package com.splitia.dto.request;

import com.splitia.model.enums.PayrollItemType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePayrollItemRequest {
    @NotNull(message = "Type is required")
    private PayrollItemType type;
    
    private String description;
    
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
}

