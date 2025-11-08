package com.splitia.service;

import com.splitia.dto.request.CreateEmployeeRequest;
import com.splitia.dto.response.EmployeeResponse;
import com.splitia.exception.BadRequestException;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.EmployeeMapper;
import com.splitia.model.User;
import com.splitia.model.enums.EmployeeStatus;
import com.splitia.model.hr.Employee;
import com.splitia.repository.EmployeeRepository;
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
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final EmployeeMapper employeeMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAllActive(pageable)
                .map(employeeMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(UUID id) {
        Employee employee = employeeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return employeeMapper.toResponse(employee);
    }
    
    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request, UUID createdById) {
        User user = userRepository.findByIdAndDeletedAtIsNull(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
        
        if (employeeRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new BadRequestException("Employee already exists for this user");
        }
        
        if (employeeRepository.findByEmployeeNumber(request.getEmployeeNumber()).isPresent()) {
            throw new BadRequestException("Employee number already exists");
        }
        
        Employee employee = new Employee();
        employee.setUser(user);
        employee.setEmployeeNumber(request.getEmployeeNumber());
        employee.setDepartment(request.getDepartment());
        employee.setPosition(request.getPosition());
        employee.setHireDate(request.getHireDate());
        employee.setSalary(request.getSalary());
        employee.setStatus(request.getStatus() != null ? request.getStatus() : EmployeeStatus.ACTIVE);
        employee.setManagerId(request.getManagerId());
        
        employee = employeeRepository.save(employee);
        EmployeeResponse response = employeeMapper.toResponse(employee);
        
        Map<String, Object> data = new HashMap<>();
        data.put("employee", response);
        webSocketNotificationService.notifyEmployeeCreated(employee.getId(), data, createdById);
        
        return response;
    }
    
    @Transactional
    public void deleteEmployee(UUID id, boolean hardDelete) {
        if (hardDelete) {
            employeeRepository.deleteById(id);
        } else {
            Employee employee = employeeRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
            employee.setDeletedAt(LocalDateTime.now());
            employeeRepository.save(employee);
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

