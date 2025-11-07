package com.splitia.dto.response;

import com.splitia.model.enums.GroupRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupUserResponse {
    private UUID id;
    private GroupRole role;
    private UUID userId;
    private String userName;
    private UUID groupId;
    private String groupName;
    private Map<String, Boolean> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

