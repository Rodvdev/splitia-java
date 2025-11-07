package com.splitia.model;

import com.splitia.model.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscription_payments")
@Data
@EqualsAndHashCode(callSuper = true)
public class SubscriptionPayment extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, precision = 19, scale = 2)
    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @Column(nullable = false)
    @NotNull
    private String currency;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Column(name = "payment_date", nullable = false)
    @NotNull
    private LocalDateTime paymentDate;
    
    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;
    
    @Column(name = "invoice_url")
    private String invoiceUrl;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    @NotNull
    private Subscription subscription;
}

