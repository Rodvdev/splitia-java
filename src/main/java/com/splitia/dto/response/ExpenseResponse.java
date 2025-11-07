package com.splitia.dto.response;

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
public class ExpenseResponse {
    private UUID id;
    private BigDecimal amount;
    private String description;
    private LocalDateTime date;
    private String currency;
    private String location;
    private String notes;
    private Boolean isSettlement;
    private UserResponse paidBy;
    private GroupResponse group;
    private CategoryResponse category;
    private List<ExpenseShareResponse> shares;
    private LocalDateTime createdAt;
}

