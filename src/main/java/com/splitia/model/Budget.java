package com.splitia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "budgets",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "month", "year", "category_id"}))
@Data
@EqualsAndHashCode(callSuper = true)
public class Budget extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @Column(nullable = false)
    @NotNull
    @Min(1)
    @Max(12)
    private Integer month;
    
    @Column(nullable = false)
    @NotNull
    @Min(2000)
    private Integer year;
    
    @Column(nullable = false)
    private String currency = "USD";
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CustomCategory category;
}

