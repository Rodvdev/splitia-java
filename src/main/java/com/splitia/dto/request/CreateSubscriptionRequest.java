package com.splitia.dto.request;

import com.splitia.model.enums.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionRequest {
    @NotNull(message = "Plan type is required")
    private SubscriptionPlan planType;
    
    private String paymentMethod;
}

