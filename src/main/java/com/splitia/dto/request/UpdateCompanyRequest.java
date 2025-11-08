package com.splitia.dto.request;

import com.splitia.model.enums.CompanySize;
import com.splitia.model.enums.Industry;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyRequest {
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;
    
    @Size(max = 255, message = "Legal name must be less than 255 characters")
    private String legalName;
    
    @Size(max = 50, message = "Tax ID must be less than 50 characters")
    private String taxId;
    
    @Size(max = 255, message = "Website must be less than 255 characters")
    private String website;
    
    private Industry industry;
    
    private CompanySize size;
    
    @Size(max = 10000, message = "Address must be less than 10000 characters")
    private String address;
    
    @Size(max = 100, message = "City must be less than 100 characters")
    private String city;
    
    @Size(max = 100, message = "Country must be less than 100 characters")
    private String country;
    
    @Size(max = 50, message = "Phone must be less than 50 characters")
    private String phone;
    
    @Email(message = "Email must be valid")
    private String email;
    
    private UUID ownerId;
}

