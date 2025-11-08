package com.splitia.service;

import com.splitia.dto.request.CreateCompanyRequest;
import com.splitia.dto.request.UpdateCompanyRequest;
import com.splitia.dto.response.CompanyResponse;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.CompanyMapper;
import com.splitia.model.User;
import com.splitia.model.contact.Company;
import com.splitia.repository.CompanyRepository;
import com.splitia.repository.UserRepository;
import com.splitia.service.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {
    
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CompanyMapper companyMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    
    @Transactional(readOnly = true)
    public Page<CompanyResponse> getAllCompanies(Pageable pageable) {
        return companyRepository.findAllActive(pageable)
                .map(companyMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(UUID id) {
        Company company = companyRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        return companyMapper.toResponse(company);
    }
    
    @Transactional
    public CompanyResponse createCompany(CreateCompanyRequest request) {
        Company company = new Company();
        company.setName(request.getName());
        company.setLegalName(request.getLegalName());
        company.setTaxId(request.getTaxId());
        company.setWebsite(request.getWebsite());
        company.setIndustry(request.getIndustry());
        company.setSize(request.getSize());
        company.setAddress(request.getAddress());
        company.setCity(request.getCity());
        company.setCountry(request.getCountry());
        company.setPhone(request.getPhone());
        company.setEmail(request.getEmail());
        
        if (request.getOwnerId() != null) {
            User owner = userRepository.findByIdAndDeletedAtIsNull(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getOwnerId()));
            company.setOwner(owner);
        }
        
        company = companyRepository.save(company);
        CompanyResponse response = companyMapper.toResponse(company);
        
        // Emit WebSocket event
        Map<String, Object> data = new HashMap<>();
        data.put("company", response);
        webSocketNotificationService.notifyCompanyCreated(company.getId(), data, getCurrentUserId());
        
        return response;
    }
    
    @Transactional
    public CompanyResponse updateCompany(UUID id, UpdateCompanyRequest request) {
        Company company = companyRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        
        if (request.getName() != null) company.setName(request.getName());
        if (request.getLegalName() != null) company.setLegalName(request.getLegalName());
        if (request.getTaxId() != null) company.setTaxId(request.getTaxId());
        if (request.getWebsite() != null) company.setWebsite(request.getWebsite());
        if (request.getIndustry() != null) company.setIndustry(request.getIndustry());
        if (request.getSize() != null) company.setSize(request.getSize());
        if (request.getAddress() != null) company.setAddress(request.getAddress());
        if (request.getCity() != null) company.setCity(request.getCity());
        if (request.getCountry() != null) company.setCountry(request.getCountry());
        if (request.getPhone() != null) company.setPhone(request.getPhone());
        if (request.getEmail() != null) company.setEmail(request.getEmail());
        
        if (request.getOwnerId() != null) {
            User owner = userRepository.findByIdAndDeletedAtIsNull(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getOwnerId()));
            company.setOwner(owner);
        }
        
        company = companyRepository.save(company);
        CompanyResponse response = companyMapper.toResponse(company);
        
        // Emit WebSocket event
        Map<String, Object> data = new HashMap<>();
        data.put("company", response);
        webSocketNotificationService.notifyCompanyCreated(company.getId(), data, getCurrentUserId());
        
        return response;
    }
    
    @Transactional
    public void deleteCompany(UUID id, boolean hardDelete) {
        if (hardDelete) {
            companyRepository.deleteById(id);
        } else {
            Company company = companyRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
            company.setDeletedAt(LocalDateTime.now());
            companyRepository.save(company);
        }
    }
    
    private UUID getCurrentUserId() {
        try {
            org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof com.splitia.security.CustomUserDetails) {
                com.splitia.security.CustomUserDetails userDetails = 
                    (com.splitia.security.CustomUserDetails) authentication.getPrincipal();
                return userDetails.getUserId();
            }
        } catch (Exception e) {
            // If not authenticated, return null
        }
        return null;
    }
}

