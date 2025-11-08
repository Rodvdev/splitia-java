package com.splitia.controller;

import com.splitia.dto.request.CreateCompanyRequest;
import com.splitia.dto.request.CreateContactRequest;
import com.splitia.dto.request.UpdateCompanyRequest;
import com.splitia.dto.request.UpdateContactRequest;
import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.CompanyResponse;
import com.splitia.dto.response.ContactResponse;
import com.splitia.service.CompanyService;
import com.splitia.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/contacts")
@RequiredArgsConstructor
@Tag(name = "Contacts", description = "Contact and company management endpoints")
public class ContactController {
    
    private final ContactService contactService;
    private final CompanyService companyService;
    
    // Contacts
    @GetMapping
    @Operation(summary = "Get all contacts")
    public ResponseEntity<ApiResponse<Page<ContactResponse>>> getAllContacts(
            Pageable pageable,
            @RequestParam(required = false) UUID companyId) {
        Page<ContactResponse> contacts = contactService.getAllContacts(pageable, companyId);
        return ResponseEntity.ok(ApiResponse.success(contacts));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get contact by ID")
    public ResponseEntity<ApiResponse<ContactResponse>> getContactById(@PathVariable UUID id) {
        ContactResponse contact = contactService.getContactById(id);
        return ResponseEntity.ok(ApiResponse.success(contact));
    }
    
    @PostMapping
    @Operation(summary = "Create a new contact")
    public ResponseEntity<ApiResponse<ContactResponse>> createContact(@Valid @RequestBody CreateContactRequest request) {
        ContactResponse contact = contactService.createContact(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(contact, "Contact created successfully"));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update contact")
    public ResponseEntity<ApiResponse<ContactResponse>> updateContact(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateContactRequest request) {
        ContactResponse contact = contactService.updateContact(id, request);
        return ResponseEntity.ok(ApiResponse.success(contact, "Contact updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete contact. Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteContact(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        contactService.deleteContact(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Contact permanently deleted" : "Contact soft deleted"));
    }
    
    // Companies
    @GetMapping("/companies")
    @Operation(summary = "Get all companies")
    public ResponseEntity<ApiResponse<Page<CompanyResponse>>> getAllCompanies(Pageable pageable) {
        Page<CompanyResponse> companies = companyService.getAllCompanies(pageable);
        return ResponseEntity.ok(ApiResponse.success(companies));
    }
    
    @GetMapping("/companies/{id}")
    @Operation(summary = "Get company by ID")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompanyById(@PathVariable UUID id) {
        CompanyResponse company = companyService.getCompanyById(id);
        return ResponseEntity.ok(ApiResponse.success(company));
    }
    
    @PostMapping("/companies")
    @Operation(summary = "Create a new company")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(@Valid @RequestBody CreateCompanyRequest request) {
        CompanyResponse company = companyService.createCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(company, "Company created successfully"));
    }
    
    @PutMapping("/companies/{id}")
    @Operation(summary = "Update company")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCompanyRequest request) {
        CompanyResponse company = companyService.updateCompany(id, request);
        return ResponseEntity.ok(ApiResponse.success(company, "Company updated successfully"));
    }
    
    @DeleteMapping("/companies/{id}")
    @Operation(summary = "Delete company. Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        companyService.deleteCompany(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Company permanently deleted" : "Company soft deleted"));
    }
}

