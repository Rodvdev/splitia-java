package com.splitia.service;

import com.splitia.dto.request.CreatePayrollRequest;
import com.splitia.dto.response.PayrollResponse;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.PayrollMapper;
import com.splitia.model.enums.PayrollItemType;
import com.splitia.model.enums.PayrollStatus;
import com.splitia.model.hr.Employee;
import com.splitia.model.hr.Payroll;
import com.splitia.model.hr.PayrollItem;
import com.splitia.repository.EmployeeRepository;
import com.splitia.repository.PayrollRepository;
import com.splitia.service.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayrollService {
    
    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final PayrollMapper payrollMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    
    @Transactional(readOnly = true)
    public Page<PayrollResponse> getAllPayrolls(Pageable pageable, UUID employeeId, PayrollStatus status) {
        if (employeeId != null) {
            return payrollRepository.findByEmployeeId(employeeId, pageable)
                    .map(payrollMapper::toResponse);
        }
        if (status != null) {
            return payrollRepository.findByStatus(status, pageable)
                    .map(payrollMapper::toResponse);
        }
        return payrollRepository.findAllActive(pageable)
                .map(payrollMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public PayrollResponse getPayrollById(UUID id) {
        Payroll payroll = payrollRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll", "id", id));
        return payrollMapper.toResponse(payroll);
    }
    
    @Transactional
    public PayrollResponse createPayroll(CreatePayrollRequest request) {
        Employee employee = employeeRepository.findByIdAndDeletedAtIsNull(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId()));
        
        Payroll payroll = new Payroll();
        payroll.setEmployee(employee);
        payroll.setPeriodStart(request.getPeriodStart());
        payroll.setPeriodEnd(request.getPeriodEnd());
        payroll.setBaseSalary(request.getBaseSalary() != null ? request.getBaseSalary() : employee.getSalary());
        payroll.setStatus(PayrollStatus.DRAFT);
        
        BigDecimal netSalary = payroll.getBaseSalary();
        List<PayrollItem> items = new ArrayList<>();
        
        // Add items
        if (request.getItems() != null) {
            for (com.splitia.dto.request.CreatePayrollItemRequest itemRequest : request.getItems()) {
                PayrollItem item = new PayrollItem();
                item.setPayroll(payroll);
                item.setType(itemRequest.getType());
                item.setDescription(itemRequest.getDescription());
                item.setAmount(itemRequest.getAmount());
                items.add(item);
                
                // Calculate net salary
                if (itemRequest.getType() == PayrollItemType.BASE_SALARY || 
                    itemRequest.getType() == PayrollItemType.BONUS || 
                    itemRequest.getType() == PayrollItemType.OVERTIME ||
                    itemRequest.getType() == PayrollItemType.ALLOWANCE) {
                    netSalary = netSalary.add(itemRequest.getAmount());
                } else {
                    netSalary = netSalary.subtract(itemRequest.getAmount());
                }
            }
        }
        
        payroll.setNetSalary(netSalary);
        payroll.setItems(items);
        payroll = payrollRepository.save(payroll);
        
        PayrollResponse response = payrollMapper.toResponse(payroll);
        
        Map<String, Object> data = new HashMap<>();
        data.put("payroll", response);
        webSocketNotificationService.notifyPayrollGenerated(payroll.getId(), data, getCurrentUserId());
        
        return response;
    }
    
    @Transactional
    public PayrollResponse approvePayroll(UUID id) {
        Payroll payroll = payrollRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll", "id", id));
        
        payroll.setStatus(PayrollStatus.APPROVED);
        payroll = payrollRepository.save(payroll);
        
        return payrollMapper.toResponse(payroll);
    }
    
    @Transactional
    public void deletePayroll(UUID id, boolean hardDelete) {
        if (hardDelete) {
            payrollRepository.deleteById(id);
        } else {
            Payroll payroll = payrollRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Payroll", "id", id));
            payroll.setDeletedAt(LocalDateTime.now());
            payrollRepository.save(payroll);
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

