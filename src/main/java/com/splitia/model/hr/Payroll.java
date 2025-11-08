package com.splitia.model.hr;

import com.splitia.model.BaseEntity;
import com.splitia.model.enums.PayrollStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "payroll")
@Data
@EqualsAndHashCode(callSuper = true)
public class Payroll extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull
    private Employee employee;
    
    @Column(name = "period_start", nullable = false)
    @NotNull
    private LocalDate periodStart;
    
    @Column(name = "period_end", nullable = false)
    @NotNull
    private LocalDate periodEnd;
    
    @Column(name = "base_salary", precision = 19, scale = 2, nullable = false)
    @NotNull
    private BigDecimal baseSalary;
    
    @Column(name = "net_salary", precision = 19, scale = 2, nullable = false)
    @NotNull
    private BigDecimal netSalary;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private PayrollStatus status = PayrollStatus.DRAFT;
    
    @OneToMany(mappedBy = "payroll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PayrollItem> items = new ArrayList<>();
}

