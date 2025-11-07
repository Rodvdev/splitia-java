package com.splitia.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskTagRequest {
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    private String name;
    
    @Size(max = 7, message = "Color must be a valid hex color code")
    private String color;
}

