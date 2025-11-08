package com.splitia.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;
    
    @Size(max = 50, message = "Icon must be less than 50 characters")
    private String icon;
    
    @Size(max = 20, message = "Color must be less than 20 characters")
    private String color;
    
    @NotNull(message = "Group ID is required")
    private UUID groupId;
}

