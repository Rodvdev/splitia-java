package com.splitia.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupInvitationRequest {
    // groupId is optional when creating an invitation via the group-scoped endpoint
    private UUID groupId;
    
    private LocalDateTime expiresAt;
    
    private Integer maxUses;
    
    private Boolean isActive = true;
}

