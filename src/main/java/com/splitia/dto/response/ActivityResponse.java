package com.splitia.dto.response;

import com.splitia.model.enums.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponse {
    private UUID id;
    private ActivityType type;
    private String subject;
    private String description;
    private LocalDateTime dueDate;
    private LocalDateTime completedDate;
    private Boolean isCompleted;
    private Integer durationMinutes;
    private UUID opportunityId;
    private String opportunityName;
    private UUID leadId;
    private String leadName;
    private UUID contactId;
    private String contactName;
    private UUID createdById;
    private String createdByName;
    private UUID assignedToId;
    private String assignedToName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

