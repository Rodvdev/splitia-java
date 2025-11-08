package com.splitia.dto.request;

import com.splitia.model.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;
    
    @Size(max = 10000, message = "Description must be less than 10000 characters")
    private String description;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private ProjectStatus status = ProjectStatus.PLANNING;
    
    private BigDecimal budget;
    
    private UUID managerId;
    
    private List<UUID> taskIds;
}

