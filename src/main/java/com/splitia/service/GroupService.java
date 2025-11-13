package com.splitia.service;

import com.splitia.dto.request.AssignPermissionsRequest;
import com.splitia.dto.request.CreateGroupRequest;
import com.splitia.dto.request.UpdateGroupMemberRequest;
import com.splitia.dto.request.UpdateGroupRequest;
import com.splitia.dto.response.GroupResponse;
import com.splitia.exception.BadRequestException;
import com.splitia.exception.ForbiddenException;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.GroupMapper;
import com.splitia.mapper.GroupInvitationMapper;
import com.splitia.dto.request.CreateGroupInvitationRequest;
import com.splitia.dto.response.GroupInvitationResponse;
import com.splitia.model.GroupInvitation;
import com.splitia.repository.GroupInvitationRepository;
import com.splitia.mapper.UserMapper;
import com.splitia.model.Conversation;
import com.splitia.model.Group;
import com.splitia.model.GroupUser;
import com.splitia.model.User;
import com.splitia.model.enums.GroupRole;
import com.splitia.repository.ConversationRepository;
import com.splitia.repository.GroupRepository;
import com.splitia.repository.GroupUserRepository;
import com.splitia.repository.UserRepository;
import com.splitia.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {
    
    private final GroupRepository groupRepository;
    private final GroupInvitationRepository groupInvitationRepository;
    private final GroupUserRepository groupUserRepository;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final GroupMapper groupMapper;
    private final GroupInvitationMapper groupInvitationMapper;
    
    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request) {
        User currentUser = getCurrentUser();
        
        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setImage(request.getImage());
        group.setCreatedBy(currentUser);
        
        group = groupRepository.save(group);
        
        // Crear GroupUser para el creador como ADMIN
        GroupUser groupUser = new GroupUser();
        groupUser.setUser(currentUser);
        groupUser.setGroup(group);
        groupUser.setRole(GroupRole.ADMIN);
        groupUserRepository.save(groupUser);
        
        // Crear conversación para el grupo
        Conversation conversation = new Conversation();
        conversation.setGroup(group);
        conversation.setIsGroupChat(true);
        conversation.setName(group.getName());
        conversationRepository.save(conversation);
        
        return groupMapper.toResponse(group);
    }

    @Transactional
    public GroupInvitationResponse createGroupInvitation(UUID groupId, CreateGroupInvitationRequest request) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));

        User currentUser = getCurrentUser();

        // Only group admins or system admins can create invitations
        if (!isGroupAdminOrSystemAdmin(currentUser, group)) {
            throw new ForbiddenException("Only group admins can create invitations");
        }

        GroupInvitation invitation = new GroupInvitation();
        invitation.setToken(java.util.UUID.randomUUID().toString());
        invitation.setExpiresAt(request.getExpiresAt());
        invitation.setMaxUses(request.getMaxUses());
        invitation.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        invitation.setGroup(group);
        invitation.setCreatedBy(currentUser);

        invitation = groupInvitationRepository.save(invitation);
        return groupInvitationMapper.toResponse(invitation);
    }
    
    public List<GroupResponse> getUserGroups() {
        User currentUser = getCurrentUser();
        List<Group> groups = groupRepository.findByUserId(currentUser.getId());
        return groupMapper.toResponseList(groups);
    }
    
    public GroupResponse getGroupById(UUID groupId) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        
        // Verificar que el usuario es miembro
        User currentUser = getCurrentUser();
        if (!groupUserRepository.existsByUserIdAndGroupId(currentUser.getId(), groupId)) {
            throw new ForbiddenException("You are not a member of this group");
        }
        
        return groupMapper.toResponse(group);
    }

    @Transactional(readOnly = true)
    public java.util.List<GroupInvitationResponse> getMyInvitations(com.splitia.model.enums.InvitationStatus status) {
        User currentUser = getCurrentUser();
        java.util.List<GroupInvitation> invitations = groupInvitationRepository.findForUser(
                currentUser.getId(), currentUser.getEmail(), status != null ? status : com.splitia.model.enums.InvitationStatus.PENDING
        );
        return groupInvitationMapper.toResponseList(invitations);
    }
    
    @Transactional
    public GroupResponse updateGroup(UUID groupId, UpdateGroupRequest request) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        
        User currentUser = getCurrentUser();
        
        // Verificar que el usuario es ADMIN
        GroupUser groupUser = groupUserRepository.findByUserIdAndGroupId(currentUser.getId(), groupId)
                .orElseThrow(() -> new ForbiddenException("You are not a member of this group"));
        
        if (groupUser.getRole() != GroupRole.ADMIN) {
            throw new ForbiddenException("Only admins can update the group");
        }
        
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
    public void softDeleteGroup(UUID groupId) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        
        User currentUser = getCurrentUser();
        
        if (!group.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Only the creator can delete the group");
        }
        
        group.setDeletedAt(LocalDateTime.now());
        groupRepository.save(group);
    }
    
    @Transactional
    public void addMember(UUID groupId, UUID userId) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        
        User currentUser = getCurrentUser();
        
        // Only group admins or system admins can add members
        if (!isGroupAdminOrSystemAdmin(currentUser, group)) {
            throw new ForbiddenException("Only admins can add members");
        }
        
        if (groupUserRepository.existsByUserIdAndGroupId(userId, groupId)) {
            throw new BadRequestException("User is already a member of this group");
        }
        
        User userToAdd = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        GroupUser groupUser = new GroupUser();
        groupUser.setUser(userToAdd);
        groupUser.setGroup(group);
        groupUser.setRole(GroupRole.MEMBER);
        groupUserRepository.save(groupUser);
    }
    
    @Transactional
    public void removeMember(UUID groupId, UUID userId) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        
        User currentUser = getCurrentUser();
        
        // Only group admins or system admins can remove members
        if (!isGroupAdminOrSystemAdmin(currentUser, group)) {
            throw new ForbiddenException("Only admins can remove members");
        }
        
        if (group.getCreatedBy().getId().equals(userId)) {
            throw new BadRequestException("Cannot remove the group creator");
        }
        
        GroupUser groupUser = groupUserRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("GroupUser", "id", userId));
        
        groupUser.setDeletedAt(LocalDateTime.now());
        groupUserRepository.save(groupUser);
    }
    
    @Transactional
    public void updateMemberRole(UUID groupId, UUID userId, UpdateGroupMemberRequest request) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        
        User currentUser = getCurrentUser();
        
        // Only group admins or system admins can update member roles
        if (!isGroupAdminOrSystemAdmin(currentUser, group)) {
            throw new ForbiddenException("Only admins can update member roles");
        }
        
        GroupUser groupUser = groupUserRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("GroupUser", "id", userId));
        
        if (request.getRole() != null) {
            groupUser.setRole(request.getRole());
        }
        if (request.getPermissions() != null) {
            groupUser.setPermissions(request.getPermissions());
        }
        
        groupUserRepository.save(groupUser);
    }
    
    @Transactional
    public void assignPermissions(UUID groupId, UUID userId, AssignPermissionsRequest request) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        
        User currentUser = getCurrentUser();
        
        // Only group admins or system admins can assign permissions
        if (!isGroupAdminOrSystemAdmin(currentUser, group)) {
            throw new ForbiddenException("Only admins can assign permissions");
        }
        
        GroupUser groupUser = groupUserRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("GroupUser", "id", userId));
        
        Map<String, Boolean> existingPermissions = groupUser.getPermissions();
        if (existingPermissions == null) {
            existingPermissions = new java.util.HashMap<>();
        }
        
        // Merge new permissions with existing ones
        if (request.getPermissions() != null) {
            existingPermissions.putAll(request.getPermissions());
        }
        
        groupUser.setPermissions(existingPermissions);
        groupUserRepository.save(groupUser);
    }
    
    private boolean isGroupAdminOrSystemAdmin(User user, Group group) {
        // System admins can do anything
        if (user.getRole() != null && user.getRole().equals("ADMIN")) {
            return true;
        }
        
        // Check if user is group admin
        GroupUser groupUser = groupUserRepository.findByUserIdAndGroupId(user.getId(), group.getId())
                .orElse(null);
        
        return groupUser != null && groupUser.getRole() == GroupRole.ADMIN;
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        return userRepository.findByIdAndDeletedAtIsNull(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));
    }
}

