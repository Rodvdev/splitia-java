package com.splitia.service;

import com.splitia.dto.request.CreateExpenseRequest;
import com.splitia.dto.request.CreateTaskRequest;
import com.splitia.dto.request.ExpenseShareRequest;
import com.splitia.dto.request.UpdateTaskRequest;
import com.splitia.dto.response.FutureExpenseShareResponse;
import com.splitia.dto.response.TaskResponse;
import com.splitia.dto.response.TaskTagResponse;
import com.splitia.exception.BadRequestException;
import com.splitia.exception.ForbiddenException;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.model.Expense;
import com.splitia.model.Group;
import com.splitia.model.GroupUser;
import com.splitia.model.Task;
import com.splitia.model.TaskTag;
import com.splitia.model.User;
import com.splitia.model.enums.GroupRole;
import com.splitia.model.enums.TaskStatus;
import com.splitia.repository.ExpenseRepository;
import com.splitia.repository.GroupRepository;
import com.splitia.repository.GroupUserRepository;
import com.splitia.repository.TaskRepository;
import com.splitia.repository.TaskTagRepository;
import com.splitia.repository.UserRepository;
import com.splitia.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final TaskTagRepository taskTagRepository;
    private final GroupRepository groupRepository;
    private final GroupUserRepository groupUserRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseService expenseService;
    private final PlanService planService;
    
    public Page<TaskResponse> getTasksByGroup(UUID groupId, Pageable pageable) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        
        User currentUser = getCurrentUser();
        verifyGroupMembership(currentUser, group);
        
        // Check if plan has Kanban feature
        planService.verifyKanbanAccess(currentUser);
        
        return taskRepository.findByGroupId(groupId, pageable)
                .map(this::toResponse);
    }
    
    public List<TaskResponse> getTasksByGroupAndStatus(UUID groupId, TaskStatus status) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        
        User currentUser = getCurrentUser();
        log.info("[KANBAN] GET tasks by group/status: userId={}, email={}, groupId={}, status={}",
                currentUser.getId(), currentUser.getEmail(), groupId, status);
        verifyGroupMembership(currentUser, group);
        log.debug("[KANBAN] Group membership verified for userId={} in groupId={}", currentUser.getId(), groupId);
        
        planService.verifyKanbanAccess(currentUser);
        log.debug("[KANBAN] Plan access verified for userId={}", currentUser.getId());
        
        return taskRepository.findByGroupIdAndStatus(groupId, status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public TaskResponse getTaskById(UUID taskId) {
        Task task = taskRepository.findByIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        User currentUser = getCurrentUser();
        verifyGroupMembership(currentUser, task.getGroup());
        
        planService.verifyKanbanAccess(currentUser);
        
        return toResponse(task);
    }
    
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", request.getGroupId()));
        
        User currentUser = getCurrentUser();
        verifyGroupMembership(currentUser, group);
        
        planService.verifyKanbanAccess(currentUser);
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setGroup(group);
        task.setCreatedBy(currentUser);
        task.setStatus(TaskStatus.TODO);
        task.setPriority(request.getPriority() != null ? request.getPriority() : "MEDIUM");
        task.setStartDate(request.getStartDate());
        task.setDueDate(request.getDueDate());
        
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findByIdAndDeletedAtIsNull(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedToId()));
            // Verify assigned user is a group member
            if (!groupUserRepository.existsByUserIdAndGroupId(request.getAssignedToId(), request.getGroupId())) {
                throw new BadRequestException("Assigned user must be a member of the group");
            }
            task.setAssignedTo(assignedTo);
        }
        
        // Set position (last in TODO column)
        Integer maxPosition = taskRepository.findMaxPositionByGroupIdAndStatus(request.getGroupId(), TaskStatus.TODO);
        task.setPosition(maxPosition != null ? maxPosition + 1 : 0);
        
        // Add tags
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<TaskTag> tags = new ArrayList<>();
            for (UUID tagId : request.getTagIds()) {
                TaskTag tag = taskTagRepository.findByIdAndDeletedAtIsNull(tagId)
                        .orElseThrow(() -> new ResourceNotFoundException("TaskTag", "id", tagId));
                if (!tag.getGroup().getId().equals(request.getGroupId())) {
                    throw new BadRequestException("Tag does not belong to this group");
                }
                tags.add(tag);
            }
            task.setTags(tags);
        }
        
        // Handle expense association
        handleExpenseAssociation(task, request.getExpenseId(), request.getCreateFutureExpense(), 
                request.getFutureExpenseAmount(), request.getFutureExpenseCurrency(), 
                request.getFutureExpensePaidById(), request.getFutureExpenseShares(), group);
        
        task = taskRepository.save(task);
        return toResponse(task);
    }
    
    @Transactional
    public TaskResponse updateTask(UUID taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findByIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        User currentUser = getCurrentUser();
        log.info("[KANBAN] PUT update task: userId={}, email={}, taskId={}, groupId={}, requestedStatus={}, currentStatus={}",
                currentUser.getId(), currentUser.getEmail(), taskId, task.getGroup().getId(),
                request.getStatus(), task.getStatus());
        verifyGroupMembership(currentUser, task.getGroup());
        log.debug("[KANBAN] Group membership verified for userId={} in groupId={}", currentUser.getId(), task.getGroup().getId());
        
        planService.verifyKanbanAccess(currentUser);
        log.debug("[KANBAN] Plan access verified for userId={}", currentUser.getId());
        
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        TaskStatus previousStatus = task.getStatus();
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
            
            // Convert future expense to real expense when task is marked as DONE
            if (request.getStatus() == TaskStatus.DONE && previousStatus != TaskStatus.DONE) {
                convertFutureExpenseToExpense(task);
            }
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getStartDate() != null) {
            task.setStartDate(request.getStartDate());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findByIdAndDeletedAtIsNull(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedToId()));
            if (!groupUserRepository.existsByUserIdAndGroupId(request.getAssignedToId(), task.getGroup().getId())) {
                throw new BadRequestException("Assigned user must be a member of the group");
            }
            task.setAssignedTo(assignedTo);
        }
        if (request.getPosition() != null) {
            task.setPosition(request.getPosition());
        }
        
        // Update tags
        if (request.getTagIds() != null) {
            List<TaskTag> tags = new ArrayList<>();
            for (UUID tagId : request.getTagIds()) {
                TaskTag tag = taskTagRepository.findByIdAndDeletedAtIsNull(tagId)
                        .orElseThrow(() -> new ResourceNotFoundException("TaskTag", "id", tagId));
                if (!tag.getGroup().getId().equals(task.getGroup().getId())) {
                    throw new BadRequestException("Tag does not belong to this group");
                }
                tags.add(tag);
            }
            task.setTags(tags);
        }
        
        // Handle expense association updates
        if (request.getExpenseId() != null || request.getFutureExpenseAmount() != null || 
            request.getFutureExpensePaidById() != null || request.getFutureExpenseShares() != null) {
            handleExpenseAssociation(task, request.getExpenseId(), null,
                    request.getFutureExpenseAmount(), request.getFutureExpenseCurrency(),
                    request.getFutureExpensePaidById(), request.getFutureExpenseShares(), task.getGroup());
        }
        
        task = taskRepository.save(task);
        return toResponse(task);
    }
    
    @Transactional
    public void softDeleteTask(UUID taskId) {
        Task task = taskRepository.findByIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        User currentUser = getCurrentUser();
        verifyGroupMembership(currentUser, task.getGroup());
        
        planService.verifyKanbanAccess(currentUser);
        
        // Only creator or group admin can delete
        if (!task.getCreatedBy().getId().equals(currentUser.getId()) && 
            !isGroupAdmin(currentUser, task.getGroup())) {
            throw new ForbiddenException("Only the creator or group admin can delete this task");
        }
        
        task.setDeletedAt(LocalDateTime.now());
        taskRepository.save(task);
    }
    
    private void verifyGroupMembership(User user, Group group) {
        if (!groupUserRepository.existsByUserIdAndGroupId(user.getId(), group.getId())) {
            throw new ForbiddenException("You are not a member of this group");
        }
    }
    
    private boolean isGroupAdmin(User user, Group group) {
        if (user.getRole() != null && user.getRole().equals("ADMIN")) {
            return true;
        }
        GroupUser groupUser = groupUserRepository.findByUserIdAndGroupId(user.getId(), group.getId())
                .orElse(null);
        return groupUser != null && groupUser.getRole() == GroupRole.ADMIN;
    }
    
    private TaskResponse toResponse(Task task) {
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
        response.setCreatedByName(task.getCreatedBy().getName() + " " + task.getCreatedBy().getLastName());
        response.setPosition(task.getPosition());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        
        if (task.getAssignedTo() != null) {
            response.setAssignedToId(task.getAssignedTo().getId());
            response.setAssignedToName(task.getAssignedTo().getName() + " " + task.getAssignedTo().getLastName());
        }
        
        if (task.getTags() != null) {
            response.setTags(task.getTags().stream()
                    .map(tag -> {
                        TaskTagResponse tagResponse = new TaskTagResponse();
                        tagResponse.setId(tag.getId());
                        tagResponse.setName(tag.getName());
                        tagResponse.setColor(tag.getColor());
                        tagResponse.setGroupId(tag.getGroup().getId());
                        tagResponse.setCreatedAt(tag.getCreatedAt());
                        return tagResponse;
                    })
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
                    task.getFutureExpensePaidBy().getLastName());
        }
        
        if (task.getFutureExpenseShares() != null && !task.getFutureExpenseShares().isEmpty()) {
            response.setFutureExpenseShares(convertFutureExpenseSharesToResponse(task.getFutureExpenseShares()));
        }
        
        return response;
    }
    
    private void handleExpenseAssociation(Task task, UUID expenseId, Boolean createFutureExpense,
                                         BigDecimal futureExpenseAmount, String futureExpenseCurrency,
                                         UUID futureExpensePaidById, List<ExpenseShareRequest> futureExpenseShares,
                                         Group group) {
        // If expenseId is provided, associate existing expense
        if (expenseId != null) {
            Expense expense = expenseRepository.findByIdAndDeletedAtIsNull(expenseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
            
            // Validate expense belongs to the same group
            if (expense.getGroup() == null || !expense.getGroup().getId().equals(group.getId())) {
                throw new BadRequestException("Expense must belong to the same group as the task");
            }
            
            task.setExpense(expense);
            // Clear future expense fields when associating existing expense
            task.setFutureExpenseAmount(null);
            task.setFutureExpenseCurrency(null);
            task.setFutureExpensePaidBy(null);
            task.setFutureExpenseShares(null);
            return;
        }
        
        // If createFutureExpense is true, create expense immediately
        if (Boolean.TRUE.equals(createFutureExpense) && futureExpenseAmount != null && 
            futureExpensePaidById != null && futureExpenseShares != null && !futureExpenseShares.isEmpty()) {
            
            // Validate shares
            validateFutureExpenseShares(futureExpenseShares, futureExpenseAmount, group);
            
            CreateExpenseRequest expenseRequest = new CreateExpenseRequest();
            expenseRequest.setAmount(futureExpenseAmount);
            expenseRequest.setDescription("Gasto asociado a tarea: " + task.getTitle());
            expenseRequest.setDate(LocalDateTime.now());
            expenseRequest.setCurrency(futureExpenseCurrency != null ? futureExpenseCurrency : "USD");
            expenseRequest.setGroupId(group.getId());
            expenseRequest.setPaidById(futureExpensePaidById);
            expenseRequest.setShares(futureExpenseShares);
            
            Expense createdExpense = expenseRepository.findByIdAndDeletedAtIsNull(
                    expenseService.createExpense(expenseRequest).getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseRequest.getPaidById()));
            
            task.setExpense(createdExpense);
            // Clear future expense fields
            task.setFutureExpenseAmount(null);
            task.setFutureExpenseCurrency(null);
            task.setFutureExpensePaidBy(null);
            task.setFutureExpenseShares(null);
            return;
        }
        
        // Otherwise, store future expense info
        if (futureExpenseAmount != null || futureExpensePaidById != null || 
            (futureExpenseShares != null && !futureExpenseShares.isEmpty())) {
            
            if (futureExpenseAmount == null) {
                throw new BadRequestException("Future expense amount is required");
            }
            if (futureExpensePaidById == null) {
                throw new BadRequestException("Future expense paid by user ID is required");
            }
            if (futureExpenseShares == null || futureExpenseShares.isEmpty()) {
                throw new BadRequestException("Future expense shares are required");
            }
            
            // Validate shares
            validateFutureExpenseShares(futureExpenseShares, futureExpenseAmount, group);
            
            User paidBy = userRepository.findByIdAndDeletedAtIsNull(futureExpensePaidById)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", futureExpensePaidById));
            
            task.setFutureExpenseAmount(futureExpenseAmount);
            task.setFutureExpenseCurrency(futureExpenseCurrency != null ? futureExpenseCurrency : "USD");
            task.setFutureExpensePaidBy(paidBy);
            task.setFutureExpenseShares(convertExpenseSharesToMapList(futureExpenseShares));
        }
    }
    
    private void validateFutureExpenseShares(List<ExpenseShareRequest> shares, BigDecimal totalAmount, Group group) {
        BigDecimal totalShares = shares.stream()
                .map(ExpenseShareRequest::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalShares.compareTo(totalAmount) != 0) {
            throw new BadRequestException("Sum of shares must equal the future expense amount");
        }
        
        // Validate all participants are group members
        for (ExpenseShareRequest share : shares) {
            if (!groupUserRepository.existsByUserIdAndGroupId(share.getUserId(), group.getId())) {
                throw new BadRequestException("All future expense participants must be members of the group");
            }
        }
    }
    
    private List<Map<String, Object>> convertExpenseSharesToMapList(List<ExpenseShareRequest> shares) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (ExpenseShareRequest share : shares) {
            Map<String, Object> shareMap = new HashMap<>();
            shareMap.put("userId", share.getUserId().toString());
            shareMap.put("amount", share.getAmount());
            shareMap.put("type", share.getType() != null ? share.getType().name() : "EQUAL");
            result.add(shareMap);
        }
        return result;
    }
    
    private List<FutureExpenseShareResponse> convertFutureExpenseSharesToResponse(List<Map<String, Object>> shares) {
        List<FutureExpenseShareResponse> result = new ArrayList<>();
        for (Map<String, Object> shareMap : shares) {
            FutureExpenseShareResponse shareResponse = new FutureExpenseShareResponse();
            UUID userId = UUID.fromString(shareMap.get("userId").toString());
            shareResponse.setUserId(userId);
            
            User user = userRepository.findByIdAndDeletedAtIsNull(userId).orElse(null);
            if (user != null) {
                shareResponse.setUserName(user.getName() + " " + user.getLastName());
            }
            
            if (shareMap.get("amount") instanceof Number) {
                shareResponse.setAmount(BigDecimal.valueOf(((Number) shareMap.get("amount")).doubleValue()));
            } else if (shareMap.get("amount") instanceof String) {
                shareResponse.setAmount(new BigDecimal((String) shareMap.get("amount")));
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
    
    @Transactional
    private void convertFutureExpenseToExpense(Task task) {
        // Only convert if there's future expense info and no expense already associated
        if (task.getExpense() != null || task.getFutureExpenseAmount() == null || 
            task.getFutureExpensePaidBy() == null || task.getFutureExpenseShares() == null || 
            task.getFutureExpenseShares().isEmpty()) {
            return;
        }
        
        // Convert future expense shares to ExpenseShareRequest list
        List<ExpenseShareRequest> shares = new ArrayList<>();
        for (Map<String, Object> shareMap : task.getFutureExpenseShares()) {
            ExpenseShareRequest shareRequest = new ExpenseShareRequest();
            shareRequest.setUserId(UUID.fromString(shareMap.get("userId").toString()));
            
            if (shareMap.get("amount") instanceof Number) {
                shareRequest.setAmount(BigDecimal.valueOf(((Number) shareMap.get("amount")).doubleValue()));
            } else if (shareMap.get("amount") instanceof String) {
                shareRequest.setAmount(new BigDecimal((String) shareMap.get("amount")));
            }
            
            if (shareMap.get("type") != null) {
                try {
                    shareRequest.setType(com.splitia.model.enums.ShareType.valueOf(shareMap.get("type").toString()));
                } catch (IllegalArgumentException e) {
                    shareRequest.setType(com.splitia.model.enums.ShareType.EQUAL);
                }
            }
            
            shares.add(shareRequest);
        }
        
        CreateExpenseRequest expenseRequest = new CreateExpenseRequest();
        expenseRequest.setAmount(task.getFutureExpenseAmount());
        expenseRequest.setDescription("Gasto completado de tarea: " + task.getTitle());
        expenseRequest.setDate(LocalDateTime.now());
        expenseRequest.setCurrency(task.getFutureExpenseCurrency() != null ? task.getFutureExpenseCurrency() : "USD");
        expenseRequest.setGroupId(task.getGroup().getId());
        expenseRequest.setPaidById(task.getFutureExpensePaidBy().getId());
        expenseRequest.setShares(shares);
        
        Expense createdExpense = expenseRepository.findByIdAndDeletedAtIsNull(
                expenseService.createExpense(expenseRequest).getId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseRequest.getPaidById()));
        
        // Associate expense to task and clear future expense fields
        task.setExpense(createdExpense);
        task.setFutureExpenseAmount(null);
        task.setFutureExpenseCurrency(null);
        task.setFutureExpensePaidBy(null);
        task.setFutureExpenseShares(null);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userRepository.findByIdAndDeletedAtIsNull(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));
    }
}

