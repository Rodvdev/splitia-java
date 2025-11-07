package com.splitia.dto.response;

import com.splitia.model.enums.SupportCategory;
import com.splitia.model.enums.SupportPriority;
import com.splitia.model.enums.SupportStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketResponse {
    private UUID id;
    private String title;
    private String description;
    private SupportCategory category;
    private SupportPriority priority;
    private SupportStatus status;
    private String resolution;
    private LocalDateTime resolvedAt;
    private UserResponse createdBy;
    private UserResponse assignedTo;
    private LocalDateTime createdAt;
}

