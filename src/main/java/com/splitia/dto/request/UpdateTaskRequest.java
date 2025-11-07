package com.splitia.dto.request;

import com.splitia.model.enums.TaskStatus;
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
public class UpdateTaskRequest {
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    private String description;
    
    private TaskStatus status;
    
    private UUID assignedToId;
    
    private LocalDate startDate;
    
    private LocalDate dueDate;
    
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    
    private List<UUID> tagIds;
    
    private Integer position; // For reordering within status column
}

