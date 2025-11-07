package com.splitia.dto.response;

import com.splitia.model.enums.GroupRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberResponse {
    private UUID id;
    private UserResponse user;
    private GroupRole role;
}

