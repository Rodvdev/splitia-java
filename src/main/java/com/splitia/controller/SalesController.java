package com.splitia.controller;

import com.splitia.dto.request.*;
import com.splitia.dto.response.*;
import com.splitia.model.enums.LeadStatus;
import com.splitia.model.enums.OpportunityStage;
import com.splitia.service.ActivityService;
import com.splitia.service.LeadService;
import com.splitia.service.OpportunityService;
import com.splitia.service.SalesForecastService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Sales management endpoints")
public class SalesController {
    
    private final OpportunityService opportunityService;
    private final LeadService leadService;
    private final ActivityService activityService;
    private final SalesForecastService salesForecastService;
    
    // Opportunities
    @GetMapping("/opportunities")
    @Operation(summary = "Get all opportunities with filters")
    public ResponseEntity<ApiResponse<Page<OpportunityResponse>>> getAllOpportunities(
            Pageable pageable,
            @RequestParam(required = false) OpportunityStage stage,
            @RequestParam(required = false) UUID assignedToId) {
        Page<OpportunityResponse> opportunities = opportunityService.getAllOpportunities(pageable, stage, assignedToId);
        return ResponseEntity.ok(ApiResponse.success(opportunities));
    }
    
    @GetMapping("/opportunities/{id}")
    @Operation(summary = "Get opportunity by ID")
    public ResponseEntity<ApiResponse<OpportunityResponse>> getOpportunityById(@PathVariable UUID id) {
        OpportunityResponse opportunity = opportunityService.getOpportunityById(id);
        return ResponseEntity.ok(ApiResponse.success(opportunity));
    }
    
    @PostMapping("/opportunities")
    @Operation(summary = "Create a new opportunity")
    public ResponseEntity<ApiResponse<OpportunityResponse>> createOpportunity(@Valid @RequestBody CreateOpportunityRequest request) {
        OpportunityResponse opportunity = opportunityService.createOpportunity(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(opportunity, "Opportunity created successfully"));
    }
    
    @PutMapping("/opportunities/{id}")
    @Operation(summary = "Update opportunity")
    public ResponseEntity<ApiResponse<OpportunityResponse>> updateOpportunity(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOpportunityRequest request) {
        OpportunityResponse opportunity = opportunityService.updateOpportunity(id, request);
        return ResponseEntity.ok(ApiResponse.success(opportunity, "Opportunity updated successfully"));
    }
    
    @PutMapping("/opportunities/{id}/stage")
    @Operation(summary = "Update opportunity stage")
    public ResponseEntity<ApiResponse<OpportunityResponse>> updateOpportunityStage(
            @PathVariable UUID id,
            @RequestParam OpportunityStage stage) {
        OpportunityResponse opportunity = opportunityService.updateStage(id, stage);
        return ResponseEntity.ok(ApiResponse.success(opportunity, "Opportunity stage updated successfully"));
    }
    
