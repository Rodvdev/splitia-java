package com.splitia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    private UUID id;
    private Boolean isGroupChat;
    private String name;
    private List<UserResponse> participants;
    private LocalDateTime createdAt;
}

