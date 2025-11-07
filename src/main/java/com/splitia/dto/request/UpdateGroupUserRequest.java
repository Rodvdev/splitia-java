package com.splitia.dto.request;

import com.splitia.model.enums.GroupRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupUserRequest {
    private GroupRole role;
    
    private Map<String, Boolean> permissions;
}

