package com.splitia.dto.request;

import com.splitia.model.enums.ContactType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateContactRequest {
    @Size(max = 100, message = "First name must be less than 100 characters")
    private String firstName;
    
    @Size(max = 100, message = "Last name must be less than 100 characters")
    private String lastName;
    
    @Email(message = "Email must be valid")
    private String email;
    
    @Size(max = 50, message = "Phone must be less than 50 characters")
    private String phone;
    
    @Size(max = 50, message = "Mobile must be less than 50 characters")
    private String mobile;
    
    @Size(max = 100, message = "Job title must be less than 100 characters")
    private String jobTitle;
    
    @Size(max = 100, message = "Department must be less than 100 characters")
    private String department;
    
    private ContactType type;
    
    @Size(max = 10000, message = "Notes must be less than 10000 characters")
    private String notes;
    
    private UUID ownerId;
    
    private UUID companyId;
    
    private List<UUID> tagIds;
}

