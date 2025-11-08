package com.splitia.service;

import com.splitia.dto.request.CreateAttendanceRequest;
import com.splitia.dto.response.AttendanceResponse;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.AttendanceMapper;
import com.splitia.model.hr.Attendance;
import com.splitia.model.hr.Employee;
import com.splitia.repository.AttendanceRepository;
import com.splitia.repository.EmployeeRepository;
import com.splitia.service.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceMapper attendanceMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> getAllAttendance(Pageable pageable, UUID employeeId) {
        if (employeeId != null) {
            return attendanceRepository.findByEmployeeId(employeeId, pageable)
                    .map(attendanceMapper::toResponse);
        }
        return attendanceRepository.findAllActive(pageable)
                .map(attendanceMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public AttendanceResponse getAttendanceById(UUID id) {
        Attendance attendance = attendanceRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance", "id", id));
        return attendanceMapper.toResponse(attendance);
    }
    
    @Transactional
    public AttendanceResponse createAttendance(CreateAttendanceRequest request) {
        Employee employee = employeeRepository.findByIdAndDeletedAtIsNull(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId()));
        
        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setDate(request.getDate());
        attendance.setCheckIn(request.getCheckIn());
        attendance.setCheckOut(request.getCheckOut());
        attendance.setStatus(request.getStatus());
        
        // Calculate hours worked
        if (attendance.getCheckIn() != null && attendance.getCheckOut() != null) {
            Duration duration = Duration.between(attendance.getCheckIn(), attendance.getCheckOut());
            attendance.setHoursWorked(duration.toHours() + (duration.toMinutes() % 60) / 60.0);
        }
        
        attendance = attendanceRepository.save(attendance);
        AttendanceResponse response = attendanceMapper.toResponse(attendance);
        
        Map<String, Object> data = new HashMap<>();
        data.put("attendance", response);
        webSocketNotificationService.notifyAttendanceCreated(attendance.getId(), data, getCurrentUserId());
        
        return response;
    }
    
    @Transactional
    public AttendanceResponse checkIn(UUID employeeId) {
        Employee employee = employeeRepository.findByIdAndDeletedAtIsNull(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        
        java.time.LocalDate today = java.time.LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employeeId, today)
                .orElse(null);
        
        if (attendance == null) {
            attendance = new Attendance();
            attendance.setEmployee(employee);
            attendance.setDate(today);
        }
        
        attendance.setCheckIn(LocalDateTime.now());
        attendance.setStatus(com.splitia.model.enums.AttendanceStatus.PRESENT);
        attendance = attendanceRepository.save(attendance);
        
        return attendanceMapper.toResponse(attendance);
    }
    
    @Transactional
    public AttendanceResponse checkOut(UUID employeeId) {
        Employee employee = employeeRepository.findByIdAndDeletedAtIsNull(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        
        java.time.LocalDate today = java.time.LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employeeId, today)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance", "employeeId", employeeId));
        
        attendance.setCheckOut(LocalDateTime.now());
        
        // Calculate hours worked
        if (attendance.getCheckIn() != null && attendance.getCheckOut() != null) {
            Duration duration = Duration.between(attendance.getCheckIn(), attendance.getCheckOut());
            attendance.setHoursWorked(duration.toHours() + (duration.toMinutes() % 60) / 60.0);
        }
        
        attendance = attendanceRepository.save(attendance);
        return attendanceMapper.toResponse(attendance);
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

