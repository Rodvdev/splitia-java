package com.splitia.dto.response;

import com.splitia.model.enums.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private UUID id;
    private UUID userId;
    private String userName;
    private String userEmail;
    private String employeeNumber;
    private String department;
    private String position;
    private LocalDate hireDate;
    private BigDecimal salary;
    private EmployeeStatus status;
    private UUID managerId;
    private String managerName;
}

