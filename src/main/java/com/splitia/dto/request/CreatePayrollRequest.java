package com.splitia.dto.request;

import com.splitia.model.enums.PayrollItemType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
public class CreatePayrollRequest {
    @NotNull(message = "Employee ID is required")
    private UUID employeeId;
    
    @NotNull(message = "Period start is required")
    private LocalDate periodStart;
    
    @NotNull(message = "Period end is required")
    private LocalDate periodEnd;
    
    private BigDecimal baseSalary;
    
    @Valid
    private List<CreatePayrollItemRequest> items;
}

