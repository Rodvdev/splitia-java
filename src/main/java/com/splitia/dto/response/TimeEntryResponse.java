package com.splitia.dto.response;

import com.splitia.model.enums.TimeEntryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntryResponse {
    private UUID id;
    private UUID projectId;
    private String projectName;
    private UUID taskId;
    private String taskTitle;
    private UUID userId;
    private String userName;
    private LocalDate date;
    private Double hours;
    private String description;
    private Boolean isBillable;
    private TimeEntryStatus status;
}

