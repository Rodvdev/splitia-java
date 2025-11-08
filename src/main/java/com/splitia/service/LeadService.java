package com.splitia.service;

import com.splitia.dto.request.CreateLeadRequest;
import com.splitia.dto.request.UpdateLeadRequest;
import com.splitia.dto.response.LeadResponse;
import com.splitia.dto.response.OpportunityResponse;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.LeadMapper;
import com.splitia.mapper.OpportunityMapper;
import com.splitia.model.User;
import com.splitia.model.contact.Contact;
import com.splitia.model.enums.LeadStatus;
import com.splitia.model.sales.Lead;
import com.splitia.model.sales.Opportunity;
import com.splitia.repository.*;
import com.splitia.service.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeadService {
    
    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final OpportunityRepository opportunityRepository;
    private final LeadMapper leadMapper;
    private final OpportunityMapper opportunityMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    
    @Transactional(readOnly = true)
    public Page<LeadResponse> getAllLeads(Pageable pageable, LeadStatus status, UUID assignedToId) {
        Specification<Lead> spec = Specification.where(null);
        
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        
        if (assignedToId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("assignedTo").get("id"), assignedToId));
        }
        
        spec = spec.and((root, query, cb) -> cb.isNull(root.get("deletedAt")));
        
        return leadRepository.findAll(spec, pageable)
                .map(leadMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public LeadResponse getLeadById(UUID id) {
        Lead lead = leadRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", id));
        return leadMapper.toResponse(lead);
    }
    
    @Transactional
    public LeadResponse createLead(CreateLeadRequest request) {
        Lead lead = new Lead();
        lead.setFirstName(request.getFirstName());
        lead.setLastName(request.getLastName());
        lead.setEmail(request.getEmail());
        lead.setPhone(request.getPhone());
        lead.setCompany(request.getCompany());
        lead.setSource(request.getSource());
        lead.setStatus(request.getStatus() != null ? request.getStatus() : LeadStatus.NEW);
        lead.setScore(request.getScore() != null ? request.getScore() : 0);
        lead.setNotes(request.getNotes());
        
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findByIdAndDeletedAtIsNull(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedToId()));
            lead.setAssignedTo(assignedTo);
        }
        
        if (request.getContactId() != null) {
            Contact contact = contactRepository.findByIdAndDeletedAtIsNull(request.getContactId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", request.getContactId()));
            lead.setContact(contact);
        }
        
        lead = leadRepository.save(lead);
        LeadResponse response = leadMapper.toResponse(lead);
        
        // Emit WebSocket event
        Map<String, Object> data = new HashMap<>();
        data.put("lead", response);
        webSocketNotificationService.notifyLeadCreated(lead.getId(), data, getCurrentUserId());
        
        return response;
    }
    
    @Transactional
    public LeadResponse updateLead(UUID id, UpdateLeadRequest request) {
        Lead lead = leadRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", id));
        
        if (request.getFirstName() != null) {
            lead.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            lead.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            lead.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            lead.setPhone(request.getPhone());
        }
        if (request.getCompany() != null) {
            lead.setCompany(request.getCompany());
        }
        if (request.getSource() != null) {
            lead.setSource(request.getSource());
        }
        if (request.getStatus() != null) {
            lead.setStatus(request.getStatus());
        }
        if (request.getScore() != null) {
            lead.setScore(request.getScore());
        }
        if (request.getNotes() != null) {
            lead.setNotes(request.getNotes());
        }
        
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findByIdAndDeletedAtIsNull(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedToId()));
            lead.setAssignedTo(assignedTo);
        }
        
        if (request.getContactId() != null) {
            Contact contact = contactRepository.findByIdAndDeletedAtIsNull(request.getContactId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", request.getContactId()));
            lead.setContact(contact);
        }
        
        lead = leadRepository.save(lead);
        return leadMapper.toResponse(lead);
    }
    
    @Transactional
    public LeadResponse updateScore(UUID id, Integer score) {
        Lead lead = leadRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", id));
        
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
        
        lead.setScore(score);
        lead = leadRepository.save(lead);
        return leadMapper.toResponse(lead);
    }
    
    @Transactional
    public OpportunityResponse convertToOpportunity(UUID leadId) {
        Lead lead = leadRepository.findByIdAndDeletedAtIsNull(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));
        
        if (lead.getConvertedToOpportunityId() != null) {
            throw new IllegalStateException("Lead has already been converted to an opportunity");
        }
        
        // Create opportunity from lead
        Opportunity opportunity = new Opportunity();
        opportunity.setName(lead.getFirstName() + " " + lead.getLastName() + " - " + lead.getCompany());
        opportunity.setDescription("Converted from lead: " + lead.getEmail());
        opportunity.setStage(com.splitia.model.enums.OpportunityStage.LEAD);
        opportunity.setProbability(lead.getScore());
        opportunity.setAssignedTo(lead.getAssignedTo());
        opportunity.setContact(lead.getContact());
        
        opportunity = opportunityRepository.save(opportunity);
        
        // Update lead
        lead.setConvertedToOpportunityId(opportunity.getId());
        lead.setStatus(LeadStatus.CONVERTED);
        leadRepository.save(lead);
        
        OpportunityResponse response = opportunityMapper.toResponse(opportunity);
        
        // Emit WebSocket event
        webSocketNotificationService.notifyLeadConverted(lead.getId(), opportunity.getId(), getCurrentUserId());
        
        return response;
    }
    
    @Transactional
    public void deleteLead(UUID id, boolean hardDelete) {
        if (hardDelete) {
            leadRepository.deleteById(id);
        } else {
            Lead lead = leadRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", id));
            lead.setDeletedAt(LocalDateTime.now());
            leadRepository.save(lead);
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

