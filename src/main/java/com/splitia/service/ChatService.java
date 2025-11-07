package com.splitia.service;

import com.splitia.dto.request.CreateConversationRequest;
import com.splitia.dto.request.SendMessageRequest;
import com.splitia.dto.request.UpdateConversationRequest;
import com.splitia.dto.request.UpdateMessageRequest;
import com.splitia.dto.response.ConversationResponse;
import com.splitia.dto.response.MessageResponse;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.ConversationMapper;
import com.splitia.mapper.MessageMapper;
import com.splitia.model.Conversation;
import com.splitia.model.ConversationParticipant;
import com.splitia.model.Message;
import com.splitia.model.User;
import com.splitia.repository.ConversationParticipantRepository;
import com.splitia.repository.ConversationRepository;
import com.splitia.repository.MessageRepository;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository conversationParticipantRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final ConversationMapper conversationMapper;
    
    public Page<ConversationResponse> getConversations(Pageable pageable) {
        User currentUser = getCurrentUser();
        return conversationRepository.findByUserId(currentUser.getId(), pageable)
                .map(conversationMapper::toResponse);
    }
    
    @Transactional
    public ConversationResponse createConversation(CreateConversationRequest request) {
        User currentUser = getCurrentUser();
        
        Conversation conversation = new Conversation();
        conversation.setName(request.getName());
        conversation.setIsGroupChat(request.getParticipantIds().size() > 1);
        
        conversation = conversationRepository.save(conversation);
        
        // Add current user as participant
        ConversationParticipant currentUserParticipant = new ConversationParticipant();
        currentUserParticipant.setConversation(conversation);
        currentUserParticipant.setUser(currentUser);
        conversationParticipantRepository.save(currentUserParticipant);
        
        // Add other participants
        for (UUID participantId : request.getParticipantIds()) {
            if (!participantId.equals(currentUser.getId())) {
                User participant = userRepository.findByIdAndDeletedAtIsNull(participantId)
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", participantId));
                
                ConversationParticipant cp = new ConversationParticipant();
                cp.setConversation(conversation);
                cp.setUser(participant);
                conversationParticipantRepository.save(cp);
            }
        }
        
        return conversationMapper.toResponse(conversation);
    }
    
    public ConversationResponse getConversationById(UUID conversationId) {
        User currentUser = getCurrentUser();
        Conversation conversation = conversationRepository.findByIdAndDeletedAtIsNull(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
        
        // Verify user is a participant
        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(currentUser.getId()) && p.getDeletedAt() == null);
        
        if (!isParticipant) {
            throw new ResourceNotFoundException("Conversation", "id", conversationId);
        }
        
        return conversationMapper.toResponse(conversation);
    }
    
    @Transactional
    public ConversationResponse updateConversation(UUID conversationId, UpdateConversationRequest request) {
        User currentUser = getCurrentUser();
        Conversation conversation = conversationRepository.findByIdAndDeletedAtIsNull(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
        
        // Verify user is a participant
        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(currentUser.getId()) && p.getDeletedAt() == null);
        
        if (!isParticipant) {
            throw new ResourceNotFoundException("Conversation", "id", conversationId);
        }
        
        if (request.getName() != null) {
            conversation.setName(request.getName());
        }
        
        conversation = conversationRepository.save(conversation);
        return conversationMapper.toResponse(conversation);
    }
    
    @Transactional
    public void softDeleteConversation(UUID conversationId) {
        User currentUser = getCurrentUser();
        Conversation conversation = conversationRepository.findByIdAndDeletedAtIsNull(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
        
        // Verify user is a participant
        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(currentUser.getId()) && p.getDeletedAt() == null);
        
        if (!isParticipant) {
            throw new ResourceNotFoundException("Conversation", "id", conversationId);
        }
        
        conversation.setDeletedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
    }
    
    @Transactional
    public MessageResponse sendMessage(SendMessageRequest request) {
        User currentUser = getCurrentUser();
        Conversation conversation = conversationRepository.findByIdAndDeletedAtIsNull(request.getConversationId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", request.getConversationId()));
        
        // Verify user is a participant
        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(currentUser.getId()) && p.getDeletedAt() == null);
        
        if (!isParticipant) {
            throw new ResourceNotFoundException("Conversation", "id", request.getConversationId());
        }
        
        Message message = new Message();
        message.setContent(request.getContent());
        message.setConversation(conversation);
        message.setSender(currentUser);
        message.setCreatedAt(LocalDateTime.now());
        message.setIsAI(false);
        
        message = messageRepository.save(message);
        return messageMapper.toResponse(message);
    }
    
    public Page<MessageResponse> getMessages(UUID conversationId, Pageable pageable) {
        User currentUser = getCurrentUser();
        Conversation conversation = conversationRepository.findByIdAndDeletedAtIsNull(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
        
        // Verify user is a participant
        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(currentUser.getId()) && p.getDeletedAt() == null);
        
        if (!isParticipant) {
            throw new ResourceNotFoundException("Conversation", "id", conversationId);
        }
        
        return messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable)
                .map(messageMapper::toResponse);
    }
    
    @Transactional
    public MessageResponse updateMessage(UUID messageId, UpdateMessageRequest request) {
        User currentUser = getCurrentUser();
        Message message = messageRepository.findByIdAndDeletedAtIsNull(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));
        
        // Verify ownership
        if (!message.getSender().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Message", "id", messageId);
        }
        
        message.setContent(request.getContent());
        message = messageRepository.save(message);
        return messageMapper.toResponse(message);
    }
    
    @Transactional
    public void softDeleteMessage(UUID messageId) {
        User currentUser = getCurrentUser();
        Message message = messageRepository.findByIdAndDeletedAtIsNull(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));
        
        // Verify ownership
        if (!message.getSender().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Message", "id", messageId);
        }
        
        message.setDeletedAt(LocalDateTime.now());
        messageRepository.save(message);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        return userRepository.findByIdAndDeletedAtIsNull(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));
    }
}

