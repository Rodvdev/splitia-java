package com.splitia.dto.response;

import com.splitia.model.enums.PayrollStatus;
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
public class PayrollResponse {
    private UUID id;
    private UUID employeeId;
    private String employeeName;
    private String employeeNumber;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal baseSalary;
    private BigDecimal netSalary;
    private PayrollStatus status;
    private List<PayrollItemResponse> items;
}

