package com.splitia.dto.request;

import com.splitia.model.enums.GroupRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupMemberRequest {
    @NotNull(message = "Role is required")
    private GroupRole role;
    
    private Map<String, Boolean> permissions;
}

