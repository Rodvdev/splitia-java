package com.splitia.service;

import com.splitia.dto.request.SendMessageRequest;
import com.splitia.dto.response.MessageResponse;
import com.splitia.mapper.MessageMapper;
import com.splitia.model.Conversation;
import com.splitia.model.Message;
import com.splitia.model.User;
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

@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    
    @Transactional
    public MessageResponse sendMessage(SendMessageRequest request) {
        User currentUser = getCurrentUser();
        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
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
        return messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable)
                .map(messageMapper::toResponse);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        return userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

