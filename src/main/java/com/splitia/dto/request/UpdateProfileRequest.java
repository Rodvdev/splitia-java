package com.splitia.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;
    
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;
    
    private String phoneNumber;
    
    private String image;
}

