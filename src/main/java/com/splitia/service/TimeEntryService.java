package com.splitia.service;

import com.splitia.dto.request.CreateTimeEntryRequest;
import com.splitia.dto.response.TimeEntryResponse;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.TimeEntryMapper;
import com.splitia.model.Task;
import com.splitia.model.User;
import com.splitia.model.project.Project;
import com.splitia.model.project.TimeEntry;
import com.splitia.repository.*;
import com.splitia.service.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TimeEntryService {
    
    private final TimeEntryRepository timeEntryRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TimeEntryMapper timeEntryMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    
    @Transactional(readOnly = true)
    public Page<TimeEntryResponse> getAllTimeEntries(Pageable pageable, UUID projectId, UUID userId, LocalDate startDate, LocalDate endDate) {
        if (projectId != null) {
            return timeEntryRepository.findByProjectId(projectId, pageable)
                    .map(timeEntryMapper::toResponse);
        }
        if (userId != null) {
            return timeEntryRepository.findByUserId(userId, pageable)
                    .map(timeEntryMapper::toResponse);
        }
        if (startDate != null && endDate != null) {
            return timeEntryRepository.findByDateRange(startDate, endDate, pageable)
                    .map(timeEntryMapper::toResponse);
        }
        return timeEntryRepository.findAllActive(pageable)
                .map(timeEntryMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public TimeEntryResponse getTimeEntryById(UUID id) {
        TimeEntry timeEntry = timeEntryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("TimeEntry", "id", id));
        return timeEntryMapper.toResponse(timeEntry);
    }
    
    @Transactional
    public TimeEntryResponse createTimeEntry(CreateTimeEntryRequest request, UUID userId) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getProjectId()));
        
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setProject(project);
        timeEntry.setUser(user);
        timeEntry.setDate(request.getDate());
        timeEntry.setHours(request.getHours());
        timeEntry.setDescription(request.getDescription());
        timeEntry.setIsBillable(request.getIsBillable() != null ? request.getIsBillable() : true);
        timeEntry.setStatus(request.getStatus() != null ? request.getStatus() : com.splitia.model.enums.TimeEntryStatus.DRAFT);
        
        if (request.getTaskId() != null) {
            Task task = taskRepository.findByIdAndDeletedAtIsNull(request.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task", "id", request.getTaskId()));
            timeEntry.setTask(task);
        }
        
        timeEntry = timeEntryRepository.save(timeEntry);
        TimeEntryResponse response = timeEntryMapper.toResponse(timeEntry);
        
        Map<String, Object> data = new HashMap<>();
        data.put("timeEntry", response);
        webSocketNotificationService.notifyTimeEntryCreated(timeEntry.getId(), data, userId);
        
        return response;
    }
    
    @Transactional
    public void deleteTimeEntry(UUID id, boolean hardDelete) {
        if (hardDelete) {
            timeEntryRepository.deleteById(id);
        } else {
            TimeEntry timeEntry = timeEntryRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("TimeEntry", "id", id));
            timeEntry.setDeletedAt(LocalDateTime.now());
            timeEntryRepository.save(timeEntry);
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
        }
        return null;
    }
}

