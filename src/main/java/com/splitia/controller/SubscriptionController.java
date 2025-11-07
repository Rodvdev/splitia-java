package com.splitia.controller;

import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.SubscriptionResponse;
import com.splitia.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Subscription management endpoints")
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;
    
    @GetMapping("/current")
    @Operation(summary = "Get current subscription")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getCurrentSubscription() {
        SubscriptionResponse subscription = subscriptionService.getCurrentSubscription();
        return ResponseEntity.ok(ApiResponse.success(subscription));
    }
}

