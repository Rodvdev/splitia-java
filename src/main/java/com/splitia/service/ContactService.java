package com.splitia.service;

import com.splitia.dto.request.CreateContactRequest;
import com.splitia.dto.request.UpdateContactRequest;
import com.splitia.dto.response.ContactResponse;
import com.splitia.exception.BadRequestException;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.ContactMapper;
import com.splitia.model.User;
import com.splitia.model.contact.Company;
import com.splitia.model.contact.Contact;
import com.splitia.model.contact.ContactTag;
import com.splitia.repository.*;
import com.splitia.service.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactService {
    
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ContactTagRepository contactTagRepository;
    private final ContactMapper contactMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    
    @Transactional(readOnly = true)
    public Page<ContactResponse> getAllContacts(Pageable pageable, UUID companyId) {
        if (companyId != null) {
            return contactRepository.findByCompanyId(companyId, pageable)
                    .map(contactMapper::toResponse);
        }
        return contactRepository.findAllActive(pageable)
                .map(contactMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public ContactResponse getContactById(UUID id) {
        Contact contact = contactRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id));
        return contactMapper.toResponse(contact);
    }
    
    @Transactional
    public ContactResponse createContact(CreateContactRequest request) {
        if (contactRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Contact with this email already exists");
        }
        
        Contact contact = new Contact();
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setMobile(request.getMobile());
        contact.setJobTitle(request.getJobTitle());
        contact.setDepartment(request.getDepartment());
        contact.setType(request.getType() != null ? request.getType() : com.splitia.model.enums.ContactType.PROSPECT);
        contact.setNotes(request.getNotes());
        
        if (request.getOwnerId() != null) {
            User owner = userRepository.findByIdAndDeletedAtIsNull(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getOwnerId()));
            contact.setOwner(owner);
        }
        
        if (request.getCompanyId() != null) {
            Company company = companyRepository.findByIdAndDeletedAtIsNull(request.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company", "id", request.getCompanyId()));
            contact.setCompany(company);
        }
        
        contact = contactRepository.save(contact);
        
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<ContactTag> tags = new ArrayList<>();
            for (UUID tagId : request.getTagIds()) {
                ContactTag tag = contactTagRepository.findById(tagId)
                        .orElseThrow(() -> new ResourceNotFoundException("ContactTag", "id", tagId));
                tags.add(tag);
            }
            contact.setTags(tags);
            contact = contactRepository.save(contact);
        }
        
        ContactResponse response = contactMapper.toResponse(contact);
        
        // Emit WebSocket event
        Map<String, Object> data = new HashMap<>();
        data.put("contact", response);
        webSocketNotificationService.notifyContactCreated(contact.getId(), data, getCurrentUserId());
        
        return response;
    }
    
    @Transactional
    public ContactResponse updateContact(UUID id, UpdateContactRequest request) {
        Contact contact = contactRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id));
        
        if (request.getFirstName() != null) contact.setFirstName(request.getFirstName());
        if (request.getLastName() != null) contact.setLastName(request.getLastName());
        if (request.getEmail() != null) {
            if (!request.getEmail().equals(contact.getEmail())) {
                if (contactRepository.findByEmail(request.getEmail()).isPresent()) {
                    throw new BadRequestException("Contact with this email already exists");
                }
                contact.setEmail(request.getEmail());
            }
        }
        if (request.getPhone() != null) contact.setPhone(request.getPhone());
        if (request.getMobile() != null) contact.setMobile(request.getMobile());
        if (request.getJobTitle() != null) contact.setJobTitle(request.getJobTitle());
        if (request.getDepartment() != null) contact.setDepartment(request.getDepartment());
        if (request.getType() != null) contact.setType(request.getType());
        if (request.getNotes() != null) contact.setNotes(request.getNotes());
        
        if (request.getOwnerId() != null) {
            User owner = userRepository.findByIdAndDeletedAtIsNull(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getOwnerId()));
            contact.setOwner(owner);
        }
        
        if (request.getCompanyId() != null) {
            Company company = companyRepository.findByIdAndDeletedAtIsNull(request.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company", "id", request.getCompanyId()));
            contact.setCompany(company);
        }
        
        if (request.getTagIds() != null) {
            List<ContactTag> tags = new ArrayList<>();
            for (UUID tagId : request.getTagIds()) {
                ContactTag tag = contactTagRepository.findById(tagId)
                        .orElseThrow(() -> new ResourceNotFoundException("ContactTag", "id", tagId));
                tags.add(tag);
            }
            contact.setTags(tags);
        }
        
        contact = contactRepository.save(contact);
        ContactResponse response = contactMapper.toResponse(contact);
        
        // Emit WebSocket event
        Map<String, Object> data = new HashMap<>();
        data.put("contact", response);
        webSocketNotificationService.notifyContactUpdated(contact.getId(), data, getCurrentUserId());
        
        return response;
    }
    
    @Transactional
    public void deleteContact(UUID id, boolean hardDelete) {
        if (hardDelete) {
            contactRepository.deleteById(id);
        } else {
            Contact contact = contactRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id));
            contact.setDeletedAt(LocalDateTime.now());
            contactRepository.save(contact);
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

