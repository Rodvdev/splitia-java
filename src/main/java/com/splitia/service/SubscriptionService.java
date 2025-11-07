package com.splitia.service;

import com.splitia.dto.request.CreateSubscriptionRequest;
import com.splitia.dto.request.UpdateSubscriptionRequest;
import com.splitia.dto.response.SubscriptionResponse;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.SubscriptionMapper;
import com.splitia.model.Subscription;
import com.splitia.model.User;
import com.splitia.model.enums.SubscriptionStatus;
import com.splitia.repository.SubscriptionRepository;
import com.splitia.repository.UserRepository;
import com.splitia.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepository userRepository;
    
    public Page<SubscriptionResponse> getSubscriptions(Pageable pageable) {
        User currentUser = getCurrentUser();
        return subscriptionRepository.findByUserId(currentUser.getId(), pageable)
                .map(subscriptionMapper::toResponse);
    }
    
    @Transactional
    public SubscriptionResponse createSubscription(CreateSubscriptionRequest request) {
        User currentUser = getCurrentUser();
        
        Subscription subscription = new Subscription();
        subscription.setPlanType(request.getPlanType());
        subscription.setPaymentMethod(request.getPaymentMethod());
        subscription.setStartDate(LocalDateTime.now());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setUser(currentUser);
        
        subscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toResponse(subscription);
    }
    
    public SubscriptionResponse getSubscriptionById(UUID subscriptionId) {
        User currentUser = getCurrentUser();
        Subscription subscription = subscriptionRepository.findByIdAndDeletedAtIsNull(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", subscriptionId));
        
        // Verify ownership
        if (!subscription.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Subscription", "id", subscriptionId);
        }
        
        return subscriptionMapper.toResponse(subscription);
    }
    
    public SubscriptionResponse getCurrentSubscription() {
        User currentUser = getCurrentUser();
        Subscription subscription = subscriptionRepository.findByUserIdAndStatus(
                currentUser.getId(), 
                SubscriptionStatus.ACTIVE
        ).orElseThrow(() -> new ResourceNotFoundException("Subscription", "status", "ACTIVE"));
        
        return subscriptionMapper.toResponse(subscription);
    }
    
    @Transactional
    public SubscriptionResponse updateSubscription(UUID subscriptionId, UpdateSubscriptionRequest request) {
        User currentUser = getCurrentUser();
        Subscription subscription = subscriptionRepository.findByIdAndDeletedAtIsNull(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", subscriptionId));
        
        // Verify ownership
        if (!subscription.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Subscription", "id", subscriptionId);
        }
        
        if (request.getPlanType() != null) {
            subscription.setPlanType(request.getPlanType());
        }
        if (request.getStatus() != null) {
            subscription.setStatus(request.getStatus());
        }
        if (request.getAutoRenew() != null) {
            subscription.setAutoRenew(request.getAutoRenew());
        }
        
        subscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toResponse(subscription);
    }
    
    @Transactional
    public void softDeleteSubscription(UUID subscriptionId) {
        User currentUser = getCurrentUser();
        Subscription subscription = subscriptionRepository.findByIdAndDeletedAtIsNull(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", subscriptionId));
        
        // Verify ownership
        if (!subscription.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Subscription", "id", subscriptionId);
        }
        
        subscription.setDeletedAt(LocalDateTime.now());
        subscriptionRepository.save(subscription);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        return userRepository.findByIdAndDeletedAtIsNull(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));
    }
}

