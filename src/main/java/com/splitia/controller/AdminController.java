package com.splitia.controller;

import com.splitia.dto.request.*;
import com.splitia.dto.response.*;
import com.splitia.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(Pageable pageable) {
        Page<UserResponse> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserResponse user = adminService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PostMapping("/users")
    @Operation(summary = "Create user (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = adminService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PutMapping("/users/{id}")
    @Operation(summary = "Update user (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = adminService.updateUser(id, request);
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
    public ResponseEntity<ApiResponse<Page<GroupResponse>>> getAllGroups(Pageable pageable) {
        Page<GroupResponse> groups = adminService.getAllGroups(pageable);
        return ResponseEntity.ok(ApiResponse.success(groups));
    }
    
    @GetMapping("/groups/{id}")
    @Operation(summary = "Get group by ID (Admin only)")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroupById(@PathVariable UUID id) {
        GroupResponse group = adminService.getGroupById(id);
        return ResponseEntity.ok(ApiResponse.success(group));
    }
    
    @PostMapping("/groups")
    @Operation(summary = "Create group (Admin only)")
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        GroupResponse group = adminService.createGroup(request);
        return ResponseEntity.ok(ApiResponse.success(group));
    }
    
    @PutMapping("/groups/{id}")
    @Operation(summary = "Update group (Admin only)")
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGroupRequest request) {
        GroupResponse group = adminService.updateGroup(id, request);
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
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> getAllExpenses(Pageable pageable) {
        Page<ExpenseResponse> expenses = adminService.getAllExpenses(pageable);
        return ResponseEntity.ok(ApiResponse.success(expenses));
    }
    
    @GetMapping("/expenses/{id}")
    @Operation(summary = "Get expense by ID (Admin only)")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(@PathVariable UUID id) {
        ExpenseResponse expense = adminService.getExpenseById(id);
        return ResponseEntity.ok(ApiResponse.success(expense));
    }
    
    @PostMapping("/expenses")
    @Operation(summary = "Create expense (Admin only)")
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(@Valid @RequestBody CreateExpenseRequest request) {
        ExpenseResponse expense = adminService.createExpense(request);
        return ResponseEntity.ok(ApiResponse.success(expense));
    }
    
    @PutMapping("/expenses/{id}")
    @Operation(summary = "Update expense (Admin only)")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateExpenseRequest request) {
        ExpenseResponse expense = adminService.updateExpense(id, request);
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
    public ResponseEntity<ApiResponse<Page<ExpenseShareResponse>>> getAllExpenseShares(Pageable pageable) {
        Page<ExpenseShareResponse> expenseShares = adminService.getAllExpenseShares(pageable);
        return ResponseEntity.ok(ApiResponse.success(expenseShares));
    }
    
    @GetMapping("/expense-shares/{id}")
    @Operation(summary = "Get expense share by ID (Admin only)")
    public ResponseEntity<ApiResponse<ExpenseShareResponse>> getExpenseShareById(@PathVariable UUID id) {
        ExpenseShareResponse expenseShare = adminService.getExpenseShareById(id);
        return ResponseEntity.ok(ApiResponse.success(expenseShare));
    }
    
    @PostMapping("/expense-shares")
    @Operation(summary = "Create expense share (Admin only)")
    public ResponseEntity<ApiResponse<ExpenseShareResponse>> createExpenseShare(@Valid @RequestBody CreateExpenseShareRequest request) {
        ExpenseShareResponse expenseShare = adminService.createExpenseShare(request);
        return ResponseEntity.ok(ApiResponse.success(expenseShare));
    }
    
    @PutMapping("/expense-shares/{id}")
    @Operation(summary = "Update expense share (Admin only)")
    public ResponseEntity<ApiResponse<ExpenseShareResponse>> updateExpenseShare(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateExpenseShareRequest request) {
        ExpenseShareResponse expenseShare = adminService.updateExpenseShare(id, request);
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
    public ResponseEntity<ApiResponse<Page<BudgetResponse>>> getAllBudgets(Pageable pageable) {
        Page<BudgetResponse> budgets = adminService.getAllBudgets(pageable);
        return ResponseEntity.ok(ApiResponse.success(budgets));
    }
    
    @GetMapping("/budgets/{id}")
    @Operation(summary = "Get budget by ID (Admin only)")
    public ResponseEntity<ApiResponse<BudgetResponse>> getBudgetById(@PathVariable UUID id) {
        BudgetResponse budget = adminService.getBudgetById(id);
        return ResponseEntity.ok(ApiResponse.success(budget));
    }
    
    @PostMapping("/budgets")
    @Operation(summary = "Create budget (Admin only)")
    public ResponseEntity<ApiResponse<BudgetResponse>> createBudget(@Valid @RequestBody CreateBudgetRequest request) {
        BudgetResponse budget = adminService.createBudget(request);
        return ResponseEntity.ok(ApiResponse.success(budget));
    }
    
    @PutMapping("/budgets/{id}")
    @Operation(summary = "Update budget (Admin only)")
    public ResponseEntity<ApiResponse<BudgetResponse>> updateBudget(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBudgetRequest request) {
        BudgetResponse budget = adminService.updateBudget(id, request);
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
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getAllCategories(Pageable pageable) {
        Page<CategoryResponse> categories = adminService.getAllCategories(pageable);
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    @GetMapping("/categories/{id}")
    @Operation(summary = "Get category by ID (Admin only)")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable UUID id) {
        CategoryResponse category = adminService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(category));
    }
    
    @PostMapping("/categories")
    @Operation(summary = "Create category (Admin only)")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse category = adminService.createCategory(request);
        return ResponseEntity.ok(ApiResponse.success(category));
    }
    
    @PutMapping("/categories/{id}")
    @Operation(summary = "Update category (Admin only)")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryResponse category = adminService.updateCategory(id, request);
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
    public ResponseEntity<ApiResponse<Page<ConversationResponse>>> getAllConversations(Pageable pageable) {
        Page<ConversationResponse> conversations = adminService.getAllConversations(pageable);
        return ResponseEntity.ok(ApiResponse.success(conversations));
    }
    
    @GetMapping("/conversations/{id}")
    @Operation(summary = "Get conversation by ID (Admin only)")
    public ResponseEntity<ApiResponse<ConversationResponse>> getConversationById(@PathVariable UUID id) {
        ConversationResponse conversation = adminService.getConversationById(id);
        return ResponseEntity.ok(ApiResponse.success(conversation));
    }
    
    @PostMapping("/conversations")
    @Operation(summary = "Create conversation (Admin only)")
    public ResponseEntity<ApiResponse<ConversationResponse>> createConversation(@Valid @RequestBody CreateConversationRequest request) {
        ConversationResponse conversation = adminService.createConversation(request);
        return ResponseEntity.ok(ApiResponse.success(conversation));
    }
    
    @PutMapping("/conversations/{id}")
    @Operation(summary = "Update conversation (Admin only)")
    public ResponseEntity<ApiResponse<ConversationResponse>> updateConversation(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateConversationRequest request) {
        ConversationResponse conversation = adminService.updateConversation(id, request);
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
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getAllMessages(Pageable pageable) {
        Page<MessageResponse> messages = adminService.getAllMessages(pageable);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
    
    @GetMapping("/messages/{id}")
    @Operation(summary = "Get message by ID (Admin only)")
    public ResponseEntity<ApiResponse<MessageResponse>> getMessageById(@PathVariable UUID id) {
        MessageResponse message = adminService.getMessageById(id);
        return ResponseEntity.ok(ApiResponse.success(message));
    }
    
    @PostMapping("/messages")
    @Operation(summary = "Create message (Admin only)")
    public ResponseEntity<ApiResponse<MessageResponse>> createMessage(@Valid @RequestBody SendMessageRequest request) {
        MessageResponse message = adminService.createMessage(request);
        return ResponseEntity.ok(ApiResponse.success(message));
    }
    
    @PutMapping("/messages/{id}")
    @Operation(summary = "Update message (Admin only)")
    public ResponseEntity<ApiResponse<MessageResponse>> updateMessage(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMessageRequest request) {
        MessageResponse message = adminService.updateMessage(id, request);
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
    public ResponseEntity<ApiResponse<Page<SettlementResponse>>> getAllSettlements(Pageable pageable) {
        Page<SettlementResponse> settlements = adminService.getAllSettlements(pageable);
        return ResponseEntity.ok(ApiResponse.success(settlements));
    }
    
    @GetMapping("/settlements/{id}")
    @Operation(summary = "Get settlement by ID (Admin only)")
    public ResponseEntity<ApiResponse<SettlementResponse>> getSettlementById(@PathVariable UUID id) {
        SettlementResponse settlement = adminService.getSettlementById(id);
        return ResponseEntity.ok(ApiResponse.success(settlement));
    }
    
    @PostMapping("/settlements")
    @Operation(summary = "Create settlement (Admin only)")
    public ResponseEntity<ApiResponse<SettlementResponse>> createSettlement(@Valid @RequestBody CreateSettlementRequest request) {
        SettlementResponse settlement = adminService.createSettlement(request);
        return ResponseEntity.ok(ApiResponse.success(settlement));
    }
    
    @PutMapping("/settlements/{id}")
    @Operation(summary = "Update settlement (Admin only)")
    public ResponseEntity<ApiResponse<SettlementResponse>> updateSettlement(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSettlementRequest request) {
        SettlementResponse settlement = adminService.updateSettlement(id, request);
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
    public ResponseEntity<ApiResponse<Page<SubscriptionResponse>>> getAllSubscriptions(Pageable pageable) {
        Page<SubscriptionResponse> subscriptions = adminService.getAllSubscriptions(pageable);
        return ResponseEntity.ok(ApiResponse.success(subscriptions));
    }
    
    @GetMapping("/subscriptions/{id}")
    @Operation(summary = "Get subscription by ID (Admin only)")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getSubscriptionById(@PathVariable UUID id) {
        SubscriptionResponse subscription = adminService.getSubscriptionById(id);
        return ResponseEntity.ok(ApiResponse.success(subscription));
    }
    
    @PostMapping("/subscriptions")
    @Operation(summary = "Create subscription (Admin only)")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> createSubscription(@Valid @RequestBody CreateSubscriptionRequest request) {
        SubscriptionResponse subscription = adminService.createSubscription(request);
        return ResponseEntity.ok(ApiResponse.success(subscription));
    }
    
    @PutMapping("/subscriptions/{id}")
    @Operation(summary = "Update subscription (Admin only)")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> updateSubscription(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSubscriptionRequest request) {
        SubscriptionResponse subscription = adminService.updateSubscription(id, request);
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
    public ResponseEntity<ApiResponse<Page<SupportTicketResponse>>> getAllSupportTickets(Pageable pageable) {
        Page<SupportTicketResponse> tickets = adminService.getAllSupportTickets(pageable);
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }
    
    @GetMapping("/support-tickets/{id}")
    @Operation(summary = "Get support ticket by ID (Admin only)")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> getSupportTicketById(@PathVariable UUID id) {
        SupportTicketResponse ticket = adminService.getSupportTicketById(id);
        return ResponseEntity.ok(ApiResponse.success(ticket));
    }
    
    @PostMapping("/support-tickets")
    @Operation(summary = "Create support ticket (Admin only)")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> createSupportTicket(@Valid @RequestBody CreateSupportTicketRequest request) {
        SupportTicketResponse ticket = adminService.createSupportTicket(request);
        return ResponseEntity.ok(ApiResponse.success(ticket));
    }
    
    @PutMapping("/support-tickets/{id}")
    @Operation(summary = "Update support ticket (Admin only)")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> updateSupportTicket(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSupportTicketRequest request) {
        SupportTicketResponse ticket = adminService.updateSupportTicket(id, request);
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
    public ResponseEntity<ApiResponse<Page<GroupInvitationResponse>>> getAllGroupInvitations(Pageable pageable) {
        Page<GroupInvitationResponse> invitations = adminService.getAllGroupInvitations(pageable);
        return ResponseEntity.ok(ApiResponse.success(invitations));
    }
    
    @GetMapping("/group-invitations/{id}")
    @Operation(summary = "Get group invitation by ID (Admin only)")
    public ResponseEntity<ApiResponse<GroupInvitationResponse>> getGroupInvitationById(@PathVariable UUID id) {
        GroupInvitationResponse invitation = adminService.getGroupInvitationById(id);
        return ResponseEntity.ok(ApiResponse.success(invitation));
    }
    
    @PostMapping("/group-invitations")
    @Operation(summary = "Create group invitation (Admin only)")
    public ResponseEntity<ApiResponse<GroupInvitationResponse>> createGroupInvitation(@Valid @RequestBody CreateGroupInvitationRequest request) {
        GroupInvitationResponse invitation = adminService.createGroupInvitation(request);
        return ResponseEntity.ok(ApiResponse.success(invitation));
    }
    
    @PutMapping("/group-invitations/{id}")
    @Operation(summary = "Update group invitation (Admin only)")
    public ResponseEntity<ApiResponse<GroupInvitationResponse>> updateGroupInvitation(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGroupInvitationRequest request) {
        GroupInvitationResponse invitation = adminService.updateGroupInvitation(id, request);
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
    public ResponseEntity<ApiResponse<Page<GroupUserResponse>>> getAllGroupUsers(Pageable pageable) {
        Page<GroupUserResponse> groupUsers = adminService.getAllGroupUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(groupUsers));
    }
    
    @GetMapping("/group-users/{id}")
    @Operation(summary = "Get group user by ID (Admin only)")
    public ResponseEntity<ApiResponse<GroupUserResponse>> getGroupUserById(@PathVariable UUID id) {
        GroupUserResponse groupUser = adminService.getGroupUserById(id);
        return ResponseEntity.ok(ApiResponse.success(groupUser));
    }
    
    @PostMapping("/group-users")
    @Operation(summary = "Create group user (Admin only)")
    public ResponseEntity<ApiResponse<GroupUserResponse>> createGroupUser(@Valid @RequestBody CreateGroupUserRequest request) {
        GroupUserResponse groupUser = adminService.createGroupUser(request);
        return ResponseEntity.ok(ApiResponse.success(groupUser));
    }
    
    @PutMapping("/group-users/{id}")
    @Operation(summary = "Update group user (Admin only)")
    public ResponseEntity<ApiResponse<GroupUserResponse>> updateGroupUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGroupUserRequest request) {
        GroupUserResponse groupUser = adminService.updateGroupUser(id, request);
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
    
    // Plans
    @GetMapping("/plans")
    @Operation(summary = "Get all plans (Admin only)")
    public ResponseEntity<ApiResponse<Page<PlanResponse>>> getAllPlans(Pageable pageable) {
        Page<PlanResponse> plans = adminService.getAllPlans(pageable);
        return ResponseEntity.ok(ApiResponse.success(plans));
    }
    
    @GetMapping("/plans/{id}")
    @Operation(summary = "Get plan by ID (Admin only)")
    public ResponseEntity<ApiResponse<PlanResponse>> getPlanById(@PathVariable UUID id) {
        PlanResponse plan = adminService.getPlanById(id);
        return ResponseEntity.ok(ApiResponse.success(plan));
    }
    
    // Tasks
    @GetMapping("/tasks")
    @Operation(summary = "Get all tasks (Admin only)")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getAllTasks(Pageable pageable) {
        Page<TaskResponse> tasks = adminService.getAllTasks(pageable);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @GetMapping("/tasks/{id}")
    @Operation(summary = "Get task by ID (Admin only)")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(@PathVariable UUID id) {
        TaskResponse task = adminService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(task));
    }
    
    @PostMapping("/tasks")
    @Operation(summary = "Create task (Admin only)")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@Valid @RequestBody CreateTaskRequest request) {
        TaskResponse task = adminService.createTask(request);
        return ResponseEntity.ok(ApiResponse.success(task));
    }
    
    @PutMapping("/tasks/{id}")
    @Operation(summary = "Update task (Admin only)")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request) {
        TaskResponse task = adminService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success(task));
    }
    
    @DeleteMapping("/tasks/{id}")
    @Operation(summary = "Delete task (Admin only). Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        adminService.deleteTask(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Task permanently deleted" : "Task soft deleted"));
    }
    
    // Task Tags
    @GetMapping("/task-tags")
    @Operation(summary = "Get all task tags (Admin only)")
    public ResponseEntity<ApiResponse<Page<TaskTagResponse>>> getAllTaskTags(Pageable pageable) {
        Page<TaskTagResponse> tags = adminService.getAllTaskTags(pageable);
        return ResponseEntity.ok(ApiResponse.success(tags));
    }
    
    @GetMapping("/task-tags/{id}")
    @Operation(summary = "Get task tag by ID (Admin only)")
    public ResponseEntity<ApiResponse<TaskTagResponse>> getTaskTagById(@PathVariable UUID id) {
        TaskTagResponse tag = adminService.getTaskTagById(id);
        return ResponseEntity.ok(ApiResponse.success(tag));
    }
    
    @PostMapping("/task-tags")
    @Operation(summary = "Create task tag (Admin only)")
    public ResponseEntity<ApiResponse<TaskTagResponse>> createTaskTag(@Valid @RequestBody CreateTaskTagRequest request) {
        TaskTagResponse tag = adminService.createTaskTag(request);
        return ResponseEntity.ok(ApiResponse.success(tag));
    }
    
    @PutMapping("/task-tags/{id}")
    @Operation(summary = "Update task tag (Admin only)")
    public ResponseEntity<ApiResponse<TaskTagResponse>> updateTaskTag(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskTagRequest request) {
        TaskTagResponse tag = adminService.updateTaskTag(id, request);
        return ResponseEntity.ok(ApiResponse.success(tag));
    }
    
    @DeleteMapping("/task-tags/{id}")
    @Operation(summary = "Delete task tag (Admin only). Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteTaskTag(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        adminService.deleteTaskTag(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Task tag permanently deleted" : "Task tag soft deleted"));
    }
}
