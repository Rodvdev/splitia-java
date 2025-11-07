package com.splitia.controller;

import com.splitia.dto.response.ApiResponse;
import com.splitia.model.*;
import com.splitia.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management endpoints")
public class AdminController {
    
    private final AdminService adminService;
    
    // Users
    @GetMapping("/users")
    @Operation(summary = "Get all users (Admin only)")
    public ResponseEntity<ApiResponse<Page<User>>> getAllUsers(Pageable pageable) {
        Page<User> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID (Admin only)")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable UUID id) {
        User user = adminService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user (Admin only). Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        adminService.deleteUser(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "User permanently deleted" : "User soft deleted"));
    }
    
    // Groups
    @GetMapping("/groups")
    @Operation(summary = "Get all groups (Admin only)")
    public ResponseEntity<ApiResponse<Page<Group>>> getAllGroups(Pageable pageable) {
        Page<Group> groups = adminService.getAllGroups(pageable);
        return ResponseEntity.ok(ApiResponse.success(groups));
    }
    
    @GetMapping("/groups/{id}")
    @Operation(summary = "Get group by ID (Admin only)")
    public ResponseEntity<ApiResponse<Group>> getGroupById(@PathVariable UUID id) {
        Group group = adminService.getGroupById(id);
        return ResponseEntity.ok(ApiResponse.success(group));
    }
    
    @DeleteMapping("/groups/{id}")
    @Operation(summary = "Delete group (Admin only). Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        adminService.deleteGroup(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Group permanently deleted" : "Group soft deleted"));
    }
    
    // Expenses
    @GetMapping("/expenses")
    @Operation(summary = "Get all expenses (Admin only)")
    public ResponseEntity<ApiResponse<Page<Expense>>> getAllExpenses(Pageable pageable) {
        Page<Expense> expenses = adminService.getAllExpenses(pageable);
        return ResponseEntity.ok(ApiResponse.success(expenses));
    }
    
    @GetMapping("/expenses/{id}")
    @Operation(summary = "Get expense by ID (Admin only)")
    public ResponseEntity<ApiResponse<Expense>> getExpenseById(@PathVariable UUID id) {
        Expense expense = adminService.getExpenseById(id);
        return ResponseEntity.ok(ApiResponse.success(expense));
    }
    
    @DeleteMapping("/expenses/{id}")
    @Operation(summary = "Delete expense (Admin only). Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        adminService.deleteExpense(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Expense permanently deleted" : "Expense soft deleted"));
    }
    
    // Expense Shares
    @GetMapping("/expense-shares")
    @Operation(summary = "Get all expense shares (Admin only)")
    public ResponseEntity<ApiResponse<Page<ExpenseShare>>> getAllExpenseShares(Pageable pageable) {
        Page<ExpenseShare> expenseShares = adminService.getAllExpenseShares(pageable);
        return ResponseEntity.ok(ApiResponse.success(expenseShares));
    }
    
    @GetMapping("/expense-shares/{id}")
    @Operation(summary = "Get expense share by ID (Admin only)")
    public ResponseEntity<ApiResponse<ExpenseShare>> getExpenseShareById(@PathVariable UUID id) {
        ExpenseShare expenseShare = adminService.getExpenseShareById(id);
        return ResponseEntity.ok(ApiResponse.success(expenseShare));
    }
    
    @DeleteMapping("/expense-shares/{id}")
    @Operation(summary = "Delete expense share (Admin only). Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteExpenseShare(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        adminService.deleteExpenseShare(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Expense share permanently deleted" : "Expense share soft deleted"));
    }
    
    // Budgets
    @GetMapping("/budgets")
    @Operation(summary = "Get all budgets (Admin only)")
    public ResponseEntity<ApiResponse<Page<Budget>>> getAllBudgets(Pageable pageable) {
        Page<Budget> budgets = adminService.getAllBudgets(pageable);
        return ResponseEntity.ok(ApiResponse.success(budgets));
    }
    
    @GetMapping("/budgets/{id}")
    @Operation(summary = "Get budget by ID (Admin only)")
    public ResponseEntity<ApiResponse<Budget>> getBudgetById(@PathVariable UUID id) {
        Budget budget = adminService.getBudgetById(id);
        return ResponseEntity.ok(ApiResponse.success(budget));
    }
    
    @DeleteMapping("/budgets/{id}")
    @Operation(summary = "Delete budget (Admin only). Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        adminService.deleteBudget(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Budget permanently deleted" : "Budget soft deleted"));
    }
    
    // Categories
    @GetMapping("/categories")
    @Operation(summary = "Get all categories (Admin only)")
    public ResponseEntity<ApiResponse<Page<CustomCategory>>> getAllCategories(Pageable pageable) {
        Page<CustomCategory> categories = adminService.getAllCategories(pageable);
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    @GetMapping("/categories/{id}")
    @Operation(summary = "Get category by ID (Admin only)")
    public ResponseEntity<ApiResponse<CustomCategory>> getCategoryById(@PathVariable UUID id) {
        CustomCategory category = adminService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(category));
    }
    
    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Delete category (Admin only). Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        adminService.deleteCategory(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Category permanently deleted" : "Category soft deleted"));
    }
    
    // Conversations
    @GetMapping("/conversations")
    @Operation(summary = "Get all conversations (Admin only)")
    public ResponseEntity<ApiResponse<Page<Conversation>>> getAllConversations(Pageable pageable) {
        Page<Conversation> conversations = adminService.getAllConversations(pageable);
        return ResponseEntity.ok(ApiResponse.success(conversations));
    }
    
    @GetMapping("/conversations/{id}")
    @Operation(summary = "Get conversation by ID (Admin only)")
    public ResponseEntity<ApiResponse<Conversation>> getConversationById(@PathVariable UUID id) {
        Conversation conversation = adminService.getConversationById(id);
        return ResponseEntity.ok(ApiResponse.success(conversation));
    }
    
    @DeleteMapping("/conversations/{id}")
    @Operation(summary = "Delete conversation (Admin only). Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteConversation(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        adminService.deleteConversation(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Conversation permanently deleted" : "Conversation soft deleted"));
    }
    
    // Messages
    @GetMapping("/messages")
    @Operation(summary = "Get all messages (Admin only)")
    public ResponseEntity<ApiResponse<Page<Message>>> getAllMessages(Pageable pageable) {
        Page<Message> messages = adminService.getAllMessages(pageable);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
    
    @GetMapping("/messages/{id}")
    @Operation(summary = "Get message by ID (Admin only)")
    public ResponseEntity<ApiResponse<Message>> getMessageById(@PathVariable UUID id) {
        Message message = adminService.getMessageById(id);
        return ResponseEntity.ok(ApiResponse.success(message));
    }
    
    @DeleteMapping("/messages/{id}")
    @Operation(summary = "Delete message (Admin only). Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        adminService.deleteMessage(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Message permanently deleted" : "Message soft deleted"));
    }
    
    // Settlements
    @GetMapping("/settlements")
    @Operation(summary = "Get all settlements (Admin only)")
    public ResponseEntity<ApiResponse<Page<Settlement>>> getAllSettlements(Pageable pageable) {
        Page<Settlement> settlements = adminService.getAllSettlements(pageable);
        return ResponseEntity.ok(ApiResponse.success(settlements));
    }
    
    @GetMapping("/settlements/{id}")
    @Operation(summary = "Get settlement by ID (Admin only)")
    public ResponseEntity<ApiResponse<Settlement>> getSettlementById(@PathVariable UUID id) {
        Settlement settlement = adminService.getSettlementById(id);
        return ResponseEntity.ok(ApiResponse.success(settlement));
    }
    
    @DeleteMapping("/settlements/{id}")
    @Operation(summary = "Delete settlement (Admin only). Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteSettlement(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        adminService.deleteSettlement(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Settlement permanently deleted" : "Settlement soft deleted"));
    }
    
    // Subscriptions
    @GetMapping("/subscriptions")
    @Operation(summary = "Get all subscriptions (Admin only)")
    public ResponseEntity<ApiResponse<Page<Subscription>>> getAllSubscriptions(Pageable pageable) {
        Page<Subscription> subscriptions = adminService.getAllSubscriptions(pageable);
        return ResponseEntity.ok(ApiResponse.success(subscriptions));
    }
    
    @GetMapping("/subscriptions/{id}")
    @Operation(summary = "Get subscription by ID (Admin only)")
    public ResponseEntity<ApiResponse<Subscription>> getSubscriptionById(@PathVariable UUID id) {
        Subscription subscription = adminService.getSubscriptionById(id);
        return ResponseEntity.ok(ApiResponse.success(subscription));
    }
    
    @DeleteMapping("/subscriptions/{id}")
    @Operation(summary = "Delete subscription (Admin only). Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteSubscription(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        adminService.deleteSubscription(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Subscription permanently deleted" : "Subscription soft deleted"));
    }
    
    // Support Tickets
    @GetMapping("/support-tickets")
    @Operation(summary = "Get all support tickets (Admin only)")
    public ResponseEntity<ApiResponse<Page<SupportTicket>>> getAllSupportTickets(Pageable pageable) {
        Page<SupportTicket> tickets = adminService.getAllSupportTickets(pageable);
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }
    
    @GetMapping("/support-tickets/{id}")
    @Operation(summary = "Get support ticket by ID (Admin only)")
    public ResponseEntity<ApiResponse<SupportTicket>> getSupportTicketById(@PathVariable UUID id) {
        SupportTicket ticket = adminService.getSupportTicketById(id);
        return ResponseEntity.ok(ApiResponse.success(ticket));
    }
    
    @DeleteMapping("/support-tickets/{id}")
    @Operation(summary = "Delete support ticket (Admin only). Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteSupportTicket(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        adminService.deleteSupportTicket(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Support ticket permanently deleted" : "Support ticket soft deleted"));
    }
    
    // Group Invitations
    @GetMapping("/group-invitations")
    @Operation(summary = "Get all group invitations (Admin only)")
    public ResponseEntity<ApiResponse<Page<GroupInvitation>>> getAllGroupInvitations(Pageable pageable) {
        Page<GroupInvitation> invitations = adminService.getAllGroupInvitations(pageable);
        return ResponseEntity.ok(ApiResponse.success(invitations));
    }
    
    @GetMapping("/group-invitations/{id}")
    @Operation(summary = "Get group invitation by ID (Admin only)")
    public ResponseEntity<ApiResponse<GroupInvitation>> getGroupInvitationById(@PathVariable UUID id) {
        GroupInvitation invitation = adminService.getGroupInvitationById(id);
        return ResponseEntity.ok(ApiResponse.success(invitation));
    }
    
    @DeleteMapping("/group-invitations/{id}")
    @Operation(summary = "Delete group invitation (Admin only). Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteGroupInvitation(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        adminService.deleteGroupInvitation(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Group invitation permanently deleted" : "Group invitation soft deleted"));
    }
    
    // Group Users
    @GetMapping("/group-users")
    @Operation(summary = "Get all group users (Admin only)")
    public ResponseEntity<ApiResponse<Page<GroupUser>>> getAllGroupUsers(Pageable pageable) {
        Page<GroupUser> groupUsers = adminService.getAllGroupUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(groupUsers));
    }
    
    @GetMapping("/group-users/{id}")
    @Operation(summary = "Get group user by ID (Admin only)")
    public ResponseEntity<ApiResponse<GroupUser>> getGroupUserById(@PathVariable UUID id) {
        GroupUser groupUser = adminService.getGroupUserById(id);
        return ResponseEntity.ok(ApiResponse.success(groupUser));
    }
    
    @DeleteMapping("/group-users/{id}")
    @Operation(summary = "Delete group user (Admin only). Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteGroupUser(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        adminService.deleteGroupUser(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Group user permanently deleted" : "Group user soft deleted"));
    }
}
