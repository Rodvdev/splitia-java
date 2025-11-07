package com.splitia.controller;

import com.splitia.dto.request.CreateSubscriptionRequest;
import com.splitia.dto.request.UpdateSubscriptionRequest;
import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.SubscriptionResponse;
import com.splitia.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Subscription management endpoints")
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;
    
    @GetMapping
    @Operation(summary = "Get user subscriptions (paginated)")
    public ResponseEntity<ApiResponse<Page<SubscriptionResponse>>> getSubscriptions(Pageable pageable) {
        Page<SubscriptionResponse> subscriptions = subscriptionService.getSubscriptions(pageable);
        return ResponseEntity.ok(ApiResponse.success(subscriptions));
    }
    
    @PostMapping
    @Operation(summary = "Create a new subscription")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> createSubscription(@Valid @RequestBody CreateSubscriptionRequest request) {
        SubscriptionResponse subscription = subscriptionService.createSubscription(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(subscription, "Subscription created successfully"));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get subscription by ID")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getSubscriptionById(@PathVariable UUID id) {
        SubscriptionResponse subscription = subscriptionService.getSubscriptionById(id);
        return ResponseEntity.ok(ApiResponse.success(subscription));
    }
    
    @GetMapping("/current")
    @Operation(summary = "Get current active subscription")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getCurrentSubscription() {
        SubscriptionResponse subscription = subscriptionService.getCurrentSubscription();
        return ResponseEntity.ok(ApiResponse.success(subscription));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update subscription")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> updateSubscription(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSubscriptionRequest request) {
        SubscriptionResponse subscription = subscriptionService.updateSubscription(id, request);
        return ResponseEntity.ok(ApiResponse.success(subscription, "Subscription updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete subscription (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteSubscription(@PathVariable UUID id) {
        subscriptionService.softDeleteSubscription(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Subscription deleted successfully"));
    }
}

