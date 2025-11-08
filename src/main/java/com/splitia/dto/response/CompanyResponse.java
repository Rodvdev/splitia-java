package com.splitia.dto.response;

import com.splitia.model.enums.CompanySize;
import com.splitia.model.enums.Industry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponse {
    private UUID id;
    private String name;
    private String legalName;
    private String taxId;
    private String website;
    private Industry industry;
    private CompanySize size;
    private String address;
    private String city;
    private String country;
    private String phone;
    private String email;
    private UUID ownerId;
    private String ownerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

