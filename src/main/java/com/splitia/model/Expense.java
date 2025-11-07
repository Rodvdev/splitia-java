package com.splitia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "expenses")
@Data
@EqualsAndHashCode(callSuper = true)
public class Expense extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String description;
    
    @Column(nullable = false)
    @NotNull
    private LocalDateTime date;
    
    @Column(nullable = false)
    private String currency = "USD";
    
    private String location;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "is_settlement", nullable = false)
    private Boolean isSettlement = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by_id", nullable = false)
    @NotNull
    private User paidBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CustomCategory category;
    
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseShare> shares = new ArrayList<>();
    
    @OneToOne(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private Settlement settlement;
}

