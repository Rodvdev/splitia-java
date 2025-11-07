package com.splitia.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupRequest {
    @Size(min = 1, max = 100, message = "Group name must be between 1 and 100 characters")
    private String name;
    
    private String description;
    
    private String image;
}

