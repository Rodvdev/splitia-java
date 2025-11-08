package com.splitia.model.hr;

import com.splitia.model.BaseEntity;
import com.splitia.model.enums.PayrollItemType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payroll_items")
@Data
@EqualsAndHashCode(callSuper = true)
public class PayrollItem extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_id", nullable = false)
    @NotNull
    private Payroll payroll;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private PayrollItemType type;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal amount;
}

