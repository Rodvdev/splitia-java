package com.splitia.dto.request;

import com.splitia.model.enums.GroupRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupUserRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Group ID is required")
    private UUID groupId;
    
    private GroupRole role = GroupRole.MEMBER;
    
    private Map<String, Boolean> permissions;
}
