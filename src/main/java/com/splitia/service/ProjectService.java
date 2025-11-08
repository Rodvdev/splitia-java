package com.splitia.service;

import com.splitia.dto.request.CreateProjectRequest;
import com.splitia.dto.request.CreateTimeEntryRequest;
import com.splitia.dto.response.ProjectResponse;
import com.splitia.dto.response.TimeEntryResponse;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.ProjectMapper;
import com.splitia.mapper.TimeEntryMapper;
import com.splitia.model.Task;
import com.splitia.model.User;
import com.splitia.model.enums.ProjectStatus;
import com.splitia.model.project.Project;
import com.splitia.model.project.TimeEntry;
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
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    
    @Transactional(readOnly = true)
    public Page<ProjectResponse> getAllProjects(Pageable pageable, ProjectStatus status, UUID managerId) {
        if (status != null) {
            return projectRepository.findByStatus(status, pageable)
                    .map(projectMapper::toResponse);
        }
        if (managerId != null) {
            return projectRepository.findByManagerId(managerId, pageable)
                    .map(projectMapper::toResponse);
        }
        return projectRepository.findAllActive(pageable)
                .map(projectMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(UUID id) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        return projectMapper.toResponse(project);
    }
    
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, UUID createdById) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setStatus(request.getStatus() != null ? request.getStatus() : ProjectStatus.PLANNING);
        project.setBudget(request.getBudget());
        
        if (request.getManagerId() != null) {
            User manager = userRepository.findByIdAndDeletedAtIsNull(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getManagerId()));
            project.setManager(manager);
        }
        
        if (request.getTaskIds() != null && !request.getTaskIds().isEmpty()) {
            List<Task> tasks = new ArrayList<>();
            for (UUID taskId : request.getTaskIds()) {
                Task task = taskRepository.findByIdAndDeletedAtIsNull(taskId)
                        .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
                tasks.add(task);
            }
            project.setTasks(tasks);
        }
        
        project = projectRepository.save(project);
        ProjectResponse response = projectMapper.toResponse(project);
        
        Map<String, Object> data = new HashMap<>();
        data.put("project", response);
        webSocketNotificationService.notifyProjectUpdated(project.getId(), data, createdById);
        
        return response;
    }
    
    @Transactional
    public ProjectResponse updateProjectStatus(UUID id, ProjectStatus status) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        
        ProjectStatus oldStatus = project.getStatus();
        project.setStatus(status);
        project = projectRepository.save(project);
        
        ProjectResponse response = projectMapper.toResponse(project);
        
        Map<String, Object> data = new HashMap<>();
        data.put("project", response);
        webSocketNotificationService.notifyProjectStatusChanged(
            project.getId(), 
            oldStatus.name(), 
            status.name(), 
            getCurrentUserId()
        );
        
        return response;
    }
    
    @Transactional
    public void deleteProject(UUID id, boolean hardDelete) {
        if (hardDelete) {
            projectRepository.deleteById(id);
        } else {
            Project project = projectRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
            project.setDeletedAt(LocalDateTime.now());
            projectRepository.save(project);
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

