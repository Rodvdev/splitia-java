package com.splitia.dto.response;

import com.splitia.model.enums.PaymentTerms;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorResponse {
    private UUID id;
    private String name;
    private String contactName;
    private String email;
    private String phoneNumber;
    private String taxId;
    private String address;
    private String city;
    private String country;
    private PaymentTerms paymentTerms;
    private Integer rating;
    private String notes;
    private UUID createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

