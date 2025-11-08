package com.splitia.model.finance;

import com.splitia.model.BaseEntity;
import com.splitia.model.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
@EqualsAndHashCode(callSuper = true)
public class Payment extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal amount;
    
    @Column(nullable = false)
    @NotNull
    private LocalDate date;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    @NotNull
    private PaymentMethod paymentMethod;
    
    @Column(name = "reference")
    private String reference;
    
    @Column(name = "is_reconciled", nullable = false)
    @NotNull
    private Boolean isReconciled = false;
    
    @Column(nullable = false)
    private String currency = "USD";
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}

