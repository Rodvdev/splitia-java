package com.splitia.dto.request;

import com.splitia.model.enums.TimeEntryStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTimeEntryRequest {
    @NotNull(message = "Project ID is required")
    private UUID projectId;
    
    private UUID taskId;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotNull(message = "Hours is required")
    private Double hours;
    
    @Size(max = 10000, message = "Description must be less than 10000 characters")
    private String description;
    
    private Boolean isBillable = true;
    
    private TimeEntryStatus status = TimeEntryStatus.DRAFT;
}

