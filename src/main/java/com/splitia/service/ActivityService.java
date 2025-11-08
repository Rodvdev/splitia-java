package com.splitia.service;

import com.splitia.dto.request.CreateActivityRequest;
import com.splitia.dto.request.UpdateActivityRequest;
import com.splitia.dto.response.ActivityResponse;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.ActivityMapper;
import com.splitia.model.User;
import com.splitia.model.contact.Contact;
import com.splitia.model.sales.Activity;
import com.splitia.model.sales.Lead;
import com.splitia.model.sales.Opportunity;
import com.splitia.repository.*;
import com.splitia.service.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {
    
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final OpportunityRepository opportunityRepository;
    private final LeadRepository leadRepository;
    private final ContactRepository contactRepository;
    private final ActivityMapper activityMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    
    @Transactional(readOnly = true)
    public Page<ActivityResponse> getAllActivities(Pageable pageable) {
        return activityRepository.findAllActive(pageable)
                .map(activityMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public ActivityResponse getActivityById(UUID id) {
        Activity activity = activityRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", id));
        return activityMapper.toResponse(activity);
    }
    
    @Transactional(readOnly = true)
    public List<ActivityResponse> getActivitiesByOpportunity(UUID opportunityId) {
        List<Activity> activities = activityRepository.findByOpportunityId(opportunityId);
        return activities.stream()
                .map(activityMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ActivityResponse> getActivitiesByLead(UUID leadId) {
        List<Activity> activities = activityRepository.findByLeadId(leadId);
        return activities.stream()
                .map(activityMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ActivityResponse> getActivitiesByContact(UUID contactId) {
        List<Activity> activities = activityRepository.findByContactId(contactId);
        return activities.stream()
                .map(activityMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ActivityResponse createActivity(CreateActivityRequest request, UUID createdById) {
        Activity activity = new Activity();
        activity.setType(request.getType());
        activity.setSubject(request.getSubject());
        activity.setDescription(request.getDescription());
        activity.setDueDate(request.getDueDate());
        activity.setDurationMinutes(request.getDurationMinutes());
        
        User createdBy = userRepository.findByIdAndDeletedAtIsNull(createdById)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", createdById));
        activity.setCreatedBy(createdBy);
        
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findByIdAndDeletedAtIsNull(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedToId()));
            activity.setAssignedTo(assignedTo);
        }
        
        if (request.getOpportunityId() != null) {
            Opportunity opportunity = opportunityRepository.findByIdAndDeletedAtIsNull(request.getOpportunityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Opportunity", "id", request.getOpportunityId()));
            activity.setOpportunity(opportunity);
        }
        
        if (request.getLeadId() != null) {
            Lead lead = leadRepository.findByIdAndDeletedAtIsNull(request.getLeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", request.getLeadId()));
            activity.setLead(lead);
        }
        
        if (request.getContactId() != null) {
            Contact contact = contactRepository.findByIdAndDeletedAtIsNull(request.getContactId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", request.getContactId()));
            activity.setContact(contact);
        }
        
        activity = activityRepository.save(activity);
        ActivityResponse response = activityMapper.toResponse(activity);
        
        // Emit WebSocket event
        Map<String, Object> data = new HashMap<>();
        data.put("activity", response);
        webSocketNotificationService.notifyActivityCreated(activity.getId(), data, createdById);
        
        return response;
    }
    
    @Transactional
    public ActivityResponse updateActivity(UUID id, UpdateActivityRequest request) {
        Activity activity = activityRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", id));
        
        if (request.getType() != null) {
            activity.setType(request.getType());
        }
        if (request.getSubject() != null) {
            activity.setSubject(request.getSubject());
        }
        if (request.getDescription() != null) {
            activity.setDescription(request.getDescription());
        }
        if (request.getDueDate() != null) {
            activity.setDueDate(request.getDueDate());
        }
        if (request.getDurationMinutes() != null) {
            activity.setDurationMinutes(request.getDurationMinutes());
        }
        
        if (request.getIsCompleted() != null) {
            activity.setIsCompleted(request.getIsCompleted());
            if (request.getIsCompleted() && activity.getCompletedDate() == null) {
                activity.setCompletedDate(LocalDateTime.now());
            } else if (!request.getIsCompleted()) {
                activity.setCompletedDate(null);
            }
        }
        
        if (request.getCompletedDate() != null) {
            activity.setCompletedDate(request.getCompletedDate());
        }
        
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findByIdAndDeletedAtIsNull(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedToId()));
            activity.setAssignedTo(assignedTo);
        }
        
        if (request.getOpportunityId() != null) {
            Opportunity opportunity = opportunityRepository.findByIdAndDeletedAtIsNull(request.getOpportunityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Opportunity", "id", request.getOpportunityId()));
            activity.setOpportunity(opportunity);
        }
        
        if (request.getLeadId() != null) {
            Lead lead = leadRepository.findByIdAndDeletedAtIsNull(request.getLeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", request.getLeadId()));
            activity.setLead(lead);
        }
        
        if (request.getContactId() != null) {
            Contact contact = contactRepository.findByIdAndDeletedAtIsNull(request.getContactId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", request.getContactId()));
            activity.setContact(contact);
        }
        
        activity = activityRepository.save(activity);
        return activityMapper.toResponse(activity);
    }
    
    @Transactional
    public ActivityResponse completeActivity(UUID id) {
        Activity activity = activityRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", id));
        
        activity.setIsCompleted(true);
        if (activity.getCompletedDate() == null) {
            activity.setCompletedDate(LocalDateTime.now());
        }
        
        activity = activityRepository.save(activity);
        return activityMapper.toResponse(activity);
    }
    
    @Transactional
    public void deleteActivity(UUID id, boolean hardDelete) {
        if (hardDelete) {
            activityRepository.deleteById(id);
        } else {
            Activity activity = activityRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", id));
            activity.setDeletedAt(LocalDateTime.now());
            activityRepository.save(activity);
        }
    }
}

