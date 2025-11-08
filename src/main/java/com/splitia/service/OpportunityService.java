package com.splitia.service;

import com.splitia.dto.request.CreateOpportunityRequest;
import com.splitia.dto.request.UpdateOpportunityRequest;
import com.splitia.dto.response.OpportunityResponse;
import com.splitia.exception.BadRequestException;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.OpportunityMapper;
import com.splitia.model.User;
import com.splitia.model.contact.Company;
import com.splitia.model.contact.Contact;
import com.splitia.model.enums.OpportunityStage;
import com.splitia.model.sales.Opportunity;
import com.splitia.repository.*;
import com.splitia.service.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OpportunityService {
    
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;
    private final OpportunityMapper opportunityMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    
    @Transactional(readOnly = true)
    public Page<OpportunityResponse> getAllOpportunities(Pageable pageable, OpportunityStage stage, UUID assignedToId) {
        Specification<Opportunity> spec = Specification.where(null);
        
        if (stage != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("stage"), stage));
        }
        
        if (assignedToId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("assignedTo").get("id"), assignedToId));
        }
        
        spec = spec.and((root, query, cb) -> cb.isNull(root.get("deletedAt")));
        
        return opportunityRepository.findAll(spec, pageable)
                .map(opportunityMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public OpportunityResponse getOpportunityById(UUID id) {
        Opportunity opportunity = opportunityRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity", "id", id));
        return opportunityMapper.toResponse(opportunity);
    }
    
    @Transactional
    public OpportunityResponse createOpportunity(CreateOpportunityRequest request) {
        Opportunity opportunity = new Opportunity();
        opportunity.setName(request.getName());
        opportunity.setDescription(request.getDescription());
        opportunity.setEstimatedValue(request.getEstimatedValue());
        opportunity.setProbability(request.getProbability() != null ? request.getProbability() : 0);
        opportunity.setStage(request.getStage() != null ? request.getStage() : OpportunityStage.LEAD);
        opportunity.setExpectedCloseDate(request.getExpectedCloseDate());
        opportunity.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findByIdAndDeletedAtIsNull(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedToId()));
            opportunity.setAssignedTo(assignedTo);
        }
        
        if (request.getContactId() != null) {
            Contact contact = contactRepository.findByIdAndDeletedAtIsNull(request.getContactId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", request.getContactId()));
            opportunity.setContact(contact);
        }
        
        if (request.getCompanyId() != null) {
            Company company = companyRepository.findByIdAndDeletedAtIsNull(request.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company", "id", request.getCompanyId()));
            opportunity.setCompany(company);
        }
        
        opportunity = opportunityRepository.save(opportunity);
        OpportunityResponse response = opportunityMapper.toResponse(opportunity);
        
        // Emit WebSocket event
        Map<String, Object> data = new HashMap<>();
        data.put("opportunity", response);
        webSocketNotificationService.notifyOpportunityCreated(opportunity.getId(), data, getCurrentUserId());
        
        return response;
    }
    
    @Transactional
    public OpportunityResponse updateOpportunity(UUID id, UpdateOpportunityRequest request) {
        Opportunity opportunity = opportunityRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity", "id", id));
        
        OpportunityStage oldStage = opportunity.getStage();
        
        if (request.getName() != null) {
            opportunity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            opportunity.setDescription(request.getDescription());
        }
        if (request.getEstimatedValue() != null) {
            opportunity.setEstimatedValue(request.getEstimatedValue());
        }
        if (request.getProbability() != null) {
            opportunity.setProbability(request.getProbability());
        }
        if (request.getStage() != null) {
            opportunity.setStage(request.getStage());
            // Auto-set dates when closing
            if (request.getStage() == OpportunityStage.CLOSED_WON || request.getStage() == OpportunityStage.CLOSED_LOST) {
                if (opportunity.getActualCloseDate() == null) {
                    opportunity.setActualCloseDate(LocalDate.now());
                }
            }
        }
        if (request.getExpectedCloseDate() != null) {
            opportunity.setExpectedCloseDate(request.getExpectedCloseDate());
        }
        if (request.getActualCloseDate() != null) {
            opportunity.setActualCloseDate(request.getActualCloseDate());
        }
        if (request.getWonAmount() != null) {
            opportunity.setWonAmount(request.getWonAmount());
        }
        if (request.getLostReason() != null) {
            opportunity.setLostReason(request.getLostReason());
        }
        if (request.getCurrency() != null) {
            opportunity.setCurrency(request.getCurrency());
        }
        
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findByIdAndDeletedAtIsNull(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedToId()));
            opportunity.setAssignedTo(assignedTo);
        }
        
        if (request.getContactId() != null) {
            Contact contact = contactRepository.findByIdAndDeletedAtIsNull(request.getContactId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", request.getContactId()));
            opportunity.setContact(contact);
        }
        
        if (request.getCompanyId() != null) {
            Company company = companyRepository.findByIdAndDeletedAtIsNull(request.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company", "id", request.getCompanyId()));
            opportunity.setCompany(company);
        }
        
        opportunity = opportunityRepository.save(opportunity);
        OpportunityResponse response = opportunityMapper.toResponse(opportunity);
        
        // Emit WebSocket events
        Map<String, Object> data = new HashMap<>();
        data.put("opportunity", response);
        webSocketNotificationService.notifyOpportunityUpdated(opportunity.getId(), data, getCurrentUserId());
        
        // If stage changed, emit stage change event
        if (request.getStage() != null && !oldStage.equals(request.getStage())) {
            webSocketNotificationService.notifyOpportunityStageChanged(
                opportunity.getId(), 
                oldStage.name(), 
                request.getStage().name(), 
                getCurrentUserId()
            );
        }
        
        return response;
    }
    
    @Transactional
    public OpportunityResponse updateStage(UUID id, OpportunityStage stage) {
        Opportunity opportunity = opportunityRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity", "id", id));
        
        OpportunityStage oldStage = opportunity.getStage();
        opportunity.setStage(stage);
        
        if (stage == OpportunityStage.CLOSED_WON || stage == OpportunityStage.CLOSED_LOST) {
            if (opportunity.getActualCloseDate() == null) {
                opportunity.setActualCloseDate(LocalDate.now());
            }
        }
        
        opportunity = opportunityRepository.save(opportunity);
        OpportunityResponse response = opportunityMapper.toResponse(opportunity);
        
        // Emit WebSocket event
        webSocketNotificationService.notifyOpportunityStageChanged(
            opportunity.getId(), 
            oldStage.name(), 
            stage.name(), 
            getCurrentUserId()
        );
        
        return response;
    }
    
    @Transactional
    public void deleteOpportunity(UUID id, boolean hardDelete) {
        if (hardDelete) {
            opportunityRepository.deleteById(id);
        } else {
            Opportunity opportunity = opportunityRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Opportunity", "id", id));
            opportunity.setDeletedAt(LocalDateTime.now());
            opportunityRepository.save(opportunity);
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

