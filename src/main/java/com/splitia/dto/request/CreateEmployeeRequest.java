package com.splitia.dto.request;

import com.splitia.model.enums.EmployeeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotBlank(message = "Employee number is required")
    @Size(max = 50, message = "Employee number must be less than 50 characters")
    private String employeeNumber;
    
    @NotBlank(message = "Department is required")
    @Size(max = 100, message = "Department must be less than 100 characters")
    private String department;
    
    @NotBlank(message = "Position is required")
    @Size(max = 100, message = "Position must be less than 100 characters")
    private String position;
    
    @NotNull(message = "Hire date is required")
    private LocalDate hireDate;
    
    private BigDecimal salary;
    
    private EmployeeStatus status = EmployeeStatus.ACTIVE;
    
    private UUID managerId;
}

