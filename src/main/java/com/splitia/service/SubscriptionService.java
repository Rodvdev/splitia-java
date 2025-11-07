package com.splitia.service;

import com.splitia.dto.response.SubscriptionResponse;
import com.splitia.mapper.SubscriptionMapper;
import com.splitia.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    
    public SubscriptionResponse getCurrentSubscription() {
        // Implementation placeholder
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

