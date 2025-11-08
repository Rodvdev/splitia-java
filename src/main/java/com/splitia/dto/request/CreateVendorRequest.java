package com.splitia.dto.request;

import com.splitia.model.enums.PaymentTerms;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVendorRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;
    
    @Size(max = 255, message = "Contact name must be less than 255 characters")
    private String contactName;
    
    @Email(message = "Email must be valid")
    private String email;
    
    @Size(max = 50, message = "Phone number must be less than 50 characters")
    private String phoneNumber;
    
    @Size(max = 50, message = "Tax ID must be less than 50 characters")
    private String taxId;
    
    private String address;
    private String city;
    private String country;
    
    private PaymentTerms paymentTerms;
    
    private Integer rating; // 1-5
    
    @Size(max = 10000, message = "Notes must be less than 10000 characters")
    private String notes;
}

