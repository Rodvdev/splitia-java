package com.splitia.dto.response;

import com.splitia.model.enums.ContactType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String mobile;
    private String jobTitle;
    private String department;
    private ContactType type;
    private String notes;
    private UUID ownerId;
    private String ownerName;
    private UUID companyId;
    private String companyName;
    private List<ContactTagResponse> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

