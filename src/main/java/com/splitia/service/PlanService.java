package com.splitia.service;

import com.splitia.exception.BadRequestException;
import com.splitia.exception.ForbiddenException;
import com.splitia.model.Plan;
import com.splitia.model.Subscription;
import com.splitia.model.User;
import com.splitia.model.enums.SubscriptionStatus;
import com.splitia.repository.PlanRepository;
import com.splitia.repository.SubscriptionRepository;
import com.splitia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlanService {
    
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    
    /**
     * Get the active plan for a user
     */
    public Plan getUserPlan(User user) {
        Subscription activeSubscription = subscriptionRepository
                .findByUserIdAndStatus(user.getId(), SubscriptionStatus.ACTIVE)
                .orElse(null);
        
        if (activeSubscription != null && activeSubscription.getPlan() != null) {
            return activeSubscription.getPlan();
        }
        
        // Default to FREE plan if no active subscription
        return planRepository.findByName("FREE")
                .orElseThrow(() -> new RuntimeException("FREE plan not found"));
    }
    
    /**
     * Verify if user has Kanban access
     */
    public void verifyKanbanAccess(User user) {
        Plan plan = getUserPlan(user);
        if (!plan.getHasKanban()) {
            throw new ForbiddenException("Kanban feature is not available in your plan. Please upgrade to PRO or ENTERPRISE.");
        }
    }
    
    /**
     * Verify if user can create more groups
     */
    public void verifyGroupLimit(User user, int currentGroupCount) {
        Plan plan = getUserPlan(user);
        if (plan.getMaxGroups() == -1) {
            return; // Unlimited
        }
        if (currentGroupCount >= plan.getMaxGroups()) {
            throw new BadRequestException(
                    String.format("You have reached the maximum number of groups (%d) for your plan. Please upgrade to create more groups.", 
                            plan.getMaxGroups())
            );
        }
    }
    
    /**
     * Verify if group can have more members
     */
    public void verifyGroupMemberLimit(User user, int currentMemberCount) {
        Plan plan = getUserPlan(user);
        if (plan.getMaxGroupMembers() == -1) {
            return; // Unlimited
        }
        if (currentMemberCount >= plan.getMaxGroupMembers()) {
            throw new BadRequestException(
                    String.format("This group has reached the maximum number of members (%d) for your plan. Please upgrade to add more members.", 
                            plan.getMaxGroupMembers())
            );
        }
    }
    
    /**
     * Verify if user can make more AI requests
     */
    public void verifyAiRequestLimit(User user, int currentAiRequestCount) {
        Plan plan = getUserPlan(user);
        if (plan.getMaxAiRequestsPerMonth() == -1) {
            return; // Unlimited
        }
        if (currentAiRequestCount >= plan.getMaxAiRequestsPerMonth()) {
            throw new BadRequestException(
                    String.format("You have reached the monthly AI request limit (%d) for your plan. Please upgrade for more requests.", 
                            plan.getMaxAiRequestsPerMonth())
            );
        }
    }
    
    /**
     * Verify if group can have more expenses
     */
    public void verifyExpenseLimit(User user, int currentExpenseCount) {
        Plan plan = getUserPlan(user);
        if (plan.getMaxExpensesPerGroup() == -1) {
            return; // Unlimited
        }
        if (currentExpenseCount >= plan.getMaxExpensesPerGroup()) {
            throw new BadRequestException(
                    String.format("This group has reached the maximum number of expenses (%d) for your plan. Please upgrade to add more expenses.", 
                            plan.getMaxExpensesPerGroup())
            );
        }
    }
    
    /**
     * Verify if group can have more budgets
     */
    public void verifyBudgetLimit(User user, int currentBudgetCount) {
        Plan plan = getUserPlan(user);
        if (plan.getMaxBudgetsPerGroup() == -1) {
            return; // Unlimited
        }
        if (currentBudgetCount >= plan.getMaxBudgetsPerGroup()) {
            throw new BadRequestException(
                    String.format("This group has reached the maximum number of budgets (%d) for your plan. Please upgrade to add more budgets.", 
                            plan.getMaxBudgetsPerGroup())
            );
        }
    }
    
    /**
     * Check if user has a specific feature
     */
    public boolean hasFeature(User user, String featureName) {
        Plan plan = getUserPlan(user);
        switch (featureName.toUpperCase()) {
            case "KANBAN":
                return plan.getHasKanban();
            case "ADVANCED_ANALYTICS":
                return plan.getHasAdvancedAnalytics();
            case "CUSTOM_CATEGORIES":
                return plan.getHasCustomCategories();
            case "EXPORT_DATA":
                return plan.getHasExportData();
            case "PRIORITY_SUPPORT":
                return plan.getHasPrioritySupport();
            default:
                return false;
        }
    }
    
    /**
     * Get plan by name
     */
    public Plan getPlanByName(String planName) {
        return planRepository.findByName(planName)
                .orElseThrow(() -> new RuntimeException("Plan not found: " + planName));
    }
    
    /**
     * Get plan by ID
     */
    public Plan getPlanById(UUID planId) {
        return planRepository.findByIdAndDeletedAtIsNull(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found: " + planId));
    }
}

