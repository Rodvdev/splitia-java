package com.splitia.service;

import com.splitia.dto.request.CreateTaskTagRequest;
import com.splitia.dto.request.UpdateTaskTagRequest;
import com.splitia.dto.response.TaskTagResponse;
import com.splitia.exception.BadRequestException;
import com.splitia.exception.ForbiddenException;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.model.Group;
import com.splitia.model.GroupUser;
import com.splitia.model.TaskTag;
import com.splitia.model.User;
import com.splitia.model.enums.GroupRole;
import com.splitia.repository.GroupRepository;
import com.splitia.repository.GroupUserRepository;
import com.splitia.repository.TaskTagRepository;
import com.splitia.repository.UserRepository;
import com.splitia.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskTagService {
    
    private final TaskTagRepository taskTagRepository;
    private final GroupRepository groupRepository;
    private final GroupUserRepository groupUserRepository;
    private final UserRepository userRepository;
    private final PlanService planService;
    
    public List<TaskTagResponse> getTagsByGroup(UUID groupId) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        
        User currentUser = getCurrentUser();
        verifyGroupMembership(currentUser, group);
        
        planService.verifyKanbanAccess(currentUser);
        
        return taskTagRepository.findByGroupId(groupId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public TaskTagResponse getTagById(UUID tagId) {
        TaskTag tag = taskTagRepository.findByIdAndDeletedAtIsNull(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("TaskTag", "id", tagId));
        
        User currentUser = getCurrentUser();
        verifyGroupMembership(currentUser, tag.getGroup());
        
        planService.verifyKanbanAccess(currentUser);
        
        return toResponse(tag);
    }
    
    @Transactional
    public TaskTagResponse createTag(CreateTaskTagRequest request) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", request.getGroupId()));
        
        User currentUser = getCurrentUser();
        verifyGroupMembership(currentUser, group);
        
        planService.verifyKanbanAccess(currentUser);
        
        // Check if tag with same name already exists in group
        if (taskTagRepository.existsByGroupIdAndName(request.getGroupId(), request.getName())) {
            throw new BadRequestException("Tag with this name already exists in this group");
        }
        
        TaskTag tag = new TaskTag();
        tag.setName(request.getName());
        tag.setColor(request.getColor());
        tag.setGroup(group);
        
        tag = taskTagRepository.save(tag);
        return toResponse(tag);
    }
    
    @Transactional
    public TaskTagResponse updateTag(UUID tagId, UpdateTaskTagRequest request) {
        TaskTag tag = taskTagRepository.findByIdAndDeletedAtIsNull(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("TaskTag", "id", tagId));
        
        User currentUser = getCurrentUser();
        verifyGroupMembership(currentUser, tag.getGroup());
        
        planService.verifyKanbanAccess(currentUser);
        
        if (request.getName() != null && !request.getName().equals(tag.getName())) {
            // Check if new name already exists
            if (taskTagRepository.existsByGroupIdAndName(tag.getGroup().getId(), request.getName())) {
                throw new BadRequestException("Tag with this name already exists in this group");
            }
            tag.setName(request.getName());
        }
        if (request.getColor() != null) {
            tag.setColor(request.getColor());
        }
        
        tag = taskTagRepository.save(tag);
        return toResponse(tag);
    }
    
    @Transactional
    public void softDeleteTag(UUID tagId) {
        TaskTag tag = taskTagRepository.findByIdAndDeletedAtIsNull(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("TaskTag", "id", tagId));
        
        User currentUser = getCurrentUser();
        verifyGroupMembership(currentUser, tag.getGroup());
        
        planService.verifyKanbanAccess(currentUser);
        
        // Only group admin can delete tags
        if (!isGroupAdmin(currentUser, tag.getGroup())) {
            throw new ForbiddenException("Only group admins can delete tags");
        }
        
        tag.setDeletedAt(LocalDateTime.now());
        taskTagRepository.save(tag);
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
    
    private TaskTagResponse toResponse(TaskTag tag) {
        TaskTagResponse response = new TaskTagResponse();
        response.setId(tag.getId());
        response.setName(tag.getName());
        response.setColor(tag.getColor());
        response.setGroupId(tag.getGroup().getId());
        response.setCreatedAt(tag.getCreatedAt());
        return response;
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userRepository.findByIdAndDeletedAtIsNull(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));
    }
}

