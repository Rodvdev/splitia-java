package com.splitia.controller;

import com.splitia.dto.request.CreateConversationRequest;
import com.splitia.dto.request.SendMessageRequest;
import com.splitia.dto.request.UpdateConversationRequest;
import com.splitia.dto.request.UpdateMessageRequest;
import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.ConversationResponse;
import com.splitia.dto.response.MessageResponse;
import com.splitia.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Chat and messaging endpoints")
public class ChatController {
    
    private final ChatService chatService;
    
    @GetMapping
    @Operation(summary = "Get user conversations (paginated)")
    public ResponseEntity<ApiResponse<Page<ConversationResponse>>> getConversations(Pageable pageable) {
        Page<ConversationResponse> conversations = chatService.getConversations(pageable);
        return ResponseEntity.ok(ApiResponse.success(conversations));
    }
    
    @PostMapping
    @Operation(summary = "Create a new conversation")
    public ResponseEntity<ApiResponse<ConversationResponse>> createConversation(@Valid @RequestBody CreateConversationRequest request) {
        ConversationResponse conversation = chatService.createConversation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(conversation, "Conversation created successfully"));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get conversation by ID")
    public ResponseEntity<ApiResponse<ConversationResponse>> getConversationById(@PathVariable UUID id) {
        ConversationResponse conversation = chatService.getConversationById(id);
        return ResponseEntity.ok(ApiResponse.success(conversation));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update conversation")
    public ResponseEntity<ApiResponse<ConversationResponse>> updateConversation(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateConversationRequest request) {
        ConversationResponse conversation = chatService.updateConversation(id, request);
        return ResponseEntity.ok(ApiResponse.success(conversation, "Conversation updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete conversation (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteConversation(@PathVariable UUID id) {
        chatService.softDeleteConversation(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Conversation deleted successfully"));
    }
    
    @PostMapping("/{conversationId}/messages")
    @Operation(summary = "Send a message")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @PathVariable UUID conversationId,
            @Valid @RequestBody SendMessageRequest request) {
        request.setConversationId(conversationId);
        MessageResponse message = chatService.sendMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(message, "Message sent successfully"));
    }
    
    @GetMapping("/{conversationId}/messages")
    @Operation(summary = "Get messages from conversation")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getMessages(
            @PathVariable UUID conversationId,
            Pageable pageable) {
        Page<MessageResponse> messages = chatService.getMessages(conversationId, pageable);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
    
    @PutMapping("/messages/{id}")
    @Operation(summary = "Update message")
    public ResponseEntity<ApiResponse<MessageResponse>> updateMessage(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMessageRequest request) {
        MessageResponse message = chatService.updateMessage(id, request);
        return ResponseEntity.ok(ApiResponse.success(message, "Message updated successfully"));
    }
    
    @DeleteMapping("/messages/{id}")
    @Operation(summary = "Delete message (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(@PathVariable UUID id) {
        chatService.softDeleteMessage(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Message deleted successfully"));
    }
}

