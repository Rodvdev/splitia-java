package com.splitia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupInvitationResponse {
    private UUID id;
    private String token;
    private LocalDateTime expiresAt;
    private Integer maxUses;
    private Integer currentUses;
    private Boolean isActive;
    private UUID groupId;
    private String groupName;
    private UUID createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

