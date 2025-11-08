package com.splitia.controller;

import com.splitia.dto.request.CreateAttendanceRequest;
import com.splitia.dto.request.CreateEmployeeRequest;
import com.splitia.dto.request.CreatePayrollRequest;
import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.AttendanceResponse;
import com.splitia.dto.response.EmployeeResponse;
import com.splitia.dto.response.PayrollResponse;
import com.splitia.model.enums.PayrollStatus;
import com.splitia.service.AttendanceService;
import com.splitia.service.EmployeeService;
import com.splitia.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/hr")
@RequiredArgsConstructor
@Tag(name = "HR", description = "Human Resources management endpoints")
public class HRController {
    
    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final PayrollService payrollService;
    
    // Employees
    @GetMapping("/employees")
    @Operation(summary = "Get all employees")
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> getAllEmployees(Pageable pageable) {
        Page<EmployeeResponse> employees = employeeService.getAllEmployees(pageable);
        return ResponseEntity.ok(ApiResponse.success(employees));
    }
    
    @GetMapping("/employees/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable UUID id) {
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success(employee));
    }
    
    @PostMapping("/employees")
    @Operation(summary = "Create a new employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        UUID createdById = getCurrentUserId();
        EmployeeResponse employee = employeeService.createEmployee(request, createdById);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(employee, "Employee created successfully"));
    }
    
    @DeleteMapping("/employees/{id}")
    @Operation(summary = "Delete employee. Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        employeeService.deleteEmployee(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Employee permanently deleted" : "Employee soft deleted"));
    }
    
    // Attendance
    @GetMapping("/attendance")
    @Operation(summary = "Get all attendance records")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getAllAttendance(
            Pageable pageable,
            @RequestParam(required = false) UUID employeeId) {
        Page<AttendanceResponse> attendance = attendanceService.getAllAttendance(pageable, employeeId);
        return ResponseEntity.ok(ApiResponse.success(attendance));
    }
    
    @GetMapping("/attendance/{id}")
    @Operation(summary = "Get attendance by ID")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getAttendanceById(@PathVariable UUID id) {
        AttendanceResponse attendance = attendanceService.getAttendanceById(id);
        return ResponseEntity.ok(ApiResponse.success(attendance));
    }
    
    @PostMapping("/attendance")
    @Operation(summary = "Create attendance record")
    public ResponseEntity<ApiResponse<AttendanceResponse>> createAttendance(@Valid @RequestBody CreateAttendanceRequest request) {
        AttendanceResponse attendance = attendanceService.createAttendance(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(attendance, "Attendance recorded successfully"));
    }
    
    @PostMapping("/attendance/{employeeId}/check-in")
    @Operation(summary = "Check in employee")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkIn(@PathVariable UUID employeeId) {
        AttendanceResponse attendance = attendanceService.checkIn(employeeId);
        return ResponseEntity.ok(ApiResponse.success(attendance, "Checked in successfully"));
    }
    
    @PostMapping("/attendance/{employeeId}/check-out")
    @Operation(summary = "Check out employee")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkOut(@PathVariable UUID employeeId) {
        AttendanceResponse attendance = attendanceService.checkOut(employeeId);
        return ResponseEntity.ok(ApiResponse.success(attendance, "Checked out successfully"));
    }
    
    // Payroll
    @GetMapping("/payroll")
    @Operation(summary = "Get all payroll records")
    public ResponseEntity<ApiResponse<Page<PayrollResponse>>> getAllPayrolls(
            Pageable pageable,
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) PayrollStatus status) {
        Page<PayrollResponse> payrolls = payrollService.getAllPayrolls(pageable, employeeId, status);
        return ResponseEntity.ok(ApiResponse.success(payrolls));
    }
    
    @GetMapping("/payroll/{id}")
    @Operation(summary = "Get payroll by ID")
    public ResponseEntity<ApiResponse<PayrollResponse>> getPayrollById(@PathVariable UUID id) {
        PayrollResponse payroll = payrollService.getPayrollById(id);
        return ResponseEntity.ok(ApiResponse.success(payroll));
    }
    
    @PostMapping("/payroll")
    @Operation(summary = "Create payroll")
    public ResponseEntity<ApiResponse<PayrollResponse>> createPayroll(@Valid @RequestBody CreatePayrollRequest request) {
        PayrollResponse payroll = payrollService.createPayroll(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(payroll, "Payroll created successfully"));
    }
    
    @PutMapping("/payroll/{id}/approve")
    @Operation(summary = "Approve payroll")
    public ResponseEntity<ApiResponse<PayrollResponse>> approvePayroll(@PathVariable UUID id) {
        PayrollResponse payroll = payrollService.approvePayroll(id);
        return ResponseEntity.ok(ApiResponse.success(payroll, "Payroll approved successfully"));
    }
    
    @DeleteMapping("/payroll/{id}")
    @Operation(summary = "Delete payroll. Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deletePayroll(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        payrollService.deletePayroll(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Payroll permanently deleted" : "Payroll soft deleted"));
    }
    
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof com.splitia.security.CustomUserDetails) {
            com.splitia.security.CustomUserDetails userDetails = (com.splitia.security.CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUserId();
        }
        return null;
    }
}

