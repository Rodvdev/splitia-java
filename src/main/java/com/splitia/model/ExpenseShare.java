package com.splitia.model;

import com.splitia.model.enums.ShareType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "expense_shares",
       uniqueConstraints = @UniqueConstraint(columnNames = {"expense_id", "user_id"}))
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpenseShare extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShareType type = ShareType.EQUAL;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    @NotNull
    private Expense expense;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;
}

