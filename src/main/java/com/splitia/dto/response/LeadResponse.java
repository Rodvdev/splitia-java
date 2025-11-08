package com.splitia.dto.response;

import com.splitia.model.enums.LeadSource;
import com.splitia.model.enums.LeadStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String company;
    private LeadSource source;
    private LeadStatus status;
    private Integer score;
    private String notes;
    private UUID convertedToOpportunityId;
    private UUID assignedToId;
    private String assignedToName;
    private UUID contactId;
    private String contactName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

