package com.splitia.dto.request;

import com.splitia.model.enums.LeadSource;
import com.splitia.model.enums.LeadStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLeadRequest {
    @Size(max = 100, message = "First name must be less than 100 characters")
    private String firstName;
    
    @Size(max = 100, message = "Last name must be less than 100 characters")
    private String lastName;
    
    @Email(message = "Email must be valid")
    private String email;
    
    @Size(max = 50, message = "Phone must be less than 50 characters")
    private String phone;
    
    @Size(max = 255, message = "Company must be less than 255 characters")
    private String company;
    
    private LeadSource source;
    
    private LeadStatus status;
    
    @Min(value = 0, message = "Score must be between 0 and 100")
    @Max(value = 100, message = "Score must be between 0 and 100")
    private Integer score;
    
    @Size(max = 10000, message = "Notes must be less than 10000 characters")
    private String notes;
    
    private UUID assignedToId;
    
    private UUID contactId;
}

