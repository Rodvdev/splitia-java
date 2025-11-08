package com.splitia.service;

import com.splitia.dto.request.*;
import com.splitia.dto.response.*;
import com.splitia.exception.BadRequestException;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.*;
import com.splitia.model.*;
import com.splitia.model.enums.GroupRole;
import com.splitia.model.enums.ShareType;
import com.splitia.model.enums.SubscriptionStatus;
import com.splitia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseShareRepository expenseShareRepository;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final SettlementRepository settlementRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final GroupInvitationRepository groupInvitationRepository;
    private final GroupUserRepository groupUserRepository;
    private final PlanRepository planRepository;
    private final TaskRepository taskRepository;
    private final TaskTagRepository taskTagRepository;
    private final ConversationParticipantRepository conversationParticipantRepository;
    private final PasswordEncoder passwordEncoder;
    
    // Mappers
    private final UserMapper userMapper;
    private final GroupMapper groupMapper;
    private final ExpenseMapper expenseMapper;
    private final BudgetMapper budgetMapper;
    private final CategoryMapper categoryMapper;
    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;
    private final SettlementMapper settlementMapper;
    private final SubscriptionMapper subscriptionMapper;
    private final SupportTicketMapper supportTicketMapper;
    private final GroupInvitationMapper groupInvitationMapper;
    private final GroupUserMapper groupUserMapper;
    private final PlanMapper planMapper;
    
    // Services for create/update operations
    private final TaskService taskService;
    private final TaskTagService taskTagService;
    
    // Users
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toResponse(user);
    }
    
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        
        User user = new User();
        user.setName(request.getName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setImage(request.getImage());
        user.setCurrency(request.getCurrency() != null ? request.getCurrency() : "PEN");
        user.setLanguage(request.getLanguage() != null ? request.getLanguage() : "es");
        user.setRole(request.getRole() != null ? request.getRole() : "USER");
        
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }
    
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getImage() != null) {
            user.setImage(request.getImage());
        }
        if (request.getCurrency() != null) {
            user.setCurrency(request.getCurrency());
        }
        if (request.getLanguage() != null) {
            user.setLanguage(request.getLanguage());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }
    
    @Transactional
    public void deleteUser(UUID id, boolean hardDelete) {
        if (hardDelete) {
            userRepository.deleteById(id);
        } else {
            User user = userRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
            user.setDeletedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }
    
    // Groups
    @Transactional(readOnly = true)
    public Page<GroupResponse> getAllGroups(Pageable pageable) {
        return groupRepository.findAll(pageable)
                .map(groupMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public GroupResponse getGroupById(UUID id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", id));
        return groupMapper.toResponse(group);
    }
    
    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request) {
        // Admin can create groups without being a member
        // We need a user to be the creator, but for admin we'll use the first admin user or create without creator validation
        User adminUser = userRepository.findAll().stream()
                .filter(u -> "ADMIN".equals(u.getRole()))
                .findFirst()
                .orElse(userRepository.findAll().stream()
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("No users found")));
        
        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setImage(request.getImage());
        group.setCreatedBy(adminUser);
        
        group = groupRepository.save(group);
        
        // Create GroupUser for creator as ADMIN
        GroupUser groupUser = new GroupUser();
        groupUser.setUser(adminUser);
        groupUser.setGroup(group);
        groupUser.setRole(GroupRole.ADMIN);
        groupUserRepository.save(groupUser);
        
        // Create conversation for the group
        Conversation conversation = new Conversation();
        conversation.setGroup(group);
        conversation.setIsGroupChat(true);
        conversation.setName(group.getName());
        conversationRepository.save(conversation);
        
        return groupMapper.toResponse(group);
    }
    
    @Transactional
    public GroupResponse updateGroup(UUID id, UpdateGroupRequest request) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", id));
        
        if (request.getName() != null) {
            group.setName(request.getName());
        }
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }
        if (request.getImage() != null) {
            group.setImage(request.getImage());
        }
        
        group = groupRepository.save(group);
        return groupMapper.toResponse(group);
    }
    
    @Transactional
    public void deleteGroup(UUID id, boolean hardDelete) {
        if (hardDelete) {
            groupRepository.deleteById(id);
        } else {
            Group group = groupRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Group", "id", id));
            group.setDeletedAt(LocalDateTime.now());
            groupRepository.save(group);
        }
    }
    
    // Expenses
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getAllExpenses(Pageable pageable) {
        return expenseRepository.findAll(pageable)
                .map(expenseMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(UUID id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
        return expenseMapper.toResponse(expense);
    }
    
    @Transactional
    public ExpenseResponse createExpense(CreateExpenseRequest request) {
        User paidBy = userRepository.findById(request.getPaidById())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getPaidById()));
        
        Expense expense = new Expense();
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setDate(request.getDate());
        expense.setCurrency(request.getCurrency());
        expense.setLocation(request.getLocation());
        expense.setNotes(request.getNotes());
        expense.setPaidBy(paidBy);
        
        if (request.getGroupId() != null) {
            Group group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Group", "id", request.getGroupId()));
            expense.setGroup(group);
        }
        
        expense = expenseRepository.save(expense);
        
        // Create shares
        BigDecimal totalShares = request.getShares().stream()
                .map(ExpenseShareRequest::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalShares.compareTo(request.getAmount()) != 0) {
            throw new BadRequestException("Sum of shares must equal the expense amount");
        }
        
        for (ExpenseShareRequest shareRequest : request.getShares()) {
            ExpenseShare share = new ExpenseShare();
            share.setExpense(expense);
            share.setUser(userRepository.findById(shareRequest.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", shareRequest.getUserId())));
            share.setAmount(shareRequest.getAmount());
            share.setType(shareRequest.getType() != null ? shareRequest.getType() : ShareType.EQUAL);
            expenseShareRepository.save(share);
        }
        
        return expenseMapper.toResponse(expense);
    }
    
    @Transactional
    public ExpenseResponse updateExpense(UUID id, UpdateExpenseRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
        
        if (request.getAmount() != null) {
            expense.setAmount(request.getAmount());
        }
        if (request.getDescription() != null) {
            expense.setDescription(request.getDescription());
        }
        if (request.getDate() != null) {
            expense.setDate(request.getDate());
        }
        if (request.getCurrency() != null) {
            expense.setCurrency(request.getCurrency());
        }
        if (request.getLocation() != null) {
            expense.setLocation(request.getLocation());
        }
        if (request.getNotes() != null) {
            expense.setNotes(request.getNotes());
        }
        
        // Update shares if provided
        if (request.getShares() != null && !request.getShares().isEmpty()) {
            // Delete existing shares
            expenseShareRepository.deleteAll(expense.getShares());
            
            // Create new shares
            BigDecimal totalShares = request.getShares().stream()
                    .map(ExpenseShareRequest::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            if (totalShares.compareTo(expense.getAmount()) != 0) {
                throw new BadRequestException("Sum of shares must equal the expense amount");
            }
            
            for (ExpenseShareRequest shareRequest : request.getShares()) {
                ExpenseShare share = new ExpenseShare();
                share.setExpense(expense);
                share.setUser(userRepository.findById(shareRequest.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", shareRequest.getUserId())));
                share.setAmount(shareRequest.getAmount());
                share.setType(shareRequest.getType() != null ? shareRequest.getType() : ShareType.EQUAL);
                expenseShareRepository.save(share);
            }
        }
        
        expense = expenseRepository.save(expense);
        return expenseMapper.toResponse(expense);
    }
    
    @Transactional
    public void deleteExpense(UUID id, boolean hardDelete) {
        if (hardDelete) {
            expenseRepository.deleteById(id);
        } else {
            Expense expense = expenseRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
            expense.setDeletedAt(LocalDateTime.now());
            expenseRepository.save(expense);
        }
    }
    
    // Expense Shares
    @Transactional(readOnly = true)
    public Page<ExpenseShareResponse> getAllExpenseShares(Pageable pageable) {
        return expenseShareRepository.findAll(pageable)
                .map(expenseMapper::toShareResponse);
    }
    
    @Transactional(readOnly = true)
    public ExpenseShareResponse getExpenseShareById(UUID id) {
        ExpenseShare expenseShare = expenseShareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseShare", "id", id));
        return expenseMapper.toShareResponse(expenseShare);
    }
    
    @Transactional
    public ExpenseShareResponse createExpenseShare(CreateExpenseShareRequest request) {
        Expense expense = expenseRepository.findById(request.getExpenseId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", request.getExpenseId()));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
        
        ExpenseShare share = new ExpenseShare();
        share.setExpense(expense);
        share.setUser(user);
        share.setAmount(request.getAmount());
        share.setType(request.getType() != null ? request.getType() : ShareType.EQUAL);
        
        share = expenseShareRepository.save(share);
        return expenseMapper.toShareResponse(share);
    }
    
    @Transactional
    public ExpenseShareResponse updateExpenseShare(UUID id, UpdateExpenseShareRequest request) {
        ExpenseShare share = expenseShareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseShare", "id", id));
        
        if (request.getAmount() != null) {
            share.setAmount(request.getAmount());
        }
        if (request.getType() != null) {
            share.setType(request.getType());
        }
        
        share = expenseShareRepository.save(share);
        return expenseMapper.toShareResponse(share);
    }
    
    @Transactional
    public void deleteExpenseShare(UUID id, boolean hardDelete) {
        if (hardDelete) {
            expenseShareRepository.deleteById(id);
        } else {
            ExpenseShare expenseShare = expenseShareRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("ExpenseShare", "id", id));
            expenseShare.setDeletedAt(LocalDateTime.now());
            expenseShareRepository.save(expenseShare);
        }
    }
    
    // Budgets
    @Transactional(readOnly = true)
    public Page<BudgetResponse> getAllBudgets(Pageable pageable) {
        return budgetRepository.findAll(pageable)
                .map(budgetMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(UUID id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", id));
        return budgetMapper.toResponse(budget);
    }
    
    @Transactional
    public BudgetResponse createBudget(CreateBudgetRequest request) {
        // Admin can create budget for any user - use first user if needed
        // For admin, we'll need userId in request or use first user
        User user = userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No users found"));
        
        Budget budget = new Budget();
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        budget.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        budget.setUser(user);
        
        budget = budgetRepository.save(budget);
        return budgetMapper.toResponse(budget);
    }
    
    @Transactional
    public BudgetResponse updateBudget(UUID id, UpdateBudgetRequest request) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", id));
        
        if (request.getAmount() != null) {
            budget.setAmount(request.getAmount());
        }
        if (request.getCurrency() != null) {
            budget.setCurrency(request.getCurrency());
        }
        
        budget = budgetRepository.save(budget);
        return budgetMapper.toResponse(budget);
    }
    
    @Transactional
    public void deleteBudget(UUID id, boolean hardDelete) {
        if (hardDelete) {
            budgetRepository.deleteById(id);
        } else {
            Budget budget = budgetRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", id));
            budget.setDeletedAt(LocalDateTime.now());
            budgetRepository.save(budget);
        }
    }
    
    // Categories
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID id) {
        CustomCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return categoryMapper.toResponse(category);
    }
    
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        // Admin can create category for any group - get the group
        Group group = groupRepository.findByIdAndDeletedAtIsNull(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", request.getGroupId()));
        
        // Check if category with same name already exists in this group
        if (categoryRepository.existsByGroupIdAndName(request.getGroupId(), request.getName())) {
            throw new BadRequestException("Category with this name already exists in this group");
        }
        
        // Use first admin user as creator, or first user if no admin exists
        User creator = userRepository.findAll().stream()
                .filter(u -> "ADMIN".equals(u.getRole()))
                .findFirst()
                .orElse(userRepository.findAll().stream()
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("No users found")));
        
        CustomCategory category = new CustomCategory();
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());
        category.setGroup(group);
        category.setCreatedBy(creator);
        
        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }
    
    @Transactional
    public CategoryResponse updateCategory(UUID id, UpdateCategoryRequest request) {
        CustomCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        if (request.getName() != null && !request.getName().equals(category.getName())) {
            // Check if new name already exists in this group
            if (categoryRepository.existsByGroupIdAndName(category.getGroup().getId(), request.getName())) {
                throw new BadRequestException("Category with this name already exists in this group");
            }
            category.setName(request.getName());
        }
        if (request.getIcon() != null) {
            category.setIcon(request.getIcon());
        }
        if (request.getColor() != null) {
            category.setColor(request.getColor());
        }
        
        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }
    
    @Transactional
    public void deleteCategory(UUID id, boolean hardDelete) {
        if (hardDelete) {
            categoryRepository.deleteById(id);
        } else {
            CustomCategory category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
            category.setDeletedAt(LocalDateTime.now());
            categoryRepository.save(category);
        }
    }
    
    // Conversations
    @Transactional(readOnly = true)
    public Page<ConversationResponse> getAllConversations(Pageable pageable) {
        return conversationRepository.findAll(pageable)
                .map(conversationMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public ConversationResponse getConversationById(UUID id) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));
        return conversationMapper.toResponse(conversation);
    }
    
    @Transactional
    public ConversationResponse createConversation(CreateConversationRequest request) {
        Conversation conversation = new Conversation();
        conversation.setName(request.getName());
        conversation.setIsGroupChat(request.getParticipantIds() != null && request.getParticipantIds().size() > 1);
        
        conversation = conversationRepository.save(conversation);
        
        // Add participants
        if (request.getParticipantIds() != null) {
            for (UUID participantId : request.getParticipantIds()) {
                User participant = userRepository.findById(participantId)
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", participantId));
                
                ConversationParticipant cp = new ConversationParticipant();
                cp.setConversation(conversation);
                cp.setUser(participant);
                conversationParticipantRepository.save(cp);
            }
        }
        
        return conversationMapper.toResponse(conversation);
    }
    
    @Transactional
    public ConversationResponse updateConversation(UUID id, UpdateConversationRequest request) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));
        
        if (request.getName() != null) {
            conversation.setName(request.getName());
        }
        
        conversation = conversationRepository.save(conversation);
        return conversationMapper.toResponse(conversation);
    }
    
    @Transactional
    public void deleteConversation(UUID id, boolean hardDelete) {
        if (hardDelete) {
            conversationRepository.deleteById(id);
        } else {
            Conversation conversation = conversationRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));
            conversation.setDeletedAt(LocalDateTime.now());
            conversationRepository.save(conversation);
        }
    }
    
    // Messages
    @Transactional(readOnly = true)
    public Page<MessageResponse> getAllMessages(Pageable pageable) {
        return messageRepository.findAll(pageable)
                .map(messageMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public MessageResponse getMessageById(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));
        return messageMapper.toResponse(message);
    }
    
    @Transactional
    public MessageResponse createMessage(SendMessageRequest request) {
        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", request.getConversationId()));
        
        // Admin can send message as any user - use first user
        User sender = userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No users found"));
        
        Message message = new Message();
        message.setContent(request.getContent());
        message.setConversation(conversation);
        message.setSender(sender);
        message.setCreatedAt(LocalDateTime.now());
        message.setIsAI(false);
        
        message = messageRepository.save(message);
        return messageMapper.toResponse(message);
    }
    
    @Transactional
    public MessageResponse updateMessage(UUID id, UpdateMessageRequest request) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));
        
        if (request.getContent() != null) {
            message.setContent(request.getContent());
        }
        
        message = messageRepository.save(message);
        return messageMapper.toResponse(message);
    }
    
    @Transactional
    public void deleteMessage(UUID id, boolean hardDelete) {
        if (hardDelete) {
            messageRepository.deleteById(id);
        } else {
            Message message = messageRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));
            message.setDeletedAt(LocalDateTime.now());
            messageRepository.save(message);
        }
    }
    
    // Settlements
    @Transactional(readOnly = true)
    public Page<SettlementResponse> getAllSettlements(Pageable pageable) {
        return settlementRepository.findAll(pageable)
                .map(settlementMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public SettlementResponse getSettlementById(UUID id) {
        Settlement settlement = settlementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Settlement", "id", id));
        return settlementMapper.toResponse(settlement);
    }
    
    @Transactional
    public SettlementResponse createSettlement(CreateSettlementRequest request) {
        // Admin can create settlement - use first user as initiatedBy
        User initiatedBy = userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No users found"));
        User settledWithUser = userRepository.findById(request.getSettledWithUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getSettledWithUserId()));
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", request.getGroupId()));
        
        Settlement settlement = new Settlement();
        settlement.setAmount(request.getAmount());
        settlement.setCurrency(request.getCurrency());
        settlement.setDescription(request.getDescription());
        settlement.setDate(request.getDate());
        settlement.setType(request.getType());
        settlement.setInitiatedBy(initiatedBy);
        settlement.setSettledWithUser(settledWithUser);
        settlement.setGroup(group);
        
        settlement = settlementRepository.save(settlement);
        return settlementMapper.toResponse(settlement);
    }
    
    @Transactional
    public SettlementResponse updateSettlement(UUID id, UpdateSettlementRequest request) {
        Settlement settlement = settlementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Settlement", "id", id));
        
        if (request.getAmount() != null) {
            settlement.setAmount(request.getAmount());
        }
        if (request.getDescription() != null) {
            settlement.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            settlement.setStatus(request.getStatus());
        }
        
        settlement = settlementRepository.save(settlement);
        return settlementMapper.toResponse(settlement);
    }
    
    @Transactional
    public void deleteSettlement(UUID id, boolean hardDelete) {
        if (hardDelete) {
            settlementRepository.deleteById(id);
        } else {
            Settlement settlement = settlementRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Settlement", "id", id));
            settlement.setDeletedAt(LocalDateTime.now());
            settlementRepository.save(settlement);
        }
    }
    
    // Subscriptions
    @Transactional(readOnly = true)
    public Page<SubscriptionResponse> getAllSubscriptions(Pageable pageable) {
        return subscriptionRepository.findAllWithUser(pageable)
                .map(subscriptionMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscriptionById(UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));
        return subscriptionMapper.toResponse(subscription);
    }
    
    @Transactional
    public SubscriptionResponse createSubscription(CreateSubscriptionRequest request) {
        // Admin can create subscription for any user - use first user if needed
        User user = userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No users found"));
        
        Subscription subscription = new Subscription();
        subscription.setPlanType(request.getPlanType());
        subscription.setPaymentMethod(request.getPaymentMethod());
        subscription.setStartDate(LocalDateTime.now());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setUser(user);
        
        subscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toResponse(subscription);
    }
    
    @Transactional
    public SubscriptionResponse updateSubscription(UUID id, UpdateSubscriptionRequest request) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));
        
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
    public void deleteSubscription(UUID id, boolean hardDelete) {
        if (hardDelete) {
            subscriptionRepository.deleteById(id);
        } else {
            Subscription subscription = subscriptionRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));
            subscription.setDeletedAt(LocalDateTime.now());
            subscriptionRepository.save(subscription);
        }
    }
    
    // Support Tickets
    @Transactional(readOnly = true)
    public Page<SupportTicketResponse> getAllSupportTickets(Pageable pageable) {
        return supportTicketRepository.findAll(pageable)
                .map(supportTicketMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public SupportTicketResponse getSupportTicketById(UUID id) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", id));
        return supportTicketMapper.toResponse(ticket);
    }
    
    @Transactional
    public SupportTicketResponse createSupportTicket(CreateSupportTicketRequest request) {
        // Admin can create ticket for any user - use first user if needed
        User createdBy = userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No users found"));
        
        SupportTicket ticket = new SupportTicket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setCategory(request.getCategory());
        ticket.setPriority(request.getPriority() != null ? request.getPriority() : com.splitia.model.enums.SupportPriority.MEDIUM);
        ticket.setCreatedBy(createdBy);
        
        ticket = supportTicketRepository.save(ticket);
        return supportTicketMapper.toResponse(ticket);
    }
    
    @Transactional
    public SupportTicketResponse updateSupportTicket(UUID id, UpdateSupportTicketRequest request) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", id));
        
        if (request.getTitle() != null) {
            ticket.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            ticket.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            ticket.setStatus(request.getStatus());
            if (request.getStatus().name().equals("RESOLVED") && ticket.getResolvedAt() == null) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
        }
        if (request.getPriority() != null) {
            ticket.setPriority(request.getPriority());
        }
        if (request.getResolution() != null) {
            ticket.setResolution(request.getResolution());
        }
        
        ticket = supportTicketRepository.save(ticket);
        return supportTicketMapper.toResponse(ticket);
    }
    
    @Transactional
    public void deleteSupportTicket(UUID id, boolean hardDelete) {
        if (hardDelete) {
            supportTicketRepository.deleteById(id);
        } else {
            SupportTicket ticket = supportTicketRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", id));
            ticket.setDeletedAt(LocalDateTime.now());
            supportTicketRepository.save(ticket);
        }
    }
    
    // Group Invitations
    @Transactional(readOnly = true)
    public Page<GroupInvitationResponse> getAllGroupInvitations(Pageable pageable) {
        return groupInvitationRepository.findAll(pageable)
                .map(groupInvitationMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public GroupInvitationResponse getGroupInvitationById(UUID id) {
        GroupInvitation invitation = groupInvitationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GroupInvitation", "id", id));
        return groupInvitationMapper.toResponse(invitation);
    }
    
    @Transactional
    public GroupInvitationResponse createGroupInvitation(CreateGroupInvitationRequest request) {
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", request.getGroupId()));
        
        // Admin can create invitation - use first admin user
        User createdBy = userRepository.findAll().stream()
                .filter(u -> "ADMIN".equals(u.getRole()))
                .findFirst()
                .orElse(userRepository.findAll().stream()
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("No users found")));
        
        GroupInvitation invitation = new GroupInvitation();
        invitation.setToken(java.util.UUID.randomUUID().toString());
        invitation.setExpiresAt(request.getExpiresAt());
        invitation.setMaxUses(request.getMaxUses());
        invitation.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        invitation.setGroup(group);
        invitation.setCreatedBy(createdBy);
        
        invitation = groupInvitationRepository.save(invitation);
        return groupInvitationMapper.toResponse(invitation);
    }
    
    @Transactional
    public GroupInvitationResponse updateGroupInvitation(UUID id, UpdateGroupInvitationRequest request) {
        GroupInvitation invitation = groupInvitationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GroupInvitation", "id", id));
        
        if (request.getExpiresAt() != null) {
            invitation.setExpiresAt(request.getExpiresAt());
        }
        if (request.getMaxUses() != null) {
            invitation.setMaxUses(request.getMaxUses());
        }
        if (request.getIsActive() != null) {
            invitation.setIsActive(request.getIsActive());
        }
        
        invitation = groupInvitationRepository.save(invitation);
        return groupInvitationMapper.toResponse(invitation);
    }
    
    @Transactional
    public void deleteGroupInvitation(UUID id, boolean hardDelete) {
        if (hardDelete) {
            groupInvitationRepository.deleteById(id);
        } else {
            GroupInvitation invitation = groupInvitationRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("GroupInvitation", "id", id));
            invitation.setDeletedAt(LocalDateTime.now());
            groupInvitationRepository.save(invitation);
        }
    }
    
    // Group Users
    @Transactional(readOnly = true)
    public Page<GroupUserResponse> getAllGroupUsers(Pageable pageable) {
        return groupUserRepository.findAll(pageable)
                .map(groupUserMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public GroupUserResponse getGroupUserById(UUID id) {
        GroupUser groupUser = groupUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GroupUser", "id", id));
        return groupUserMapper.toResponse(groupUser);
    }
    
    @Transactional
    public GroupUserResponse createGroupUser(CreateGroupUserRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", request.getGroupId()));
        
        GroupUser groupUser = new GroupUser();
        groupUser.setUser(user);
        groupUser.setGroup(group);
        groupUser.setRole(request.getRole() != null ? request.getRole() : GroupRole.MEMBER);
        groupUser.setPermissions(request.getPermissions());
        
        groupUser = groupUserRepository.save(groupUser);
        return groupUserMapper.toResponse(groupUser);
    }
    
    @Transactional
    public GroupUserResponse updateGroupUser(UUID id, UpdateGroupUserRequest request) {
        GroupUser groupUser = groupUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GroupUser", "id", id));
        
        if (request.getRole() != null) {
            groupUser.setRole(request.getRole());
        }
        if (request.getPermissions() != null) {
            groupUser.setPermissions(request.getPermissions());
        }
        
        groupUser = groupUserRepository.save(groupUser);
        return groupUserMapper.toResponse(groupUser);
    }
    
    @Transactional
    public void deleteGroupUser(UUID id, boolean hardDelete) {
        if (hardDelete) {
            groupUserRepository.deleteById(id);
        } else {
            GroupUser groupUser = groupUserRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("GroupUser", "id", id));
            groupUser.setDeletedAt(LocalDateTime.now());
            groupUserRepository.save(groupUser);
        }
    }
    
    // Plans
    @Transactional(readOnly = true)
    public Page<PlanResponse> getAllPlans(Pageable pageable) {
        return planRepository.findAll(pageable)
                .map(planMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public PlanResponse getPlanById(UUID id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", id));
        return planMapper.toResponse(plan);
    }
    
    // Tasks
    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(this::taskToResponse);
    }
    
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        return taskToResponse(task);
    }
    
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        // Use TaskService but bypass security checks for admin
        // For now, we'll use TaskService directly - admin should have proper permissions
        return taskService.createTask(request);
    }
    
    @Transactional
    public TaskResponse updateTask(UUID id, UpdateTaskRequest request) {
        // Use TaskService but bypass security checks for admin
        return taskService.updateTask(id, request);
    }
    
    @Transactional
    public void deleteTask(UUID id, boolean hardDelete) {
        if (hardDelete) {
            taskRepository.deleteById(id);
        } else {
            Task task = taskRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
            task.setDeletedAt(LocalDateTime.now());
            taskRepository.save(task);
        }
    }
    
    // Task Tags
    @Transactional(readOnly = true)
    public Page<TaskTagResponse> getAllTaskTags(Pageable pageable) {
        return taskTagRepository.findAll(pageable)
                .map(this::taskTagToResponse);
    }
    
    @Transactional(readOnly = true)
    public TaskTagResponse getTaskTagById(UUID id) {
        TaskTag tag = taskTagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskTag", "id", id));
        return taskTagToResponse(tag);
    }
    
    @Transactional
    public TaskTagResponse createTaskTag(CreateTaskTagRequest request) {
        // Use TaskTagService but bypass security checks for admin
        return taskTagService.createTag(request);
    }
    
    @Transactional
    public TaskTagResponse updateTaskTag(UUID id, UpdateTaskTagRequest request) {
        // Use TaskTagService but bypass security checks for admin
        return taskTagService.updateTag(id, request);
    }
    
    @Transactional
    public void deleteTaskTag(UUID id, boolean hardDelete) {
        if (hardDelete) {
            taskTagRepository.deleteById(id);
        } else {
            TaskTag tag = taskTagRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("TaskTag", "id", id));
            tag.setDeletedAt(LocalDateTime.now());
            taskTagRepository.save(tag);
        }
    }
    
    // Helper methods for Task and TaskTag conversion
    private TaskResponse taskToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setStartDate(task.getStartDate());
        response.setDueDate(task.getDueDate());
        response.setPriority(task.getPriority());
        response.setGroupId(task.getGroup().getId());
        response.setGroupName(task.getGroup().getName());
        response.setCreatedById(task.getCreatedBy().getId());
        response.setCreatedByName(task.getCreatedBy().getName() + " " + 
                (task.getCreatedBy().getLastName() != null ? task.getCreatedBy().getLastName() : ""));
        response.setPosition(task.getPosition());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        
        if (task.getAssignedTo() != null) {
            response.setAssignedToId(task.getAssignedTo().getId());
            response.setAssignedToName(task.getAssignedTo().getName() + " " + 
                    (task.getAssignedTo().getLastName() != null ? task.getAssignedTo().getLastName() : ""));
        }
        
        if (task.getTags() != null && !task.getTags().isEmpty()) {
            response.setTags(task.getTags().stream()
                    .map(this::taskTagToResponse)
                    .collect(Collectors.toList()));
        }
        
        // Expense association
        if (task.getExpense() != null) {
            response.setExpenseId(task.getExpense().getId());
        }
        
        // Future expense fields
        if (task.getFutureExpenseAmount() != null) {
            response.setFutureExpenseAmount(task.getFutureExpenseAmount());
            response.setFutureExpenseCurrency(task.getFutureExpenseCurrency());
        }
        
        if (task.getFutureExpensePaidBy() != null) {
            response.setFutureExpensePaidById(task.getFutureExpensePaidBy().getId());
            response.setFutureExpensePaidByName(task.getFutureExpensePaidBy().getName() + " " + 
                    (task.getFutureExpensePaidBy().getLastName() != null ? task.getFutureExpensePaidBy().getLastName() : ""));
        }
        
        if (task.getFutureExpenseShares() != null && !task.getFutureExpenseShares().isEmpty()) {
            response.setFutureExpenseShares(convertFutureExpenseSharesToResponse(task.getFutureExpenseShares()));
        }
        
        return response;
    }
    
    private TaskTagResponse taskTagToResponse(TaskTag tag) {
        TaskTagResponse response = new TaskTagResponse();
        response.setId(tag.getId());
        response.setName(tag.getName());
        response.setColor(tag.getColor());
        response.setGroupId(tag.getGroup().getId());
        response.setCreatedAt(tag.getCreatedAt());
        return response;
    }
    
    private List<FutureExpenseShareResponse> convertFutureExpenseSharesToResponse(List<Map<String, Object>> shares) {
        List<FutureExpenseShareResponse> result = new ArrayList<>();
        for (Map<String, Object> shareMap : shares) {
            FutureExpenseShareResponse shareResponse = new FutureExpenseShareResponse();
            
            if (shareMap.get("userId") != null) {
                shareResponse.setUserId(UUID.fromString(shareMap.get("userId").toString()));
            }
            
            if (shareMap.get("userName") != null) {
                shareResponse.setUserName(shareMap.get("userName").toString());
            }
            
            if (shareMap.get("amount") != null) {
                if (shareMap.get("amount") instanceof Number) {
                    shareResponse.setAmount(BigDecimal.valueOf(((Number) shareMap.get("amount")).doubleValue()));
                } else if (shareMap.get("amount") instanceof String) {
                    shareResponse.setAmount(new BigDecimal((String) shareMap.get("amount")));
                }
            }
            
            if (shareMap.get("type") != null) {
                try {
                    shareResponse.setType(com.splitia.model.enums.ShareType.valueOf(shareMap.get("type").toString()));
                } catch (IllegalArgumentException e) {
                    shareResponse.setType(com.splitia.model.enums.ShareType.EQUAL);
                }
            }
            
            result.add(shareResponse);
        }
        return result;
    }
}
