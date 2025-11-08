package com.splitia.dto.request;

import com.splitia.model.enums.ActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateActivityRequest {
    @NotNull(message = "Type is required")
    private ActivityType type;
    
    @NotBlank(message = "Subject is required")
    @Size(max = 255, message = "Subject must be less than 255 characters")
    private String subject;
    
    @Size(max = 10000, message = "Description must be less than 10000 characters")
    private String description;
    
    private LocalDateTime dueDate;
    
    private UUID opportunityId;
    
    private UUID leadId;
    
    private UUID contactId;
    
    private UUID assignedToId;
    
    private Integer durationMinutes;
}