    @DeleteMapping("/opportunities/{id}")
    @Operation(summary = "Delete opportunity. Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteOpportunity(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        opportunityService.deleteOpportunity(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Opportunity permanently deleted" : "Opportunity soft deleted"));
    }
    
    @GetMapping("/opportunities/pipeline")
    @Operation(summary = "Get pipeline view grouped by stage")
    public ResponseEntity<ApiResponse<Map<OpportunityStage, List<OpportunityResponse>>>> getPipeline() {
        Map<OpportunityStage, List<OpportunityResponse>> pipeline = salesForecastService.getPipelineView();
        return ResponseEntity.ok(ApiResponse.success(pipeline));
    }
    
    // Leads
    @GetMapping("/leads")
    @Operation(summary = "Get all leads with filters")
    public ResponseEntity<ApiResponse<Page<LeadResponse>>> getAllLeads(
            Pageable pageable,
            @RequestParam(required = false) LeadStatus status,
            @RequestParam(required = false) UUID assignedToId) {
        Page<LeadResponse> leads = leadService.getAllLeads(pageable, status, assignedToId);
        return ResponseEntity.ok(ApiResponse.success(leads));
    }
    
    @GetMapping("/leads/{id}")
    @Operation(summary = "Get lead by ID")
    public ResponseEntity<ApiResponse<LeadResponse>> getLeadById(@PathVariable UUID id) {
        LeadResponse lead = leadService.getLeadById(id);
        return ResponseEntity.ok(ApiResponse.success(lead));
    }
    
    @PostMapping("/leads")
    @Operation(summary = "Create a new lead")
    public ResponseEntity<ApiResponse<LeadResponse>> createLead(@Valid @RequestBody CreateLeadRequest request) {
        LeadResponse lead = leadService.createLead(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(lead, "Lead created successfully"));
    }
    
    @PutMapping("/leads/{id}")
    @Operation(summary = "Update lead")
    public ResponseEntity<ApiResponse<LeadResponse>> updateLead(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateLeadRequest request) {
        LeadResponse lead = leadService.updateLead(id, request);
        return ResponseEntity.ok(ApiResponse.success(lead, "Lead updated successfully"));
    }
    
    @PutMapping("/leads/{id}/score")
    @Operation(summary = "Update lead score")
    public ResponseEntity<ApiResponse<LeadResponse>> updateLeadScore(
            @PathVariable UUID id,
            @RequestParam Integer score) {
        LeadResponse lead = leadService.updateScore(id, score);
        return ResponseEntity.ok(ApiResponse.success(lead, "Lead score updated successfully"));
    }
    
    @PostMapping("/leads/{id}/convert")
    @Operation(summary = "Convert lead to opportunity")
    public ResponseEntity<ApiResponse<OpportunityResponse>> convertLeadToOpportunity(@PathVariable UUID id) {
        OpportunityResponse opportunity = leadService.convertToOpportunity(id);
        return ResponseEntity.ok(ApiResponse.success(opportunity, "Lead converted to opportunity successfully"));
    }
    
    @DeleteMapping("/leads/{id}")
    @Operation(summary = "Delete lead. Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteLead(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        leadService.deleteLead(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Lead permanently deleted" : "Lead soft deleted"));
    }
    
    // Activities
    @GetMapping("/activities")
    @Operation(summary = "Get all activities")
    public ResponseEntity<ApiResponse<Page<ActivityResponse>>> getAllActivities(Pageable pageable) {
        Page<ActivityResponse> activities = activityService.getAllActivities(pageable);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }
    
    @GetMapping("/activities/{id}")
    @Operation(summary = "Get activity by ID")
    public ResponseEntity<ApiResponse<ActivityResponse>> getActivityById(@PathVariable UUID id) {
        ActivityResponse activity = activityService.getActivityById(id);
        return ResponseEntity.ok(ApiResponse.success(activity));
    }
    
    @GetMapping("/activities/opportunity/{opportunityId}")
    @Operation(summary = "Get activities for an opportunity")
    public ResponseEntity<ApiResponse<List<ActivityResponse>>> getActivitiesByOpportunity(@PathVariable UUID opportunityId) {
        List<ActivityResponse> activities = activityService.getActivitiesByOpportunity(opportunityId);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }
    
    @GetMapping("/activities/lead/{leadId}")
    @Operation(summary = "Get activities for a lead")
    public ResponseEntity<ApiResponse<List<ActivityResponse>>> getActivitiesByLead(@PathVariable UUID leadId) {
        List<ActivityResponse> activities = activityService.getActivitiesByLead(leadId);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }
    
    @PostMapping("/activities")
    @Operation(summary = "Create a new activity")
    public ResponseEntity<ApiResponse<ActivityResponse>> createActivity(@Valid @RequestBody CreateActivityRequest request) {
        UUID createdById = getCurrentUserId();
        ActivityResponse activity = activityService.createActivity(request, createdById);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(activity, "Activity created successfully"));
    }
    
    @PutMapping("/activities/{id}")
    @Operation(summary = "Update activity")
    public ResponseEntity<ApiResponse<ActivityResponse>> updateActivity(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateActivityRequest request) {
        ActivityResponse activity = activityService.updateActivity(id, request);
        return ResponseEntity.ok(ApiResponse.success(activity, "Activity updated successfully"));
    }
    
    @PutMapping("/activities/{id}/complete")
    @Operation(summary = "Mark activity as completed")
    public ResponseEntity<ApiResponse<ActivityResponse>> completeActivity(@PathVariable UUID id) {
        ActivityResponse activity = activityService.completeActivity(id);
        return ResponseEntity.ok(ApiResponse.success(activity, "Activity marked as completed"));
    }
    
    @DeleteMapping("/activities/{id}")
    @Operation(summary = "Delete activity. Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteActivity(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        activityService.deleteActivity(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Activity permanently deleted" : "Activity soft deleted"));
    }
    
    // Forecasting and Metrics
    @GetMapping("/forecasting")
    @Operation(summary = "Get sales forecasting data")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getForecasting(
            @RequestParam(required = false, defaultValue = "MONTH") String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> forecasting = salesForecastService.getForecasting(period, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(forecasting));
    }
    
    @GetMapping("/metrics")
    @Operation(summary = "Get sales metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMetrics() {
        Map<String, Object> metrics = salesForecastService.getMetrics();
        return ResponseEntity.ok(ApiResponse.success(metrics));
    }
    
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof com.splitia.security.CustomUserDetails) {
            com.splitia.security.CustomUserDetails userDetails = (com.splitia.security.CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUserId();
        }
        // Fallback for admin endpoints - use first admin user
        return null; // Will be handled by service
    }
}

