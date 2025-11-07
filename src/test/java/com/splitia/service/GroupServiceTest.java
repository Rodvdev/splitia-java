package com.splitia.service;

import com.splitia.dto.request.CreateGroupRequest;
import com.splitia.mapper.GroupMapper;
import com.splitia.model.Group;
import com.splitia.model.User;
import com.splitia.model.enums.GroupRole;
import com.splitia.repository.ConversationRepository;
import com.splitia.repository.GroupRepository;
import com.splitia.repository.GroupUserRepository;
import com.splitia.repository.UserRepository;
import com.splitia.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {
    
    @Mock
    private GroupRepository groupRepository;
    
    @Mock
    private GroupUserRepository groupUserRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ConversationRepository conversationRepository;
    
    @Mock
    private GroupMapper groupMapper;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;
    
    @InjectMocks
    private GroupService groupService;
    
    private User currentUser;
    private CreateGroupRequest createGroupRequest;
    
    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(UUID.randomUUID());
        currentUser.setEmail("test@example.com");
        
        createGroupRequest = new CreateGroupRequest();
        createGroupRequest.setName("Test Group");
        createGroupRequest.setDescription("Test Description");
        
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        CustomUserDetails userDetails = new CustomUserDetails(currentUser);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.of(currentUser));
    }
    
    @Test
    void testCreateGroup_Success() {
        // Given
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> {
            Group group = invocation.getArgument(0);
            group.setId(UUID.randomUUID());
            return group;
        });
        
        // When & Then
        assertDoesNotThrow(() -> groupService.createGroup(createGroupRequest));
        verify(groupRepository, times(1)).save(any(Group.class));
        verify(groupUserRepository, times(1)).save(any());
        verify(conversationRepository, times(1)).save(any());
    }
}

