package com.splitia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponse {
    private UUID id;
    private BigDecimal amount;
    private Integer month;
    private Integer year;
    private String currency;
    private CategoryResponse category;
    private LocalDateTime createdAt;
}

