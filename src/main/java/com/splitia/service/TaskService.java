package com.splitia.service;

import com.splitia.dto.request.CreateTaskRequest;
import com.splitia.dto.request.UpdateTaskRequest;
import com.splitia.dto.response.TaskResponse;
import com.splitia.dto.response.TaskTagResponse;
import com.splitia.exception.BadRequestException;
import com.splitia.exception.ForbiddenException;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.model.Group;
import com.splitia.model.GroupUser;
import com.splitia.model.Task;
import com.splitia.model.TaskTag;
import com.splitia.model.User;
import com.splitia.model.enums.GroupRole;
import com.splitia.model.enums.TaskStatus;
import com.splitia.repository.GroupRepository;
import com.splitia.repository.GroupUserRepository;
import com.splitia.repository.TaskRepository;
import com.splitia.repository.TaskTagRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final TaskTagRepository taskTagRepository;
    private final GroupRepository groupRepository;
    private final GroupUserRepository groupUserRepository;
    private final UserRepository userRepository;
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
        verifyGroupMembership(currentUser, group);
        
        planService.verifyKanbanAccess(currentUser);
        
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
        
        task = taskRepository.save(task);
        return toResponse(task);
    }
    
    @Transactional
    public TaskResponse updateTask(UUID taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findByIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        User currentUser = getCurrentUser();
        verifyGroupMembership(currentUser, task.getGroup());
        
        planService.verifyKanbanAccess(currentUser);
        
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
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
        
        return response;
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userRepository.findByIdAndDeletedAtIsNull(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));
    }
}

