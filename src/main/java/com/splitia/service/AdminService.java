package com.splitia.service;

import com.splitia.exception.ResourceNotFoundException;
import com.splitia.model.*;
import com.splitia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

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
    
    // Users
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
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
    public Page<Group> getAllGroups(Pageable pageable) {
        return groupRepository.findAll(pageable);
    }
    
    public Group getGroupById(UUID id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", id));
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
    public Page<Expense> getAllExpenses(Pageable pageable) {
        return expenseRepository.findAll(pageable);
    }
    
    public Expense getExpenseById(UUID id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
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
    public Page<ExpenseShare> getAllExpenseShares(Pageable pageable) {
        return expenseShareRepository.findAll(pageable);
    }
    
    public ExpenseShare getExpenseShareById(UUID id) {
        return expenseShareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseShare", "id", id));
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
    public Page<Budget> getAllBudgets(Pageable pageable) {
        return budgetRepository.findAll(pageable);
    }
    
    public Budget getBudgetById(UUID id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", id));
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
    public Page<CustomCategory> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
    
    public CustomCategory getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
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
    public Page<Conversation> getAllConversations(Pageable pageable) {
        return conversationRepository.findAll(pageable);
    }
    
    public Conversation getConversationById(UUID id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));
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
    public Page<Message> getAllMessages(Pageable pageable) {
        return messageRepository.findAll(pageable);
    }
    
    public Message getMessageById(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));
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
    public Page<Settlement> getAllSettlements(Pageable pageable) {
        return settlementRepository.findAll(pageable);
    }
    
    public Settlement getSettlementById(UUID id) {
        return settlementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Settlement", "id", id));
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
    public Page<Subscription> getAllSubscriptions(Pageable pageable) {
        return subscriptionRepository.findAll(pageable);
    }
    
    public Subscription getSubscriptionById(UUID id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));
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
    public Page<SupportTicket> getAllSupportTickets(Pageable pageable) {
        return supportTicketRepository.findAll(pageable);
    }
    
    public SupportTicket getSupportTicketById(UUID id) {
        return supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", id));
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
    public Page<GroupInvitation> getAllGroupInvitations(Pageable pageable) {
        return groupInvitationRepository.findAll(pageable);
    }
    
    public GroupInvitation getGroupInvitationById(UUID id) {
        return groupInvitationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GroupInvitation", "id", id));
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
    public Page<GroupUser> getAllGroupUsers(Pageable pageable) {
        return groupUserRepository.findAll(pageable);
    }
    
    public GroupUser getGroupUserById(UUID id) {
        return groupUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GroupUser", "id", id));
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
}
