package com.splitia.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignPermissionsRequest {
    @NotNull(message = "Permissions are required")
    private Map<String, Boolean> permissions;
}

