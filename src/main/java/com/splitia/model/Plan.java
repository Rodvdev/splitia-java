package com.splitia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "plans")
@Data
@EqualsAndHashCode(callSuper = true)
public class Plan extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    @NotNull
    private String name; // FREE, PRO, ENTERPRISE
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "price_per_month", precision = 19, scale = 2, nullable = false)
    @NotNull
    private BigDecimal pricePerMonth;
    
    @Column(nullable = false)
    private String currency = "USD";
    
    // Feature limits
    @Column(name = "max_groups", nullable = false)
    @NotNull
    private Integer maxGroups = 1;
    
    @Column(name = "max_group_members", nullable = false)
    @NotNull
    private Integer maxGroupMembers = 5;
    
    @Column(name = "max_ai_requests_per_month", nullable = false)
    @NotNull
    private Integer maxAiRequestsPerMonth = 0;
    
    @Column(name = "max_expenses_per_group", nullable = false)
    @NotNull
    private Integer maxExpensesPerGroup = 50;
    
    @Column(name = "max_budgets_per_group", nullable = false)
    @NotNull
    private Integer maxBudgetsPerGroup = 5;
    
    @Column(name = "has_kanban", nullable = false)
    @NotNull
    private Boolean hasKanban = false;
    
    @Column(name = "has_advanced_analytics", nullable = false)
    @NotNull
    private Boolean hasAdvancedAnalytics = false;
    
    @Column(name = "has_custom_categories", nullable = false)
    @NotNull
    private Boolean hasCustomCategories = true;
    
    @Column(name = "has_export_data", nullable = false)
    @NotNull
    private Boolean hasExportData = false;
    
    @Column(name = "has_priority_support", nullable = false)
    @NotNull
    private Boolean hasPrioritySupport = false;
    
    @Column(name = "is_active", nullable = false)
    @NotNull
    private Boolean isActive = true;
}

