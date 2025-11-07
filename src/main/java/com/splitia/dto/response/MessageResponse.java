package com.splitia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private UUID id;
    private String content;
    private Boolean isAI;
    private UserResponse sender;
    private LocalDateTime createdAt;
}

