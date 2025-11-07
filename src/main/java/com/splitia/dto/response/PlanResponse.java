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
public class PlanResponse {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal pricePerMonth;
    private String currency;
    private Integer maxGroups;
    private Integer maxGroupMembers;
    private Integer maxAiRequestsPerMonth;
    private Integer maxExpensesPerGroup;
    private Integer maxBudgetsPerGroup;
    private Boolean hasKanban;
    private Boolean hasAdvancedAnalytics;
    private Boolean hasCustomCategories;
    private Boolean hasExportData;
    private Boolean hasPrioritySupport;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

