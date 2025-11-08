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
public class GroupResponse {
    private UUID id;
    private String name;
    private String description;
    private String image;
    private UUID conversationId;  // ID de la conversación del grupo
    private UserResponse createdBy;
    private List<GroupMemberResponse> members;
    private LocalDateTime createdAt;
}

