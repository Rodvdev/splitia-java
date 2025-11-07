package com.splitia.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupInvitationRequest {
    private LocalDateTime expiresAt;
    
    private Integer maxUses;
    
    private Boolean isActive;
}

