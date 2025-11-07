package com.splitia.controller;

import com.splitia.dto.request.SendMessageRequest;
import com.splitia.dto.response.ApiResponse;
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
}

