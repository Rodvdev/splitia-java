package com.splitia.dto.request;

import com.splitia.model.enums.SubscriptionPlan;
import com.splitia.model.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubscriptionRequest {
    private SubscriptionPlan planType;
    
    private SubscriptionStatus status;
    
    private Boolean autoRenew;
}

