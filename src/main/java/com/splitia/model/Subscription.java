package com.splitia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.splitia.model.enums.SubscriptionPlan;
import com.splitia.model.enums.SubscriptionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Subscription extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false)
    @NotNull
    private SubscriptionPlan planType = SubscriptionPlan.FREE;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    @JsonIgnore
    private Plan plan;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;
    
    @Column(name = "start_date", nullable = false)
    @NotNull
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "auto_renew", nullable = false)
    private Boolean autoRenew = false;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "stripe_subscription_id")
    private String stripeSubscriptionId;
    
    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;
    
    @Column(name = "price_per_month", precision = 19, scale = 2)
    private BigDecimal pricePerMonth;
    
    @Column(nullable = false)
    private String currency = "USD";
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    @JsonIgnore
    private User user;
    
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SubscriptionPayment> payments = new ArrayList<>();
}
