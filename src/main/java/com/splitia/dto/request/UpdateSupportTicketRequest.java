package com.splitia.dto.request;

import com.splitia.model.enums.SupportPriority;
import com.splitia.model.enums.SupportStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSupportTicketRequest {
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;
    
    private String description;
    
    private SupportStatus status;
    
    private SupportPriority priority;
    
    private String resolution;
}

