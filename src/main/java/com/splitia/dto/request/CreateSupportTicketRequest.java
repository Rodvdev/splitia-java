package com.splitia.dto.request;

import com.splitia.model.enums.SupportCategory;
import com.splitia.model.enums.SupportPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSupportTicketRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(min = 1, message = "Description cannot be empty")
    private String description;
    
    @NotNull(message = "Category is required")
    private SupportCategory category;
    
    private SupportPriority priority = SupportPriority.MEDIUM;
}

