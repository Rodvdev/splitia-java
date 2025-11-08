package com.splitia.dto.response;

import com.splitia.model.enums.SubscriptionPlan;
import com.splitia.model.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    private UUID id;
    private SubscriptionPlan planType;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean autoRenew;
    private BigDecimal pricePerMonth;
    private String currency;
    private LocalDateTime createdAt;
    private UUID userId;
    private String userName;
}

