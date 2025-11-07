package com.splitia.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    private String description;
    
    @NotNull(message = "Group ID is required")
    private UUID groupId;
    
    private UUID assignedToId;
    
    private LocalDate startDate;
    
    private LocalDate dueDate;
    
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, URGENT
    
    private List<UUID> tagIds;
}

